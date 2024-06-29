package com.naeem.blogs.domain.core.authorization.jwtentity; 
 
import org.springframework.data.jpa.repository.JpaRepository; 
import org.springframework.stereotype.Repository;

@Repository
public interface IJwtRepository extends JpaRepository<JwtEntity, Long> { 

    JwtEntity findByAuthorizationTokenAndAuthenticationToken(String authorizationToken, String authenticationToken); 
    
    JwtEntity findByAuthorizationToken(String authorizationToken);
    
} 
