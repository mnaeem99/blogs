package com.naeem.blogs.application.core.users.dto;

import java.time.*;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateUsersInput {

  	private LocalDateTime createdAt;
  
  	@NotNull(message = "email Should not be null")
  	@Length(max = 100, message = "email must be less than 100 characters")
  	private String email;
  
  	@NotNull(message = "passwordHash Should not be null")
  	@Length(max = 255, message = "passwordHash must be less than 255 characters")
  	private String passwordHash;
  
  	private LocalDateTime updatedAt;
  
  	@NotNull(message = "username Should not be null")
  	@Length(max = 50, message = "username must be less than 50 characters")
  	private String username;
  

}

