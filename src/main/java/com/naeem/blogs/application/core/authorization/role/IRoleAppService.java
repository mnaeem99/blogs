package com.naeem.blogs.application.core.authorization.role;

import org.springframework.data.domain.Pageable;
import com.naeem.blogs.application.core.authorization.role.dto.*;
import com.naeem.blogs.commons.search.SearchCriteria;
import java.net.MalformedURLException;
import java.util.*;

public interface IRoleAppService {
	
	//CRUD Operations
	CreateRoleOutput create(CreateRoleInput role);

    void delete(Long id);

    UpdateRoleOutput update(Long id, UpdateRoleInput input);

    FindRoleByIdOutput findById(Long id);


    List<FindRoleByIdOutput> find(SearchCriteria search, Pageable pageable) throws MalformedURLException;
	
 	FindRoleByNameOutput findByRoleName(String roleName);
    
    //Join Column Parsers

	Map<String,String> parseRolepermissionsJoinColumn(String keysString);

	Map<String,String> parseUsersrolesJoinColumn(String keysString);
}

