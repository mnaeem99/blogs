package com.naeem.blogs.application.core.postcategories;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import com.naeem.blogs.domain.core.categories.Categories;
import com.naeem.blogs.domain.core.posts.Posts;
import com.naeem.blogs.application.core.postcategories.dto.*;
import com.naeem.blogs.domain.core.postcategories.PostCategories;
import java.time.*;

@Mapper(componentModel = "spring")
public interface IPostCategoriesMapper {
   PostCategories createPostCategoriesInputToPostCategories(CreatePostCategoriesInput postCategoriesDto);
   
   @Mappings({ 
   @Mapping(source = "entity.categories.name", target = "categoriesDescriptiveField"),                    
   @Mapping(source = "entity.posts.postId", target = "postsDescriptiveField"),                    
   }) 
   CreatePostCategoriesOutput postCategoriesToCreatePostCategoriesOutput(PostCategories entity);
   
    PostCategories updatePostCategoriesInputToPostCategories(UpdatePostCategoriesInput postCategoriesDto);
    
    @Mappings({ 
    @Mapping(source = "entity.categories.name", target = "categoriesDescriptiveField"),                    
    @Mapping(source = "entity.posts.postId", target = "postsDescriptiveField"),                    
   	}) 
   	UpdatePostCategoriesOutput postCategoriesToUpdatePostCategoriesOutput(PostCategories entity);
   	@Mappings({ 
   	@Mapping(source = "entity.categories.name", target = "categoriesDescriptiveField"),                    
   	@Mapping(source = "entity.posts.postId", target = "postsDescriptiveField"),                    
   	}) 
   	FindPostCategoriesByIdOutput postCategoriesToFindPostCategoriesByIdOutput(PostCategories entity);


   @Mappings({
   @Mapping(source = "categories.categoryId", target = "categoryId"),                  
   @Mapping(source = "foundPostCategories.categoryId", target = "postCategoriesCategoryId"),
   @Mapping(source = "foundPostCategories.postId", target = "postCategoriesPostId"),
   })
   GetCategoriesOutput categoriesToGetCategoriesOutput(Categories categories, PostCategories foundPostCategories);
   
   @Mappings({
   @Mapping(source = "posts.postId", target = "postId"),                  
   @Mapping(source = "foundPostCategories.categoryId", target = "postCategoriesCategoryId"),
   @Mapping(source = "foundPostCategories.postId", target = "postCategoriesPostId"),
   })
   GetPostsOutput postsToGetPostsOutput(Posts posts, PostCategories foundPostCategories);
   
}

