package com.naeem.blogs.application.core.posts.dto;

import java.time.*;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreatePostsInput {

  	@NotNull(message = "content Should not be null")
  	private String content;
  
  	private LocalDateTime createdAt;
  
  	@NotNull(message = "title Should not be null")
  	@Length(max = 255, message = "title must be less than 255 characters")
  	private String title;
  
  	private LocalDateTime updatedAt;
  
  	private Integer authorId;

}

