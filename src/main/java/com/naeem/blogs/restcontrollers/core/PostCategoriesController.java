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
import com.naeem.blogs.domain.core.postcategories.PostCategoriesId;
import com.naeem.blogs.commons.search.SearchCriteria;
import com.naeem.blogs.commons.search.SearchUtils;
import com.naeem.blogs.commons.search.OffsetBasedPageRequest;
import com.naeem.blogs.application.core.postcategories.IPostCategoriesAppService;
import com.naeem.blogs.application.core.postcategories.dto.*;
import com.naeem.blogs.application.core.categories.ICategoriesAppService;
import com.naeem.blogs.application.core.posts.IPostsAppService;
import java.util.*;
import java.time.*;
import java.net.MalformedURLException;
import com.naeem.blogs.commons.logging.LoggingHelper;

@RestController
@RequestMapping("/postCategories")
@RequiredArgsConstructor
public class PostCategoriesController {

	@Qualifier("postCategoriesAppService")
	@NonNull protected final IPostCategoriesAppService _postCategoriesAppService;
    @Qualifier("categoriesAppService")
	@NonNull  protected final ICategoriesAppService  _categoriesAppService;

    @Qualifier("postsAppService")
	@NonNull  protected final IPostsAppService  _postsAppService;

	@NonNull protected final LoggingHelper logHelper;

	@NonNull protected final Environment env;

	@RequestMapping(method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<CreatePostCategoriesOutput> create( @RequestBody @Valid CreatePostCategoriesInput postCategories) {
		CreatePostCategoriesOutput output=_postCategoriesAppService.create(postCategories);
		return new ResponseEntity<>(output, HttpStatus.OK);
	}

	// ------------ Delete postCategories ------------
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, consumes = {"application/json"})
	public void delete(@PathVariable String id) {

		PostCategoriesId postcategoriesid =_postCategoriesAppService.parsePostCategoriesKey(id);
		if(postcategoriesid == null) {
			throw new EntityNotFoundException(String.format("Invalid id=%s", id));
		}	

		FindPostCategoriesByIdOutput output = _postCategoriesAppService.findById(postcategoriesid);
    	if(output == null) {
    		throw new EntityNotFoundException(String.format("There does not exist a postCategories with a id=%s", id));
    	}	

		_postCategoriesAppService.delete(postcategoriesid);
    }


	// ------------ Update postCategories ------------
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<UpdatePostCategoriesOutput> update(@PathVariable String id,  @RequestBody @Valid UpdatePostCategoriesInput postCategories) {

		PostCategoriesId postcategoriesid =_postCategoriesAppService.parsePostCategoriesKey(id);

		if(postcategoriesid == null) {
			throw new EntityNotFoundException(String.format("Invalid id=%s", id));
		}

		FindPostCategoriesByIdOutput currentPostCategories = _postCategoriesAppService.findById(postcategoriesid);
		if(currentPostCategories == null) {
			throw new EntityNotFoundException(String.format("Unable to update. PostCategories with id=%s not found.", id));
		}

		postCategories.setVersiono(currentPostCategories.getVersiono());
		UpdatePostCategoriesOutput output = _postCategoriesAppService.update(postcategoriesid,postCategories);
		return new ResponseEntity<>(output, HttpStatus.OK);
	}
    

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<FindPostCategoriesByIdOutput> findById(@PathVariable String id) {

		PostCategoriesId postcategoriesid =_postCategoriesAppService.parsePostCategoriesKey(id);
		if(postcategoriesid == null) {
			throw new EntityNotFoundException(String.format("Invalid id=%s", id));
		}	

		FindPostCategoriesByIdOutput output = _postCategoriesAppService.findById(postcategoriesid);
        if(output == null) {
    		throw new EntityNotFoundException("Not found");
    	}
    	
		return new ResponseEntity<>(output, HttpStatus.OK);
	}
	@RequestMapping(method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<List<FindPostCategoriesByIdOutput>> find(@RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort) throws EntityNotFoundException, MalformedURLException {

		if (offset == null) { offset = env.getProperty("blog.offset.default"); }
		if (limit == null) { limit = env.getProperty("blog.limit.default"); }

		Pageable Pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);
		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);

		return new ResponseEntity<>(_postCategoriesAppService.find(searchCriteria,Pageable), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{id}/categories", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<GetCategoriesOutput> getCategories(@PathVariable String id) {
		PostCategoriesId postcategoriesid =_postCategoriesAppService.parsePostCategoriesKey(id);
		if(postcategoriesid == null) {
			throw new EntityNotFoundException(String.format("Invalid id=%s",id));
		}

		GetCategoriesOutput output= _postCategoriesAppService.getCategories(postcategoriesid);
		if(output ==null) {
			throw new EntityNotFoundException("Not found");
	    }		

		return new ResponseEntity<>(output, HttpStatus.OK);
	}
	@RequestMapping(value = "/{id}/posts", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<GetPostsOutput> getPosts(@PathVariable String id) {
		PostCategoriesId postcategoriesid =_postCategoriesAppService.parsePostCategoriesKey(id);
		if(postcategoriesid == null) {
			throw new EntityNotFoundException(String.format("Invalid id=%s",id));
		}

		GetPostsOutput output= _postCategoriesAppService.getPosts(postcategoriesid);
		if(output ==null) {
			throw new EntityNotFoundException("Not found");
	    }		

		return new ResponseEntity<>(output, HttpStatus.OK);
	}
}


