package com.naeem.blogs.application.core.authorization.rolepermission;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import com.naeem.blogs.application.core.authorization.rolepermission.dto.*;
import com.naeem.blogs.domain.core.authorization.rolepermission.IRolepermissionRepository;
import com.naeem.blogs.domain.core.authorization.rolepermission.QRolepermission;
import com.naeem.blogs.domain.core.authorization.rolepermission.Rolepermission;
import com.naeem.blogs.domain.core.authorization.rolepermission.RolepermissionId;
import com.naeem.blogs.domain.core.authorization.permission.IPermissionRepository;
import com.naeem.blogs.domain.core.authorization.permission.Permission;
import com.naeem.blogs.domain.core.authorization.role.IRoleRepository;
import com.naeem.blogs.domain.core.authorization.role.Role;
import com.naeem.blogs.domain.core.authorization.usersrole.IUsersroleRepository;
import com.naeem.blogs.domain.core.authorization.usersrole.Usersrole;
import com.naeem.blogs.security.JWTAppService;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page; 
import org.springframework.data.domain.Pageable; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;
import com.naeem.blogs.commons.search.*;
import com.naeem.blogs.commons.logging.LoggingHelper;
import com.querydsl.core.BooleanBuilder;
import java.net.MalformedURLException;
import java.time.*;
import java.util.*;
import javax.persistence.EntityNotFoundException;

@Service("rolepermissionAppService")
@RequiredArgsConstructor
public class RolepermissionAppService implements IRolepermissionAppService {
	@NonNull protected final JWTAppService _jwtAppService;
	
	@Qualifier("usersroleRepository")
	@NonNull protected final IUsersroleRepository _usersroleRepository;
    
	@Qualifier("rolepermissionRepository")
	@NonNull protected final IRolepermissionRepository _rolepermissionRepository;

	
    @Qualifier("permissionRepository")
	@NonNull protected final IPermissionRepository _permissionRepository;

    @Qualifier("roleRepository")
	@NonNull protected final IRoleRepository _roleRepository;

	@Qualifier("IRolepermissionMapperImpl")
	@NonNull protected final IRolepermissionMapper mapper;

	@NonNull protected final LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
	public CreateRolepermissionOutput create(CreateRolepermissionInput input) {

		Rolepermission rolepermission = mapper.createRolepermissionInputToRolepermission(input);
		Permission foundPermission = null;
		Role foundRole = null;
	  	if(input.getPermissionId()!=null) {
			foundPermission = _permissionRepository.findById(input.getPermissionId()).orElse(null);
			
		}
		else {
			return null;
		}
	  	if(input.getRoleId()!=null) {
			foundRole = _roleRepository.findById(input.getRoleId()).orElse(null);
			
		}
		else {
			return null;
		}
		
		if(foundPermission != null && foundRole != null) {
			if(!checkIfPermissionAlreadyAssigned(foundRole, foundPermission))
			{
				rolepermission.setPermission(foundPermission);
				rolepermission.setRole(foundRole);
			}
		}
		else {
		return null;
		}


		Rolepermission createdRolepermission = _rolepermissionRepository.save(rolepermission);
		return mapper.rolepermissionToCreateRolepermissionOutput(createdRolepermission);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public UpdateRolepermissionOutput update(RolepermissionId rolepermissionId, UpdateRolepermissionInput input) {

		Rolepermission existing = _rolepermissionRepository.findById(rolepermissionId).orElseThrow(() -> new EntityNotFoundException("Rolepermission not found"));

		Rolepermission rolepermission = mapper.updateRolepermissionInputToRolepermission(input);
		Permission foundPermission = null;
		Role foundRole = null;
        
	  	if(input.getPermissionId()!=null) { 
			foundPermission = _permissionRepository.findById(input.getPermissionId()).orElse(null);
		}
		else {
			return null;
		}
        
	  	if(input.getRoleId()!=null) { 
			foundRole = _roleRepository.findById(input.getRoleId()).orElse(null);
		}
		else {
			return null;
		}
		
		if(foundPermission != null && foundRole != null) {
			if(checkIfPermissionAlreadyAssigned(foundRole, foundPermission))
			{
				foundPermission.addRolepermissions(rolepermission);
				foundRole.addRolepermissions(rolepermission);
			}
		}
		else {
			return null;
		}
		
		Rolepermission updatedRolepermission = _rolepermissionRepository.save(rolepermission);
		return mapper.rolepermissionToUpdateRolepermissionOutput(updatedRolepermission);
		
	}
	
	protected boolean checkIfPermissionAlreadyAssigned(Role foundRole,Permission foundPermission)
	{
		List<Rolepermission> rolePermissionList = _rolepermissionRepository.findByRoleId(foundRole.getId());

		Iterator pIterator = rolePermissionList.iterator();
			while (pIterator.hasNext()) { 
				Rolepermission pe = (Rolepermission) pIterator.next();
				if (pe.getPermission() == foundPermission ) {
					return true;
				}
			}
			
	return false;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(RolepermissionId rolepermissionId) {

		Rolepermission existing = _rolepermissionRepository.findById(rolepermissionId).orElseThrow(() -> new EntityNotFoundException("Rolepermission not found"));
	 	
        if(existing.getPermission() !=null)
        {
        existing.getPermission().removeRolepermissions(existing);
        }
        if(existing.getRole() !=null)
        {
        existing.getRole().removeRolepermissions(existing);
        }
        if(existing !=null) {
			_rolepermissionRepository.delete(existing);
		}
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public FindRolepermissionByIdOutput findById(RolepermissionId rolepermissionId) {

		Rolepermission foundRolepermission = _rolepermissionRepository.findById(rolepermissionId).orElse(null);
		if (foundRolepermission == null)  
			return null; 
 	   
 	    return mapper.rolepermissionToFindRolepermissionByIdOutput(foundRolepermission);
	}

	public void deleteUserTokens(Long roleId)
    {
    	Role role =  _roleRepository.findById(roleId).orElse(null);
		List<Usersrole> userRole = _usersroleRepository.findByRoleId(roleId);

		for (Usersrole ur : userRole)
		{
			if(ur.getUsers() != null)
				_jwtAppService.deleteAllUserTokens(ur.getUsers().getUsername()); 
		}

    }
    
    //Permission
	// ReST API Call - GET /rolepermission/1/permission
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public GetPermissionOutput getPermission(RolepermissionId rolepermissionId) {

		Rolepermission foundRolepermission = _rolepermissionRepository.findById(rolepermissionId).orElse(null);
		if (foundRolepermission == null) {
			logHelper.getLogger().error("There does not exist a rolepermission wth a id='{}'", rolepermissionId);
			return null;
		}
		Permission re = foundRolepermission.getPermission();
		return mapper.permissionToGetPermissionOutput(re, foundRolepermission);
	}
	
    //Role
	// ReST API Call - GET /rolepermission/1/role
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public GetRoleOutput getRole(RolepermissionId rolepermissionId) {

		Rolepermission foundRolepermission = _rolepermissionRepository.findById(rolepermissionId).orElse(null);
		if (foundRolepermission == null) {
			logHelper.getLogger().error("There does not exist a rolepermission wth a id='{}'", rolepermissionId);
			return null;
		}
		Role re = foundRolepermission.getRole();
		return mapper.roleToGetRoleOutput(re, foundRolepermission);
	}
	
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<FindRolepermissionByIdOutput> find(SearchCriteria search, Pageable pageable) throws MalformedURLException  {

		Page<Rolepermission> foundRolepermission = _rolepermissionRepository.findAll(search(search), pageable);
		List<Rolepermission> rolepermissionList = foundRolepermission.getContent();
		Iterator<Rolepermission> rolepermissionIterator = rolepermissionList.iterator(); 
		List<FindRolepermissionByIdOutput> output = new ArrayList<>();

		while (rolepermissionIterator.hasNext()) {
		Rolepermission rolepermission = rolepermissionIterator.next();
 	    output.add(mapper.rolepermissionToFindRolepermissionByIdOutput(rolepermission));
		}
		return output;
	}
	
	protected BooleanBuilder search(SearchCriteria search) throws MalformedURLException {

		QRolepermission rolepermission= QRolepermission.rolepermissionEntity;
		if(search != null) {
			Map<String,SearchFields> map = new HashMap<>();
			for(SearchFields fieldDetails: search.getFields())
			{
				map.put(fieldDetails.getFieldName(),fieldDetails);
			}
			List<String> keysList = new ArrayList<String>(map.keySet());
			checkProperties(keysList);
			return searchKeyValuePair(rolepermission, map,search.getJoinColumns());
		}
		return null;
	}
	
	protected void checkProperties(List<String> list) throws MalformedURLException  {
		for (int i = 0; i < list.size(); i++) {
			if(!(
		        list.get(i).replace("%20","").trim().equals("permission") ||
		        list.get(i).replace("%20","").trim().equals("role") ||
				list.get(i).replace("%20","").trim().equals("permissionId") ||
				list.get(i).replace("%20","").trim().equals("roleId")
			)) 
			{
			 throw new MalformedURLException("Wrong URL Format: Property " + list.get(i) + " not found!" );
			}
		}
	}
	
	protected BooleanBuilder searchKeyValuePair(QRolepermission rolepermission, Map<String,SearchFields> map,Map<String,String> joinColumns) {
		BooleanBuilder builder = new BooleanBuilder();
        
		Iterator<Map.Entry<String, SearchFields>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, SearchFields> details = iterator.next();

			 if(details.getKey().replace("%20","").trim().equals("permissionId")) {
			 	if(details.getValue().getOperator().equals("contains")) {
					builder.and(rolepermission.permissionId.like(details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(rolepermission.permissionId.eq(Long.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("notEqual") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(rolepermission.permissionId.ne(Long.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("range")) {
				  	if(StringUtils.isNumeric(details.getValue().getStartingValue()) && StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(rolepermission.permissionId.between(Long.valueOf(details.getValue().getStartingValue()), Long.valueOf(details.getValue().getEndingValue())));
                   	} else if(StringUtils.isNumeric(details.getValue().getStartingValue())) {
                	  	builder.and(rolepermission.permissionId.goe(Long.valueOf(details.getValue().getStartingValue())));
                   	} else if(StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	  	builder.and(rolepermission.permissionId.loe(Long.valueOf(details.getValue().getEndingValue())));
					}
				}
			}
			 if(details.getKey().replace("%20","").trim().equals("roleId")) {
			 	if(details.getValue().getOperator().equals("contains")) {
					builder.and(rolepermission.roleId.like(details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(rolepermission.roleId.eq(Long.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("notEqual") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(rolepermission.roleId.ne(Long.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("range")) {
				  	if(StringUtils.isNumeric(details.getValue().getStartingValue()) && StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(rolepermission.roleId.between(Long.valueOf(details.getValue().getStartingValue()), Long.valueOf(details.getValue().getEndingValue())));
                   	} else if(StringUtils.isNumeric(details.getValue().getStartingValue())) {
                	  	builder.and(rolepermission.roleId.goe(Long.valueOf(details.getValue().getStartingValue())));
                   	} else if(StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	  	builder.and(rolepermission.roleId.loe(Long.valueOf(details.getValue().getEndingValue())));
					}
				}
			}
	    
		    if(details.getKey().replace("%20","").trim().equals("permission")) {
				if(details.getValue().getOperator().equals("contains")) {
					builder.and(rolepermission.permission.displayName.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals")) {
					builder.and(rolepermission.permission.displayName.eq(details.getValue().getSearchValue()));
				} else if(details.getValue().getOperator().equals("notEqual")) {
					builder.and(rolepermission.permission.displayName.ne(details.getValue().getSearchValue()));
				}
			}
		    if(details.getKey().replace("%20","").trim().equals("role")) {
				if(details.getValue().getOperator().equals("contains")) {
					builder.and(rolepermission.role.displayName.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals")) {
					builder.and(rolepermission.role.displayName.eq(details.getValue().getSearchValue()));
				} else if(details.getValue().getOperator().equals("notEqual")) {
					builder.and(rolepermission.role.displayName.ne(details.getValue().getSearchValue()));
				}
			}
		}
		
		for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
        if(joinCol != null && joinCol.getKey().equals("permissionId")) {
		    builder.and(rolepermission.permission.id.eq(Long.parseLong(joinCol.getValue())));
        }
        
		if(joinCol != null && joinCol.getKey().equals("permission")) {
		    builder.and(rolepermission.permission.displayName.eq(joinCol.getValue()));
        }
        }
		for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
        if(joinCol != null && joinCol.getKey().equals("roleId")) {
		    builder.and(rolepermission.role.id.eq(Long.parseLong(joinCol.getValue())));
        }
        
		if(joinCol != null && joinCol.getKey().equals("role")) {
		    builder.and(rolepermission.role.displayName.eq(joinCol.getValue()));
        }
        }
		return builder;
	}
	
	public RolepermissionId parseRolepermissionKey(String keysString) {
		
		String[] keyEntries = keysString.split(",");
		RolepermissionId rolepermissionId = new RolepermissionId();
		
		Map<String,String> keyMap = new HashMap<String,String>();
		if(keyEntries.length > 1) {
			for(String keyEntry: keyEntries)
			{
				String[] keyEntryArr = keyEntry.split("=");
				if(keyEntryArr.length > 1) {
					keyMap.put(keyEntryArr[0], keyEntryArr[1]);					
				}
				else {
					return null;
				}
			}
		}
		else {
			String[] keyEntryArr = keysString.split("=");
			if(keyEntryArr.length > 1) {
				keyMap.put(keyEntryArr[0], keyEntryArr[1]);					
			}
			else {
				return null;
			}
		}
		
		rolepermissionId.setPermissionId(Long.valueOf(keyMap.get("permissionId")));
		rolepermissionId.setRoleId(Long.valueOf(keyMap.get("roleId")));
		return rolepermissionId;
		
	}	
    
    
}



