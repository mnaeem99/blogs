package com.naeem.blogs.restcontrollers.core;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import javax.persistence.EntityExistsException;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import com.naeem.blogs.commons.search.SearchCriteria;
import com.naeem.blogs.commons.search.SearchUtils;
import com.naeem.blogs.commons.search.OffsetBasedPageRequest;
import com.naeem.blogs.application.core.authorization.users.IUsersAppService;
import com.naeem.blogs.application.core.authorization.users.dto.*;
import com.naeem.blogs.application.core.comments.ICommentsAppService;
import com.naeem.blogs.application.core.comments.dto.FindCommentsByIdOutput;
import com.naeem.blogs.application.core.likes.ILikesAppService;
import com.naeem.blogs.application.core.likes.dto.FindLikesByIdOutput;
import com.naeem.blogs.application.core.posts.IPostsAppService;
import com.naeem.blogs.application.core.posts.dto.FindPostsByIdOutput;
import com.naeem.blogs.application.core.authorization.userspermission.IUserspermissionAppService;
import com.naeem.blogs.application.core.authorization.userspermission.dto.FindUserspermissionByIdOutput;
import com.naeem.blogs.application.core.authorization.usersrole.IUsersroleAppService;
import com.naeem.blogs.application.core.authorization.usersrole.dto.FindUsersroleByIdOutput;
import com.naeem.blogs.domain.core.authorization.users.Users;
import com.naeem.blogs.application.core.authorization.users.dto.FindUsersByIdOutput;
import com.naeem.blogs.security.JWTAppService;
import java.util.*;
import java.time.*;
import java.net.MalformedURLException;
import com.naeem.blogs.commons.logging.LoggingHelper;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

	@Qualifier("usersAppService")
	@NonNull protected final IUsersAppService _usersAppService;
    @Qualifier("commentsAppService")
	@NonNull  protected final ICommentsAppService  _commentsAppService;

    @Qualifier("likesAppService")
	@NonNull  protected final ILikesAppService  _likesAppService;

    @Qualifier("postsAppService")
	@NonNull  protected final IPostsAppService  _postsAppService;

    @Qualifier("userspermissionAppService")
	@NonNull  protected final IUserspermissionAppService  _userspermissionAppService;

    @Qualifier("usersroleAppService")
	@NonNull  protected final IUsersroleAppService  _usersroleAppService;

	@NonNull protected final PasswordEncoder pEncoder;

	@NonNull protected final JWTAppService _jwtAppService;

	@NonNull protected final LoggingHelper logHelper;

	@NonNull protected final Environment env;

    @PreAuthorize("hasAnyAuthority('USERSENTITY_CREATE')")
	@RequestMapping(method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<CreateUsersOutput> create( @RequestBody @Valid CreateUsersInput users) {
		FindUsersByUsernameOutput foundUsers = _usersAppService.findByUsername(users.getUsername());

	        if (foundUsers != null) {
	            logHelper.getLogger().error("There already exists a users with a Username='{}'", users.getUsername());
	            throw new EntityExistsException(
	                    String.format("There already exists a users with Username =%s", users.getUsername()));
	        }
	    users.setIsEmailConfirmed(true);
	    users.setPassword(pEncoder.encode(users.getPassword()));
	    foundUsers = _usersAppService.findByEmailAddress(users.getEmailAddress());

	        if (foundUsers != null) {
	            logHelper.getLogger().error("There already exists a users with a email ='{}'", users.getEmailAddress());
	            throw new EntityExistsException(
	                    String.format("There already exists a users with email =%s", users.getEmailAddress()));
	        }

		CreateUsersOutput output=_usersAppService.create(users);
		return new ResponseEntity<>(output, HttpStatus.OK);
	}

	// ------------ Delete users ------------
	@PreAuthorize("hasAnyAuthority('USERSENTITY_DELETE')")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, consumes = {"application/json"})
	public void delete(@PathVariable String id) {

    	FindUsersByIdOutput output = _usersAppService.findById(Long.valueOf(id));
    	if(output == null) {
    		throw new EntityNotFoundException(String.format("There does not exist a users with a id=%s", id));
    	}	

    	_usersAppService.delete(Long.valueOf(id));
    }

	@RequestMapping(value = "/updateProfile", method = RequestMethod.PUT, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<UsersProfile> updateProfile(@RequestBody @Valid UsersProfile usersProfile) {

		Users users = _usersAppService.getUsers();

        FindUsersByUsernameOutput usersOutput;
		usersOutput = _usersAppService.findByUsername(users.getUsername());
		if(usersOutput != null &&
		usersOutput.getUserId() != users.getUserId() )
		{
			logHelper.getLogger().error("There already exists a users with user name='{}'", users.getUsername());
			throw new EntityExistsException(
					String.format("There already exists a users with user name=%s", users.getUsername()));
		}
		usersOutput = _usersAppService.findByEmailAddress(users.getEmailAddress());
		if(usersOutput != null &&
		usersOutput.getUserId() != users.getUserId() )
		{
			logHelper.getLogger().error("There already exists a users with a email='{}'", users.getEmailAddress());
			throw new EntityExistsException(
					String.format("There already exists a users with a email=%s", users.getEmailAddress()));
		}

		FindUsersWithAllFieldsByIdOutput currentUsers =  _usersAppService.findWithAllFieldsById(users.getUserId());
		return new ResponseEntity<>(_usersAppService.updateUsersProfile(currentUsers,usersProfile), HttpStatus.OK);
	}

	// ------------ Update users ------------
    @PreAuthorize("hasAnyAuthority('USERSENTITY_UPDATE')")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<UpdateUsersOutput> update(@PathVariable String id,  @RequestBody @Valid UpdateUsersInput users) {

	    FindUsersWithAllFieldsByIdOutput currentUsers = _usersAppService.findWithAllFieldsById(Long.valueOf(id));
		if(currentUsers == null) {
			throw new EntityNotFoundException(String.format("Unable to update. Users with id=%s not found.", id));
		}
	    users.setPassword(pEncoder.encode(currentUsers.getPassword()));
	    if(currentUsers.getIsActive() && !users.getIsActive()) {
           _jwtAppService.deleteAllUserTokens(currentUsers.getUsername());
        }

		users.setVersiono(currentUsers.getVersiono());
	    UpdateUsersOutput output = _usersAppService.update(Long.valueOf(id),users);
		return new ResponseEntity<>(output, HttpStatus.OK);
	}
    
	@RequestMapping(value = "/getProfile",method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<UsersProfile> getProfile() {

		Users users = _usersAppService.getUsers();

		FindUsersByIdOutput currentUsers = _usersAppService.findById(users.getUserId());
		return new ResponseEntity<>(_usersAppService.getProfile(currentUsers), HttpStatus.OK);
	}

    @RequestMapping(value = "/updateTheme", method = RequestMethod.PUT, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<HashMap<String,String>> updateTheme(@RequestParam @Valid String theme) {

		Users users = _usersAppService.getUsers();
		_usersAppService.updateTheme(users, theme);

		String msg = "Theme updated successfully !";
		HashMap resultMap = new HashMap<String,String>();
		resultMap.put("message", msg);
		return new ResponseEntity<>(resultMap, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateLanguage", method = RequestMethod.PUT, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<HashMap<String,String>> updateLanguage(@RequestParam @Valid String language) {

		Users users = _usersAppService.getUsers();
		_usersAppService.updateLanguage(users, language);

		String msg = "Language updated successfully !";
		HashMap resultMap = new HashMap<String,String>();
		resultMap.put("message", msg);
		return new ResponseEntity<>(resultMap, HttpStatus.OK);
	}

    @PreAuthorize("hasAnyAuthority('USERSENTITY_READ')")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<FindUsersByIdOutput> findById(@PathVariable String id) {

    	FindUsersByIdOutput output = _usersAppService.findById(Long.valueOf(id));
        if(output == null) {
    		throw new EntityNotFoundException("Not found");
    	}
    	
		return new ResponseEntity<>(output, HttpStatus.OK);
	}
    @PreAuthorize("hasAnyAuthority('USERSENTITY_READ')")
	@RequestMapping(method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<List<FindUsersByIdOutput>> find(@RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort) throws EntityNotFoundException, MalformedURLException {

		if (offset == null) { offset = env.getProperty("blog.offset.default"); }
		if (limit == null) { limit = env.getProperty("blog.limit.default"); }

		Pageable Pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);
		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);

		return new ResponseEntity<>(_usersAppService.find(searchCriteria,Pageable), HttpStatus.OK);
	}
	
    @PreAuthorize("hasAnyAuthority('USERSENTITY_READ')")
	@RequestMapping(value = "/{id}/comments", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<List<FindCommentsByIdOutput>> getComments(@PathVariable String id, @RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort) throws EntityNotFoundException, MalformedURLException {
   		if (offset == null) { offset = env.getProperty("blog.offset.default"); }
		if (limit == null) { limit = env.getProperty("blog.limit.default"); }

		Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);

		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);
		Map<String,String> joinColDetails=_usersAppService.parseCommentsJoinColumn(id);
		if(joinColDetails == null) {
			throw new EntityNotFoundException("Invalid join column");
		}

		searchCriteria.setJoinColumns(joinColDetails);

    	List<FindCommentsByIdOutput> output = _commentsAppService.find(searchCriteria,pageable);
    	
    	if(output == null) {
			throw new EntityNotFoundException("Not found");
		}
		
		return new ResponseEntity<>(output, HttpStatus.OK);
	}
    @PreAuthorize("hasAnyAuthority('USERSENTITY_READ')")
	@RequestMapping(value = "/{id}/likes", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<List<FindLikesByIdOutput>> getLikes(@PathVariable String id, @RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort) throws EntityNotFoundException, MalformedURLException {
   		if (offset == null) { offset = env.getProperty("blog.offset.default"); }
		if (limit == null) { limit = env.getProperty("blog.limit.default"); }

		Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);

		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);
		Map<String,String> joinColDetails=_usersAppService.parseLikesJoinColumn(id);
		if(joinColDetails == null) {
			throw new EntityNotFoundException("Invalid join column");
		}

		searchCriteria.setJoinColumns(joinColDetails);

    	List<FindLikesByIdOutput> output = _likesAppService.find(searchCriteria,pageable);
    	
    	if(output == null) {
			throw new EntityNotFoundException("Not found");
		}
		
		return new ResponseEntity<>(output, HttpStatus.OK);
	}
    @PreAuthorize("hasAnyAuthority('USERSENTITY_READ')")
	@RequestMapping(value = "/{id}/posts", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<List<FindPostsByIdOutput>> getPosts(@PathVariable String id, @RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort) throws EntityNotFoundException, MalformedURLException {
   		if (offset == null) { offset = env.getProperty("blog.offset.default"); }
		if (limit == null) { limit = env.getProperty("blog.limit.default"); }

		Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);

		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);
		Map<String,String> joinColDetails=_usersAppService.parsePostsJoinColumn(id);
		if(joinColDetails == null) {
			throw new EntityNotFoundException("Invalid join column");
		}

		searchCriteria.setJoinColumns(joinColDetails);

    	List<FindPostsByIdOutput> output = _postsAppService.find(searchCriteria,pageable);
    	
    	if(output == null) {
			throw new EntityNotFoundException("Not found");
		}
		
		return new ResponseEntity<>(output, HttpStatus.OK);
	}
    @PreAuthorize("hasAnyAuthority('USERSENTITY_READ')")
	@RequestMapping(value = "/{id}/userspermissions", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<List<FindUserspermissionByIdOutput>> getUserspermissions(@PathVariable String id, @RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort) throws EntityNotFoundException, MalformedURLException {
   		if (offset == null) { offset = env.getProperty("blog.offset.default"); }
		if (limit == null) { limit = env.getProperty("blog.limit.default"); }

		Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);

		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);
		Map<String,String> joinColDetails=_usersAppService.parseUserspermissionsJoinColumn(id);
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
    @PreAuthorize("hasAnyAuthority('USERSENTITY_READ')")
	@RequestMapping(value = "/{id}/usersroles", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<List<FindUsersroleByIdOutput>> getUsersroles(@PathVariable String id, @RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort) throws EntityNotFoundException, MalformedURLException {
   		if (offset == null) { offset = env.getProperty("blog.offset.default"); }
		if (limit == null) { limit = env.getProperty("blog.limit.default"); }

		Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);

		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);
		Map<String,String> joinColDetails=_usersAppService.parseUsersrolesJoinColumn(id);
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


