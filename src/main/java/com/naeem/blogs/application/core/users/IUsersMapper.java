package com.naeem.blogs.application.core.users;

import org.mapstruct.Mapper;
import com.naeem.blogs.application.core.users.dto.*;
import com.naeem.blogs.domain.core.users.Users;
import java.time.*;

@Mapper(componentModel = "spring")
public interface IUsersMapper {
   Users createUsersInputToUsers(CreateUsersInput usersDto);
   CreateUsersOutput usersToCreateUsersOutput(Users entity);
   
    Users updateUsersInputToUsers(UpdateUsersInput usersDto);
    
   	UpdateUsersOutput usersToUpdateUsersOutput(Users entity);
   	FindUsersByIdOutput usersToFindUsersByIdOutput(Users entity);


}

