package com.naeem.blogs.restcontrollers.extended;

import org.springframework.web.bind.annotation.*;
import com.naeem.blogs.restcontrollers.core.RolepermissionController;
import com.naeem.blogs.application.extended.authorization.rolepermission.IRolepermissionAppServiceExtended;

import com.naeem.blogs.application.extended.authorization.permission.IPermissionAppServiceExtended;
import com.naeem.blogs.application.extended.authorization.role.IRoleAppServiceExtended;
import org.springframework.core.env.Environment;
import com.naeem.blogs.commons.logging.LoggingHelper;

@RestController
@RequestMapping("/rolepermission/extended")
public class RolepermissionControllerExtended extends RolepermissionController {

		public RolepermissionControllerExtended(IRolepermissionAppServiceExtended rolepermissionAppServiceExtended, IPermissionAppServiceExtended permissionAppServiceExtended, IRoleAppServiceExtended roleAppServiceExtended,
	     LoggingHelper helper, Environment env) {
		super(
		rolepermissionAppServiceExtended,
		
    	permissionAppServiceExtended,
		
    	roleAppServiceExtended,
		helper, env);
	}

	//Add your custom code here

}

