package com.naeem.blogs.application.core.posttags;

import com.naeem.blogs.domain.core.posttags.PostTagsId;
import org.springframework.data.domain.Pageable;
import com.naeem.blogs.application.core.posttags.dto.*;
import com.naeem.blogs.commons.search.SearchCriteria;
import java.net.MalformedURLException;
import java.util.*;

public interface IPostTagsAppService {
	
	//CRUD Operations
	CreatePostTagsOutput create(CreatePostTagsInput posttags);

    void delete(PostTagsId postTagsId);

    UpdatePostTagsOutput update(PostTagsId postTagsId, UpdatePostTagsInput input);

    FindPostTagsByIdOutput findById(PostTagsId postTagsId);


    List<FindPostTagsByIdOutput> find(SearchCriteria search, Pageable pageable) throws MalformedURLException;
	//Relationship Operations
	//Relationship Operations
    
    GetPostsOutput getPosts(PostTagsId postTagsId);
    
    GetTagsOutput getTags(PostTagsId postTagsId);
    
    //Join Column Parsers
    
	PostTagsId parsePostTagsKey(String keysString);
}

