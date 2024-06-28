package com.naeem.blogs.application.extended.posttags;

import org.springframework.stereotype.Service;
import com.naeem.blogs.application.core.posttags.PostTagsAppService;

import com.naeem.blogs.domain.extended.posttags.IPostTagsRepositoryExtended;
import com.naeem.blogs.domain.extended.posts.IPostsRepositoryExtended;
import com.naeem.blogs.domain.extended.tags.ITagsRepositoryExtended;
import com.naeem.blogs.commons.logging.LoggingHelper;

@Service("postTagsAppServiceExtended")
public class PostTagsAppServiceExtended extends PostTagsAppService implements IPostTagsAppServiceExtended {

	public PostTagsAppServiceExtended(IPostTagsRepositoryExtended postTagsRepositoryExtended,
				IPostsRepositoryExtended postsRepositoryExtended,ITagsRepositoryExtended tagsRepositoryExtended,IPostTagsMapperExtended mapper,LoggingHelper logHelper) {

		super(postTagsRepositoryExtended,
		postsRepositoryExtended,tagsRepositoryExtended,mapper,logHelper);

	}

 	//Add your custom code here
 
}

