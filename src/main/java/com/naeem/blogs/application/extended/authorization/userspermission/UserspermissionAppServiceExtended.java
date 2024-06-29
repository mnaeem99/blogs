package com.naeem.blogs.application.extended.authorization.userspermission;

import org.springframework.stereotype.Service;
import com.naeem.blogs.application.core.authorization.userspermission.UserspermissionAppService;

import com.naeem.blogs.domain.extended.authorization.userspermission.IUserspermissionRepositoryExtended;
import com.naeem.blogs.domain.extended.authorization.permission.IPermissionRepositoryExtended;
import com.naeem.blogs.domain.extended.authorization.users.IUsersRepositoryExtended;
import com.naeem.blogs.commons.logging.LoggingHelper;

@Service("userspermissionAppServiceExtended")
public class UserspermissionAppServiceExtended extends UserspermissionAppService implements IUserspermissionAppServiceExtended {

	public UserspermissionAppServiceExtended(IUserspermissionRepositoryExtended userspermissionRepositoryExtended,
				IPermissionRepositoryExtended permissionRepositoryExtended,IUsersRepositoryExtended usersRepositoryExtended,IUserspermissionMapperExtended mapper,LoggingHelper logHelper) {

		super(userspermissionRepositoryExtended,
		permissionRepositoryExtended,usersRepositoryExtended,mapper,logHelper);

	}

 	//Add your custom code here
 
}

