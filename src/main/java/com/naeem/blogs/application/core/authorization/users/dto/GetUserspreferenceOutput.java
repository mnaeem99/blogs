package com.naeem.blogs.application.core.authorization.users.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetUserspreferenceOutput {

 	private String lastName;
 	private Boolean isActive;
 	private Long userId;
 	private String firstName;
 	private String emailAddress;
 	private String password;
 	private String phoneNumber;
 	private Boolean isEmailConfirmed;
 	private String username;
  	private Long usersUserId;

}

