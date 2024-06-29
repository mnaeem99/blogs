package com.naeem.blogs.application.core.authorization.permission;

import org.springframework.data.domain.Pageable;
import com.naeem.blogs.application.core.authorization.permission.dto.*;
import com.naeem.blogs.commons.search.SearchCriteria;
import java.net.MalformedURLException;
import java.util.*;

public interface IPermissionAppService {
	
	//CRUD Operations
	CreatePermissionOutput create(CreatePermissionInput permission);

    void delete(Long id);

    UpdatePermissionOutput update(Long id, UpdatePermissionInput input);

    FindPermissionByIdOutput findById(Long id);


    List<FindPermissionByIdOutput> find(SearchCriteria search, Pageable pageable) throws MalformedURLException;
	
 	FindPermissionByNameOutput findByPermissionName(String permissionName);
    
    //Join Column Parsers

	Map<String,String> parseRolepermissionsJoinColumn(String keysString);

	Map<String,String> parseUserspermissionsJoinColumn(String keysString);
}

