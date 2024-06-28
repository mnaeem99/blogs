package com.naeem.blogs.restcontrollers.extended;

import org.springframework.web.bind.annotation.*;
import com.naeem.blogs.restcontrollers.core.PostCategoriesController;
import com.naeem.blogs.application.extended.postcategories.IPostCategoriesAppServiceExtended;

import com.naeem.blogs.application.extended.categories.ICategoriesAppServiceExtended;
import com.naeem.blogs.application.extended.posts.IPostsAppServiceExtended;
import org.springframework.core.env.Environment;
import com.naeem.blogs.commons.logging.LoggingHelper;

@RestController
@RequestMapping("/postCategories/extended")
public class PostCategoriesControllerExtended extends PostCategoriesController {

		public PostCategoriesControllerExtended(IPostCategoriesAppServiceExtended postCategoriesAppServiceExtended, ICategoriesAppServiceExtended categoriesAppServiceExtended, IPostsAppServiceExtended postsAppServiceExtended,
	     LoggingHelper helper, Environment env) {
		super(
		postCategoriesAppServiceExtended,
		
    	categoriesAppServiceExtended,
		
    	postsAppServiceExtended,
		helper, env);
	}

	//Add your custom code here

}

