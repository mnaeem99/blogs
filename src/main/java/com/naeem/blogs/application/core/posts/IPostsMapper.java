package com.naeem.blogs.application.core.posts;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import com.naeem.blogs.domain.core.users.Users;
import com.naeem.blogs.application.core.posts.dto.*;
import com.naeem.blogs.domain.core.posts.Posts;
import java.time.*;

@Mapper(componentModel = "spring")
public interface IPostsMapper {
   Posts createPostsInputToPosts(CreatePostsInput postsDto);
   
   @Mappings({ 
   @Mapping(source = "entity.users.userId", target = "authorId"),                   
   @Mapping(source = "entity.users.email", target = "usersDescriptiveField"),                    
   }) 
   CreatePostsOutput postsToCreatePostsOutput(Posts entity);
   
    Posts updatePostsInputToPosts(UpdatePostsInput postsDto);
    
    @Mappings({ 
    @Mapping(source = "entity.users.userId", target = "authorId"),                   
    @Mapping(source = "entity.users.email", target = "usersDescriptiveField"),                    
   	}) 
   	UpdatePostsOutput postsToUpdatePostsOutput(Posts entity);
   	@Mappings({ 
   	@Mapping(source = "entity.users.userId", target = "authorId"),                   
   	@Mapping(source = "entity.users.email", target = "usersDescriptiveField"),                    
   	}) 
   	FindPostsByIdOutput postsToFindPostsByIdOutput(Posts entity);


   @Mappings({
   @Mapping(source = "users.createdAt", target = "createdAt"),                  
   @Mapping(source = "users.updatedAt", target = "updatedAt"),                  
   @Mapping(source = "foundPosts.postId", target = "postsPostId"),
   })
   GetUsersOutput usersToGetUsersOutput(Users users, Posts foundPosts);
   
}

