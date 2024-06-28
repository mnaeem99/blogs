package com.naeem.blogs.application.core.posttags;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import com.naeem.blogs.application.core.posttags.dto.*;
import com.naeem.blogs.domain.core.posttags.IPostTagsRepository;
import com.naeem.blogs.domain.core.posttags.QPostTags;
import com.naeem.blogs.domain.core.posttags.PostTags;
import com.naeem.blogs.domain.core.posttags.PostTagsId;
import com.naeem.blogs.domain.core.posts.IPostsRepository;
import com.naeem.blogs.domain.core.posts.Posts;
import com.naeem.blogs.domain.core.tags.ITagsRepository;
import com.naeem.blogs.domain.core.tags.Tags;


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

@Service("postTagsAppService")
@RequiredArgsConstructor
public class PostTagsAppService implements IPostTagsAppService {
    
	@Qualifier("postTagsRepository")
	@NonNull protected final IPostTagsRepository _postTagsRepository;

	
    @Qualifier("postsRepository")
	@NonNull protected final IPostsRepository _postsRepository;

    @Qualifier("tagsRepository")
	@NonNull protected final ITagsRepository _tagsRepository;

	@Qualifier("IPostTagsMapperImpl")
	@NonNull protected final IPostTagsMapper mapper;

	@NonNull protected final LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
	public CreatePostTagsOutput create(CreatePostTagsInput input) {

		PostTags postTags = mapper.createPostTagsInputToPostTags(input);
		Posts foundPosts = null;
		Tags foundTags = null;
	  	if(input.getPostId()!=null) {
			foundPosts = _postsRepository.findById(input.getPostId()).orElse(null);
			
			if(foundPosts!=null) {
				foundPosts.addPostTags(postTags);
			}
		}
	  	if(input.getTagId()!=null) {
			foundTags = _tagsRepository.findById(input.getTagId()).orElse(null);
			
			if(foundTags!=null) {
				foundTags.addPostTags(postTags);
			}
		}

		PostTags createdPostTags = _postTagsRepository.save(postTags);
		return mapper.postTagsToCreatePostTagsOutput(createdPostTags);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public UpdatePostTagsOutput update(PostTagsId postTagsId, UpdatePostTagsInput input) {

		PostTags existing = _postTagsRepository.findById(postTagsId).orElseThrow(() -> new EntityNotFoundException("PostTags not found"));

		PostTags postTags = mapper.updatePostTagsInputToPostTags(input);
		Posts foundPosts = null;
		Tags foundTags = null;
        
	  	if(input.getPostId()!=null) { 
			foundPosts = _postsRepository.findById(input.getPostId()).orElse(null);
		
			if(foundPosts!=null) {
				foundPosts.addPostTags(postTags);
			}
		}
        
	  	if(input.getTagId()!=null) { 
			foundTags = _tagsRepository.findById(input.getTagId()).orElse(null);
		
			if(foundTags!=null) {
				foundTags.addPostTags(postTags);
			}
		}
		
		PostTags updatedPostTags = _postTagsRepository.save(postTags);
		return mapper.postTagsToUpdatePostTagsOutput(updatedPostTags);
	}
	
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(PostTagsId postTagsId) {

		PostTags existing = _postTagsRepository.findById(postTagsId).orElseThrow(() -> new EntityNotFoundException("PostTags not found"));
	 	
        if(existing.getPosts() !=null)
        {
        existing.getPosts().removePostTags(existing);
        }
        if(existing.getTags() !=null)
        {
        existing.getTags().removePostTags(existing);
        }
        if(existing !=null) {
			_postTagsRepository.delete(existing);
		}
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public FindPostTagsByIdOutput findById(PostTagsId postTagsId) {

		PostTags foundPostTags = _postTagsRepository.findById(postTagsId).orElse(null);
		if (foundPostTags == null)  
			return null; 
 	   
 	    return mapper.postTagsToFindPostTagsByIdOutput(foundPostTags);
	}

    //Posts
	// ReST API Call - GET /postTags/1/posts
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public GetPostsOutput getPosts(PostTagsId postTagsId) {

		PostTags foundPostTags = _postTagsRepository.findById(postTagsId).orElse(null);
		if (foundPostTags == null) {
			logHelper.getLogger().error("There does not exist a postTags wth a id='{}'", postTagsId);
			return null;
		}
		Posts re = foundPostTags.getPosts();
		return mapper.postsToGetPostsOutput(re, foundPostTags);
	}
	
    //Tags
	// ReST API Call - GET /postTags/1/tags
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public GetTagsOutput getTags(PostTagsId postTagsId) {

		PostTags foundPostTags = _postTagsRepository.findById(postTagsId).orElse(null);
		if (foundPostTags == null) {
			logHelper.getLogger().error("There does not exist a postTags wth a id='{}'", postTagsId);
			return null;
		}
		Tags re = foundPostTags.getTags();
		return mapper.tagsToGetTagsOutput(re, foundPostTags);
	}
	
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<FindPostTagsByIdOutput> find(SearchCriteria search, Pageable pageable) throws MalformedURLException  {

		Page<PostTags> foundPostTags = _postTagsRepository.findAll(search(search), pageable);
		List<PostTags> postTagsList = foundPostTags.getContent();
		Iterator<PostTags> postTagsIterator = postTagsList.iterator(); 
		List<FindPostTagsByIdOutput> output = new ArrayList<>();

		while (postTagsIterator.hasNext()) {
		PostTags postTags = postTagsIterator.next();
 	    output.add(mapper.postTagsToFindPostTagsByIdOutput(postTags));
		}
		return output;
	}
	
	protected BooleanBuilder search(SearchCriteria search) throws MalformedURLException {

		QPostTags postTags= QPostTags.postTagsEntity;
		if(search != null) {
			Map<String,SearchFields> map = new HashMap<>();
			for(SearchFields fieldDetails: search.getFields())
			{
				map.put(fieldDetails.getFieldName(),fieldDetails);
			}
			List<String> keysList = new ArrayList<String>(map.keySet());
			checkProperties(keysList);
			return searchKeyValuePair(postTags, map,search.getJoinColumns());
		}
		return null;
	}
	
	protected void checkProperties(List<String> list) throws MalformedURLException  {
		for (int i = 0; i < list.size(); i++) {
			if(!(
		        list.get(i).replace("%20","").trim().equals("posts") ||
		        list.get(i).replace("%20","").trim().equals("tags") ||
				list.get(i).replace("%20","").trim().equals("postId") ||
				list.get(i).replace("%20","").trim().equals("tagId")
			)) 
			{
			 throw new MalformedURLException("Wrong URL Format: Property " + list.get(i) + " not found!" );
			}
		}
	}
	
	protected BooleanBuilder searchKeyValuePair(QPostTags postTags, Map<String,SearchFields> map,Map<String,String> joinColumns) {
		BooleanBuilder builder = new BooleanBuilder();
        
		Iterator<Map.Entry<String, SearchFields>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, SearchFields> details = iterator.next();

			if(details.getKey().replace("%20","").trim().equals("postId")) {
				if(details.getValue().getOperator().equals("contains") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(postTags.postId.like(details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(postTags.postId.eq(Integer.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("notEqual") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(postTags.postId.ne(Integer.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("range")) {
				   if(StringUtils.isNumeric(details.getValue().getStartingValue()) && StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(postTags.postId.between(Integer.valueOf(details.getValue().getStartingValue()), Integer.valueOf(details.getValue().getEndingValue())));
                   } else if(StringUtils.isNumeric(details.getValue().getStartingValue())) {
                	   builder.and(postTags.postId.goe(Integer.valueOf(details.getValue().getStartingValue())));
                   } else if(StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(postTags.postId.loe(Integer.valueOf(details.getValue().getEndingValue())));
				   }
				}
			}
			if(details.getKey().replace("%20","").trim().equals("tagId")) {
				if(details.getValue().getOperator().equals("contains") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(postTags.tagId.like(details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(postTags.tagId.eq(Integer.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("notEqual") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(postTags.tagId.ne(Integer.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("range")) {
				   if(StringUtils.isNumeric(details.getValue().getStartingValue()) && StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(postTags.tagId.between(Integer.valueOf(details.getValue().getStartingValue()), Integer.valueOf(details.getValue().getEndingValue())));
                   } else if(StringUtils.isNumeric(details.getValue().getStartingValue())) {
                	   builder.and(postTags.tagId.goe(Integer.valueOf(details.getValue().getStartingValue())));
                   } else if(StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(postTags.tagId.loe(Integer.valueOf(details.getValue().getEndingValue())));
				   }
				}
			}
	    
		     if(details.getKey().replace("%20","").trim().equals("posts")) {
				if(details.getValue().getOperator().equals("contains") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(postTags.posts.postId.like(details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(postTags.posts.postId.eq(Integer.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("notEqual") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(postTags.posts.postId.ne(Integer.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("range")) {
				   if(StringUtils.isNumeric(details.getValue().getStartingValue()) && StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(postTags.posts.postId.between(Integer.valueOf(details.getValue().getStartingValue()), Integer.valueOf(details.getValue().getEndingValue())));
                   } else if(StringUtils.isNumeric(details.getValue().getStartingValue())) {
                	   builder.and(postTags.posts.postId.goe(Integer.valueOf(details.getValue().getStartingValue())));
                   } else if(StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(postTags.posts.postId.loe(Integer.valueOf(details.getValue().getEndingValue())));
				   }
				}
			}
		    if(details.getKey().replace("%20","").trim().equals("tags")) {
				if(details.getValue().getOperator().equals("contains")) {
					builder.and(postTags.tags.name.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals")) {
					builder.and(postTags.tags.name.eq(details.getValue().getSearchValue()));
				} else if(details.getValue().getOperator().equals("notEqual")) {
					builder.and(postTags.tags.name.ne(details.getValue().getSearchValue()));
				}
			}
		}
		
		for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
		if(joinCol != null && joinCol.getKey().equals("postId")) {
		    builder.and(postTags.posts.postId.eq(Integer.parseInt(joinCol.getValue())));
		}
        
        }
		for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
		if(joinCol != null && joinCol.getKey().equals("tagId")) {
		    builder.and(postTags.tags.tagId.eq(Integer.parseInt(joinCol.getValue())));
		}
        
		if(joinCol != null && joinCol.getKey().equals("tags")) {
		    builder.and(postTags.tags.name.eq(joinCol.getValue()));
        }
        }
		return builder;
	}
	
	public PostTagsId parsePostTagsKey(String keysString) {
		
		String[] keyEntries = keysString.split(",");
		PostTagsId postTagsId = new PostTagsId();
		
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
		
		postTagsId.setPostId(Integer.valueOf(keyMap.get("postId")));
		postTagsId.setTagId(Integer.valueOf(keyMap.get("tagId")));
		return postTagsId;
		
	}	
    
    
}



