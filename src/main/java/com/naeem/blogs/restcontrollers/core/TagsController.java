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
import com.naeem.blogs.application.core.tags.ITagsAppService;
import com.naeem.blogs.application.core.tags.dto.*;
import com.naeem.blogs.application.core.posttags.IPostTagsAppService;
import com.naeem.blogs.application.core.posttags.dto.FindPostTagsByIdOutput;
import java.util.*;
import java.time.*;
import java.net.MalformedURLException;
import com.naeem.blogs.commons.logging.LoggingHelper;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagsController {

	@Qualifier("tagsAppService")
	@NonNull protected final ITagsAppService _tagsAppService;
    @Qualifier("postTagsAppService")
	@NonNull  protected final IPostTagsAppService  _postTagsAppService;

	@NonNull protected final LoggingHelper logHelper;

	@NonNull protected final Environment env;

    @PreAuthorize("hasAnyAuthority('TAGSENTITY_CREATE')")
	@RequestMapping(method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<CreateTagsOutput> create( @RequestBody @Valid CreateTagsInput tags) {
		CreateTagsOutput output=_tagsAppService.create(tags);
		return new ResponseEntity<>(output, HttpStatus.OK);
	}

	// ------------ Delete tags ------------
	@PreAuthorize("hasAnyAuthority('TAGSENTITY_DELETE')")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, consumes = {"application/json"})
	public void delete(@PathVariable String id) {

    	FindTagsByIdOutput output = _tagsAppService.findById(Integer.valueOf(id));
    	if(output == null) {
    		throw new EntityNotFoundException(String.format("There does not exist a tags with a id=%s", id));
    	}	

    	_tagsAppService.delete(Integer.valueOf(id));
    }


	// ------------ Update tags ------------
    @PreAuthorize("hasAnyAuthority('TAGSENTITY_UPDATE')")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<UpdateTagsOutput> update(@PathVariable String id,  @RequestBody @Valid UpdateTagsInput tags) {

	    FindTagsByIdOutput currentTags = _tagsAppService.findById(Integer.valueOf(id));
		if(currentTags == null) {
			throw new EntityNotFoundException(String.format("Unable to update. Tags with id=%s not found.", id));
		}

		tags.setVersiono(currentTags.getVersiono());
	    UpdateTagsOutput output = _tagsAppService.update(Integer.valueOf(id),tags);
		return new ResponseEntity<>(output, HttpStatus.OK);
	}
    

    @PreAuthorize("hasAnyAuthority('TAGSENTITY_READ')")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<FindTagsByIdOutput> findById(@PathVariable String id) {

    	FindTagsByIdOutput output = _tagsAppService.findById(Integer.valueOf(id));
        if(output == null) {
    		throw new EntityNotFoundException("Not found");
    	}
    	
		return new ResponseEntity<>(output, HttpStatus.OK);
	}
    @PreAuthorize("hasAnyAuthority('TAGSENTITY_READ')")
	@RequestMapping(method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<List<FindTagsByIdOutput>> find(@RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort) throws EntityNotFoundException, MalformedURLException {

		if (offset == null) { offset = env.getProperty("blog.offset.default"); }
		if (limit == null) { limit = env.getProperty("blog.limit.default"); }

		Pageable Pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);
		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);

		return new ResponseEntity<>(_tagsAppService.find(searchCriteria,Pageable), HttpStatus.OK);
	}
	
    @PreAuthorize("hasAnyAuthority('TAGSENTITY_READ')")
	@RequestMapping(value = "/{id}/postTags", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<List<FindPostTagsByIdOutput>> getPostTags(@PathVariable String id, @RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort) throws EntityNotFoundException, MalformedURLException {
   		if (offset == null) { offset = env.getProperty("blog.offset.default"); }
		if (limit == null) { limit = env.getProperty("blog.limit.default"); }

		Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);

		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);
		Map<String,String> joinColDetails=_tagsAppService.parsePostTagsJoinColumn(id);
		if(joinColDetails == null) {
			throw new EntityNotFoundException("Invalid join column");
		}

		searchCriteria.setJoinColumns(joinColDetails);

    	List<FindPostTagsByIdOutput> output = _postTagsAppService.find(searchCriteria,pageable);
    	
    	if(output == null) {
			throw new EntityNotFoundException("Not found");
		}
		
		return new ResponseEntity<>(output, HttpStatus.OK);
	}
}


