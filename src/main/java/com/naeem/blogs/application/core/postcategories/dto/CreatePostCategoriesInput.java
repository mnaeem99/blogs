package com.naeem.blogs.application.core.postcategories.dto;

import java.time.*;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreatePostCategoriesInput {

  	@NotNull(message = "categoryId Should not be null")
  	private Integer categoryId;
  
  	@NotNull(message = "postId Should not be null")
  	private Integer postId;
  

}

