package com.naeem.blogs.application.core.likes.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateLikesInput {

  	private LocalDateTime createdAt;
  
  	private Integer postId;
  	private Integer userId;

}

