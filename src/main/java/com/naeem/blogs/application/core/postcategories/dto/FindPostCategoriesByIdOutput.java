package com.naeem.blogs.application.core.postcategories.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FindPostCategoriesByIdOutput {

  	private Integer categoryId;
  	private Integer postId;
  	private String categoriesDescriptiveField;
  	private Integer postsDescriptiveField;
	private Long versiono;
 
}

