package com.naeem.blogs.application.core.comments.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetPostsOutput {

 	private String content;
 	private LocalDateTime createdAt;
 	private Integer postId;
 	private String title;
 	private LocalDateTime updatedAt;
  	private Integer commentsCommentId;

}

