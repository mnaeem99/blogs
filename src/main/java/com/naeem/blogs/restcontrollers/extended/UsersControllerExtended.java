package com.naeem.blogs.restcontrollers.extended;

import org.springframework.web.bind.annotation.*;
import com.naeem.blogs.restcontrollers.core.UsersController;
import com.naeem.blogs.application.extended.users.IUsersAppServiceExtended;

import com.naeem.blogs.application.extended.comments.ICommentsAppServiceExtended;
import com.naeem.blogs.application.extended.likes.ILikesAppServiceExtended;
import com.naeem.blogs.application.extended.posts.IPostsAppServiceExtended;
import org.springframework.core.env.Environment;
import com.naeem.blogs.commons.logging.LoggingHelper;

@RestController
@RequestMapping("/users/extended")
public class UsersControllerExtended extends UsersController {

		public UsersControllerExtended(IUsersAppServiceExtended usersAppServiceExtended, ICommentsAppServiceExtended commentsAppServiceExtended, ILikesAppServiceExtended likesAppServiceExtended, IPostsAppServiceExtended postsAppServiceExtended,
	     LoggingHelper helper, Environment env) {
		super(
		usersAppServiceExtended,
		
    	commentsAppServiceExtended,
		
    	likesAppServiceExtended,
		
    	postsAppServiceExtended,
		helper, env);
	}

	//Add your custom code here

}

