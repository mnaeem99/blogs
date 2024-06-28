package com.naeem.blogs.application.core.categories.dto;

import java.time.*;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateCategoriesInput {

  	@NotNull(message = "categoryId Should not be null")
  	private Integer categoryId;
  	
  	private String description;
  	
  	@NotNull(message = "name Should not be null")
 	@Length(max = 100, message = "name must be less than 100 characters")
  	private String name;
  	
  	private Long versiono;
  
}
