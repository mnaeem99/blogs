package com.naeem.blogs.application.extended.posts;

import org.springframework.stereotype.Service;
import com.naeem.blogs.application.core.posts.PostsAppService;

import com.naeem.blogs.domain.extended.posts.IPostsRepositoryExtended;
import com.naeem.blogs.domain.extended.users.IUsersRepositoryExtended;
import com.naeem.blogs.commons.logging.LoggingHelper;

@Service("postsAppServiceExtended")
public class PostsAppServiceExtended extends PostsAppService implements IPostsAppServiceExtended {

	public PostsAppServiceExtended(IPostsRepositoryExtended postsRepositoryExtended,
				IUsersRepositoryExtended usersRepositoryExtended,IPostsMapperExtended mapper,LoggingHelper logHelper) {

		super(postsRepositoryExtended,
		usersRepositoryExtended,mapper,logHelper);

	}

 	//Add your custom code here
 
}

