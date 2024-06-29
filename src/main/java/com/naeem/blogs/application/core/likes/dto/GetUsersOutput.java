package com.naeem.blogs.application.core.likes.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetUsersOutput {

 	private String emailAddress;
 	private String firstName;
 	private Boolean isActive;
 	private Boolean isEmailConfirmed;
 	private String lastName;
 	private String password;
 	private String phoneNumber;
 	private Long userId;
 	private String username;
  	private Integer likesLikeId;

}

