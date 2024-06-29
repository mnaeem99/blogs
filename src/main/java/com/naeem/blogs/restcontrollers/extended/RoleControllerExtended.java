package com.naeem.blogs.restcontrollers.extended;

import org.springframework.web.bind.annotation.*;
import com.naeem.blogs.restcontrollers.core.RoleController;
import com.naeem.blogs.application.extended.authorization.role.IRoleAppServiceExtended;

import com.naeem.blogs.application.extended.authorization.rolepermission.IRolepermissionAppServiceExtended;
import com.naeem.blogs.application.extended.authorization.usersrole.IUsersroleAppServiceExtended;
import org.springframework.core.env.Environment;
import com.naeem.blogs.commons.logging.LoggingHelper;

@RestController
@RequestMapping("/role/extended")
public class RoleControllerExtended extends RoleController {

		public RoleControllerExtended(IRoleAppServiceExtended roleAppServiceExtended, IRolepermissionAppServiceExtended rolepermissionAppServiceExtended, IUsersroleAppServiceExtended usersroleAppServiceExtended,
	     LoggingHelper helper, Environment env) {
		super(
		roleAppServiceExtended,
		
    	rolepermissionAppServiceExtended,
		
    	usersroleAppServiceExtended,
		helper, env);
	}

	//Add your custom code here

}

