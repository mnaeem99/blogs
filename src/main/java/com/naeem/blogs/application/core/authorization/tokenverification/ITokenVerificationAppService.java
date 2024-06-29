package com.naeem.blogs.application.core.authorization.tokenverification;

import com.naeem.blogs.domain.core.authorization.tokenverification.Tokenverification;

public interface ITokenVerificationAppService {
	
	Tokenverification findByTokenAndType(String token, String type);

	Tokenverification generateToken(String type,Long usersId);

	Tokenverification findByUsersIdAndType(Long usersId, String type);

	void deleteToken(Tokenverification entity);

}

