package com.naeem.blogs.application.core.authorization.usersrole.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetUsersOutput {

 	private String lastName;
 	private Boolean isActive;
 	private Long userId;
 	private String firstName;
 	private String emailAddress;
 	private String password;
 	private Boolean isEmailConfirmed;
 	private String username;
  	private Long usersroleRoleId;
  	private Long usersroleUsersUserId;

}

