package com.naeem.blogs.application.extended.categories;

import org.springframework.stereotype.Service;
import com.naeem.blogs.application.core.categories.CategoriesAppService;

import com.naeem.blogs.domain.extended.categories.ICategoriesRepositoryExtended;
import com.naeem.blogs.commons.logging.LoggingHelper;

@Service("categoriesAppServiceExtended")
public class CategoriesAppServiceExtended extends CategoriesAppService implements ICategoriesAppServiceExtended {

	public CategoriesAppServiceExtended(ICategoriesRepositoryExtended categoriesRepositoryExtended,
				ICategoriesMapperExtended mapper,LoggingHelper logHelper) {

		super(categoriesRepositoryExtended,
		mapper,logHelper);

	}

 	//Add your custom code here
 
}

