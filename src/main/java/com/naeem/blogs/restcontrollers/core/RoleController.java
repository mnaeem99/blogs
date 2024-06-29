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
import com.naeem.blogs.application.core.authorization.role.IRoleAppService;
import com.naeem.blogs.application.core.authorization.role.dto.*;
import com.naeem.blogs.application.core.authorization.rolepermission.IRolepermissionAppService;
import com.naeem.blogs.application.core.authorization.rolepermission.dto.FindRolepermissionByIdOutput;
import com.naeem.blogs.application.core.authorization.usersrole.IUsersroleAppService;
import com.naeem.blogs.application.core.authorization.usersrole.dto.FindUsersroleByIdOutput;
import java.util.*;
import java.time.*;
import java.net.MalformedURLException;
import com.naeem.blogs.commons.logging.LoggingHelper;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

	@Qualifier("roleAppService")
	@NonNull protected final IRoleAppService _roleAppService;
    @Qualifier("rolepermissionAppService")
	@NonNull  protected final IRolepermissionAppService  _rolepermissionAppService;

    @Qualifier("usersroleAppService")
	@NonNull  protected final IUsersroleAppService  _usersroleAppService;

	@NonNull protected final LoggingHelper logHelper;

	@NonNull protected final Environment env;

    @PreAuthorize("hasAnyAuthority('ROLEENTITY_CREATE')")
	@RequestMapping(method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<CreateRoleOutput> create( @RequestBody @Valid CreateRoleInput role) {
		CreateRoleOutput output=_roleAppService.create(role);
		return new ResponseEntity<>(output, HttpStatus.OK);
	}

	// ------------ Delete role ------------
	@PreAuthorize("hasAnyAuthority('ROLEENTITY_DELETE')")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, consumes = {"application/json"})
	public void delete(@PathVariable String id) {

    	FindRoleByIdOutput output = _roleAppService.findById(Long.valueOf(id));
    	if(output == null) {
    		throw new EntityNotFoundException(String.format("There does not exist a role with a id=%s", id));
    	}	

    	_roleAppService.delete(Long.valueOf(id));
    }


	// ------------ Update role ------------
    @PreAuthorize("hasAnyAuthority('ROLEENTITY_UPDATE')")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<UpdateRoleOutput> update(@PathVariable String id,  @RequestBody @Valid UpdateRoleInput role) {

	    FindRoleByIdOutput currentRole = _roleAppService.findById(Long.valueOf(id));
		if(currentRole == null) {
			throw new EntityNotFoundException(String.format("Unable to update. Role with id=%s not found.", id));
		}

		role.setVersiono(currentRole.getVersiono());
	    UpdateRoleOutput output = _roleAppService.update(Long.valueOf(id),role);
		return new ResponseEntity<>(output, HttpStatus.OK);
	}
    

    @PreAuthorize("hasAnyAuthority('ROLEENTITY_READ')")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<FindRoleByIdOutput> findById(@PathVariable String id) {

    	FindRoleByIdOutput output = _roleAppService.findById(Long.valueOf(id));
        if(output == null) {
    		throw new EntityNotFoundException("Not found");
    	}
    	
		return new ResponseEntity<>(output, HttpStatus.OK);
	}
    @PreAuthorize("hasAnyAuthority('ROLEENTITY_READ')")
	@RequestMapping(method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<List<FindRoleByIdOutput>> find(@RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort) throws EntityNotFoundException, MalformedURLException {

		if (offset == null) { offset = env.getProperty("blog.offset.default"); }
		if (limit == null) { limit = env.getProperty("blog.limit.default"); }

		Pageable Pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);
		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);

		return new ResponseEntity<>(_roleAppService.find(searchCriteria,Pageable), HttpStatus.OK);
	}
	
    @PreAuthorize("hasAnyAuthority('ROLEENTITY_READ')")
	@RequestMapping(value = "/{id}/rolepermissions", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<List<FindRolepermissionByIdOutput>> getRolepermissions(@PathVariable String id, @RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort) throws EntityNotFoundException, MalformedURLException {
   		if (offset == null) { offset = env.getProperty("blog.offset.default"); }
		if (limit == null) { limit = env.getProperty("blog.limit.default"); }

		Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);

		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);
		Map<String,String> joinColDetails=_roleAppService.parseRolepermissionsJoinColumn(id);
		if(joinColDetails == null) {
			throw new EntityNotFoundException("Invalid join column");
		}

		searchCriteria.setJoinColumns(joinColDetails);

    	List<FindRolepermissionByIdOutput> output = _rolepermissionAppService.find(searchCriteria,pageable);
    	
    	if(output == null) {
			throw new EntityNotFoundException("Not found");
		}
		
		return new ResponseEntity<>(output, HttpStatus.OK);
	}
    @PreAuthorize("hasAnyAuthority('ROLEENTITY_READ')")
	@RequestMapping(value = "/{id}/usersroles", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<List<FindUsersroleByIdOutput>> getUsersroles(@PathVariable String id, @RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort) throws EntityNotFoundException, MalformedURLException {
   		if (offset == null) { offset = env.getProperty("blog.offset.default"); }
		if (limit == null) { limit = env.getProperty("blog.limit.default"); }

		Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);

		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);
		Map<String,String> joinColDetails=_roleAppService.parseUsersrolesJoinColumn(id);
		if(joinColDetails == null) {
			throw new EntityNotFoundException("Invalid join column");
		}

		searchCriteria.setJoinColumns(joinColDetails);

    	List<FindUsersroleByIdOutput> output = _usersroleAppService.find(searchCriteria,pageable);
    	
    	if(output == null) {
			throw new EntityNotFoundException("Not found");
		}
		
		return new ResponseEntity<>(output, HttpStatus.OK);
	}
}


