package com.naeem.blogs.application.core.posts.dto;

import java.time.*;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdatePostsInput {

  	@NotNull(message = "content Should not be null")
  	private String content;
  	
  	private LocalDateTime createdAt;
  	
  	@NotNull(message = "postId Should not be null")
  	private Integer postId;
  	
  	@NotNull(message = "title Should not be null")
 	@Length(max = 255, message = "title must be less than 255 characters")
  	private String title;
  	
  	private LocalDateTime updatedAt;
  	
  	private Integer authorId;
  	private Long versiono;
  
}

