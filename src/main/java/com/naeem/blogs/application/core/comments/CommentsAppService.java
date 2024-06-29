package com.naeem.blogs.application.core.comments;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import com.naeem.blogs.application.core.comments.dto.*;
import com.naeem.blogs.domain.core.comments.ICommentsRepository;
import com.naeem.blogs.domain.core.comments.QComments;
import com.naeem.blogs.domain.core.comments.Comments;
import com.naeem.blogs.domain.core.posts.IPostsRepository;
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

@Service("commentsAppService")
@RequiredArgsConstructor
public class CommentsAppService implements ICommentsAppService {
    
	@Qualifier("commentsRepository")
	@NonNull protected final ICommentsRepository _commentsRepository;

	
    @Qualifier("postsRepository")
	@NonNull protected final IPostsRepository _postsRepository;

    @Qualifier("usersRepository")
	@NonNull protected final IUsersRepository _usersRepository;

	@Qualifier("ICommentsMapperImpl")
	@NonNull protected final ICommentsMapper mapper;

	@NonNull protected final LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
	public CreateCommentsOutput create(CreateCommentsInput input) {

		Comments comments = mapper.createCommentsInputToComments(input);
		Posts foundPosts = null;
		Users foundUsers = null;
	  	if(input.getPostId()!=null) {
			foundPosts = _postsRepository.findById(input.getPostId()).orElse(null);
			
			if(foundPosts!=null) {
				foundPosts.addComments(comments);
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
	  	if(input.getAuthorId()!=null) {
		    foundUsers = _usersRepository.findById(Long.parseLong(input.getAuthorId().toString())).orElse(null);
			
			if(foundUsers!=null) {
				foundUsers.addComments(comments);
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}

		Comments createdComments = _commentsRepository.save(comments);
		return mapper.commentsToCreateCommentsOutput(createdComments);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public UpdateCommentsOutput update(Integer commentsId, UpdateCommentsInput input) {

		Comments existing = _commentsRepository.findById(commentsId).orElseThrow(() -> new EntityNotFoundException("Comments not found"));

		Comments comments = mapper.updateCommentsInputToComments(input);
		Posts foundPosts = null;
		Users foundUsers = null;
        
	  	if(input.getPostId()!=null) { 
			foundPosts = _postsRepository.findById(input.getPostId()).orElse(null);
		
			if(foundPosts!=null) {
				foundPosts.addComments(comments);
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
        
	  	if(input.getAuthorId()!=null) { 
		    foundUsers = _usersRepository.findById(Long.parseLong(input.getAuthorId().toString())).orElse(null);
		
			if(foundUsers!=null) {
				foundUsers.addComments(comments);
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
		
		Comments updatedComments = _commentsRepository.save(comments);
		return mapper.commentsToUpdateCommentsOutput(updatedComments);
	}
	
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Integer commentsId) {

		Comments existing = _commentsRepository.findById(commentsId).orElseThrow(() -> new EntityNotFoundException("Comments not found"));
	 	
        if(existing.getPosts() !=null)
        {
        existing.getPosts().removeComments(existing);
        }
        if(existing.getUsers() !=null)
        {
        existing.getUsers().removeComments(existing);
        }
        if(existing !=null) {
			_commentsRepository.delete(existing);
		}
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public FindCommentsByIdOutput findById(Integer commentsId) {

		Comments foundComments = _commentsRepository.findById(commentsId).orElse(null);
		if (foundComments == null)  
			return null; 
 	   
 	    return mapper.commentsToFindCommentsByIdOutput(foundComments);
	}

    //Posts
	// ReST API Call - GET /comments/1/posts
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public GetPostsOutput getPosts(Integer commentsId) {

		Comments foundComments = _commentsRepository.findById(commentsId).orElse(null);
		if (foundComments == null) {
			logHelper.getLogger().error("There does not exist a comments wth a id='{}'", commentsId);
			return null;
		}
		Posts re = foundComments.getPosts();
		return mapper.postsToGetPostsOutput(re, foundComments);
	}
	
    //Users
	// ReST API Call - GET /comments/1/users
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public GetUsersOutput getUsers(Integer commentsId) {

		Comments foundComments = _commentsRepository.findById(commentsId).orElse(null);
		if (foundComments == null) {
			logHelper.getLogger().error("There does not exist a comments wth a id='{}'", commentsId);
			return null;
		}
		Users re = foundComments.getUsers();
		return mapper.usersToGetUsersOutput(re, foundComments);
	}
	
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<FindCommentsByIdOutput> find(SearchCriteria search, Pageable pageable) throws MalformedURLException  {

		Page<Comments> foundComments = _commentsRepository.findAll(search(search), pageable);
		List<Comments> commentsList = foundComments.getContent();
		Iterator<Comments> commentsIterator = commentsList.iterator(); 
		List<FindCommentsByIdOutput> output = new ArrayList<>();

		while (commentsIterator.hasNext()) {
		Comments comments = commentsIterator.next();
 	    output.add(mapper.commentsToFindCommentsByIdOutput(comments));
		}
		return output;
	}
	
	protected BooleanBuilder search(SearchCriteria search) throws MalformedURLException {

		QComments comments= QComments.commentsEntity;
		if(search != null) {
			Map<String,SearchFields> map = new HashMap<>();
			for(SearchFields fieldDetails: search.getFields())
			{
				map.put(fieldDetails.getFieldName(),fieldDetails);
			}
			List<String> keysList = new ArrayList<String>(map.keySet());
			checkProperties(keysList);
			return searchKeyValuePair(comments, map,search.getJoinColumns());
		}
		return null;
	}
	
	protected void checkProperties(List<String> list) throws MalformedURLException  {
		for (int i = 0; i < list.size(); i++) {
			if(!(
		        list.get(i).replace("%20","").trim().equals("posts") ||
				list.get(i).replace("%20","").trim().equals("postId") ||
		        list.get(i).replace("%20","").trim().equals("users") ||
				list.get(i).replace("%20","").trim().equals("authorId") ||
				list.get(i).replace("%20","").trim().equals("commentId") ||
				list.get(i).replace("%20","").trim().equals("content") ||
				list.get(i).replace("%20","").trim().equals("createdAt")
			)) 
			{
			 throw new MalformedURLException("Wrong URL Format: Property " + list.get(i) + " not found!" );
			}
		}
	}
	
	protected BooleanBuilder searchKeyValuePair(QComments comments, Map<String,SearchFields> map,Map<String,String> joinColumns) {
		BooleanBuilder builder = new BooleanBuilder();
        
		Iterator<Map.Entry<String, SearchFields>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, SearchFields> details = iterator.next();

			if(details.getKey().replace("%20","").trim().equals("commentId")) {
				if(details.getValue().getOperator().equals("contains") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(comments.commentId.like(details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(comments.commentId.eq(Integer.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("notEqual") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(comments.commentId.ne(Integer.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("range")) {
				   if(StringUtils.isNumeric(details.getValue().getStartingValue()) && StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(comments.commentId.between(Integer.valueOf(details.getValue().getStartingValue()), Integer.valueOf(details.getValue().getEndingValue())));
                   } else if(StringUtils.isNumeric(details.getValue().getStartingValue())) {
                	   builder.and(comments.commentId.goe(Integer.valueOf(details.getValue().getStartingValue())));
                   } else if(StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(comments.commentId.loe(Integer.valueOf(details.getValue().getEndingValue())));
				   }
				}
			}
            if(details.getKey().replace("%20","").trim().equals("content")) {
				if(details.getValue().getOperator().equals("contains")) {
					builder.and(comments.content.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals")) {
					builder.and(comments.content.eq(details.getValue().getSearchValue()));
				} else if(details.getValue().getOperator().equals("notEqual")) {
					builder.and(comments.content.ne(details.getValue().getSearchValue()));
				}
			}
			if(details.getKey().replace("%20","").trim().equals("createdAt")) {
				if(details.getValue().getOperator().equals("equals") && SearchUtils.stringToLocalDateTime(details.getValue().getSearchValue()) !=null) {
					builder.and(comments.createdAt.eq(SearchUtils.stringToLocalDateTime(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("notEqual") && SearchUtils.stringToLocalDateTime(details.getValue().getSearchValue()) !=null) {
					builder.and(comments.createdAt.ne(SearchUtils.stringToLocalDateTime(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("range")) {
				   LocalDateTime startLocalDateTime= SearchUtils.stringToLocalDateTime(details.getValue().getStartingValue());
				   LocalDateTime endLocalDateTime= SearchUtils.stringToLocalDateTime(details.getValue().getEndingValue());
				   if(startLocalDateTime!=null && endLocalDateTime!=null) {
					   builder.and(comments.createdAt.between(startLocalDateTime,endLocalDateTime));
				   } else if(endLocalDateTime!=null) {
					   builder.and(comments.createdAt.loe(endLocalDateTime));
                   } else if(startLocalDateTime!=null) {
                	   builder.and(comments.createdAt.goe(startLocalDateTime));  
                   }
                }     
			}
	    
		     if(details.getKey().replace("%20","").trim().equals("posts")) {
				if(details.getValue().getOperator().equals("contains") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(comments.posts.postId.like(details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(comments.posts.postId.eq(Integer.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("notEqual") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(comments.posts.postId.ne(Integer.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("range")) {
				   if(StringUtils.isNumeric(details.getValue().getStartingValue()) && StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(comments.posts.postId.between(Integer.valueOf(details.getValue().getStartingValue()), Integer.valueOf(details.getValue().getEndingValue())));
                   } else if(StringUtils.isNumeric(details.getValue().getStartingValue())) {
                	   builder.and(comments.posts.postId.goe(Integer.valueOf(details.getValue().getStartingValue())));
                   } else if(StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(comments.posts.postId.loe(Integer.valueOf(details.getValue().getEndingValue())));
				   }
				}
			}
		    if(details.getKey().replace("%20","").trim().equals("users")) {
				if(details.getValue().getOperator().equals("contains")) {
					builder.and(comments.users.username.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals")) {
					builder.and(comments.users.username.eq(details.getValue().getSearchValue()));
				} else if(details.getValue().getOperator().equals("notEqual")) {
					builder.and(comments.users.username.ne(details.getValue().getSearchValue()));
				}
			}
		}
		
		for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
		if(joinCol != null && joinCol.getKey().equals("postId")) {
		    builder.and(comments.posts.postId.eq(Integer.parseInt(joinCol.getValue())));
		}
        
        }
		for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
        if(joinCol != null && joinCol.getKey().equals("authorId")) {
		    builder.and(comments.users.userId.eq(Long.parseLong(joinCol.getValue())));
        }
        
		if(joinCol != null && joinCol.getKey().equals("users")) {
		    builder.and(comments.users.username.eq(joinCol.getValue()));
        }
        }
		return builder;
	}
	
    
    
}



