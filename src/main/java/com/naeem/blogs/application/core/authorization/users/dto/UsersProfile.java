package com.naeem.blogs.application.core.authorization.users.dto;

import java.time.*;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UsersProfile {

	@NotNull(message = "username Should not be null")
	@Length(max = 32, message = "username must be less than 32 characters")
	private String username;
	
	@NotNull(message = "emailAddress Should not be null")
	@Length(max = 256, message = "emailAddress must be less than 256 characters")
	@Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message = "EmailAddress should be valid")
	private String emailAddress;
	
    @NotNull(message = "firstName Should not be null")
  	private String firstName;
  	
    @NotNull(message = "lastName Should not be null")
  	private String lastName;
  	
  	private String phoneNumber;
  	
	
	private String theme;
    private String language;
	
}

