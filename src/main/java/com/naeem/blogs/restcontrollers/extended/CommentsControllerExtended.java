package com.naeem.blogs.restcontrollers.extended;

import org.springframework.web.bind.annotation.*;
import com.naeem.blogs.restcontrollers.core.CommentsController;
import com.naeem.blogs.application.extended.comments.ICommentsAppServiceExtended;

import com.naeem.blogs.application.extended.posts.IPostsAppServiceExtended;
import com.naeem.blogs.application.extended.users.IUsersAppServiceExtended;
import org.springframework.core.env.Environment;
import com.naeem.blogs.commons.logging.LoggingHelper;

@RestController
@RequestMapping("/comments/extended")
public class CommentsControllerExtended extends CommentsController {

		public CommentsControllerExtended(ICommentsAppServiceExtended commentsAppServiceExtended, IPostsAppServiceExtended postsAppServiceExtended, IUsersAppServiceExtended usersAppServiceExtended,
	     LoggingHelper helper, Environment env) {
		super(
		commentsAppServiceExtended,
		
    	postsAppServiceExtended,
		
    	usersAppServiceExtended,
		helper, env);
	}

	//Add your custom code here

}

