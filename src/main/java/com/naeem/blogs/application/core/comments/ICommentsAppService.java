package com.naeem.blogs.application.core.comments;

import org.springframework.data.domain.Pageable;
import com.naeem.blogs.application.core.comments.dto.*;
import com.naeem.blogs.commons.search.SearchCriteria;
import java.net.MalformedURLException;
import java.util.*;

public interface ICommentsAppService {
	
	//CRUD Operations
	CreateCommentsOutput create(CreateCommentsInput comments);

    void delete(Integer id);

    UpdateCommentsOutput update(Integer id, UpdateCommentsInput input);

    FindCommentsByIdOutput findById(Integer id);


    List<FindCommentsByIdOutput> find(SearchCriteria search, Pageable pageable) throws MalformedURLException;
	//Relationship Operations
	//Relationship Operations
    
    GetPostsOutput getPosts(Integer commentsid);
    
    GetUsersOutput getUsers(Integer commentsid);
    
    //Join Column Parsers
}

