package com.naeem.blogs.domain.core.posttags;

import java.time.*;
import javax.persistence.*;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter @Setter
@NoArgsConstructor
public class PostTagsId implements Serializable {

	private static final long serialVersionUID = 1L;
	
    private Integer postId;
    private Integer tagId;
    
    public PostTagsId(Integer postId,Integer tagId) {
 	this.postId = postId;
 	this.tagId = tagId;
    }
    
}
