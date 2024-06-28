package com.naeem.blogs.application.core.postcategories;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import com.naeem.blogs.application.core.postcategories.dto.*;
import com.naeem.blogs.domain.core.postcategories.IPostCategoriesRepository;
import com.naeem.blogs.domain.core.postcategories.QPostCategories;
import com.naeem.blogs.domain.core.postcategories.PostCategories;
import com.naeem.blogs.domain.core.postcategories.PostCategoriesId;
import com.naeem.blogs.domain.core.categories.ICategoriesRepository;
import com.naeem.blogs.domain.core.categories.Categories;
import com.naeem.blogs.domain.core.posts.IPostsRepository;
import com.naeem.blogs.domain.core.posts.Posts;


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

@Service("postCategoriesAppService")
@RequiredArgsConstructor
public class PostCategoriesAppService implements IPostCategoriesAppService {
    
	@Qualifier("postCategoriesRepository")
	@NonNull protected final IPostCategoriesRepository _postCategoriesRepository;

	
    @Qualifier("categoriesRepository")
	@NonNull protected final ICategoriesRepository _categoriesRepository;

    @Qualifier("postsRepository")
	@NonNull protected final IPostsRepository _postsRepository;

	@Qualifier("IPostCategoriesMapperImpl")
	@NonNull protected final IPostCategoriesMapper mapper;

	@NonNull protected final LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
	public CreatePostCategoriesOutput create(CreatePostCategoriesInput input) {

		PostCategories postCategories = mapper.createPostCategoriesInputToPostCategories(input);
		Categories foundCategories = null;
		Posts foundPosts = null;
	  	if(input.getCategoryId()!=null) {
			foundCategories = _categoriesRepository.findById(input.getCategoryId()).orElse(null);
			
			if(foundCategories!=null) {
				foundCategories.addPostCategories(postCategories);
			}
		}
	  	if(input.getPostId()!=null) {
			foundPosts = _postsRepository.findById(input.getPostId()).orElse(null);
			
			if(foundPosts!=null) {
				foundPosts.addPostCategories(postCategories);
			}
		}

		PostCategories createdPostCategories = _postCategoriesRepository.save(postCategories);
		return mapper.postCategoriesToCreatePostCategoriesOutput(createdPostCategories);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public UpdatePostCategoriesOutput update(PostCategoriesId postCategoriesId, UpdatePostCategoriesInput input) {

		PostCategories existing = _postCategoriesRepository.findById(postCategoriesId).orElseThrow(() -> new EntityNotFoundException("PostCategories not found"));

		PostCategories postCategories = mapper.updatePostCategoriesInputToPostCategories(input);
		Categories foundCategories = null;
		Posts foundPosts = null;
        
	  	if(input.getCategoryId()!=null) { 
			foundCategories = _categoriesRepository.findById(input.getCategoryId()).orElse(null);
		
			if(foundCategories!=null) {
				foundCategories.addPostCategories(postCategories);
			}
		}
        
	  	if(input.getPostId()!=null) { 
			foundPosts = _postsRepository.findById(input.getPostId()).orElse(null);
		
			if(foundPosts!=null) {
				foundPosts.addPostCategories(postCategories);
			}
		}
		
		PostCategories updatedPostCategories = _postCategoriesRepository.save(postCategories);
		return mapper.postCategoriesToUpdatePostCategoriesOutput(updatedPostCategories);
	}
	
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(PostCategoriesId postCategoriesId) {

		PostCategories existing = _postCategoriesRepository.findById(postCategoriesId).orElseThrow(() -> new EntityNotFoundException("PostCategories not found"));
	 	
        if(existing.getCategories() !=null)
        {
        existing.getCategories().removePostCategories(existing);
        }
        if(existing.getPosts() !=null)
        {
        existing.getPosts().removePostCategories(existing);
        }
        if(existing !=null) {
			_postCategoriesRepository.delete(existing);
		}
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public FindPostCategoriesByIdOutput findById(PostCategoriesId postCategoriesId) {

		PostCategories foundPostCategories = _postCategoriesRepository.findById(postCategoriesId).orElse(null);
		if (foundPostCategories == null)  
			return null; 
 	   
 	    return mapper.postCategoriesToFindPostCategoriesByIdOutput(foundPostCategories);
	}

    //Categories
	// ReST API Call - GET /postCategories/1/categories
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public GetCategoriesOutput getCategories(PostCategoriesId postCategoriesId) {

		PostCategories foundPostCategories = _postCategoriesRepository.findById(postCategoriesId).orElse(null);
		if (foundPostCategories == null) {
			logHelper.getLogger().error("There does not exist a postCategories wth a id='{}'", postCategoriesId);
			return null;
		}
		Categories re = foundPostCategories.getCategories();
		return mapper.categoriesToGetCategoriesOutput(re, foundPostCategories);
	}
	
    //Posts
	// ReST API Call - GET /postCategories/1/posts
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public GetPostsOutput getPosts(PostCategoriesId postCategoriesId) {

		PostCategories foundPostCategories = _postCategoriesRepository.findById(postCategoriesId).orElse(null);
		if (foundPostCategories == null) {
			logHelper.getLogger().error("There does not exist a postCategories wth a id='{}'", postCategoriesId);
			return null;
		}
		Posts re = foundPostCategories.getPosts();
		return mapper.postsToGetPostsOutput(re, foundPostCategories);
	}
	
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<FindPostCategoriesByIdOutput> find(SearchCriteria search, Pageable pageable) throws MalformedURLException  {

		Page<PostCategories> foundPostCategories = _postCategoriesRepository.findAll(search(search), pageable);
		List<PostCategories> postCategoriesList = foundPostCategories.getContent();
		Iterator<PostCategories> postCategoriesIterator = postCategoriesList.iterator(); 
		List<FindPostCategoriesByIdOutput> output = new ArrayList<>();

		while (postCategoriesIterator.hasNext()) {
		PostCategories postCategories = postCategoriesIterator.next();
 	    output.add(mapper.postCategoriesToFindPostCategoriesByIdOutput(postCategories));
		}
		return output;
	}
	
	protected BooleanBuilder search(SearchCriteria search) throws MalformedURLException {

		QPostCategories postCategories= QPostCategories.postCategoriesEntity;
		if(search != null) {
			Map<String,SearchFields> map = new HashMap<>();
			for(SearchFields fieldDetails: search.getFields())
			{
				map.put(fieldDetails.getFieldName(),fieldDetails);
			}
			List<String> keysList = new ArrayList<String>(map.keySet());
			checkProperties(keysList);
			return searchKeyValuePair(postCategories, map,search.getJoinColumns());
		}
		return null;
	}
	
	protected void checkProperties(List<String> list) throws MalformedURLException  {
		for (int i = 0; i < list.size(); i++) {
			if(!(
		        list.get(i).replace("%20","").trim().equals("categories") ||
		        list.get(i).replace("%20","").trim().equals("posts") ||
				list.get(i).replace("%20","").trim().equals("categoryId") ||
				list.get(i).replace("%20","").trim().equals("postId")
			)) 
			{
			 throw new MalformedURLException("Wrong URL Format: Property " + list.get(i) + " not found!" );
			}
		}
	}
	
	protected BooleanBuilder searchKeyValuePair(QPostCategories postCategories, Map<String,SearchFields> map,Map<String,String> joinColumns) {
		BooleanBuilder builder = new BooleanBuilder();
        
		Iterator<Map.Entry<String, SearchFields>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, SearchFields> details = iterator.next();

			if(details.getKey().replace("%20","").trim().equals("categoryId")) {
				if(details.getValue().getOperator().equals("contains") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(postCategories.categoryId.like(details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(postCategories.categoryId.eq(Integer.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("notEqual") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(postCategories.categoryId.ne(Integer.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("range")) {
				   if(StringUtils.isNumeric(details.getValue().getStartingValue()) && StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(postCategories.categoryId.between(Integer.valueOf(details.getValue().getStartingValue()), Integer.valueOf(details.getValue().getEndingValue())));
                   } else if(StringUtils.isNumeric(details.getValue().getStartingValue())) {
                	   builder.and(postCategories.categoryId.goe(Integer.valueOf(details.getValue().getStartingValue())));
                   } else if(StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(postCategories.categoryId.loe(Integer.valueOf(details.getValue().getEndingValue())));
				   }
				}
			}
			if(details.getKey().replace("%20","").trim().equals("postId")) {
				if(details.getValue().getOperator().equals("contains") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(postCategories.postId.like(details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(postCategories.postId.eq(Integer.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("notEqual") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(postCategories.postId.ne(Integer.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("range")) {
				   if(StringUtils.isNumeric(details.getValue().getStartingValue()) && StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(postCategories.postId.between(Integer.valueOf(details.getValue().getStartingValue()), Integer.valueOf(details.getValue().getEndingValue())));
                   } else if(StringUtils.isNumeric(details.getValue().getStartingValue())) {
                	   builder.and(postCategories.postId.goe(Integer.valueOf(details.getValue().getStartingValue())));
                   } else if(StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(postCategories.postId.loe(Integer.valueOf(details.getValue().getEndingValue())));
				   }
				}
			}
	    
		    if(details.getKey().replace("%20","").trim().equals("categories")) {
				if(details.getValue().getOperator().equals("contains")) {
					builder.and(postCategories.categories.name.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals")) {
					builder.and(postCategories.categories.name.eq(details.getValue().getSearchValue()));
				} else if(details.getValue().getOperator().equals("notEqual")) {
					builder.and(postCategories.categories.name.ne(details.getValue().getSearchValue()));
				}
			}
		     if(details.getKey().replace("%20","").trim().equals("posts")) {
				if(details.getValue().getOperator().equals("contains") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(postCategories.posts.postId.like(details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(postCategories.posts.postId.eq(Integer.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("notEqual") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(postCategories.posts.postId.ne(Integer.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("range")) {
				   if(StringUtils.isNumeric(details.getValue().getStartingValue()) && StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(postCategories.posts.postId.between(Integer.valueOf(details.getValue().getStartingValue()), Integer.valueOf(details.getValue().getEndingValue())));
                   } else if(StringUtils.isNumeric(details.getValue().getStartingValue())) {
                	   builder.and(postCategories.posts.postId.goe(Integer.valueOf(details.getValue().getStartingValue())));
                   } else if(StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(postCategories.posts.postId.loe(Integer.valueOf(details.getValue().getEndingValue())));
				   }
				}
			}
		}
		
		for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
		if(joinCol != null && joinCol.getKey().equals("categoryId")) {
		    builder.and(postCategories.categories.categoryId.eq(Integer.parseInt(joinCol.getValue())));
		}
        
		if(joinCol != null && joinCol.getKey().equals("categories")) {
		    builder.and(postCategories.categories.name.eq(joinCol.getValue()));
        }
        }
		for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
		if(joinCol != null && joinCol.getKey().equals("postId")) {
		    builder.and(postCategories.posts.postId.eq(Integer.parseInt(joinCol.getValue())));
		}
        
        }
		return builder;
	}
	
	public PostCategoriesId parsePostCategoriesKey(String keysString) {
		
		String[] keyEntries = keysString.split(",");
		PostCategoriesId postCategoriesId = new PostCategoriesId();
		
		Map<String,String> keyMap = new HashMap<String,String>();
		if(keyEntries.length > 1) {
			for(String keyEntry: keyEntries)
			{
				String[] keyEntryArr = keyEntry.split("=");
				if(keyEntryArr.length > 1) {
					keyMap.put(keyEntryArr[0], keyEntryArr[1]);					
				}
				else {
					return null;
				}
			}
		}
		else {
			String[] keyEntryArr = keysString.split("=");
			if(keyEntryArr.length > 1) {
				keyMap.put(keyEntryArr[0], keyEntryArr[1]);					
			}
			else {
				return null;
			}
		}
		
		postCategoriesId.setCategoryId(Integer.valueOf(keyMap.get("categoryId")));
		postCategoriesId.setPostId(Integer.valueOf(keyMap.get("postId")));
		return postCategoriesId;
		
	}	
    
    
}



