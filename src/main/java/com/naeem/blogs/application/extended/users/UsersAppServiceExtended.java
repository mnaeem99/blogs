package com.naeem.blogs.application.extended.users;

import org.springframework.stereotype.Service;
import com.naeem.blogs.application.core.users.UsersAppService;

import com.naeem.blogs.domain.extended.users.IUsersRepositoryExtended;
import com.naeem.blogs.commons.logging.LoggingHelper;

@Service("usersAppServiceExtended")
public class UsersAppServiceExtended extends UsersAppService implements IUsersAppServiceExtended {

	public UsersAppServiceExtended(IUsersRepositoryExtended usersRepositoryExtended,
				IUsersMapperExtended mapper,LoggingHelper logHelper) {

		super(usersRepositoryExtended,
		mapper,logHelper);

	}

 	//Add your custom code here
 
}

