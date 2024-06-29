package com.naeem.blogs.application.core.authorization.usersrole;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import com.naeem.blogs.domain.core.authorization.role.Role;
import com.naeem.blogs.domain.core.authorization.users.Users;
import com.naeem.blogs.application.core.authorization.usersrole.dto.*;
import com.naeem.blogs.domain.core.authorization.usersrole.Usersrole;
import java.time.*;

@Mapper(componentModel = "spring")
public interface IUsersroleMapper {
   Usersrole createUsersroleInputToUsersrole(CreateUsersroleInput usersroleDto);
   
   @Mappings({ 
   @Mapping(source = "users.userId", target = "usersUserId"),  
   @Mapping(source = "users.username", target = "usersDescriptiveField"),
   @Mapping(source = "role.displayName", target = "roleDescriptiveField"),                   
   @Mapping(source = "role.id", target = "roleId")                   
   }) 
   CreateUsersroleOutput usersroleToCreateUsersroleOutput(Usersrole entity);
   
    Usersrole updateUsersroleInputToUsersrole(UpdateUsersroleInput usersroleDto);
    
    @Mappings({ 
    @Mapping(source = "entity.role.displayName", target = "roleDescriptiveField"),                    
    @Mapping(source = "entity.users.username", target = "usersDescriptiveField"),                    
   	}) 
   	UpdateUsersroleOutput usersroleToUpdateUsersroleOutput(Usersrole entity);
   	@Mappings({ 
   	@Mapping(source = "entity.role.displayName", target = "roleDescriptiveField"),                    
   	@Mapping(source = "entity.users.username", target = "usersDescriptiveField"),                    
   	}) 
   	FindUsersroleByIdOutput usersroleToFindUsersroleByIdOutput(Usersrole entity);


   @Mappings({
   @Mapping(source = "foundUsersrole.roleId", target = "usersroleRoleId"),
   @Mapping(source = "foundUsersrole.usersUserId", target = "usersroleUsersUserId"),
   })
   GetRoleOutput roleToGetRoleOutput(Role role, Usersrole foundUsersrole);
   
   @Mappings({
   @Mapping(source = "foundUsersrole.roleId", target = "usersroleRoleId"),
   @Mapping(source = "foundUsersrole.usersUserId", target = "usersroleUsersUserId"),
   })
   GetUsersOutput usersToGetUsersOutput(Users users, Usersrole foundUsersrole);
   
}

