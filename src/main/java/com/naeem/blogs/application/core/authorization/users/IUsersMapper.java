package com.naeem.blogs.application.core.authorization.users;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import com.naeem.blogs.domain.core.authorization.userspreference.Userspreference;
import com.naeem.blogs.application.core.authorization.users.dto.*;
import com.naeem.blogs.domain.core.authorization.users.Users;
import java.time.*;

@Mapper(componentModel = "spring")
public interface IUsersMapper {
   Users createUsersInputToUsers(CreateUsersInput usersDto);
   
   @Mappings({
   @Mapping(source = "entity.userId", target = "userId"),
   })
   CreateUsersOutput usersToCreateUsersOutput(Users entity,Userspreference userPreference);
   
    @Mappings({
    @Mapping(source = "usersProfile.firstName", target = "firstName"),
    @Mapping(source = "usersProfile.lastName", target = "lastName"),
    @Mapping(source = "usersProfile.phoneNumber", target = "phoneNumber"),
    @Mapping(source = "usersProfile.username", target = "username"),
    @Mapping(source = "usersProfile.emailAddress", target = "emailAddress")
    })
    UpdateUsersInput findUsersWithAllFieldsByIdOutputAndUsersProfileToUpdateUsersInput(FindUsersWithAllFieldsByIdOutput users, UsersProfile usersProfile);
    
    Users findUsersWithAllFieldsByIdOutputToUsers(FindUsersWithAllFieldsByIdOutput users);
    
    UsersProfile updateUsersOutputToUsersProfile(UpdateUsersOutput usersDto);
    
    UsersProfile findUsersByIdOutputToUsersProfile(FindUsersByIdOutput users);
    
    Users updateUsersInputToUsers(UpdateUsersInput usersDto);
    
   	UpdateUsersOutput usersToUpdateUsersOutput(Users entity);
   	@Mappings({
    @Mapping(source = "entity.versiono", target = "versiono"),
    @Mapping(source = "entity.userId", target = "userId"),
   	})
   	FindUsersByIdOutput usersToFindUsersByIdOutput(Users entity,Userspreference userPreference);

   	FindUsersWithAllFieldsByIdOutput usersToFindUsersWithAllFieldsByIdOutput(Users entity);
    FindUsersByUsernameOutput usersToFindUsersByUsernameOutput(Users entity);

}

