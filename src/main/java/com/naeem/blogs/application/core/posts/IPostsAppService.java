package com.naeem.blogs.application.core.posts;

import org.springframework.data.domain.Pageable;
import com.naeem.blogs.application.core.posts.dto.*;
import com.naeem.blogs.commons.search.SearchCriteria;
import java.net.MalformedURLException;
import java.util.*;

public interface IPostsAppService {
	
	//CRUD Operations
	CreatePostsOutput create(CreatePostsInput posts);

    void delete(Integer id);

    UpdatePostsOutput update(Integer id, UpdatePostsInput input);

    FindPostsByIdOutput findById(Integer id);


    List<FindPostsByIdOutput> find(SearchCriteria search, Pageable pageable) throws MalformedURLException;
	//Relationship Operations
    
    GetUsersOutput getUsers(Integer postsid);
    
    //Join Column Parsers

	Map<String,String> parseCommentsJoinColumn(String keysString);

	Map<String,String> parseLikesJoinColumn(String keysString);

	Map<String,String> parsePostCategoriesJoinColumn(String keysString);

	Map<String,String> parsePostTagsJoinColumn(String keysString);
}

