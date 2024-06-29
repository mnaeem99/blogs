package com.naeem.blogs.application.core.posts;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import com.naeem.blogs.application.core.posts.dto.*;
import com.naeem.blogs.domain.core.posts.IPostsRepository;
import com.naeem.blogs.domain.core.posts.QPosts;
import com.naeem.blogs.domain.core.posts.Posts;
import com.naeem.blogs.domain.core.authorization.users.IUsersRepository;
import com.naeem.blogs.domain.core.authorization.users.Users;


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

@Service("postsAppService")
@RequiredArgsConstructor
public class PostsAppService implements IPostsAppService {
    
	@Qualifier("postsRepository")
	@NonNull protected final IPostsRepository _postsRepository;

	
    @Qualifier("usersRepository")
	@NonNull protected final IUsersRepository _usersRepository;

	@Qualifier("IPostsMapperImpl")
	@NonNull protected final IPostsMapper mapper;

	@NonNull protected final LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
	public CreatePostsOutput create(CreatePostsInput input) {

		Posts posts = mapper.createPostsInputToPosts(input);
		Users foundUsers = null;
	  	if(input.getAuthorId()!=null) {
		    foundUsers = _usersRepository.findById(Long.parseLong(input.getAuthorId().toString())).orElse(null);
			
			if(foundUsers!=null) {
				foundUsers.addPosts(posts);
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}

		Posts createdPosts = _postsRepository.save(posts);
		return mapper.postsToCreatePostsOutput(createdPosts);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public UpdatePostsOutput update(Integer postsId, UpdatePostsInput input) {

		Posts existing = _postsRepository.findById(postsId).orElseThrow(() -> new EntityNotFoundException("Posts not found"));

		Posts posts = mapper.updatePostsInputToPosts(input);
		posts.setCommentsSet(existing.getCommentsSet());
		posts.setLikesSet(existing.getLikesSet());
		posts.setPostCategoriesSet(existing.getPostCategoriesSet());
		posts.setPostTagsSet(existing.getPostTagsSet());
		Users foundUsers = null;
        
	  	if(input.getAuthorId()!=null) { 
		    foundUsers = _usersRepository.findById(Long.parseLong(input.getAuthorId().toString())).orElse(null);
		
			if(foundUsers!=null) {
				foundUsers.addPosts(posts);
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
		
		Posts updatedPosts = _postsRepository.save(posts);
		return mapper.postsToUpdatePostsOutput(updatedPosts);
	}
	
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Integer postsId) {

		Posts existing = _postsRepository.findById(postsId).orElseThrow(() -> new EntityNotFoundException("Posts not found"));
	 	
        if(existing.getUsers() !=null)
        {
        existing.getUsers().removePosts(existing);
        }
        if(existing !=null) {
			_postsRepository.delete(existing);
		}
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public FindPostsByIdOutput findById(Integer postsId) {

		Posts foundPosts = _postsRepository.findById(postsId).orElse(null);
		if (foundPosts == null)  
			return null; 
 	   
 	    return mapper.postsToFindPostsByIdOutput(foundPosts);
	}

    //Users
	// ReST API Call - GET /posts/1/users
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public GetUsersOutput getUsers(Integer postsId) {

		Posts foundPosts = _postsRepository.findById(postsId).orElse(null);
		if (foundPosts == null) {
			logHelper.getLogger().error("There does not exist a posts wth a id='{}'", postsId);
			return null;
		}
		Users re = foundPosts.getUsers();
		return mapper.usersToGetUsersOutput(re, foundPosts);
	}
	
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<FindPostsByIdOutput> find(SearchCriteria search, Pageable pageable) throws MalformedURLException  {

		Page<Posts> foundPosts = _postsRepository.findAll(search(search), pageable);
		List<Posts> postsList = foundPosts.getContent();
		Iterator<Posts> postsIterator = postsList.iterator(); 
		List<FindPostsByIdOutput> output = new ArrayList<>();

		while (postsIterator.hasNext()) {
		Posts posts = postsIterator.next();
 	    output.add(mapper.postsToFindPostsByIdOutput(posts));
		}
		return output;
	}
	
	protected BooleanBuilder search(SearchCriteria search) throws MalformedURLException {

		QPosts posts= QPosts.postsEntity;
		if(search != null) {
			Map<String,SearchFields> map = new HashMap<>();
			for(SearchFields fieldDetails: search.getFields())
			{
				map.put(fieldDetails.getFieldName(),fieldDetails);
			}
			List<String> keysList = new ArrayList<String>(map.keySet());
			checkProperties(keysList);
			return searchKeyValuePair(posts, map,search.getJoinColumns());
		}
		return null;
	}
	
	protected void checkProperties(List<String> list) throws MalformedURLException  {
		for (int i = 0; i < list.size(); i++) {
			if(!(
		        list.get(i).replace("%20","").trim().equals("users") ||
				list.get(i).replace("%20","").trim().equals("authorId") ||
				list.get(i).replace("%20","").trim().equals("content") ||
				list.get(i).replace("%20","").trim().equals("createdAt") ||
				list.get(i).replace("%20","").trim().equals("postId") ||
				list.get(i).replace("%20","").trim().equals("title") ||
				list.get(i).replace("%20","").trim().equals("updatedAt")
			)) 
			{
			 throw new MalformedURLException("Wrong URL Format: Property " + list.get(i) + " not found!" );
			}
		}
	}
	
	protected BooleanBuilder searchKeyValuePair(QPosts posts, Map<String,SearchFields> map,Map<String,String> joinColumns) {
		BooleanBuilder builder = new BooleanBuilder();
        
		Iterator<Map.Entry<String, SearchFields>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, SearchFields> details = iterator.next();

            if(details.getKey().replace("%20","").trim().equals("content")) {
				if(details.getValue().getOperator().equals("contains")) {
					builder.and(posts.content.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals")) {
					builder.and(posts.content.eq(details.getValue().getSearchValue()));
				} else if(details.getValue().getOperator().equals("notEqual")) {
					builder.and(posts.content.ne(details.getValue().getSearchValue()));
				}
			}
			if(details.getKey().replace("%20","").trim().equals("createdAt")) {
				if(details.getValue().getOperator().equals("equals") && SearchUtils.stringToLocalDateTime(details.getValue().getSearchValue()) !=null) {
					builder.and(posts.createdAt.eq(SearchUtils.stringToLocalDateTime(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("notEqual") && SearchUtils.stringToLocalDateTime(details.getValue().getSearchValue()) !=null) {
					builder.and(posts.createdAt.ne(SearchUtils.stringToLocalDateTime(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("range")) {
				   LocalDateTime startLocalDateTime= SearchUtils.stringToLocalDateTime(details.getValue().getStartingValue());
				   LocalDateTime endLocalDateTime= SearchUtils.stringToLocalDateTime(details.getValue().getEndingValue());
				   if(startLocalDateTime!=null && endLocalDateTime!=null) {
					   builder.and(posts.createdAt.between(startLocalDateTime,endLocalDateTime));
				   } else if(endLocalDateTime!=null) {
					   builder.and(posts.createdAt.loe(endLocalDateTime));
                   } else if(startLocalDateTime!=null) {
                	   builder.and(posts.createdAt.goe(startLocalDateTime));  
                   }
                }     
			}
			if(details.getKey().replace("%20","").trim().equals("postId")) {
				if(details.getValue().getOperator().equals("contains") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(posts.postId.like(details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(posts.postId.eq(Integer.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("notEqual") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(posts.postId.ne(Integer.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("range")) {
				   if(StringUtils.isNumeric(details.getValue().getStartingValue()) && StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(posts.postId.between(Integer.valueOf(details.getValue().getStartingValue()), Integer.valueOf(details.getValue().getEndingValue())));
                   } else if(StringUtils.isNumeric(details.getValue().getStartingValue())) {
                	   builder.and(posts.postId.goe(Integer.valueOf(details.getValue().getStartingValue())));
                   } else if(StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(posts.postId.loe(Integer.valueOf(details.getValue().getEndingValue())));
				   }
				}
			}
            if(details.getKey().replace("%20","").trim().equals("title")) {
				if(details.getValue().getOperator().equals("contains")) {
					builder.and(posts.title.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals")) {
					builder.and(posts.title.eq(details.getValue().getSearchValue()));
				} else if(details.getValue().getOperator().equals("notEqual")) {
					builder.and(posts.title.ne(details.getValue().getSearchValue()));
				}
			}
			if(details.getKey().replace("%20","").trim().equals("updatedAt")) {
				if(details.getValue().getOperator().equals("equals") && SearchUtils.stringToLocalDateTime(details.getValue().getSearchValue()) !=null) {
					builder.and(posts.updatedAt.eq(SearchUtils.stringToLocalDateTime(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("notEqual") && SearchUtils.stringToLocalDateTime(details.getValue().getSearchValue()) !=null) {
					builder.and(posts.updatedAt.ne(SearchUtils.stringToLocalDateTime(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("range")) {
				   LocalDateTime startLocalDateTime= SearchUtils.stringToLocalDateTime(details.getValue().getStartingValue());
				   LocalDateTime endLocalDateTime= SearchUtils.stringToLocalDateTime(details.getValue().getEndingValue());
				   if(startLocalDateTime!=null && endLocalDateTime!=null) {
					   builder.and(posts.updatedAt.between(startLocalDateTime,endLocalDateTime));
				   } else if(endLocalDateTime!=null) {
					   builder.and(posts.updatedAt.loe(endLocalDateTime));
                   } else if(startLocalDateTime!=null) {
                	   builder.and(posts.updatedAt.goe(startLocalDateTime));  
                   }
                }     
			}
	    
		    if(details.getKey().replace("%20","").trim().equals("users")) {
				if(details.getValue().getOperator().equals("contains")) {
					builder.and(posts.users.username.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals")) {
					builder.and(posts.users.username.eq(details.getValue().getSearchValue()));
				} else if(details.getValue().getOperator().equals("notEqual")) {
					builder.and(posts.users.username.ne(details.getValue().getSearchValue()));
				}
			}
		}
		
		for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
        if(joinCol != null && joinCol.getKey().equals("authorId")) {
		    builder.and(posts.users.userId.eq(Long.parseLong(joinCol.getValue())));
        }
        
		if(joinCol != null && joinCol.getKey().equals("users")) {
		    builder.and(posts.users.username.eq(joinCol.getValue()));
        }
        }
		return builder;
	}
	
	public Map<String,String> parseCommentsJoinColumn(String keysString) {
		
		Map<String,String> joinColumnMap = new HashMap<String,String>();
		joinColumnMap.put("postId", keysString);
		  
		return joinColumnMap;
	}
    
	public Map<String,String> parseLikesJoinColumn(String keysString) {
		
		Map<String,String> joinColumnMap = new HashMap<String,String>();
		joinColumnMap.put("postId", keysString);
		  
		return joinColumnMap;
	}
    
	public Map<String,String> parsePostCategoriesJoinColumn(String keysString) {
		
		Map<String,String> joinColumnMap = new HashMap<String,String>();
		joinColumnMap.put("postId", keysString);
		  
		return joinColumnMap;
	}
    
	public Map<String,String> parsePostTagsJoinColumn(String keysString) {
		
		Map<String,String> joinColumnMap = new HashMap<String,String>();
		joinColumnMap.put("postId", keysString);
		  
		return joinColumnMap;
	}
    
    
}



