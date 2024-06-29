package com.naeem.blogs.application.core.authorization.users;

import org.springframework.data.domain.Pageable;
import com.naeem.blogs.domain.core.authorization.users.Users;
import com.naeem.blogs.domain.core.authorization.userspreference.Userspreference;
import com.naeem.blogs.application.core.authorization.users.dto.*;
import com.naeem.blogs.commons.search.SearchCriteria;
import java.net.MalformedURLException;
import java.util.*;

public interface IUsersAppService {
	
	//CRUD Operations
	CreateUsersOutput create(CreateUsersInput users);

    void delete(Long id);

    UpdateUsersOutput update(Long id, UpdateUsersInput input);

    FindUsersByIdOutput findById(Long id);


    List<FindUsersByIdOutput> find(SearchCriteria search, Pageable pageable) throws MalformedURLException;
    
   	Userspreference createDefaultUsersPreference(Users users);
   	
   	void updateTheme(Users users, String theme);
   	
   	void updateLanguage(Users users, String language);
    
    void updateUsersData(FindUsersWithAllFieldsByIdOutput users);
	
	UsersProfile updateUsersProfile(FindUsersWithAllFieldsByIdOutput users, UsersProfile usersProfile);
	
	FindUsersWithAllFieldsByIdOutput findWithAllFieldsById(Long usersId);
	
	UsersProfile getProfile(FindUsersByIdOutput user);
	 
	Users getUsers();
	
	FindUsersByUsernameOutput findByUsername(String username);
	
	FindUsersByUsernameOutput findByEmailAddress(String emailAddress);
    
    //Join Column Parsers

	Map<String,String> parseCommentsJoinColumn(String keysString);

	Map<String,String> parseLikesJoinColumn(String keysString);

	Map<String,String> parsePostsJoinColumn(String keysString);

	Map<String,String> parseUserspermissionsJoinColumn(String keysString);

	Map<String,String> parseUsersrolesJoinColumn(String keysString);
}

