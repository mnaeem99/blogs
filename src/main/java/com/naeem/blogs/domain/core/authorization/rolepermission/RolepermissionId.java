package com.naeem.blogs.domain.core.authorization.rolepermission;

import java.time.*;
import javax.persistence.*;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter @Setter
@NoArgsConstructor
public class RolepermissionId implements Serializable {

	private static final long serialVersionUID = 1L;
	
    private Long permissionId;
    private Long roleId;
    
    public RolepermissionId(Long permissionId,Long roleId) {
 	this.permissionId = permissionId;
 	this.roleId = roleId;
    }
    
}
