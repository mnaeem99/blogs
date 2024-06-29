package com.naeem.blogs.application.core.authorization.userspermission;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import com.naeem.blogs.application.core.authorization.userspermission.dto.*;
import com.naeem.blogs.domain.core.authorization.userspermission.IUserspermissionRepository;
import com.naeem.blogs.domain.core.authorization.userspermission.QUserspermission;
import com.naeem.blogs.domain.core.authorization.userspermission.Userspermission;
import com.naeem.blogs.domain.core.authorization.userspermission.UserspermissionId;
import com.naeem.blogs.domain.core.authorization.permission.IPermissionRepository;
import com.naeem.blogs.domain.core.authorization.permission.Permission;
import com.naeem.blogs.domain.core.authorization.users.IUsersRepository;
import com.naeem.blogs.domain.core.authorization.users.Users;


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

@Service("userspermissionAppService")
@RequiredArgsConstructor
public class UserspermissionAppService implements IUserspermissionAppService {
    
	@Qualifier("userspermissionRepository")
	@NonNull protected final IUserspermissionRepository _userspermissionRepository;

	
    @Qualifier("permissionRepository")
	@NonNull protected final IPermissionRepository _permissionRepository;

    @Qualifier("usersRepository")
	@NonNull protected final IUsersRepository _usersRepository;

	@Qualifier("IUserspermissionMapperImpl")
	@NonNull protected final IUserspermissionMapper mapper;

	@NonNull protected final LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
	public CreateUserspermissionOutput create(CreateUserspermissionInput input) {

		Userspermission userspermission = mapper.createUserspermissionInputToUserspermission(input);
		Permission foundPermission = null;
		Users foundUsers = null;
	  	if(input.getPermissionId()!=null) {
			foundPermission = _permissionRepository.findById(input.getPermissionId()).orElse(null);
			
		}
		else {
			return null;
		}
	  	if(input.getUsersUserId()!=null) {
			foundUsers = _usersRepository.findById(input.getUsersUserId()).orElse(null);
			
		}
		else {
			return null;
		}
		if(foundUsers != null || foundPermission != null)
		{			
			if(!checkIfPermissionAlreadyAssigned(foundUsers, foundPermission))
			{
				foundPermission.addUserspermissions(userspermission);
				foundUsers.addUserspermissions(userspermission);
			}		
		}
		else {
			return null;
		}

		Userspermission createdUserspermission = _userspermissionRepository.save(userspermission);
        CreateUserspermissionOutput output = mapper.userspermissionToCreateUserspermissionOutput(createdUserspermission);

		output.setRevoked(input.getRevoked());
		return output;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public UpdateUserspermissionOutput update(UserspermissionId userspermissionId, UpdateUserspermissionInput input) {

		Userspermission existing = _userspermissionRepository.findById(userspermissionId).orElseThrow(() -> new EntityNotFoundException("Userspermission not found"));

		Userspermission userspermission = mapper.updateUserspermissionInputToUserspermission(input);
		Permission foundPermission = null;
		Users foundUsers = null;
        
	  	if(input.getPermissionId()!=null) { 
			foundPermission = _permissionRepository.findById(input.getPermissionId()).orElse(null);
		}
		else {
			return null;
		}
        
	  	if(input.getUsersUserId()!=null) { 
			foundUsers = _usersRepository.findById(input.getUsersUserId()).orElse(null);
		}
		else {
			return null;
		}
		if(foundUsers != null || foundPermission != null)
		{			
			if(checkIfPermissionAlreadyAssigned(foundUsers, foundPermission))
			{
				userspermission.setRevoked(input.getRevoked());
				foundPermission.addUserspermissions(userspermission);
				foundUsers.addUserspermissions(userspermission);
			}		
		}
		else {
			return null;
		}
	    Userspermission updatedUserspermission = _userspermissionRepository.save(userspermission);
		return mapper.userspermissionToUpdateUserspermissionOutput(updatedUserspermission);
	}
	
	public boolean checkIfPermissionAlreadyAssigned(Users foundUsers,Permission foundPermission)
	{

		List<Userspermission> usersPermission = _userspermissionRepository.findByUsersUserId(foundUsers.getUserId());

		Iterator pIterator = usersPermission.iterator();
		while (pIterator.hasNext()) {
			Userspermission pe = (Userspermission) pIterator.next();
			if (pe.getPermission() == foundPermission ) {
				return true;
			}
		}
			
		return false;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(UserspermissionId userspermissionId) {

		Userspermission existing = _userspermissionRepository.findById(userspermissionId).orElseThrow(() -> new EntityNotFoundException("Userspermission not found"));
	 	
        if(existing.getPermission() !=null)
        {
        existing.getPermission().removeUserspermissions(existing);
        }
        if(existing.getUsers() !=null)
        {
        existing.getUsers().removeUserspermissions(existing);
        }
        if(existing !=null) {
			_userspermissionRepository.delete(existing);
		}
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public FindUserspermissionByIdOutput findById(UserspermissionId userspermissionId) {

		Userspermission foundUserspermission = _userspermissionRepository.findById(userspermissionId).orElse(null);
		if (foundUserspermission == null)  
			return null; 
 	   
 	    return mapper.userspermissionToFindUserspermissionByIdOutput(foundUserspermission);
	}

    //Permission
	// ReST API Call - GET /userspermission/1/permission
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public GetPermissionOutput getPermission(UserspermissionId userspermissionId) {

		Userspermission foundUserspermission = _userspermissionRepository.findById(userspermissionId).orElse(null);
		if (foundUserspermission == null) {
			logHelper.getLogger().error("There does not exist a userspermission wth a id='{}'", userspermissionId);
			return null;
		}
		Permission re = foundUserspermission.getPermission();
		return mapper.permissionToGetPermissionOutput(re, foundUserspermission);
	}
	
    //Users
	// ReST API Call - GET /userspermission/1/users
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public GetUsersOutput getUsers(UserspermissionId userspermissionId) {

		Userspermission foundUserspermission = _userspermissionRepository.findById(userspermissionId).orElse(null);
		if (foundUserspermission == null) {
			logHelper.getLogger().error("There does not exist a userspermission wth a id='{}'", userspermissionId);
			return null;
		}
		Users re = foundUserspermission.getUsers();
		return mapper.usersToGetUsersOutput(re, foundUserspermission);
	}
	
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<FindUserspermissionByIdOutput> find(SearchCriteria search, Pageable pageable) throws MalformedURLException  {

		Page<Userspermission> foundUserspermission = _userspermissionRepository.findAll(search(search), pageable);
		List<Userspermission> userspermissionList = foundUserspermission.getContent();
		Iterator<Userspermission> userspermissionIterator = userspermissionList.iterator(); 
		List<FindUserspermissionByIdOutput> output = new ArrayList<>();

		while (userspermissionIterator.hasNext()) {
		Userspermission userspermission = userspermissionIterator.next();
 	    output.add(mapper.userspermissionToFindUserspermissionByIdOutput(userspermission));
		}
		return output;
	}
	
	protected BooleanBuilder search(SearchCriteria search) throws MalformedURLException {

		QUserspermission userspermission= QUserspermission.userspermissionEntity;
		if(search != null) {
			Map<String,SearchFields> map = new HashMap<>();
			for(SearchFields fieldDetails: search.getFields())
			{
				map.put(fieldDetails.getFieldName(),fieldDetails);
			}
			List<String> keysList = new ArrayList<String>(map.keySet());
			checkProperties(keysList);
			return searchKeyValuePair(userspermission, map,search.getJoinColumns());
		}
		return null;
	}
	
	protected void checkProperties(List<String> list) throws MalformedURLException  {
		for (int i = 0; i < list.size(); i++) {
			if(!(
		        list.get(i).replace("%20","").trim().equals("permission") ||
		        list.get(i).replace("%20","").trim().equals("users") ||
				list.get(i).replace("%20","").trim().equals("permissionId") ||
				list.get(i).replace("%20","").trim().equals("revoked") ||
				list.get(i).replace("%20","").trim().equals("usersUserId")
			)) 
			{
			 throw new MalformedURLException("Wrong URL Format: Property " + list.get(i) + " not found!" );
			}
		}
	}
	
	protected BooleanBuilder searchKeyValuePair(QUserspermission userspermission, Map<String,SearchFields> map,Map<String,String> joinColumns) {
		BooleanBuilder builder = new BooleanBuilder();
        
		Iterator<Map.Entry<String, SearchFields>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, SearchFields> details = iterator.next();

			 if(details.getKey().replace("%20","").trim().equals("permissionId")) {
			 	if(details.getValue().getOperator().equals("contains")) {
					builder.and(userspermission.permissionId.like(details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(userspermission.permissionId.eq(Long.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("notEqual") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(userspermission.permissionId.ne(Long.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("range")) {
				  	if(StringUtils.isNumeric(details.getValue().getStartingValue()) && StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(userspermission.permissionId.between(Long.valueOf(details.getValue().getStartingValue()), Long.valueOf(details.getValue().getEndingValue())));
                   	} else if(StringUtils.isNumeric(details.getValue().getStartingValue())) {
                	  	builder.and(userspermission.permissionId.goe(Long.valueOf(details.getValue().getStartingValue())));
                   	} else if(StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	  	builder.and(userspermission.permissionId.loe(Long.valueOf(details.getValue().getEndingValue())));
					}
				}
			}
			if(details.getKey().replace("%20","").trim().equals("revoked")) {
				if(details.getValue().getOperator().equals("equals") && (details.getValue().getSearchValue().equalsIgnoreCase("true") || details.getValue().getSearchValue().equalsIgnoreCase("false"))) {
					builder.and(userspermission.revoked.eq(Boolean.parseBoolean(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("notEqual") && (details.getValue().getSearchValue().equalsIgnoreCase("true") || details.getValue().getSearchValue().equalsIgnoreCase("false"))) {
					builder.and(userspermission.revoked.ne(Boolean.parseBoolean(details.getValue().getSearchValue())));
				}
			}
			 if(details.getKey().replace("%20","").trim().equals("usersUserId")) {
			 	if(details.getValue().getOperator().equals("contains")) {
					builder.and(userspermission.usersUserId.like(details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(userspermission.usersUserId.eq(Long.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("notEqual") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(userspermission.usersUserId.ne(Long.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("range")) {
				  	if(StringUtils.isNumeric(details.getValue().getStartingValue()) && StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(userspermission.usersUserId.between(Long.valueOf(details.getValue().getStartingValue()), Long.valueOf(details.getValue().getEndingValue())));
                   	} else if(StringUtils.isNumeric(details.getValue().getStartingValue())) {
                	  	builder.and(userspermission.usersUserId.goe(Long.valueOf(details.getValue().getStartingValue())));
                   	} else if(StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	  	builder.and(userspermission.usersUserId.loe(Long.valueOf(details.getValue().getEndingValue())));
					}
				}
			}
	    
		    if(details.getKey().replace("%20","").trim().equals("permission")) {
				if(details.getValue().getOperator().equals("contains")) {
					builder.and(userspermission.permission.displayName.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals")) {
					builder.and(userspermission.permission.displayName.eq(details.getValue().getSearchValue()));
				} else if(details.getValue().getOperator().equals("notEqual")) {
					builder.and(userspermission.permission.displayName.ne(details.getValue().getSearchValue()));
				}
			}
		    if(details.getKey().replace("%20","").trim().equals("users")) {
				if(details.getValue().getOperator().equals("contains")) {
					builder.and(userspermission.users.username.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals")) {
					builder.and(userspermission.users.username.eq(details.getValue().getSearchValue()));
				} else if(details.getValue().getOperator().equals("notEqual")) {
					builder.and(userspermission.users.username.ne(details.getValue().getSearchValue()));
				}
			}
		}
		
		for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
        if(joinCol != null && joinCol.getKey().equals("permissionId")) {
		    builder.and(userspermission.permission.id.eq(Long.parseLong(joinCol.getValue())));
        }
        
		if(joinCol != null && joinCol.getKey().equals("permission")) {
		    builder.and(userspermission.permission.displayName.eq(joinCol.getValue()));
        }
        }
		for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
        if(joinCol != null && joinCol.getKey().equals("usersUserId")) {
		    builder.and(userspermission.users.userId.eq(Long.parseLong(joinCol.getValue())));
        }
        
		if(joinCol != null && joinCol.getKey().equals("users")) {
		    builder.and(userspermission.users.username.eq(joinCol.getValue()));
        }
        }
		return builder;
	}
	
	public UserspermissionId parseUserspermissionKey(String keysString) {
		
		String[] keyEntries = keysString.split(",");
		UserspermissionId userspermissionId = new UserspermissionId();
		
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
		
		userspermissionId.setPermissionId(Long.valueOf(keyMap.get("permissionId")));
		userspermissionId.setUsersUserId(Long.valueOf(keyMap.get("usersUserId")));
		return userspermissionId;
		
	}	
    
    
}



