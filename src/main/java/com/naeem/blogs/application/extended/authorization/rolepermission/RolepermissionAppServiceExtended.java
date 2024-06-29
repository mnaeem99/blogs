package com.naeem.blogs.application.extended.authorization.rolepermission;

import org.springframework.stereotype.Service;
import com.naeem.blogs.application.core.authorization.rolepermission.RolepermissionAppService;

import com.naeem.blogs.domain.extended.authorization.rolepermission.IRolepermissionRepositoryExtended;
import com.naeem.blogs.domain.extended.authorization.permission.IPermissionRepositoryExtended;
import com.naeem.blogs.domain.extended.authorization.role.IRoleRepositoryExtended;
import com.naeem.blogs.domain.extended.authorization.usersrole.IUsersroleRepositoryExtended;
import com.naeem.blogs.security.JWTAppService;
import com.naeem.blogs.commons.logging.LoggingHelper;

@Service("rolepermissionAppServiceExtended")
public class RolepermissionAppServiceExtended extends RolepermissionAppService implements IRolepermissionAppServiceExtended {

	public RolepermissionAppServiceExtended(JWTAppService jwtAppService,IUsersroleRepositoryExtended usersroleRepositoryExtended,IRolepermissionRepositoryExtended rolepermissionRepositoryExtended,
				IPermissionRepositoryExtended permissionRepositoryExtended,IRoleRepositoryExtended roleRepositoryExtended,IRolepermissionMapperExtended mapper,LoggingHelper logHelper) {

		super(jwtAppService, usersroleRepositoryExtended,rolepermissionRepositoryExtended,
		permissionRepositoryExtended,roleRepositoryExtended,mapper,logHelper);

	}

 	//Add your custom code here
 
}

