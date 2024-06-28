package com.naeem.blogs.application.core.posttags.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetTagsOutput {

 	private String name;
 	private Integer tagId;
  	private Integer postTagsPostId;
  	private Integer postTagsTagId;

}

