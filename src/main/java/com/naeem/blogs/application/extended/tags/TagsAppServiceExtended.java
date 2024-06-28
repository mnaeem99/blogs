package com.naeem.blogs.application.extended.tags;

import org.springframework.stereotype.Service;
import com.naeem.blogs.application.core.tags.TagsAppService;

import com.naeem.blogs.domain.extended.tags.ITagsRepositoryExtended;
import com.naeem.blogs.commons.logging.LoggingHelper;

@Service("tagsAppServiceExtended")
public class TagsAppServiceExtended extends TagsAppService implements ITagsAppServiceExtended {

	public TagsAppServiceExtended(ITagsRepositoryExtended tagsRepositoryExtended,
				ITagsMapperExtended mapper,LoggingHelper logHelper) {

		super(tagsRepositoryExtended,
		mapper,logHelper);

	}

 	//Add your custom code here
 
}

