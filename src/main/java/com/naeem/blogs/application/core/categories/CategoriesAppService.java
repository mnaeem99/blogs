package com.naeem.blogs.application.core.categories;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import com.naeem.blogs.application.core.categories.dto.*;
import com.naeem.blogs.domain.core.categories.ICategoriesRepository;
import com.naeem.blogs.domain.core.categories.QCategories;
import com.naeem.blogs.domain.core.categories.Categories;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page; 
import org.springframework.data.domain.Pageable; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;
import com.naeem.blogs.commons.search.*;
import com.naeem.blogs.commons.logging.LoggingHelper;
import com.querydsl.core.BooleanBuilder;
import java.net.MalformedURLException;
import java.time.*;
import java.util.*;
import javax.persistence.EntityNotFoundException;

@Service("categoriesAppService")
@RequiredArgsConstructor
public class CategoriesAppService implements ICategoriesAppService {
    
	@Qualifier("categoriesRepository")
	@NonNull protected final ICategoriesRepository _categoriesRepository;

	
	@Qualifier("ICategoriesMapperImpl")
	@NonNull protected final ICategoriesMapper mapper;

	@NonNull protected final LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
	public CreateCategoriesOutput create(CreateCategoriesInput input) {

		Categories categories = mapper.createCategoriesInputToCategories(input);

		Categories createdCategories = _categoriesRepository.save(categories);
		return mapper.categoriesToCreateCategoriesOutput(createdCategories);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public UpdateCategoriesOutput update(Integer categoriesId, UpdateCategoriesInput input) {

		Categories existing = _categoriesRepository.findById(categoriesId).orElseThrow(() -> new EntityNotFoundException("Categories not found"));

		Categories categories = mapper.updateCategoriesInputToCategories(input);
		categories.setPostCategoriesSet(existing.getPostCategoriesSet());
		
		Categories updatedCategories = _categoriesRepository.save(categories);
		return mapper.categoriesToUpdateCategoriesOutput(updatedCategories);
	}
	
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Integer categoriesId) {

		Categories existing = _categoriesRepository.findById(categoriesId).orElseThrow(() -> new EntityNotFoundException("Categories not found"));
	 	
        if(existing !=null) {
			_categoriesRepository.delete(existing);
		}
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public FindCategoriesByIdOutput findById(Integer categoriesId) {

		Categories foundCategories = _categoriesRepository.findById(categoriesId).orElse(null);
		if (foundCategories == null)  
			return null; 
 	   
 	    return mapper.categoriesToFindCategoriesByIdOutput(foundCategories);
	}

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<FindCategoriesByIdOutput> find(SearchCriteria search, Pageable pageable) throws MalformedURLException  {

		Page<Categories> foundCategories = _categoriesRepository.findAll(search(search), pageable);
		List<Categories> categoriesList = foundCategories.getContent();
		Iterator<Categories> categoriesIterator = categoriesList.iterator(); 
		List<FindCategoriesByIdOutput> output = new ArrayList<>();

		while (categoriesIterator.hasNext()) {
		Categories categories = categoriesIterator.next();
 	    output.add(mapper.categoriesToFindCategoriesByIdOutput(categories));
		}
		return output;
	}
	
	protected BooleanBuilder search(SearchCriteria search) throws MalformedURLException {

		QCategories categories= QCategories.categoriesEntity;
		if(search != null) {
			Map<String,SearchFields> map = new HashMap<>();
			for(SearchFields fieldDetails: search.getFields())
			{
				map.put(fieldDetails.getFieldName(),fieldDetails);
			}
			List<String> keysList = new ArrayList<String>(map.keySet());
			checkProperties(keysList);
			return searchKeyValuePair(categories, map,search.getJoinColumns());
		}
		return null;
	}
	
	protected void checkProperties(List<String> list) throws MalformedURLException  {
		for (int i = 0; i < list.size(); i++) {
			if(!(
				list.get(i).replace("%20","").trim().equals("categoryId") ||
				list.get(i).replace("%20","").trim().equals("description") ||
				list.get(i).replace("%20","").trim().equals("name")
			)) 
			{
			 throw new MalformedURLException("Wrong URL Format: Property " + list.get(i) + " not found!" );
			}
		}
	}
	
	protected BooleanBuilder searchKeyValuePair(QCategories categories, Map<String,SearchFields> map,Map<String,String> joinColumns) {
		BooleanBuilder builder = new BooleanBuilder();
        
		Iterator<Map.Entry<String, SearchFields>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, SearchFields> details = iterator.next();

			if(details.getKey().replace("%20","").trim().equals("categoryId")) {
				if(details.getValue().getOperator().equals("contains") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(categories.categoryId.like(details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(categories.categoryId.eq(Integer.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("notEqual") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(categories.categoryId.ne(Integer.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("range")) {
				   if(StringUtils.isNumeric(details.getValue().getStartingValue()) && StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(categories.categoryId.between(Integer.valueOf(details.getValue().getStartingValue()), Integer.valueOf(details.getValue().getEndingValue())));
                   } else if(StringUtils.isNumeric(details.getValue().getStartingValue())) {
                	   builder.and(categories.categoryId.goe(Integer.valueOf(details.getValue().getStartingValue())));
                   } else if(StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(categories.categoryId.loe(Integer.valueOf(details.getValue().getEndingValue())));
				   }
				}
			}
            if(details.getKey().replace("%20","").trim().equals("description")) {
				if(details.getValue().getOperator().equals("contains")) {
					builder.and(categories.description.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals")) {
					builder.and(categories.description.eq(details.getValue().getSearchValue()));
				} else if(details.getValue().getOperator().equals("notEqual")) {
					builder.and(categories.description.ne(details.getValue().getSearchValue()));
				}
			}
            if(details.getKey().replace("%20","").trim().equals("name")) {
				if(details.getValue().getOperator().equals("contains")) {
					builder.and(categories.name.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals")) {
					builder.and(categories.name.eq(details.getValue().getSearchValue()));
				} else if(details.getValue().getOperator().equals("notEqual")) {
					builder.and(categories.name.ne(details.getValue().getSearchValue()));
				}
			}
	    
		}
		
		return builder;
	}
	
	public Map<String,String> parsePostCategoriesJoinColumn(String keysString) {
		
		Map<String,String> joinColumnMap = new HashMap<String,String>();
		joinColumnMap.put("categoryId", keysString);
		  
		return joinColumnMap;
	}
    
}



