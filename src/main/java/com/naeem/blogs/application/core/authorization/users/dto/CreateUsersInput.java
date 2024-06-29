package com.naeem.blogs.application.core.authorization.users.dto;

import java.time.*;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateUsersInput {

	@Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message = "Email Address should be valid")
  	@Length(max = 256, message = "emailAddress must be less than 256 characters")
  	private String emailAddress;
  
  	@Length(max = 32, message = "firstName must be less than 32 characters")
  	private String firstName;
  
  	private Boolean isActive = false;
  
  	private Boolean isEmailConfirmed;
  
  	@Length(max = 32, message = "lastName must be less than 32 characters")
  	private String lastName;
  
  	@Length(max = 128, message = "password must be less than 128 characters")
  	private String password;
  
  	@Length(max = 32, message = "phoneNumber must be less than 32 characters")
  	private String phoneNumber;
  
  	@Length(max = 32, message = "username must be less than 32 characters")
  	private String username;
  

}

