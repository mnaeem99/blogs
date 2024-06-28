package com.naeem.blogs.application.core.postcategories;

import com.naeem.blogs.domain.core.postcategories.PostCategoriesId;
import org.springframework.data.domain.Pageable;
import com.naeem.blogs.application.core.postcategories.dto.*;
import com.naeem.blogs.commons.search.SearchCriteria;
import java.net.MalformedURLException;
import java.util.*;

public interface IPostCategoriesAppService {
	
	//CRUD Operations
	CreatePostCategoriesOutput create(CreatePostCategoriesInput postcategories);

    void delete(PostCategoriesId postCategoriesId);

    UpdatePostCategoriesOutput update(PostCategoriesId postCategoriesId, UpdatePostCategoriesInput input);

    FindPostCategoriesByIdOutput findById(PostCategoriesId postCategoriesId);


    List<FindPostCategoriesByIdOutput> find(SearchCriteria search, Pageable pageable) throws MalformedURLException;
	//Relationship Operations
	//Relationship Operations
    
    GetCategoriesOutput getCategories(PostCategoriesId postCategoriesId);
    
    GetPostsOutput getPosts(PostCategoriesId postCategoriesId);
    
    //Join Column Parsers
    
	PostCategoriesId parsePostCategoriesKey(String keysString);
}

