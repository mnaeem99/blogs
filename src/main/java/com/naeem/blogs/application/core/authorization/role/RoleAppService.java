package com.naeem.blogs.application.core.authorization.role;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import com.naeem.blogs.application.core.authorization.role.dto.*;
import com.naeem.blogs.domain.core.authorization.role.IRoleRepository;
import com.naeem.blogs.domain.core.authorization.role.QRole;
import com.naeem.blogs.domain.core.authorization.role.Role;


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

@Service("roleAppService")
@RequiredArgsConstructor
public class RoleAppService implements IRoleAppService {
    
	@Qualifier("roleRepository")
	@NonNull protected final IRoleRepository _roleRepository;

	
	@Qualifier("IRoleMapperImpl")
	@NonNull protected final IRoleMapper mapper;

	@NonNull protected final LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
	public CreateRoleOutput create(CreateRoleInput input) {

		Role role = mapper.createRoleInputToRole(input);

		Role createdRole = _roleRepository.save(role);
		return mapper.roleToCreateRoleOutput(createdRole);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public UpdateRoleOutput update(Long roleId, UpdateRoleInput input) {

		Role existing = _roleRepository.findById(roleId).orElseThrow(() -> new EntityNotFoundException("Role not found"));

		Role role = mapper.updateRoleInputToRole(input);
		role.setRolepermissionsSet(existing.getRolepermissionsSet());
		role.setUsersrolesSet(existing.getUsersrolesSet());
		
		Role updatedRole = _roleRepository.save(role);
		return mapper.roleToUpdateRoleOutput(updatedRole);
	}
	
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Long roleId) {

		Role existing = _roleRepository.findById(roleId).orElseThrow(() -> new EntityNotFoundException("Role not found"));
	 	
        if(existing !=null) {
			_roleRepository.delete(existing);
		}
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public FindRoleByIdOutput findById(Long roleId) {

		Role foundRole = _roleRepository.findById(roleId).orElse(null);
		if (foundRole == null)  
			return null; 
 	   
 	    return mapper.roleToFindRoleByIdOutput(foundRole);
	}

 	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public FindRoleByNameOutput findByRoleName(String roleName) {

		Role foundRole = _roleRepository.findByRoleName(roleName);
		if (foundRole == null) {
			return null;
		}

		return mapper.roleToFindRoleByNameOutput(foundRole);
	}
	
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<FindRoleByIdOutput> find(SearchCriteria search, Pageable pageable) throws MalformedURLException  {

		Page<Role> foundRole = _roleRepository.findAll(search(search), pageable);
		List<Role> roleList = foundRole.getContent();
		Iterator<Role> roleIterator = roleList.iterator(); 
		List<FindRoleByIdOutput> output = new ArrayList<>();

		while (roleIterator.hasNext()) {
		Role role = roleIterator.next();
 	    output.add(mapper.roleToFindRoleByIdOutput(role));
		}
		return output;
	}
	
	protected BooleanBuilder search(SearchCriteria search) throws MalformedURLException {

		QRole role= QRole.roleEntity;
		if(search != null) {
			Map<String,SearchFields> map = new HashMap<>();
			for(SearchFields fieldDetails: search.getFields())
			{
				map.put(fieldDetails.getFieldName(),fieldDetails);
			}
			List<String> keysList = new ArrayList<String>(map.keySet());
			checkProperties(keysList);
			return searchKeyValuePair(role, map,search.getJoinColumns());
		}
		return null;
	}
	
	protected void checkProperties(List<String> list) throws MalformedURLException  {
		for (int i = 0; i < list.size(); i++) {
			if(!(
				list.get(i).replace("%20","").trim().equals("displayName") ||
				list.get(i).replace("%20","").trim().equals("id") ||
				list.get(i).replace("%20","").trim().equals("name")
			)) 
			{
			 throw new MalformedURLException("Wrong URL Format: Property " + list.get(i) + " not found!" );
			}
		}
	}
	
	protected BooleanBuilder searchKeyValuePair(QRole role, Map<String,SearchFields> map,Map<String,String> joinColumns) {
		BooleanBuilder builder = new BooleanBuilder();
        
		Iterator<Map.Entry<String, SearchFields>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, SearchFields> details = iterator.next();

            if(details.getKey().replace("%20","").trim().equals("displayName")) {
				if(details.getValue().getOperator().equals("contains")) {
					builder.and(role.displayName.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals")) {
					builder.and(role.displayName.eq(details.getValue().getSearchValue()));
				} else if(details.getValue().getOperator().equals("notEqual")) {
					builder.and(role.displayName.ne(details.getValue().getSearchValue()));
				}
			}
			 if(details.getKey().replace("%20","").trim().equals("id")) {
			 	if(details.getValue().getOperator().equals("contains")) {
					builder.and(role.id.like(details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(role.id.eq(Long.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("notEqual") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(role.id.ne(Long.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("range")) {
				  	if(StringUtils.isNumeric(details.getValue().getStartingValue()) && StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(role.id.between(Long.valueOf(details.getValue().getStartingValue()), Long.valueOf(details.getValue().getEndingValue())));
                   	} else if(StringUtils.isNumeric(details.getValue().getStartingValue())) {
                	  	builder.and(role.id.goe(Long.valueOf(details.getValue().getStartingValue())));
                   	} else if(StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	  	builder.and(role.id.loe(Long.valueOf(details.getValue().getEndingValue())));
					}
				}
			}
            if(details.getKey().replace("%20","").trim().equals("name")) {
				if(details.getValue().getOperator().equals("contains")) {
					builder.and(role.name.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals")) {
					builder.and(role.name.eq(details.getValue().getSearchValue()));
				} else if(details.getValue().getOperator().equals("notEqual")) {
					builder.and(role.name.ne(details.getValue().getSearchValue()));
				}
			}
	    
		}
		
		return builder;
	}
	
	public Map<String,String> parseRolepermissionsJoinColumn(String keysString) {
		
		Map<String,String> joinColumnMap = new HashMap<String,String>();
		joinColumnMap.put("roleId", keysString);
		  
		return joinColumnMap;
	}
    
	public Map<String,String> parseUsersrolesJoinColumn(String keysString) {
		
		Map<String,String> joinColumnMap = new HashMap<String,String>();
		joinColumnMap.put("roleId", keysString);
		  
		return joinColumnMap;
	}
    
}



