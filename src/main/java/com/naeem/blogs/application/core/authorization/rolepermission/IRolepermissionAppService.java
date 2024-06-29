package com.naeem.blogs.application.core.authorization.rolepermission;

import com.naeem.blogs.domain.core.authorization.rolepermission.RolepermissionId;
import org.springframework.data.domain.Pageable;
import com.naeem.blogs.application.core.authorization.rolepermission.dto.*;
import com.naeem.blogs.commons.search.SearchCriteria;
import java.net.MalformedURLException;
import java.util.*;

public interface IRolepermissionAppService {
	
	//CRUD Operations
	CreateRolepermissionOutput create(CreateRolepermissionInput rolepermission);

    void delete(RolepermissionId rolepermissionId);

    UpdateRolepermissionOutput update(RolepermissionId rolepermissionId, UpdateRolepermissionInput input);

    FindRolepermissionByIdOutput findById(RolepermissionId rolepermissionId);


    List<FindRolepermissionByIdOutput> find(SearchCriteria search, Pageable pageable) throws MalformedURLException;
	
	void deleteUserTokens(Long roleId);
	//Relationship Operations
	//Relationship Operations
    
    GetPermissionOutput getPermission(RolepermissionId rolepermissionId);
    
    GetRoleOutput getRole(RolepermissionId rolepermissionId);
    
    //Join Column Parsers
    
	RolepermissionId parseRolepermissionKey(String keysString);
}

