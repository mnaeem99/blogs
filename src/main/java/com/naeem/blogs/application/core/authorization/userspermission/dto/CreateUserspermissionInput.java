package com.naeem.blogs.application.core.authorization.userspermission.dto;

import java.time.*;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateUserspermissionInput {

  	@NotNull(message = "permissionId Should not be null")
  	private Long permissionId;
  
  	private Boolean revoked;
  
  	@NotNull(message = "usersUserId Should not be null")
  	private Long usersUserId;
  

}

