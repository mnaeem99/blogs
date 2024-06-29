package com.naeem.blogs.security;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
import javax.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.naeem.blogs.domain.core.authorization.rolepermission.Rolepermission;
import com.naeem.blogs.domain.core.authorization.users.Users;
import com.naeem.blogs.domain.core.authorization.userspermission.Userspermission;
import com.naeem.blogs.domain.core.authorization.rolepermission.IRolepermissionRepository;
import com.naeem.blogs.domain.core.authorization.usersrole.Usersrole;
import com.naeem.blogs.domain.core.authorization.usersrole.IUsersroleRepository;
import com.naeem.blogs.domain.core.authorization.userspermission.IUserspermissionRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    @Qualifier("usersroleRepository")
    @NonNull private final IUsersroleRepository usersroleRepository;
    
    @Qualifier("rolepermissionRepository")
    @NonNull private final IRolepermissionRepository rolepermissionRepository;
    
    @Qualifier("userspermissionRepository")
    @NonNull private final IUserspermissionRepository userspermissionRepository;
  
    public List<String> getAllPermissionsFromUserAndRole(Users users) {

        List<String> permissions = new ArrayList<>();
        List<Usersrole> ure = usersroleRepository.findByUsersUserId(users.getUserId());

        for (Usersrole ur : ure) {

            List<Rolepermission> srp= rolepermissionRepository.findByRoleId(ur.getRoleId());
            for (Rolepermission item : srp) {
                permissions.add(item.getPermission().getName());
            }
        }

        List<Userspermission> spe = userspermissionRepository.findByUsersUserId(users.getUserId());

        for (Userspermission up : spe) {

            if(permissions.contains(up.getPermission().getName()) && (up.getRevoked() != null && up.getRevoked()))
            {
                permissions.remove(up.getPermission().getName());
            }
            if(!permissions.contains(up.getPermission().getName()) && (up.getRevoked()==null || !up.getRevoked()))
            {
                permissions.add(up.getPermission().getName());

            }
        }
		
		return permissions
				.stream()
				.distinct()
				.collect(Collectors.toList());
	}
	
	public String getTokenFromCookies(Cookie[] cookies)
    {
    	if(cookies !=null) {
    		for(Cookie c : cookies)
			{
				if(c.getName().equals(SecurityConstants.HEADER_STRING_AUTHENTICATION)) {
					return c.getValue();
				}
			}
    	}
    	return null;
    }
	
	public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
            .map(authentication -> {
                if (authentication.getPrincipal() instanceof UserDetails) {
                    UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
                    return springSecurityUser.getUsername();
                } else if (authentication.getPrincipal() instanceof String) {
                    return (String) authentication.getPrincipal();
                }
                return null;
            });
    }

}

