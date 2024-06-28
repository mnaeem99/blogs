package com.naeem.blogs.application.core.likes;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import com.naeem.blogs.application.core.likes.dto.*;
import com.naeem.blogs.domain.core.likes.ILikesRepository;
import com.naeem.blogs.domain.core.likes.QLikes;
import com.naeem.blogs.domain.core.likes.Likes;
import com.naeem.blogs.domain.core.posts.IPostsRepository;
import com.naeem.blogs.domain.core.posts.Posts;
import com.naeem.blogs.domain.core.users.IUsersRepository;
import com.naeem.blogs.domain.core.users.Users;


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

@Service("likesAppService")
@RequiredArgsConstructor
public class LikesAppService implements ILikesAppService {
    
	@Qualifier("likesRepository")
	@NonNull protected final ILikesRepository _likesRepository;

	
    @Qualifier("postsRepository")
	@NonNull protected final IPostsRepository _postsRepository;

    @Qualifier("usersRepository")
	@NonNull protected final IUsersRepository _usersRepository;

	@Qualifier("ILikesMapperImpl")
	@NonNull protected final ILikesMapper mapper;

	@NonNull protected final LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
	public CreateLikesOutput create(CreateLikesInput input) {

		Likes likes = mapper.createLikesInputToLikes(input);
		Posts foundPosts = null;
		Users foundUsers = null;
	  	if(input.getPostId()!=null) {
			foundPosts = _postsRepository.findById(input.getPostId()).orElse(null);
			
			if(foundPosts!=null) {
				foundPosts.addLikes(likes);
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
	  	if(input.getUserId()!=null) {
			foundUsers = _usersRepository.findById(input.getUserId()).orElse(null);
			
			if(foundUsers!=null) {
				foundUsers.addLikes(likes);
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}

		Likes createdLikes = _likesRepository.save(likes);
		return mapper.likesToCreateLikesOutput(createdLikes);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public UpdateLikesOutput update(Integer likesId, UpdateLikesInput input) {

		Likes existing = _likesRepository.findById(likesId).orElseThrow(() -> new EntityNotFoundException("Likes not found"));

		Likes likes = mapper.updateLikesInputToLikes(input);
		Posts foundPosts = null;
		Users foundUsers = null;
        
	  	if(input.getPostId()!=null) { 
			foundPosts = _postsRepository.findById(input.getPostId()).orElse(null);
		
			if(foundPosts!=null) {
				foundPosts.addLikes(likes);
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
        
	  	if(input.getUserId()!=null) { 
			foundUsers = _usersRepository.findById(input.getUserId()).orElse(null);
		
			if(foundUsers!=null) {
				foundUsers.addLikes(likes);
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
		
		Likes updatedLikes = _likesRepository.save(likes);
		return mapper.likesToUpdateLikesOutput(updatedLikes);
	}
	
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Integer likesId) {

		Likes existing = _likesRepository.findById(likesId).orElseThrow(() -> new EntityNotFoundException("Likes not found"));
	 	
        if(existing.getPosts() !=null)
        {
        existing.getPosts().removeLikes(existing);
        }
        if(existing.getUsers() !=null)
        {
        existing.getUsers().removeLikes(existing);
        }
        if(existing !=null) {
			_likesRepository.delete(existing);
		}
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public FindLikesByIdOutput findById(Integer likesId) {

		Likes foundLikes = _likesRepository.findById(likesId).orElse(null);
		if (foundLikes == null)  
			return null; 
 	   
 	    return mapper.likesToFindLikesByIdOutput(foundLikes);
	}

    //Posts
	// ReST API Call - GET /likes/1/posts
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public GetPostsOutput getPosts(Integer likesId) {

		Likes foundLikes = _likesRepository.findById(likesId).orElse(null);
		if (foundLikes == null) {
			logHelper.getLogger().error("There does not exist a likes wth a id='{}'", likesId);
			return null;
		}
		Posts re = foundLikes.getPosts();
		return mapper.postsToGetPostsOutput(re, foundLikes);
	}
	
    //Users
	// ReST API Call - GET /likes/1/users
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public GetUsersOutput getUsers(Integer likesId) {

		Likes foundLikes = _likesRepository.findById(likesId).orElse(null);
		if (foundLikes == null) {
			logHelper.getLogger().error("There does not exist a likes wth a id='{}'", likesId);
			return null;
		}
		Users re = foundLikes.getUsers();
		return mapper.usersToGetUsersOutput(re, foundLikes);
	}
	
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<FindLikesByIdOutput> find(SearchCriteria search, Pageable pageable) throws MalformedURLException  {

		Page<Likes> foundLikes = _likesRepository.findAll(search(search), pageable);
		List<Likes> likesList = foundLikes.getContent();
		Iterator<Likes> likesIterator = likesList.iterator(); 
		List<FindLikesByIdOutput> output = new ArrayList<>();

		while (likesIterator.hasNext()) {
		Likes likes = likesIterator.next();
 	    output.add(mapper.likesToFindLikesByIdOutput(likes));
		}
		return output;
	}
	
	protected BooleanBuilder search(SearchCriteria search) throws MalformedURLException {

		QLikes likes= QLikes.likesEntity;
		if(search != null) {
			Map<String,SearchFields> map = new HashMap<>();
			for(SearchFields fieldDetails: search.getFields())
			{
				map.put(fieldDetails.getFieldName(),fieldDetails);
			}
			List<String> keysList = new ArrayList<String>(map.keySet());
			checkProperties(keysList);
			return searchKeyValuePair(likes, map,search.getJoinColumns());
		}
		return null;
	}
	
	protected void checkProperties(List<String> list) throws MalformedURLException  {
		for (int i = 0; i < list.size(); i++) {
			if(!(
		        list.get(i).replace("%20","").trim().equals("posts") ||
				list.get(i).replace("%20","").trim().equals("postId") ||
		        list.get(i).replace("%20","").trim().equals("users") ||
				list.get(i).replace("%20","").trim().equals("userId") ||
				list.get(i).replace("%20","").trim().equals("createdAt") ||
				list.get(i).replace("%20","").trim().equals("likeId")
			)) 
			{
			 throw new MalformedURLException("Wrong URL Format: Property " + list.get(i) + " not found!" );
			}
		}
	}
	
	protected BooleanBuilder searchKeyValuePair(QLikes likes, Map<String,SearchFields> map,Map<String,String> joinColumns) {
		BooleanBuilder builder = new BooleanBuilder();
        
		Iterator<Map.Entry<String, SearchFields>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, SearchFields> details = iterator.next();

			if(details.getKey().replace("%20","").trim().equals("createdAt")) {
				if(details.getValue().getOperator().equals("equals") && SearchUtils.stringToLocalDateTime(details.getValue().getSearchValue()) !=null) {
					builder.and(likes.createdAt.eq(SearchUtils.stringToLocalDateTime(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("notEqual") && SearchUtils.stringToLocalDateTime(details.getValue().getSearchValue()) !=null) {
					builder.and(likes.createdAt.ne(SearchUtils.stringToLocalDateTime(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("range")) {
				   LocalDateTime startLocalDateTime= SearchUtils.stringToLocalDateTime(details.getValue().getStartingValue());
				   LocalDateTime endLocalDateTime= SearchUtils.stringToLocalDateTime(details.getValue().getEndingValue());
				   if(startLocalDateTime!=null && endLocalDateTime!=null) {
					   builder.and(likes.createdAt.between(startLocalDateTime,endLocalDateTime));
				   } else if(endLocalDateTime!=null) {
					   builder.and(likes.createdAt.loe(endLocalDateTime));
                   } else if(startLocalDateTime!=null) {
                	   builder.and(likes.createdAt.goe(startLocalDateTime));  
                   }
                }     
			}
			if(details.getKey().replace("%20","").trim().equals("likeId")) {
				if(details.getValue().getOperator().equals("contains") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(likes.likeId.like(details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(likes.likeId.eq(Integer.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("notEqual") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(likes.likeId.ne(Integer.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("range")) {
				   if(StringUtils.isNumeric(details.getValue().getStartingValue()) && StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(likes.likeId.between(Integer.valueOf(details.getValue().getStartingValue()), Integer.valueOf(details.getValue().getEndingValue())));
                   } else if(StringUtils.isNumeric(details.getValue().getStartingValue())) {
                	   builder.and(likes.likeId.goe(Integer.valueOf(details.getValue().getStartingValue())));
                   } else if(StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(likes.likeId.loe(Integer.valueOf(details.getValue().getEndingValue())));
				   }
				}
			}
	    
		     if(details.getKey().replace("%20","").trim().equals("posts")) {
				if(details.getValue().getOperator().equals("contains") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(likes.posts.postId.like(details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(likes.posts.postId.eq(Integer.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("notEqual") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(likes.posts.postId.ne(Integer.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("range")) {
				   if(StringUtils.isNumeric(details.getValue().getStartingValue()) && StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(likes.posts.postId.between(Integer.valueOf(details.getValue().getStartingValue()), Integer.valueOf(details.getValue().getEndingValue())));
                   } else if(StringUtils.isNumeric(details.getValue().getStartingValue())) {
                	   builder.and(likes.posts.postId.goe(Integer.valueOf(details.getValue().getStartingValue())));
                   } else if(StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(likes.posts.postId.loe(Integer.valueOf(details.getValue().getEndingValue())));
				   }
				}
			}
		    if(details.getKey().replace("%20","").trim().equals("users")) {
				if(details.getValue().getOperator().equals("contains")) {
					builder.and(likes.users.email.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals")) {
					builder.and(likes.users.email.eq(details.getValue().getSearchValue()));
				} else if(details.getValue().getOperator().equals("notEqual")) {
					builder.and(likes.users.email.ne(details.getValue().getSearchValue()));
				}
			}
		}
		
		for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
		if(joinCol != null && joinCol.getKey().equals("postId")) {
		    builder.and(likes.posts.postId.eq(Integer.parseInt(joinCol.getValue())));
		}
        
        }
		for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
		if(joinCol != null && joinCol.getKey().equals("userId")) {
		    builder.and(likes.users.userId.eq(Integer.parseInt(joinCol.getValue())));
		}
        
		if(joinCol != null && joinCol.getKey().equals("users")) {
		    builder.and(likes.users.email.eq(joinCol.getValue()));
        }
        }
		return builder;
	}
	
    
    
}



