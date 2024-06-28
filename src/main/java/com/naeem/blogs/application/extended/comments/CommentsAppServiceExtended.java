package com.naeem.blogs.application.extended.comments;

import org.springframework.stereotype.Service;
import com.naeem.blogs.application.core.comments.CommentsAppService;

import com.naeem.blogs.domain.extended.comments.ICommentsRepositoryExtended;
import com.naeem.blogs.domain.extended.posts.IPostsRepositoryExtended;
import com.naeem.blogs.domain.extended.users.IUsersRepositoryExtended;
import com.naeem.blogs.commons.logging.LoggingHelper;

@Service("commentsAppServiceExtended")
public class CommentsAppServiceExtended extends CommentsAppService implements ICommentsAppServiceExtended {

	public CommentsAppServiceExtended(ICommentsRepositoryExtended commentsRepositoryExtended,
				IPostsRepositoryExtended postsRepositoryExtended,IUsersRepositoryExtended usersRepositoryExtended,ICommentsMapperExtended mapper,LoggingHelper logHelper) {

		super(commentsRepositoryExtended,
		postsRepositoryExtended,usersRepositoryExtended,mapper,logHelper);

	}

 	//Add your custom code here
 
}

