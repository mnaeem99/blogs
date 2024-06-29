package com.naeem.blogs.application.extended.authorization.permission;

import org.springframework.stereotype.Service;
import com.naeem.blogs.application.core.authorization.permission.PermissionAppService;

import com.naeem.blogs.domain.extended.authorization.permission.IPermissionRepositoryExtended;
import com.naeem.blogs.commons.logging.LoggingHelper;

@Service("permissionAppServiceExtended")
public class PermissionAppServiceExtended extends PermissionAppService implements IPermissionAppServiceExtended {

	public PermissionAppServiceExtended(IPermissionRepositoryExtended permissionRepositoryExtended,
				IPermissionMapperExtended mapper,LoggingHelper logHelper) {

		super(permissionRepositoryExtended,
		mapper,logHelper);

	}

 	//Add your custom code here
 
}

