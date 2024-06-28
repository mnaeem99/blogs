package com.naeem.blogs.application.core.tags.dto;

import java.time.*;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateTagsInput {

  	@NotNull(message = "name Should not be null")
  	@Length(max = 100, message = "name must be less than 100 characters")
  	private String name;
  

}

