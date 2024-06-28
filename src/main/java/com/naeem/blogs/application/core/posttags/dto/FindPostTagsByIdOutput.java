package com.naeem.blogs.application.core.posttags.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FindPostTagsByIdOutput {

  	private Integer postId;
  	private Integer tagId;
  	private Integer postsDescriptiveField;
  	private String tagsDescriptiveField;
	private Long versiono;
 
}

