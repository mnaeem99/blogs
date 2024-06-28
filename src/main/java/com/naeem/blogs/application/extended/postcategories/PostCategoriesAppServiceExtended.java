package com.naeem.blogs.application.extended.postcategories;

import org.springframework.stereotype.Service;
import com.naeem.blogs.application.core.postcategories.PostCategoriesAppService;

import com.naeem.blogs.domain.extended.postcategories.IPostCategoriesRepositoryExtended;
import com.naeem.blogs.domain.extended.categories.ICategoriesRepositoryExtended;
import com.naeem.blogs.domain.extended.posts.IPostsRepositoryExtended;
import com.naeem.blogs.commons.logging.LoggingHelper;

@Service("postCategoriesAppServiceExtended")
public class PostCategoriesAppServiceExtended extends PostCategoriesAppService implements IPostCategoriesAppServiceExtended {

	public PostCategoriesAppServiceExtended(IPostCategoriesRepositoryExtended postCategoriesRepositoryExtended,
				ICategoriesRepositoryExtended categoriesRepositoryExtended,IPostsRepositoryExtended postsRepositoryExtended,IPostCategoriesMapperExtended mapper,LoggingHelper logHelper) {

		super(postCategoriesRepositoryExtended,
		categoriesRepositoryExtended,postsRepositoryExtended,mapper,logHelper);

	}

 	//Add your custom code here
 
}

