package com.naeem.blogs.domain.core.authorization.usersrole;

import java.time.*;
import javax.persistence.*;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter @Setter
@NoArgsConstructor
public class UsersroleId implements Serializable {

	private static final long serialVersionUID = 1L;
	
    private Long roleId;
    private Long usersUserId;
    
    public UsersroleId(Long roleId,Long usersUserId) {
 	this.roleId = roleId;
 	this.usersUserId = usersUserId;
    }
    
}
