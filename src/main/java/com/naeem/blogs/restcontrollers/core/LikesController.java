package com.naeem.blogs.restcontrollers.core;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.naeem.blogs.commons.search.SearchCriteria;
import com.naeem.blogs.commons.search.SearchUtils;
import com.naeem.blogs.commons.search.OffsetBasedPageRequest;
import com.naeem.blogs.application.core.likes.ILikesAppService;
import com.naeem.blogs.application.core.likes.dto.*;
import com.naeem.blogs.application.core.posts.IPostsAppService;
import com.naeem.blogs.application.core.authorization.users.IUsersAppService;
import java.util.*;
import java.time.*;
import java.net.MalformedURLException;
import com.naeem.blogs.commons.logging.LoggingHelper;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikesController {

	@Qualifier("likesAppService")
	@NonNull protected final ILikesAppService _likesAppService;
    @Qualifier("postsAppService")
	@NonNull  protected final IPostsAppService  _postsAppService;

    @Qualifier("usersAppService")
	@NonNull  protected final IUsersAppService  _usersAppService;

	@NonNull protected final LoggingHelper logHelper;

	@NonNull protected final Environment env;

    @PreAuthorize("hasAnyAuthority('LIKESENTITY_CREATE')")
	@RequestMapping(method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<CreateLikesOutput> create( @RequestBody @Valid CreateLikesInput likes) {
		CreateLikesOutput output=_likesAppService.create(likes);
		if(output == null) {
			throw new EntityNotFoundException("No record found");
		}
		return new ResponseEntity<>(output, HttpStatus.OK);
	}

	// ------------ Delete likes ------------
	@PreAuthorize("hasAnyAuthority('LIKESENTITY_DELETE')")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, consumes = {"application/json"})
	public void delete(@PathVariable String id) {

    	FindLikesByIdOutput output = _likesAppService.findById(Integer.valueOf(id));
    	if(output == null) {
    		throw new EntityNotFoundException(String.format("There does not exist a likes with a id=%s", id));
    	}	

    	_likesAppService.delete(Integer.valueOf(id));
    }


	// ------------ Update likes ------------
    @PreAuthorize("hasAnyAuthority('LIKESENTITY_UPDATE')")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<UpdateLikesOutput> update(@PathVariable String id,  @RequestBody @Valid UpdateLikesInput likes) {

	    FindLikesByIdOutput currentLikes = _likesAppService.findById(Integer.valueOf(id));
		if(currentLikes == null) {
			throw new EntityNotFoundException(String.format("Unable to update. Likes with id=%s not found.", id));
		}

		likes.setVersiono(currentLikes.getVersiono());
	    UpdateLikesOutput output = _likesAppService.update(Integer.valueOf(id),likes);
		if(output == null) {
    		throw new EntityNotFoundException("No record found");
    	}
		return new ResponseEntity<>(output, HttpStatus.OK);
	}
    

    @PreAuthorize("hasAnyAuthority('LIKESENTITY_READ')")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<FindLikesByIdOutput> findById(@PathVariable String id) {

    	FindLikesByIdOutput output = _likesAppService.findById(Integer.valueOf(id));
        if(output == null) {
    		throw new EntityNotFoundException("Not found");
    	}
    	
		return new ResponseEntity<>(output, HttpStatus.OK);
	}
    @PreAuthorize("hasAnyAuthority('LIKESENTITY_READ')")
	@RequestMapping(method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<List<FindLikesByIdOutput>> find(@RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort) throws EntityNotFoundException, MalformedURLException {

		if (offset == null) { offset = env.getProperty("blog.offset.default"); }
		if (limit == null) { limit = env.getProperty("blog.limit.default"); }

		Pageable Pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);
		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);

		return new ResponseEntity<>(_likesAppService.find(searchCriteria,Pageable), HttpStatus.OK);
	}
	
    @PreAuthorize("hasAnyAuthority('LIKESENTITY_READ')")
	@RequestMapping(value = "/{id}/posts", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<GetPostsOutput> getPosts(@PathVariable String id) {
    	GetPostsOutput output= _likesAppService.getPosts(Integer.valueOf(id));
		if(output ==null) {
			throw new EntityNotFoundException("Not found");
	    }		

		return new ResponseEntity<>(output, HttpStatus.OK);
	}
    @PreAuthorize("hasAnyAuthority('LIKESENTITY_READ')")
	@RequestMapping(value = "/{id}/users", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<GetUsersOutput> getUsers(@PathVariable String id) {
    	GetUsersOutput output= _likesAppService.getUsers(Integer.valueOf(id));
		if(output ==null) {
			throw new EntityNotFoundException("Not found");
	    }		

		return new ResponseEntity<>(output, HttpStatus.OK);
	}
}


