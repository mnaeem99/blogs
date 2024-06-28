package com.naeem.blogs.application.core.posts.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdatePostsOutput {

  	private String content;
  	private LocalDateTime createdAt;
  	private Integer postId;
  	private String title;
  	private LocalDateTime updatedAt;
  	private Integer authorId;
	private String usersDescriptiveField;

}
