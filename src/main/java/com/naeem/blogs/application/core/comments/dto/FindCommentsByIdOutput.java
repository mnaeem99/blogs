package com.naeem.blogs.application.core.comments.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FindCommentsByIdOutput {

  	private Integer commentId;
  	private String content;
  	private LocalDateTime createdAt;
  	private Integer postId;
  	private Integer postsDescriptiveField;
  	private Integer authorId;
  	private String usersDescriptiveField;
	private Long versiono;
 
}

