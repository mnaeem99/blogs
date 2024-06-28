package com.naeem.blogs.application.core.posttags.dto;

import java.time.*;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreatePostTagsInput {

  	@NotNull(message = "postId Should not be null")
  	private Integer postId;
  
  	@NotNull(message = "tagId Should not be null")
  	private Integer tagId;
  

}

