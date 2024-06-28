package com.naeem.blogs.application.core.comments;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import com.naeem.blogs.domain.core.posts.Posts;
import com.naeem.blogs.domain.core.users.Users;
import com.naeem.blogs.application.core.comments.dto.*;
import com.naeem.blogs.domain.core.comments.Comments;
import java.time.*;

@Mapper(componentModel = "spring")
public interface ICommentsMapper {
   Comments createCommentsInputToComments(CreateCommentsInput commentsDto);
   
   @Mappings({ 
   @Mapping(source = "entity.posts.postId", target = "postId"),                   
   @Mapping(source = "entity.posts.postId", target = "postsDescriptiveField"),                    
   @Mapping(source = "entity.users.userId", target = "authorId"),                   
   @Mapping(source = "entity.users.email", target = "usersDescriptiveField"),                    
   }) 
   CreateCommentsOutput commentsToCreateCommentsOutput(Comments entity);
   
    Comments updateCommentsInputToComments(UpdateCommentsInput commentsDto);
    
    @Mappings({ 
    @Mapping(source = "entity.posts.postId", target = "postId"),                   
    @Mapping(source = "entity.posts.postId", target = "postsDescriptiveField"),                    
    @Mapping(source = "entity.users.userId", target = "authorId"),                   
    @Mapping(source = "entity.users.email", target = "usersDescriptiveField"),                    
   	}) 
   	UpdateCommentsOutput commentsToUpdateCommentsOutput(Comments entity);
   	@Mappings({ 
   	@Mapping(source = "entity.posts.postId", target = "postId"),                   
   	@Mapping(source = "entity.posts.postId", target = "postsDescriptiveField"),                    
   	@Mapping(source = "entity.users.userId", target = "authorId"),                   
   	@Mapping(source = "entity.users.email", target = "usersDescriptiveField"),                    
   	}) 
   	FindCommentsByIdOutput commentsToFindCommentsByIdOutput(Comments entity);


   @Mappings({
   @Mapping(source = "posts.content", target = "content"),                  
   @Mapping(source = "posts.createdAt", target = "createdAt"),                  
   @Mapping(source = "foundComments.commentId", target = "commentsCommentId"),
   })
   GetPostsOutput postsToGetPostsOutput(Posts posts, Comments foundComments);
   
   @Mappings({
   @Mapping(source = "users.createdAt", target = "createdAt"),                  
   @Mapping(source = "foundComments.commentId", target = "commentsCommentId"),
   })
   GetUsersOutput usersToGetUsersOutput(Users users, Comments foundComments);
   
}

