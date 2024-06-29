package com.naeem.blogs.restcontrollers.extended;

import org.springframework.web.bind.annotation.*;
import com.naeem.blogs.restcontrollers.core.UsersController;
import com.naeem.blogs.application.extended.authorization.users.IUsersAppServiceExtended;

import com.naeem.blogs.application.extended.comments.ICommentsAppServiceExtended;
import com.naeem.blogs.application.extended.likes.ILikesAppServiceExtended;
import com.naeem.blogs.application.extended.posts.IPostsAppServiceExtended;
import com.naeem.blogs.application.extended.authorization.userspermission.IUserspermissionAppServiceExtended;
import com.naeem.blogs.application.extended.authorization.usersrole.IUsersroleAppServiceExtended;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.naeem.blogs.security.JWTAppService;
import org.springframework.core.env.Environment;
import com.naeem.blogs.commons.logging.LoggingHelper;

@RestController
@RequestMapping("/users/extended")
public class UsersControllerExtended extends UsersController {

		public UsersControllerExtended(IUsersAppServiceExtended usersAppServiceExtended, ICommentsAppServiceExtended commentsAppServiceExtended, ILikesAppServiceExtended likesAppServiceExtended, IPostsAppServiceExtended postsAppServiceExtended, IUserspermissionAppServiceExtended userspermissionAppServiceExtended, IUsersroleAppServiceExtended usersroleAppServiceExtended,
	    PasswordEncoder pEncoder,JWTAppService jwtAppService, LoggingHelper helper, Environment env) {
		super(
		usersAppServiceExtended,
		
    	commentsAppServiceExtended,
		
    	likesAppServiceExtended,
		
    	postsAppServiceExtended,
		
    	userspermissionAppServiceExtended,
		
    	usersroleAppServiceExtended,
	    pEncoder,
	    jwtAppService,
		helper, env);
	}

	//Add your custom code here

}

