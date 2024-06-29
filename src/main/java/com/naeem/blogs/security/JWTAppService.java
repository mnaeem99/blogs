package com.naeem.blogs.security;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.naeem.blogs.domain.core.authorization.jwtentity.IJwtRepository;
import com.naeem.blogs.domain.core.authorization.jwtentity.JwtEntity;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JWTAppService {

    @NonNull private final IJwtRepository _jwtRepository;
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteAllUserTokens(String userName) {
	
	List<JwtEntity> userTokens = _jwtRepository.findAll();
        userTokens.removeAll(Collections.singleton(null));

        for (JwtEntity jwt : userTokens) {
            if(jwt.getUserName().equals(userName)) {
                _jwtRepository.delete(jwt);
            }
	    } 

    }
    
    @Transactional(propagation = Propagation.REQUIRED)
	public void deleteToken(String token) {

     JwtEntity jwt = _jwtRepository.findByAuthorizationToken(token);
     _jwtRepository.delete(jwt);

    }
}

