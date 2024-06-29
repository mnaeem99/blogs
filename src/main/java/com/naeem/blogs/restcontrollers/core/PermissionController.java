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
import com.naeem.blogs.application.core.authorization.permission.IPermissionAppService;
import com.naeem.blogs.application.core.authorization.permission.dto.*;
import com.naeem.blogs.application.core.authorization.rolepermission.IRolepermissionAppService;
import com.naeem.blogs.application.core.authorization.rolepermission.dto.FindRolepermissionByIdOutput;
import com.naeem.blogs.application.core.authorization.userspermission.IUserspermissionAppService;
import com.naeem.blogs.application.core.authorization.userspermission.dto.FindUserspermissionByIdOutput;
import java.util.*;
import java.time.*;
import java.net.MalformedURLException;
import com.naeem.blogs.commons.logging.LoggingHelper;

@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionController {

	@Qualifier("permissionAppService")
	@NonNull protected final IPermissionAppService _permissionAppService;
    @Qualifier("rolepermissionAppService")
	@NonNull  protected final IRolepermissionAppService  _rolepermissionAppService;

    @Qualifier("userspermissionAppService")
	@NonNull  protected final IUserspermissionAppService  _userspermissionAppService;

	@NonNull protected final LoggingHelper logHelper;

	@NonNull protected final Environment env;

    @PreAuthorize("hasAnyAuthority('PERMISSIONENTITY_CREATE')")
	@RequestMapping(method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<CreatePermissionOutput> create( @RequestBody @Valid CreatePermissionInput permission) {
		CreatePermissionOutput output=_permissionAppService.create(permission);
		return new ResponseEntity<>(output, HttpStatus.OK);
	}

	// ------------ Delete permission ------------
	@PreAuthorize("hasAnyAuthority('PERMISSIONENTITY_DELETE')")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, consumes = {"application/json"})
	public void delete(@PathVariable String id) {

    	FindPermissionByIdOutput output = _permissionAppService.findById(Long.valueOf(id));
    	if(output == null) {
    		throw new EntityNotFoundException(String.format("There does not exist a permission with a id=%s", id));
    	}	

    	_permissionAppService.delete(Long.valueOf(id));
    }


	// ------------ Update permission ------------
    @PreAuthorize("hasAnyAuthority('PERMISSIONENTITY_UPDATE')")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<UpdatePermissionOutput> update(@PathVariable String id,  @RequestBody @Valid UpdatePermissionInput permission) {

	    FindPermissionByIdOutput currentPermission = _permissionAppService.findById(Long.valueOf(id));
		if(currentPermission == null) {
			throw new EntityNotFoundException(String.format("Unable to update. Permission with id=%s not found.", id));
		}

		permission.setVersiono(currentPermission.getVersiono());
	    UpdatePermissionOutput output = _permissionAppService.update(Long.valueOf(id),permission);
		return new ResponseEntity<>(output, HttpStatus.OK);
	}
    

    @PreAuthorize("hasAnyAuthority('PERMISSIONENTITY_READ')")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<FindPermissionByIdOutput> findById(@PathVariable String id) {

    	FindPermissionByIdOutput output = _permissionAppService.findById(Long.valueOf(id));
        if(output == null) {
    		throw new EntityNotFoundException("Not found");
    	}
    	
		return new ResponseEntity<>(output, HttpStatus.OK);
	}
    @PreAuthorize("hasAnyAuthority('PERMISSIONENTITY_READ')")
	@RequestMapping(method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<List<FindPermissionByIdOutput>> find(@RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort) throws EntityNotFoundException, MalformedURLException {

		if (offset == null) { offset = env.getProperty("blog.offset.default"); }
		if (limit == null) { limit = env.getProperty("blog.limit.default"); }

		Pageable Pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);
		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);

		return new ResponseEntity<>(_permissionAppService.find(searchCriteria,Pageable), HttpStatus.OK);
	}
	
    @PreAuthorize("hasAnyAuthority('PERMISSIONENTITY_READ')")
	@RequestMapping(value = "/{id}/rolepermissions", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<List<FindRolepermissionByIdOutput>> getRolepermissions(@PathVariable String id, @RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort) throws EntityNotFoundException, MalformedURLException {
   		if (offset == null) { offset = env.getProperty("blog.offset.default"); }
		if (limit == null) { limit = env.getProperty("blog.limit.default"); }

		Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);

		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);
		Map<String,String> joinColDetails=_permissionAppService.parseRolepermissionsJoinColumn(id);
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
    @PreAuthorize("hasAnyAuthority('PERMISSIONENTITY_READ')")
	@RequestMapping(value = "/{id}/userspermissions", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<List<FindUserspermissionByIdOutput>> getUserspermissions(@PathVariable String id, @RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort) throws EntityNotFoundException, MalformedURLException {
   		if (offset == null) { offset = env.getProperty("blog.offset.default"); }
		if (limit == null) { limit = env.getProperty("blog.limit.default"); }

		Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);

		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);
		Map<String,String> joinColDetails=_permissionAppService.parseUserspermissionsJoinColumn(id);
		if(joinColDetails == null) {
			throw new EntityNotFoundException("Invalid join column");
		}

		searchCriteria.setJoinColumns(joinColDetails);

    	List<FindUserspermissionByIdOutput> output = _userspermissionAppService.find(searchCriteria,pageable);
    	
    	if(output == null) {
			throw new EntityNotFoundException("Not found");
		}
		
		return new ResponseEntity<>(output, HttpStatus.OK);
	}
}


