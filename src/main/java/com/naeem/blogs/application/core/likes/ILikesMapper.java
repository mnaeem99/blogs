package com.naeem.blogs.application.core.likes;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import com.naeem.blogs.domain.core.posts.Posts;
import com.naeem.blogs.domain.core.users.Users;
import com.naeem.blogs.application.core.likes.dto.*;
import com.naeem.blogs.domain.core.likes.Likes;
import java.time.*;

@Mapper(componentModel = "spring")
public interface ILikesMapper {
   Likes createLikesInputToLikes(CreateLikesInput likesDto);
   
   @Mappings({ 
   @Mapping(source = "entity.posts.postId", target = "postId"),                   
   @Mapping(source = "entity.posts.postId", target = "postsDescriptiveField"),                    
   @Mapping(source = "entity.users.userId", target = "userId"),                   
   @Mapping(source = "entity.users.email", target = "usersDescriptiveField"),                    
   }) 
   CreateLikesOutput likesToCreateLikesOutput(Likes entity);
   
    Likes updateLikesInputToLikes(UpdateLikesInput likesDto);
    
    @Mappings({ 
    @Mapping(source = "entity.posts.postId", target = "postId"),                   
    @Mapping(source = "entity.posts.postId", target = "postsDescriptiveField"),                    
    @Mapping(source = "entity.users.userId", target = "userId"),                   
    @Mapping(source = "entity.users.email", target = "usersDescriptiveField"),                    
   	}) 
   	UpdateLikesOutput likesToUpdateLikesOutput(Likes entity);
   	@Mappings({ 
   	@Mapping(source = "entity.posts.postId", target = "postId"),                   
   	@Mapping(source = "entity.posts.postId", target = "postsDescriptiveField"),                    
   	@Mapping(source = "entity.users.userId", target = "userId"),                   
   	@Mapping(source = "entity.users.email", target = "usersDescriptiveField"),                    
   	}) 
   	FindLikesByIdOutput likesToFindLikesByIdOutput(Likes entity);


   @Mappings({
   @Mapping(source = "users.createdAt", target = "createdAt"),                  
   @Mapping(source = "foundLikes.likeId", target = "likesLikeId"),
   })
   GetUsersOutput usersToGetUsersOutput(Users users, Likes foundLikes);
   
   @Mappings({
   @Mapping(source = "posts.createdAt", target = "createdAt"),                  
   @Mapping(source = "foundLikes.likeId", target = "likesLikeId"),
   })
   GetPostsOutput postsToGetPostsOutput(Posts posts, Likes foundLikes);
   
}

