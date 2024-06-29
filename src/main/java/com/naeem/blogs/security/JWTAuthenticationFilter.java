package com.naeem.blogs.security;

import com.naeem.blogs.domain.core.authorization.jwtentity.IJwtRepository;
import com.naeem.blogs.domain.core.authorization.jwtentity.JwtEntity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import com.naeem.blogs.domain.core.authorization.users.Users;
import com.naeem.blogs.domain.core.authorization.users.IUsersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;

import javax.servlet.http.Cookie;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private IUsersRepository _userRepository;
    
    private IJwtRepository jwtRepo;
    private AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager,ApplicationContext ctx,IUsersRepository usersRepository) {
        this.authenticationManager = authenticationManager;
    	this._userRepository = usersRepository;
		this.jwtRepo = ctx.getBean(IJwtRepository.class);
    }
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            LoginUserInput creds = new ObjectMapper()
                    .readValue(request.getInputStream(), LoginUserInput.class);
        Users user = _userRepository.findByUsernameIgnoreCase(creds.getUserName());       
        if(user != null && user.getIsActive())
		{
			return authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(creds.getUserName(),creds.getPassword(),new ArrayList<>()));
		}
		else
			throw new InvalidCredentialsException("Invalid Credentials");
		} catch (IOException | InvalidCredentialsException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication auth) throws IOException {

        Claims claims = Jwts.claims();
        Claims cookieClaims = Jwts.claims();
        String userName = "";
        response.setContentType("application/json");
		PrintWriter out = response.getWriter();
   
            if (auth != null && auth.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
                userName = ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername();
                claims.setSubject(userName);
                cookieClaims.setSubject(userName);
            }
        
        claims.put("scopes", (auth.getAuthorities().stream().map(s -> s.toString()).collect(Collectors.toList())));
        Users user = _userRepository.findByUsernameIgnoreCase(userName);       
        if (user != null && (user.getIsEmailConfirmed() == null || !user.getIsEmailConfirmed())) {

			out.println("{");
			out.println(
					"\"message\":" + "\"User email is not confirmed\","
					);
			out.println(
					"\"status\":" + "\"EmailNotConfirmed\""
					);
			out.println("}");
			out.close();
			return;
		} else {
        
        Date expDate = new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME);
		Long time = SecurityConstants.EXPIRATION_TIME/1000;
		
		claims.setExpiration(expDate);
		cookieClaims.setExpiration(expDate);
		
		String authorizationToken = Jwts.builder()
				.setClaims(claims)
				.signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET.getBytes())
				.compact();
		
		String authenticationToken = Jwts.builder()
				.setClaims(cookieClaims)
				.signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET.getBytes())
				.compact();
 
        // Add the user and token to the JwtEntity table 
        JwtEntity jwt = new JwtEntity(); 
        jwt.setAuthorizationToken(SecurityConstants.TOKEN_PREFIX + authorizationToken); 
        jwt.setUserName(userName); 
        jwt.setAuthenticationToken(SecurityConstants.TOKEN_PREFIX + authenticationToken);
        
        jwtRepo.save(jwt); 
        
        Cookie authenticationCookie =  new Cookie(SecurityConstants.HEADER_STRING_AUTHENTICATION, SecurityConstants.TOKEN_PREFIX + authenticationToken);
		authenticationCookie.setPath("/");
		authenticationCookie.setSecure(true);
		authenticationCookie.setHttpOnly(true);
		authenticationCookie.setMaxAge(time.intValue());
		
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.addCookie(authenticationCookie);

        response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + authorizationToken);
        response.setContentType("application/json");

        out.println("{");
        out.println("\"token\":" + "\"" + SecurityConstants.TOKEN_PREFIX + authorizationToken + "\"");
        out.println("}");
        out.close();
        }

    }
    
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
	    logger.info("Authentication failed");
	    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    response.setContentType(MediaType.TEXT_PLAIN_VALUE);
	    response.getWriter().print(authException.getLocalizedMessage());
	    response.getWriter().flush();
    }

}

