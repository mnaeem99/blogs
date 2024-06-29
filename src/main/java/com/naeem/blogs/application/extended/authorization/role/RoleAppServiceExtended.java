package com.naeem.blogs.application.extended.authorization.role;

import org.springframework.stereotype.Service;
import com.naeem.blogs.application.core.authorization.role.RoleAppService;

import com.naeem.blogs.domain.extended.authorization.role.IRoleRepositoryExtended;
import com.naeem.blogs.commons.logging.LoggingHelper;

@Service("roleAppServiceExtended")
public class RoleAppServiceExtended extends RoleAppService implements IRoleAppServiceExtended {

	public RoleAppServiceExtended(IRoleRepositoryExtended roleRepositoryExtended,
				IRoleMapperExtended mapper,LoggingHelper logHelper) {

		super(roleRepositoryExtended,
		mapper,logHelper);

	}

 	//Add your custom code here
 
}

