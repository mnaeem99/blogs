package com.naeem.blogs.application.core.posts;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import com.naeem.blogs.domain.core.authorization.users.Users;
import com.naeem.blogs.application.core.posts.dto.*;
import com.naeem.blogs.domain.core.posts.Posts;
import java.time.*;

@Mapper(componentModel = "spring")
public interface IPostsMapper {
   Posts createPostsInputToPosts(CreatePostsInput postsDto);
   
   @Mappings({ 
   @Mapping(source = "entity.users.userId", target = "authorId"),                   
   @Mapping(source = "entity.users.username", target = "usersDescriptiveField"),                    
   }) 
   CreatePostsOutput postsToCreatePostsOutput(Posts entity);
   
    Posts updatePostsInputToPosts(UpdatePostsInput postsDto);
    
    @Mappings({ 
    @Mapping(source = "entity.users.userId", target = "authorId"),                   
    @Mapping(source = "entity.users.username", target = "usersDescriptiveField"),                    
   	}) 
   	UpdatePostsOutput postsToUpdatePostsOutput(Posts entity);
   	@Mappings({ 
   	@Mapping(source = "entity.users.userId", target = "authorId"),                   
   	@Mapping(source = "entity.users.username", target = "usersDescriptiveField"),                    
   	}) 
   	FindPostsByIdOutput postsToFindPostsByIdOutput(Posts entity);


   @Mappings({
   @Mapping(source = "foundPosts.postId", target = "postsPostId"),
   })
   GetUsersOutput usersToGetUsersOutput(Users users, Posts foundPosts);
   
}

