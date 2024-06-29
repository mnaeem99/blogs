package com.naeem.blogs.restcontrollers.extended;

import org.springframework.web.bind.annotation.*;
import com.naeem.blogs.restcontrollers.core.UsersroleController;
import com.naeem.blogs.application.extended.authorization.usersrole.IUsersroleAppServiceExtended;

import com.naeem.blogs.application.extended.authorization.role.IRoleAppServiceExtended;
import com.naeem.blogs.application.extended.authorization.users.IUsersAppServiceExtended;
import com.naeem.blogs.security.JWTAppService;
import org.springframework.core.env.Environment;
import com.naeem.blogs.commons.logging.LoggingHelper;

@RestController
@RequestMapping("/usersrole/extended")
public class UsersroleControllerExtended extends UsersroleController {

		public UsersroleControllerExtended(IUsersroleAppServiceExtended usersroleAppServiceExtended, IRoleAppServiceExtended roleAppServiceExtended, IUsersAppServiceExtended usersAppServiceExtended,
	    JWTAppService jwtAppService, LoggingHelper helper, Environment env) {
		super(
		usersroleAppServiceExtended,
		
    	roleAppServiceExtended,
		
    	usersAppServiceExtended,
	    jwtAppService,
		helper, env);
	}

	//Add your custom code here

}

