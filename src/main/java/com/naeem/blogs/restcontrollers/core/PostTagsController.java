package com.naeem.blogs.restcontrollers.core;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.naeem.blogs.domain.core.posttags.PostTagsId;
import com.naeem.blogs.commons.search.SearchCriteria;
import com.naeem.blogs.commons.search.SearchUtils;
import com.naeem.blogs.commons.search.OffsetBasedPageRequest;
import com.naeem.blogs.application.core.posttags.IPostTagsAppService;
import com.naeem.blogs.application.core.posttags.dto.*;
import com.naeem.blogs.application.core.posts.IPostsAppService;
import com.naeem.blogs.application.core.tags.ITagsAppService;
import java.util.*;
import java.time.*;
import java.net.MalformedURLException;
import com.naeem.blogs.commons.logging.LoggingHelper;

@RestController
@RequestMapping("/postTags")
@RequiredArgsConstructor
public class PostTagsController {

	@Qualifier("postTagsAppService")
	@NonNull protected final IPostTagsAppService _postTagsAppService;
    @Qualifier("postsAppService")
	@NonNull  protected final IPostsAppService  _postsAppService;

    @Qualifier("tagsAppService")
	@NonNull  protected final ITagsAppService  _tagsAppService;

	@NonNull protected final LoggingHelper logHelper;

	@NonNull protected final Environment env;

	@RequestMapping(method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<CreatePostTagsOutput> create( @RequestBody @Valid CreatePostTagsInput postTags) {
		CreatePostTagsOutput output=_postTagsAppService.create(postTags);
		return new ResponseEntity<>(output, HttpStatus.OK);
	}

	// ------------ Delete postTags ------------
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, consumes = {"application/json"})
	public void delete(@PathVariable String id) {

		PostTagsId posttagsid =_postTagsAppService.parsePostTagsKey(id);
		if(posttagsid == null) {
			throw new EntityNotFoundException(String.format("Invalid id=%s", id));
		}	

		FindPostTagsByIdOutput output = _postTagsAppService.findById(posttagsid);
    	if(output == null) {
    		throw new EntityNotFoundException(String.format("There does not exist a postTags with a id=%s", id));
    	}	

		_postTagsAppService.delete(posttagsid);
    }


	// ------------ Update postTags ------------
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<UpdatePostTagsOutput> update(@PathVariable String id,  @RequestBody @Valid UpdatePostTagsInput postTags) {

		PostTagsId posttagsid =_postTagsAppService.parsePostTagsKey(id);

		if(posttagsid == null) {
			throw new EntityNotFoundException(String.format("Invalid id=%s", id));
		}

		FindPostTagsByIdOutput currentPostTags = _postTagsAppService.findById(posttagsid);
		if(currentPostTags == null) {
			throw new EntityNotFoundException(String.format("Unable to update. PostTags with id=%s not found.", id));
		}

		postTags.setVersiono(currentPostTags.getVersiono());
		UpdatePostTagsOutput output = _postTagsAppService.update(posttagsid,postTags);
		return new ResponseEntity<>(output, HttpStatus.OK);
	}
    

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<FindPostTagsByIdOutput> findById(@PathVariable String id) {

		PostTagsId posttagsid =_postTagsAppService.parsePostTagsKey(id);
		if(posttagsid == null) {
			throw new EntityNotFoundException(String.format("Invalid id=%s", id));
		}	

		FindPostTagsByIdOutput output = _postTagsAppService.findById(posttagsid);
        if(output == null) {
    		throw new EntityNotFoundException("Not found");
    	}
    	
		return new ResponseEntity<>(output, HttpStatus.OK);
	}
	@RequestMapping(method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<List<FindPostTagsByIdOutput>> find(@RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort) throws EntityNotFoundException, MalformedURLException {

		if (offset == null) { offset = env.getProperty("blog.offset.default"); }
		if (limit == null) { limit = env.getProperty("blog.limit.default"); }

		Pageable Pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);
		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);

		return new ResponseEntity<>(_postTagsAppService.find(searchCriteria,Pageable), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{id}/posts", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<GetPostsOutput> getPosts(@PathVariable String id) {
		PostTagsId posttagsid =_postTagsAppService.parsePostTagsKey(id);
		if(posttagsid == null) {
			throw new EntityNotFoundException(String.format("Invalid id=%s",id));
		}

		GetPostsOutput output= _postTagsAppService.getPosts(posttagsid);
		if(output ==null) {
			throw new EntityNotFoundException("Not found");
	    }		

		return new ResponseEntity<>(output, HttpStatus.OK);
	}
	@RequestMapping(value = "/{id}/tags", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<GetTagsOutput> getTags(@PathVariable String id) {
		PostTagsId posttagsid =_postTagsAppService.parsePostTagsKey(id);
		if(posttagsid == null) {
			throw new EntityNotFoundException(String.format("Invalid id=%s",id));
		}

		GetTagsOutput output= _postTagsAppService.getTags(posttagsid);
		if(output ==null) {
			throw new EntityNotFoundException("Not found");
	    }		

		return new ResponseEntity<>(output, HttpStatus.OK);
	}
}


