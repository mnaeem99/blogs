package com.naeem.blogs.application.core.likes;

import org.springframework.data.domain.Pageable;
import com.naeem.blogs.application.core.likes.dto.*;
import com.naeem.blogs.commons.search.SearchCriteria;
import java.net.MalformedURLException;
import java.util.*;

public interface ILikesAppService {
	
	//CRUD Operations
	CreateLikesOutput create(CreateLikesInput likes);

    void delete(Integer id);

    UpdateLikesOutput update(Integer id, UpdateLikesInput input);

    FindLikesByIdOutput findById(Integer id);


    List<FindLikesByIdOutput> find(SearchCriteria search, Pageable pageable) throws MalformedURLException;
	//Relationship Operations
	//Relationship Operations
    
    GetPostsOutput getPosts(Integer likesid);
    
    GetUsersOutput getUsers(Integer likesid);
    
    //Join Column Parsers
}

