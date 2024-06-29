package com.naeem.blogs.application.core.authorization.permission;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import com.naeem.blogs.application.core.authorization.permission.dto.*;
import com.naeem.blogs.domain.core.authorization.permission.IPermissionRepository;
import com.naeem.blogs.domain.core.authorization.permission.QPermission;
import com.naeem.blogs.domain.core.authorization.permission.Permission;


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

@Service("permissionAppService")
@RequiredArgsConstructor
public class PermissionAppService implements IPermissionAppService {
    
	@Qualifier("permissionRepository")
	@NonNull protected final IPermissionRepository _permissionRepository;

	
	@Qualifier("IPermissionMapperImpl")
	@NonNull protected final IPermissionMapper mapper;

	@NonNull protected final LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
	public CreatePermissionOutput create(CreatePermissionInput input) {

		Permission permission = mapper.createPermissionInputToPermission(input);

		Permission createdPermission = _permissionRepository.save(permission);
		return mapper.permissionToCreatePermissionOutput(createdPermission);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public UpdatePermissionOutput update(Long permissionId, UpdatePermissionInput input) {

		Permission existing = _permissionRepository.findById(permissionId).orElseThrow(() -> new EntityNotFoundException("Permission not found"));

		Permission permission = mapper.updatePermissionInputToPermission(input);
		permission.setRolepermissionsSet(existing.getRolepermissionsSet());
		permission.setUserspermissionsSet(existing.getUserspermissionsSet());
		
		Permission updatedPermission = _permissionRepository.save(permission);
		return mapper.permissionToUpdatePermissionOutput(updatedPermission);
	}
	
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Long permissionId) {

		Permission existing = _permissionRepository.findById(permissionId).orElseThrow(() -> new EntityNotFoundException("Permission not found"));
	 	
        if(existing !=null) {
			_permissionRepository.delete(existing);
		}
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public FindPermissionByIdOutput findById(Long permissionId) {

		Permission foundPermission = _permissionRepository.findById(permissionId).orElse(null);
		if (foundPermission == null)  
			return null; 
 	   
 	    return mapper.permissionToFindPermissionByIdOutput(foundPermission);
	}

 	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public FindPermissionByNameOutput findByPermissionName(String permissionName) {

		Permission foundPermission = _permissionRepository.findByPermissionName(permissionName);
		if (foundPermission == null) {
			return null;
		}

		return mapper.permissionToFindPermissionByNameOutput(foundPermission);
	}
	
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<FindPermissionByIdOutput> find(SearchCriteria search, Pageable pageable) throws MalformedURLException  {

		Page<Permission> foundPermission = _permissionRepository.findAll(search(search), pageable);
		List<Permission> permissionList = foundPermission.getContent();
		Iterator<Permission> permissionIterator = permissionList.iterator(); 
		List<FindPermissionByIdOutput> output = new ArrayList<>();

		while (permissionIterator.hasNext()) {
		Permission permission = permissionIterator.next();
 	    output.add(mapper.permissionToFindPermissionByIdOutput(permission));
		}
		return output;
	}
	
	protected BooleanBuilder search(SearchCriteria search) throws MalformedURLException {

		QPermission permission= QPermission.permissionEntity;
		if(search != null) {
			Map<String,SearchFields> map = new HashMap<>();
			for(SearchFields fieldDetails: search.getFields())
			{
				map.put(fieldDetails.getFieldName(),fieldDetails);
			}
			List<String> keysList = new ArrayList<String>(map.keySet());
			checkProperties(keysList);
			return searchKeyValuePair(permission, map,search.getJoinColumns());
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
	
	protected BooleanBuilder searchKeyValuePair(QPermission permission, Map<String,SearchFields> map,Map<String,String> joinColumns) {
		BooleanBuilder builder = new BooleanBuilder();
        
		Iterator<Map.Entry<String, SearchFields>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, SearchFields> details = iterator.next();

            if(details.getKey().replace("%20","").trim().equals("displayName")) {
				if(details.getValue().getOperator().equals("contains")) {
					builder.and(permission.displayName.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals")) {
					builder.and(permission.displayName.eq(details.getValue().getSearchValue()));
				} else if(details.getValue().getOperator().equals("notEqual")) {
					builder.and(permission.displayName.ne(details.getValue().getSearchValue()));
				}
			}
			 if(details.getKey().replace("%20","").trim().equals("id")) {
			 	if(details.getValue().getOperator().equals("contains")) {
					builder.and(permission.id.like(details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(permission.id.eq(Long.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("notEqual") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(permission.id.ne(Long.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("range")) {
				  	if(StringUtils.isNumeric(details.getValue().getStartingValue()) && StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(permission.id.between(Long.valueOf(details.getValue().getStartingValue()), Long.valueOf(details.getValue().getEndingValue())));
                   	} else if(StringUtils.isNumeric(details.getValue().getStartingValue())) {
                	  	builder.and(permission.id.goe(Long.valueOf(details.getValue().getStartingValue())));
                   	} else if(StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	  	builder.and(permission.id.loe(Long.valueOf(details.getValue().getEndingValue())));
					}
				}
			}
            if(details.getKey().replace("%20","").trim().equals("name")) {
				if(details.getValue().getOperator().equals("contains")) {
					builder.and(permission.name.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals")) {
					builder.and(permission.name.eq(details.getValue().getSearchValue()));
				} else if(details.getValue().getOperator().equals("notEqual")) {
					builder.and(permission.name.ne(details.getValue().getSearchValue()));
				}
			}
	    
		}
		
		return builder;
	}
	
	public Map<String,String> parseRolepermissionsJoinColumn(String keysString) {
		
		Map<String,String> joinColumnMap = new HashMap<String,String>();
		joinColumnMap.put("permissionId", keysString);
		  
		return joinColumnMap;
	}
    
	public Map<String,String> parseUserspermissionsJoinColumn(String keysString) {
		
		Map<String,String> joinColumnMap = new HashMap<String,String>();
		joinColumnMap.put("permissionId", keysString);
		  
		return joinColumnMap;
	}
    
}



