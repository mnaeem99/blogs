package com.naeem.blogs.domain.core.postcategories;

import java.time.*;
import javax.persistence.*;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter @Setter
@NoArgsConstructor
public class PostCategoriesId implements Serializable {

	private static final long serialVersionUID = 1L;
	
    private Integer categoryId;
    private Integer postId;
    
    public PostCategoriesId(Integer categoryId,Integer postId) {
 	this.categoryId = categoryId;
 	this.postId = postId;
    }
    
}
