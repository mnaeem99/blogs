package com.naeem.blogs.restcontrollers.extended;

import org.springframework.web.bind.annotation.*;
import com.naeem.blogs.restcontrollers.core.PostTagsController;
import com.naeem.blogs.application.extended.posttags.IPostTagsAppServiceExtended;

import com.naeem.blogs.application.extended.posts.IPostsAppServiceExtended;
import com.naeem.blogs.application.extended.tags.ITagsAppServiceExtended;
import org.springframework.core.env.Environment;
import com.naeem.blogs.commons.logging.LoggingHelper;

@RestController
@RequestMapping("/postTags/extended")
public class PostTagsControllerExtended extends PostTagsController {

		public PostTagsControllerExtended(IPostTagsAppServiceExtended postTagsAppServiceExtended, IPostsAppServiceExtended postsAppServiceExtended, ITagsAppServiceExtended tagsAppServiceExtended,
	     LoggingHelper helper, Environment env) {
		super(
		postTagsAppServiceExtended,
		
    	postsAppServiceExtended,
		
    	tagsAppServiceExtended,
		helper, env);
	}

	//Add your custom code here

}

