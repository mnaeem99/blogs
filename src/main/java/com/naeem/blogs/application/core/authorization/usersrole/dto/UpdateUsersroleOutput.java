package com.naeem.blogs.application.core.authorization.usersrole.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateUsersroleOutput {

  	private Long roleId;
  	private Long usersUserId;
	private String roleDescriptiveField;
	private String usersDescriptiveField;

}
