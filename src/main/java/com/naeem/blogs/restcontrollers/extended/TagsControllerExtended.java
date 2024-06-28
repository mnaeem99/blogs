package com.naeem.blogs.restcontrollers.extended;

import org.springframework.web.bind.annotation.*;
import com.naeem.blogs.restcontrollers.core.TagsController;
import com.naeem.blogs.application.extended.tags.ITagsAppServiceExtended;

import com.naeem.blogs.application.extended.posttags.IPostTagsAppServiceExtended;
import org.springframework.core.env.Environment;
import com.naeem.blogs.commons.logging.LoggingHelper;

@RestController
@RequestMapping("/tags/extended")
public class TagsControllerExtended extends TagsController {

		public TagsControllerExtended(ITagsAppServiceExtended tagsAppServiceExtended, IPostTagsAppServiceExtended postTagsAppServiceExtended,
	     LoggingHelper helper, Environment env) {
		super(
		tagsAppServiceExtended,
		
    	postTagsAppServiceExtended,
		helper, env);
	}

	//Add your custom code here

}

