package com.naeem.blogs.addons.registration.restcontrollers;

import java.util.Date;
import java.util.HashMap;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import com.naeem.blogs.application.core.authorization.tokenverification.ITokenVerificationAppService;
import com.naeem.blogs.application.core.authorization.users.IUsersAppService;
import com.naeem.blogs.application.core.authorization.users.dto.*;
import com.naeem.blogs.commons.logging.LoggingHelper;
import com.naeem.blogs.domain.core.authorization.tokenverification.Tokenverification;
import com.naeem.blogs.addons.email.application.mail.IEmailService;

@RestController
@RequestMapping("/register")
public class RegistrationController {

	@Autowired
	private PasswordEncoder pEncoder;

	@Autowired 
	private ITokenVerificationAppService _tokenAppService;

	@Autowired
	@Qualifier("usersAppService")
	private IUsersAppService _usersAppService;

	@Autowired
	private LoggingHelper logHelper;

	@Autowired
	private IEmailService _emailService;

	public static final long ACCOUNT_VERIFICATION_TOKEN_EXPIRATION_TIME = 86_400_000;

	@RequestMapping(method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<HashMap<String,String>> registerUserAccount(
		@RequestBody CreateUsersInput users,
		@RequestParam("clientUrl") final String clientUrl) {

		FindUsersByUsernameOutput foundUsers = _usersAppService.findByUsername(users.getUsername());

	        if (foundUsers != null) {
	            logHelper.getLogger().error("There already exists a users with a Username={}", users.getUsername());
	            throw new EntityExistsException(
	                    String.format("There already exists a users with Username =%s", users.getUsername()));
	        }
	    foundUsers = _usersAppService.findByEmailAddress(users.getEmailAddress());

	        if (foundUsers != null) {
	            logHelper.getLogger().error("There already exists a users with a email = {}", users.getEmailAddress());
	            throw new EntityExistsException(
	                    String.format("There already exists a users with email =%s", users.getEmailAddress()));
	        }
	        
	    users.setPassword(pEncoder.encode(users.getPassword()));
	    users.setIsActive(true);
	    users.setIsEmailConfirmed(false);
	    

		CreateUsersOutput output =_usersAppService.create(users);
		if(output == null) {
			throw new EntityNotFoundException("No record found");
		}
		
		sendVerificationEmail(clientUrl, output.getEmailAddress(), output.getUserId());
		String msg = "Account verfication link has been sent to " + users.getEmailAddress();
		HashMap resultMap = new HashMap<String,String>();
		resultMap.put("message", msg);

		return new ResponseEntity<>(resultMap, HttpStatus.OK);

	}

	@RequestMapping(value = "/verifyEmail", method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<HashMap<String,String>> verifyEmail(@RequestParam("token") final String token) {

		Tokenverification tokenEntity = _tokenAppService.findByTokenAndType(token, "registration");
		if(tokenEntity == null) {
			throw new EntityNotFoundException("Invalid verification link.");
		}
		
		FindUsersWithAllFieldsByIdOutput output = _usersAppService.findWithAllFieldsById(tokenEntity.getUsersUserId());
		if(output == null) {
			throw new EntityNotFoundException("Invalid verification link.");
		}
		
		if(new Date().after(tokenEntity.getExpirationTime()))
		{
			_tokenAppService.deleteToken(tokenEntity);
			_usersAppService.delete(tokenEntity.getUsersUserId());
    		
			logHelper.getLogger().error("Token has expired, please register again");
			throw new EntityNotFoundException("Token has expired, please register again");
		}

		output.setIsEmailConfirmed(true);
		_usersAppService.updateUsersData(output);
		_tokenAppService.deleteToken(tokenEntity);

		String msg = "Users Verified!";
		HashMap resultMap = new HashMap<String,String>();
		resultMap.put("message", msg);
		return new ResponseEntity<>(resultMap, HttpStatus.OK);

	}
	
	@RequestMapping(value = "/resendVerificationEmail/{username}", method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<HashMap<String, String>> resendVerificationEmail(
		@PathVariable final String username,
		@RequestParam("clientUrl") final String clientUrl) {
			
			
		FindUsersByUsernameOutput foundUsers = _usersAppService.findByUsername(username);

        if (foundUsers == null) {
            logHelper.getLogger().error("There does not exist a users with Username= '{}'", foundUsers.getUsername());
            throw new EntityExistsException(
                    String.format("There does not exist a users with Username =%s", foundUsers.getUsername()));
        }
        
        if (foundUsers != null && Boolean.TRUE.equals(foundUsers.getIsEmailConfirmed())) {
			logHelper.getLogger().error("Users with Username= '{}' is already verified.", username);
			throw new EntityExistsException(
				String.format("Users with Username=%s is already verified.", username)
			);
		}

		sendVerificationEmail(clientUrl, foundUsers.getEmailAddress(), foundUsers.getUserId());
		
		String msg = "Account verfication link has been resent to " + foundUsers.getEmailAddress();

		HashMap<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("message", msg);
		return new ResponseEntity<>(resultMap, HttpStatus.OK);
	}
	
	private void sendVerificationEmail(String clientUrl, String emailAddress, Long usersId) {
		
		Tokenverification tokenEntity = _tokenAppService.generateToken(
				"registration",
				usersId
			);

		String subject = "Account Verfication";
		String emailText = "To verify your account, click the link below:\n" +
			clientUrl + "/verify-email?token=" + tokenEntity.getToken();
				
		_emailService.sendEmail(_emailService.buildEmail(emailAddress, subject, emailText));
	}

}

