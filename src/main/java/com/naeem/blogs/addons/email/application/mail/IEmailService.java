package com.naeem.blogs.addons.email.application.mail;

import org.springframework.mail.SimpleMailMessage;

public interface IEmailService {

	SimpleMailMessage buildEmail(String email, String subject, String emailText);
	
	void sendEmail(SimpleMailMessage email);
	
}

