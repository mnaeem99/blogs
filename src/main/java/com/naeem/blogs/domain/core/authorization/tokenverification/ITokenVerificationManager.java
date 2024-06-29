package com.naeem.blogs.domain.core.authorization.tokenverification;

import java.time.*;

public interface ITokenVerificationManager {

	Tokenverification save(Tokenverification entity);
	
	void delete(Tokenverification entity);
	 
	Tokenverification findByTokenAndType(String token, String tokenType);
	 
	Tokenverification findByUsersIdAndType(Long userId, String tokenType);
}

