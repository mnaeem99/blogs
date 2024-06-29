package com.naeem.blogs.application.extended.authorization.users;

import org.springframework.stereotype.Service;
import com.naeem.blogs.application.core.authorization.users.UsersAppService;

import com.naeem.blogs.domain.extended.authorization.users.IUsersRepositoryExtended;
import com.naeem.blogs.domain.core.authorization.userspreference.IUserspreferenceRepository;
import com.naeem.blogs.commons.logging.LoggingHelper;

@Service("usersAppServiceExtended")
public class UsersAppServiceExtended extends UsersAppService implements IUsersAppServiceExtended {

	public UsersAppServiceExtended(IUsersRepositoryExtended usersRepositoryExtended,
				IUserspreferenceRepository userspreferenceRepository,IUsersMapperExtended mapper,LoggingHelper logHelper) {

		super(usersRepositoryExtended,
		userspreferenceRepository,mapper,logHelper);

	}

 	//Add your custom code here
 
}

