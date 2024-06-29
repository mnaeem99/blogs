package com.naeem.blogs.application.core.authorization.users.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateUsersOutput {

  	private String emailAddress;
  	private String firstName;
  	private Boolean isActive;
  	private Boolean isEmailConfirmed;
  	private String lastName;
  	private String phoneNumber;
  	private Long userId;
  	private String username;

}
