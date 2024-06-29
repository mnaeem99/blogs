package com.naeem.blogs.domain.core.authorization.tokenverification;

import java.time.*;
import javax.persistence.*;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter @Setter
@NoArgsConstructor
public class TokenverificationId implements Serializable {

	private static final long serialVersionUID = 1L;
	
    private String tokenType;
    private Long usersUserId;
    
    public TokenverificationId(String tokenType,Long usersUserId) {
 	this.tokenType = tokenType;
 	this.usersUserId = usersUserId;
    }
    
}
