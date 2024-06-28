package com.naeem.blogs.application.core.likes.dto;

import java.time.*;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateLikesInput {

  	private LocalDateTime createdAt;
  	
  	@NotNull(message = "likeId Should not be null")
  	private Integer likeId;
  	
  	private Integer postId;
  	private Integer userId;
  	private Long versiono;
  
}

