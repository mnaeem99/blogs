package com.naeem.blogs.application.core.postcategories.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetCategoriesOutput {

 	private Integer categoryId;
 	private String description;
 	private String name;
  	private Integer postCategoriesCategoryId;
  	private Integer postCategoriesPostId;

}

