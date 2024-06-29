package com.naeem.blogs.application.core.authorization.users.dto;

import java.time.*;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateUsersInput {

  	@Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message = "Email Address should be valid")
  	@NotNull(message = "emailAddress Should not be null")
 	@Length(max = 256, message = "emailAddress must be less than 256 characters")
  	private String emailAddress;
  	
  	@NotNull(message = "firstName Should not be null")
 	@Length(max = 32, message = "firstName must be less than 32 characters")
  	private String firstName;
  	
  	@NotNull(message = "isActive Should not be null")
  	private Boolean isActive = false;
  	
  	@NotNull(message = "isEmailConfirmed Should not be null")
  	private Boolean isEmailConfirmed;
  	
  	@NotNull(message = "lastName Should not be null")
 	@Length(max = 32, message = "lastName must be less than 32 characters")
  	private String lastName;
  	
 	@Length(max = 128, message = "password must be less than 128 characters")
  	private String password;
  	
 	@Length(max = 32, message = "phoneNumber must be less than 32 characters")
  	private String phoneNumber;
  	
  	@NotNull(message = "userId Should not be null")
  	private Long userId;
  	
  	@NotNull(message = "username Should not be null")
 	@Length(max = 32, message = "username must be less than 32 characters")
  	private String username;
  	
  	private Long versiono;
  
}

