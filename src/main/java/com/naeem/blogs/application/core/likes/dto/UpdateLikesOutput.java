package com.naeem.blogs.application.core.likes.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateLikesOutput {

  	private LocalDateTime createdAt;
  	private Integer likeId;
  	private Integer postId;
	private Integer postsDescriptiveField;
  	private Integer userId;
	private String usersDescriptiveField;

}
