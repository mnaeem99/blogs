package com.naeem.blogs.application.core.authorization.usersrole.dto;

import java.time.*;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateUsersroleInput {

  	@NotNull(message = "roleId Should not be null")
  	private Long roleId;
  	
  	@NotNull(message = "usersUserId Should not be null")
  	private Long usersUserId;
  	
  	private Long versiono;
  
}

