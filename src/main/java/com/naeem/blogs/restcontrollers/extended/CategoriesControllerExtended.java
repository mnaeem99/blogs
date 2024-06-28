package com.naeem.blogs.restcontrollers.extended;

import org.springframework.web.bind.annotation.*;
import com.naeem.blogs.restcontrollers.core.CategoriesController;
import com.naeem.blogs.application.extended.categories.ICategoriesAppServiceExtended;

import com.naeem.blogs.application.extended.postcategories.IPostCategoriesAppServiceExtended;
import org.springframework.core.env.Environment;
import com.naeem.blogs.commons.logging.LoggingHelper;

@RestController
@RequestMapping("/categories/extended")
public class CategoriesControllerExtended extends CategoriesController {

		public CategoriesControllerExtended(ICategoriesAppServiceExtended categoriesAppServiceExtended, IPostCategoriesAppServiceExtended postCategoriesAppServiceExtended,
	     LoggingHelper helper, Environment env) {
		super(
		categoriesAppServiceExtended,
		
    	postCategoriesAppServiceExtended,
		helper, env);
	}

	//Add your custom code here

}

