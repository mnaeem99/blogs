package com.naeem.blogs.application.extended.likes;

import org.springframework.stereotype.Service;
import com.naeem.blogs.application.core.likes.LikesAppService;

import com.naeem.blogs.domain.extended.likes.ILikesRepositoryExtended;
import com.naeem.blogs.domain.extended.posts.IPostsRepositoryExtended;
import com.naeem.blogs.domain.extended.users.IUsersRepositoryExtended;
import com.naeem.blogs.commons.logging.LoggingHelper;

@Service("likesAppServiceExtended")
public class LikesAppServiceExtended extends LikesAppService implements ILikesAppServiceExtended {

	public LikesAppServiceExtended(ILikesRepositoryExtended likesRepositoryExtended,
				IPostsRepositoryExtended postsRepositoryExtended,IUsersRepositoryExtended usersRepositoryExtended,ILikesMapperExtended mapper,LoggingHelper logHelper) {

		super(likesRepositoryExtended,
		postsRepositoryExtended,usersRepositoryExtended,mapper,logHelper);

	}

 	//Add your custom code here
 
}

