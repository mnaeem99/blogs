package com.naeem.blogs.security;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.naeem.blogs.commons.error.ApiError;
import com.naeem.blogs.commons.error.ExceptionMessageConstants;
import com.naeem.blogs.commons.logging.LoggingHelper;
import io.jsonwebtoken.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.stream.Collectors;
import com.naeem.blogs.domain.core.authorization.jwtentity.JwtEntity;
import com.naeem.blogs.domain.core.authorization.jwtentity.IJwtRepository;
import java.util.*;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    
    private SecurityUtils securityUtils;
    private IJwtRepository jwtRepo;
    public JWTAuthorizationFilter(AuthenticationManager authManager,ApplicationContext ctx) {
        super(authManager);
        this.securityUtils = ctx.getBean(SecurityUtils.class);
		this.jwtRepo = ctx.getBean(IJwtRepository.class);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String authenticationToken = securityUtils.getTokenFromCookies(req.getCookies()); 
		String authorizationToken = req.getHeader(SecurityConstants.HEADER_STRING);

		if (authorizationToken == null || authenticationToken == null || !authorizationToken.startsWith(SecurityConstants.TOKEN_PREFIX) || !authenticationToken.startsWith(SecurityConstants.TOKEN_PREFIX)) {
			chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = null;
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED);
        LoggingHelper logHelper = new LoggingHelper();
        try {
            authentication = getAuthentication(req);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(req, res);
            return;

        } catch (ExpiredJwtException exception) {
            apiError.setMessage(ExceptionMessageConstants.TOKEN_EXPIRED);
            logHelper.getLogger().error("An Exception Occurred:", exception);
            res.setStatus(401);
        } catch (UnsupportedJwtException exception) {
            apiError.setMessage(ExceptionMessageConstants.TOKEN_UNSUPPORTED);
            logHelper.getLogger().error("An Exception Occurred:", exception);
            res.setStatus(401);
        } catch (MalformedJwtException exception) {
            apiError.setMessage(ExceptionMessageConstants.TOKEN_MALFORMED);
            logHelper.getLogger().error("An Exception Occurred:", exception);
            res.setStatus(401);
        } catch (SignatureException exception) {
            apiError.setMessage(ExceptionMessageConstants.TOKEN_INCORRECT_SIGNATURE);
            logHelper.getLogger().error("An Exception Occurred:", exception);
            res.setStatus(401);
        } catch (IllegalArgumentException exception) {
            apiError.setMessage(ExceptionMessageConstants.TOKEN_ILLEGAL_ARGUMENT);
            logHelper.getLogger().error("An Exception Occurred:", exception);
            res.setStatus(401);
        } catch (JwtException exception) {
             apiError.setMessage(ExceptionMessageConstants.TOKEN_UNAUTHORIZED);
             logHelper.getLogger().error("An Exception Occurred:", exception);
             res.setStatus(401);
	    }


        OutputStream out = res.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, apiError);
        out.flush();
    }



    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) throws JwtException {

        String authorizationToken = request.getHeader(SecurityConstants.HEADER_STRING);
        String authenticationToken = securityUtils.getTokenFromCookies(request.getCookies());
		// Check that the token is inactive in the JwtEntity table
		
         // Check that the token is inactive in the JwtEntity table
         JwtEntity jwt = jwtRepo.findByAuthorizationTokenAndAuthenticationToken(authorizationToken,authenticationToken);
 
         if(jwt == null) {
             throw new JwtException("Token Does Not Exist");
         }
        Claims claims;
       	if (StringUtils.isNotEmpty(authenticationToken) && authenticationToken.startsWith(SecurityConstants.TOKEN_PREFIX)) {
			claims = Jwts.parser()
					.setSigningKey(SecurityConstants.SECRET.getBytes())
					.parseClaimsJws(authenticationToken.replace(SecurityConstants.TOKEN_PREFIX, ""))
					.getBody();
		}
        
		if (StringUtils.isNotEmpty(authorizationToken) && authorizationToken.startsWith(SecurityConstants.TOKEN_PREFIX)) {
        	String userName = null;
            List<GrantedAuthority> authorities = null;
            claims = Jwts.parser()
                        .setSigningKey(SecurityConstants.SECRET.getBytes())
                        .parseClaimsJws(authorizationToken.replace(SecurityConstants.TOKEN_PREFIX, ""))
                        .getBody();
            userName = claims.getSubject();
            List<String> scopes = claims.get("scopes", List.class);
            authorities = scopes.stream()
                        .map(authority -> new SimpleGrantedAuthority(authority))
                        .collect(Collectors.toList());
                        

        if (StringUtils.isNotEmpty(userName)) {
        	return new UsernamePasswordAuthenticationToken(userName, null, authorities);
        }
        }
        return null;

    }

}
