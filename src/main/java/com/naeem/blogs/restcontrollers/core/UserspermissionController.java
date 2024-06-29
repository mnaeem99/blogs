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
import com.naeem.blogs.domain.core.authorization.userspermission.UserspermissionId;
import com.naeem.blogs.commons.search.SearchCriteria;
import com.naeem.blogs.commons.search.SearchUtils;
import com.naeem.blogs.commons.search.OffsetBasedPageRequest;
import com.naeem.blogs.application.core.authorization.userspermission.IUserspermissionAppService;
import com.naeem.blogs.application.core.authorization.userspermission.dto.*;
import com.naeem.blogs.application.core.authorization.permission.IPermissionAppService;
import com.naeem.blogs.application.core.authorization.users.IUsersAppService;
import com.naeem.blogs.application.core.authorization.users.dto.FindUsersByIdOutput;
import com.naeem.blogs.security.JWTAppService;
import java.util.*;
import java.time.*;
import java.net.MalformedURLException;
import com.naeem.blogs.commons.logging.LoggingHelper;

@RestController
@RequestMapping("/userspermission")
@RequiredArgsConstructor
public class UserspermissionController {

	@Qualifier("userspermissionAppService")
	@NonNull protected final IUserspermissionAppService _userspermissionAppService;
    @Qualifier("permissionAppService")
	@NonNull  protected final IPermissionAppService  _permissionAppService;

    @Qualifier("usersAppService")
	@NonNull  protected final IUsersAppService  _usersAppService;

	@NonNull protected final JWTAppService _jwtAppService;

	@NonNull protected final LoggingHelper logHelper;

	@NonNull protected final Environment env;

    @PreAuthorize("hasAnyAuthority('USERSPERMISSIONENTITY_CREATE')")
	@RequestMapping(method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<CreateUserspermissionOutput> create( @RequestBody @Valid CreateUserspermissionInput userspermission) {
		CreateUserspermissionOutput output=_userspermissionAppService.create(userspermission);
		if(output == null) {
			throw new EntityNotFoundException("No record found");
		}
		FindUsersByIdOutput foundUsers =_usersAppService.findById(output.getUsersUserId());
		_jwtAppService.deleteAllUserTokens(foundUsers.getUsername());

		return new ResponseEntity<>(output, HttpStatus.OK);
	}

	// ------------ Delete userspermission ------------
	@PreAuthorize("hasAnyAuthority('USERSPERMISSIONENTITY_DELETE')")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, consumes = {"application/json"})
	public void delete(@PathVariable String id) {

		UserspermissionId userspermissionid =_userspermissionAppService.parseUserspermissionKey(id);
		if(userspermissionid == null) {
			throw new EntityNotFoundException(String.format("Invalid id=%s", id));
		}	

		FindUserspermissionByIdOutput output = _userspermissionAppService.findById(userspermissionid);
    	if(output == null) {
    		throw new EntityNotFoundException(String.format("There does not exist a userspermission with a id=%s", id));
    	}	

		_userspermissionAppService.delete(userspermissionid);
		FindUsersByIdOutput foundUsers =_usersAppService.findById(output.getUsersUserId());
		_jwtAppService.deleteAllUserTokens(foundUsers.getUsername());
    }


	// ------------ Update userspermission ------------
    @PreAuthorize("hasAnyAuthority('USERSPERMISSIONENTITY_UPDATE')")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<UpdateUserspermissionOutput> update(@PathVariable String id,  @RequestBody @Valid UpdateUserspermissionInput userspermission) {

		UserspermissionId userspermissionid =_userspermissionAppService.parseUserspermissionKey(id);

		if(userspermissionid == null) {
			throw new EntityNotFoundException(String.format("Invalid id=%s", id));
		}

		FindUserspermissionByIdOutput currentUserspermission = _userspermissionAppService.findById(userspermissionid);
		if(currentUserspermission == null) {
			throw new EntityNotFoundException(String.format("Unable to update. Userspermission with id=%s not found.", id));
		}

		userspermission.setVersiono(currentUserspermission.getVersiono());
		FindUsersByIdOutput foundUsers =_usersAppService.findById(userspermissionid.getUsersUserId());
		_jwtAppService.deleteAllUserTokens(foundUsers.getUsername());

		UpdateUserspermissionOutput output = _userspermissionAppService.update(userspermissionid,userspermission);
		if(output == null) {
    		throw new EntityNotFoundException("No record found");
    	}
		return new ResponseEntity<>(output, HttpStatus.OK);
	}
    

    @PreAuthorize("hasAnyAuthority('USERSPERMISSIONENTITY_READ')")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<FindUserspermissionByIdOutput> findById(@PathVariable String id) {

		UserspermissionId userspermissionid =_userspermissionAppService.parseUserspermissionKey(id);
		if(userspermissionid == null) {
			throw new EntityNotFoundException(String.format("Invalid id=%s", id));
		}	

		FindUserspermissionByIdOutput output = _userspermissionAppService.findById(userspermissionid);
        if(output == null) {
    		throw new EntityNotFoundException("Not found");
    	}
    	
		return new ResponseEntity<>(output, HttpStatus.OK);
	}
    @PreAuthorize("hasAnyAuthority('USERSPERMISSIONENTITY_READ')")
	@RequestMapping(method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<List<FindUserspermissionByIdOutput>> find(@RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort) throws EntityNotFoundException, MalformedURLException {

		if (offset == null) { offset = env.getProperty("blog.offset.default"); }
		if (limit == null) { limit = env.getProperty("blog.limit.default"); }

		Pageable Pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);
		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);

		return new ResponseEntity<>(_userspermissionAppService.find(searchCriteria,Pageable), HttpStatus.OK);
	}
	
    @PreAuthorize("hasAnyAuthority('USERSPERMISSIONENTITY_READ')")
	@RequestMapping(value = "/{id}/permission", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<GetPermissionOutput> getPermission(@PathVariable String id) {
		UserspermissionId userspermissionid =_userspermissionAppService.parseUserspermissionKey(id);
		if(userspermissionid == null) {
			throw new EntityNotFoundException(String.format("Invalid id=%s",id));
		}

		GetPermissionOutput output= _userspermissionAppService.getPermission(userspermissionid);
		if(output ==null) {
			throw new EntityNotFoundException("Not found");
	    }		

		return new ResponseEntity<>(output, HttpStatus.OK);
	}
    @PreAuthorize("hasAnyAuthority('USERSPERMISSIONENTITY_READ')")
	@RequestMapping(value = "/{id}/users", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<GetUsersOutput> getUsers(@PathVariable String id) {
		UserspermissionId userspermissionid =_userspermissionAppService.parseUserspermissionKey(id);
		if(userspermissionid == null) {
			throw new EntityNotFoundException(String.format("Invalid id=%s",id));
		}

		GetUsersOutput output= _userspermissionAppService.getUsers(userspermissionid);
		if(output ==null) {
			throw new EntityNotFoundException("Not found");
	    }		

		return new ResponseEntity<>(output, HttpStatus.OK);
	}
}


