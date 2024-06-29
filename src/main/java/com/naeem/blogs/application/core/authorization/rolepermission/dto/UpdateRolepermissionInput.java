package com.naeem.blogs.application.core.authorization.rolepermission.dto;

import java.time.*;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateRolepermissionInput {

  	@NotNull(message = "permissionId Should not be null")
  	private Long permissionId;
  	
  	@NotNull(message = "roleId Should not be null")
  	private Long roleId;
  	
  	private Long versiono;
  
}

