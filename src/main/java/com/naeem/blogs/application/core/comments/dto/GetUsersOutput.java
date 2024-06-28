package com.naeem.blogs.application.core.comments.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetUsersOutput {

 	private LocalDateTime createdAt;
 	private String email;
 	private String passwordHash;
 	private LocalDateTime updatedAt;
 	private Integer userId;
 	private String username;
  	private Integer commentsCommentId;

}

