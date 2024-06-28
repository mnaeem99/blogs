package com.naeem.blogs.application.core.users;

import org.springframework.data.domain.Pageable;
import com.naeem.blogs.application.core.users.dto.*;
import com.naeem.blogs.commons.search.SearchCriteria;
import java.net.MalformedURLException;
import java.util.*;

public interface IUsersAppService {
	
	//CRUD Operations
	CreateUsersOutput create(CreateUsersInput users);

    void delete(Integer id);

    UpdateUsersOutput update(Integer id, UpdateUsersInput input);

    FindUsersByIdOutput findById(Integer id);


    List<FindUsersByIdOutput> find(SearchCriteria search, Pageable pageable) throws MalformedURLException;
    
    //Join Column Parsers

	Map<String,String> parseCommentsJoinColumn(String keysString);

	Map<String,String> parseLikesJoinColumn(String keysString);

	Map<String,String> parsePostsJoinColumn(String keysString);
}

