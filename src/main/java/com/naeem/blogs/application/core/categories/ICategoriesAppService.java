package com.naeem.blogs.application.core.categories;

import org.springframework.data.domain.Pageable;
import com.naeem.blogs.application.core.categories.dto.*;
import com.naeem.blogs.commons.search.SearchCriteria;
import java.net.MalformedURLException;
import java.util.*;

public interface ICategoriesAppService {
	
	//CRUD Operations
	CreateCategoriesOutput create(CreateCategoriesInput categories);

    void delete(Integer id);

    UpdateCategoriesOutput update(Integer id, UpdateCategoriesInput input);

    FindCategoriesByIdOutput findById(Integer id);


    List<FindCategoriesByIdOutput> find(SearchCriteria search, Pageable pageable) throws MalformedURLException;
    
    //Join Column Parsers

	Map<String,String> parsePostCategoriesJoinColumn(String keysString);
}

