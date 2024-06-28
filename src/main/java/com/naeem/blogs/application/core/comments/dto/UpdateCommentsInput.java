package com.naeem.blogs.application.core.comments.dto;

import java.time.*;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateCommentsInput {

  	@NotNull(message = "commentId Should not be null")
  	private Integer commentId;
  	
  	@NotNull(message = "content Should not be null")
  	private String content;
  	
  	private LocalDateTime createdAt;
  	
  	private Integer postId;
  	private Integer authorId;
  	private Long versiono;
  
}

