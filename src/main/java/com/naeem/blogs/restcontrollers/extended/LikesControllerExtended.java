package com.naeem.blogs.restcontrollers.extended;

import org.springframework.web.bind.annotation.*;
import com.naeem.blogs.restcontrollers.core.LikesController;
import com.naeem.blogs.application.extended.likes.ILikesAppServiceExtended;

import com.naeem.blogs.application.extended.posts.IPostsAppServiceExtended;
import com.naeem.blogs.application.extended.users.IUsersAppServiceExtended;
import org.springframework.core.env.Environment;
import com.naeem.blogs.commons.logging.LoggingHelper;

@RestController
@RequestMapping("/likes/extended")
public class LikesControllerExtended extends LikesController {

		public LikesControllerExtended(ILikesAppServiceExtended likesAppServiceExtended, IPostsAppServiceExtended postsAppServiceExtended, IUsersAppServiceExtended usersAppServiceExtended,
	     LoggingHelper helper, Environment env) {
		super(
		likesAppServiceExtended,
		
    	postsAppServiceExtended,
		
    	usersAppServiceExtended,
		helper, env);
	}

	//Add your custom code here

}

