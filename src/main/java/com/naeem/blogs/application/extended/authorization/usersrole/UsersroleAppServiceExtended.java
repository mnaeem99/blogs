package com.naeem.blogs.application.extended.authorization.usersrole;

import org.springframework.stereotype.Service;
import com.naeem.blogs.application.core.authorization.usersrole.UsersroleAppService;

import com.naeem.blogs.domain.extended.authorization.usersrole.IUsersroleRepositoryExtended;
import com.naeem.blogs.domain.extended.authorization.role.IRoleRepositoryExtended;
import com.naeem.blogs.domain.extended.authorization.users.IUsersRepositoryExtended;
import com.naeem.blogs.commons.logging.LoggingHelper;

@Service("usersroleAppServiceExtended")
public class UsersroleAppServiceExtended extends UsersroleAppService implements IUsersroleAppServiceExtended {

	public UsersroleAppServiceExtended(IUsersroleRepositoryExtended usersroleRepositoryExtended,
				IRoleRepositoryExtended roleRepositoryExtended,IUsersRepositoryExtended usersRepositoryExtended,IUsersroleMapperExtended mapper,LoggingHelper logHelper) {

		super(usersroleRepositoryExtended,
		roleRepositoryExtended,usersRepositoryExtended,mapper,logHelper);

	}

 	//Add your custom code here
 
}

