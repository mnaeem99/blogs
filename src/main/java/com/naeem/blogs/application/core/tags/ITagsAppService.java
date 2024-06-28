package com.naeem.blogs.application.core.tags;

import org.springframework.data.domain.Pageable;
import com.naeem.blogs.application.core.tags.dto.*;
import com.naeem.blogs.commons.search.SearchCriteria;
import java.net.MalformedURLException;
import java.util.*;

public interface ITagsAppService {
	
	//CRUD Operations
	CreateTagsOutput create(CreateTagsInput tags);

    void delete(Integer id);

    UpdateTagsOutput update(Integer id, UpdateTagsInput input);

    FindTagsByIdOutput findById(Integer id);


    List<FindTagsByIdOutput> find(SearchCriteria search, Pageable pageable) throws MalformedURLException;
    
    //Join Column Parsers

	Map<String,String> parsePostTagsJoinColumn(String keysString);
}

