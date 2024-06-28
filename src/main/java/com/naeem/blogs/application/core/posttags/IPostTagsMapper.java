package com.naeem.blogs.application.core.posttags;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import com.naeem.blogs.domain.core.posts.Posts;
import com.naeem.blogs.domain.core.tags.Tags;
import com.naeem.blogs.application.core.posttags.dto.*;
import com.naeem.blogs.domain.core.posttags.PostTags;
import java.time.*;

@Mapper(componentModel = "spring")
public interface IPostTagsMapper {
   PostTags createPostTagsInputToPostTags(CreatePostTagsInput postTagsDto);
   
   @Mappings({ 
   @Mapping(source = "entity.posts.postId", target = "postsDescriptiveField"),                    
   @Mapping(source = "entity.tags.name", target = "tagsDescriptiveField"),                    
   }) 
   CreatePostTagsOutput postTagsToCreatePostTagsOutput(PostTags entity);
   
    PostTags updatePostTagsInputToPostTags(UpdatePostTagsInput postTagsDto);
    
    @Mappings({ 
    @Mapping(source = "entity.posts.postId", target = "postsDescriptiveField"),                    
    @Mapping(source = "entity.tags.name", target = "tagsDescriptiveField"),                    
   	}) 
   	UpdatePostTagsOutput postTagsToUpdatePostTagsOutput(PostTags entity);
   	@Mappings({ 
   	@Mapping(source = "entity.posts.postId", target = "postsDescriptiveField"),                    
   	@Mapping(source = "entity.tags.name", target = "tagsDescriptiveField"),                    
   	}) 
   	FindPostTagsByIdOutput postTagsToFindPostTagsByIdOutput(PostTags entity);


   @Mappings({
   @Mapping(source = "tags.tagId", target = "tagId"),                  
   @Mapping(source = "foundPostTags.postId", target = "postTagsPostId"),
   @Mapping(source = "foundPostTags.tagId", target = "postTagsTagId"),
   })
   GetTagsOutput tagsToGetTagsOutput(Tags tags, PostTags foundPostTags);
   
   @Mappings({
   @Mapping(source = "posts.postId", target = "postId"),                  
   @Mapping(source = "foundPostTags.postId", target = "postTagsPostId"),
   @Mapping(source = "foundPostTags.tagId", target = "postTagsTagId"),
   })
   GetPostsOutput postsToGetPostsOutput(Posts posts, PostTags foundPostTags);
   
}

