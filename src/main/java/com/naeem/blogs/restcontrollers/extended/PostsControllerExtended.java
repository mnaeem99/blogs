package com.naeem.blogs.restcontrollers.extended;

import org.springframework.web.bind.annotation.*;
import com.naeem.blogs.restcontrollers.core.PostsController;
import com.naeem.blogs.application.extended.posts.IPostsAppServiceExtended;

import com.naeem.blogs.application.extended.comments.ICommentsAppServiceExtended;
import com.naeem.blogs.application.extended.likes.ILikesAppServiceExtended;
import com.naeem.blogs.application.extended.postcategories.IPostCategoriesAppServiceExtended;
import com.naeem.blogs.application.extended.posttags.IPostTagsAppServiceExtended;
import com.naeem.blogs.application.extended.authorization.users.IUsersAppServiceExtended;
import org.springframework.core.env.Environment;
import com.naeem.blogs.commons.logging.LoggingHelper;

@RestController
@RequestMapping("/posts/extended")
public class PostsControllerExtended extends PostsController {

		public PostsControllerExtended(IPostsAppServiceExtended postsAppServiceExtended, ICommentsAppServiceExtended commentsAppServiceExtended, ILikesAppServiceExtended likesAppServiceExtended, IPostCategoriesAppServiceExtended postCategoriesAppServiceExtended, IPostTagsAppServiceExtended postTagsAppServiceExtended, IUsersAppServiceExtended usersAppServiceExtended,
	     LoggingHelper helper, Environment env) {
		super(
		postsAppServiceExtended,
		
    	commentsAppServiceExtended,
		
    	likesAppServiceExtended,
		
    	postCategoriesAppServiceExtended,
		
    	postTagsAppServiceExtended,
		
    	usersAppServiceExtended,
		helper, env);
	}

	//Add your custom code here

}

