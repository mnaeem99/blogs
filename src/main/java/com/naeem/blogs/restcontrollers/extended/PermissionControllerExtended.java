package com.naeem.blogs.restcontrollers.extended;

import org.springframework.web.bind.annotation.*;
import com.naeem.blogs.restcontrollers.core.PermissionController;
import com.naeem.blogs.application.extended.authorization.permission.IPermissionAppServiceExtended;

import com.naeem.blogs.application.extended.authorization.rolepermission.IRolepermissionAppServiceExtended;
import com.naeem.blogs.application.extended.authorization.userspermission.IUserspermissionAppServiceExtended;
import org.springframework.core.env.Environment;
import com.naeem.blogs.commons.logging.LoggingHelper;

@RestController
@RequestMapping("/permission/extended")
public class PermissionControllerExtended extends PermissionController {

		public PermissionControllerExtended(IPermissionAppServiceExtended permissionAppServiceExtended, IRolepermissionAppServiceExtended rolepermissionAppServiceExtended, IUserspermissionAppServiceExtended userspermissionAppServiceExtended,
	     LoggingHelper helper, Environment env) {
		super(
		permissionAppServiceExtended,
		
    	rolepermissionAppServiceExtended,
		
    	userspermissionAppServiceExtended,
		helper, env);
	}

	//Add your custom code here

}

