package com.naeem.blogs.application.core.authorization.userspermission;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import com.naeem.blogs.domain.core.authorization.permission.Permission;
import com.naeem.blogs.domain.core.authorization.users.Users;
import com.naeem.blogs.application.core.authorization.userspermission.dto.*;
import com.naeem.blogs.domain.core.authorization.userspermission.Userspermission;
import java.time.*;

@Mapper(componentModel = "spring")
public interface IUserspermissionMapper {
   Userspermission createUserspermissionInputToUserspermission(CreateUserspermissionInput userspermissionDto);
   
   @Mappings({ 
   @Mapping(source = "users.userId", target = "usersUserId"),  
   @Mapping(source = "users.username", target = "usersDescriptiveField"),
   @Mapping(source = "permission.name", target = "permissionDescriptiveField"),                   
   @Mapping(source = "permission.id", target = "permissionId")                   
   }) 
   CreateUserspermissionOutput userspermissionToCreateUserspermissionOutput(Userspermission entity);
   
    Userspermission updateUserspermissionInputToUserspermission(UpdateUserspermissionInput userspermissionDto);
    
    @Mappings({ 
    @Mapping(source = "entity.permission.displayName", target = "permissionDescriptiveField"),                    
    @Mapping(source = "entity.users.username", target = "usersDescriptiveField"),                    
   	}) 
   	UpdateUserspermissionOutput userspermissionToUpdateUserspermissionOutput(Userspermission entity);
   	@Mappings({ 
   	@Mapping(source = "entity.permission.displayName", target = "permissionDescriptiveField"),                    
   	@Mapping(source = "entity.users.username", target = "usersDescriptiveField"),                    
   	}) 
   	FindUserspermissionByIdOutput userspermissionToFindUserspermissionByIdOutput(Userspermission entity);


   @Mappings({
   @Mapping(source = "foundUserspermission.permissionId", target = "userspermissionPermissionId"),
   @Mapping(source = "foundUserspermission.usersUserId", target = "userspermissionUsersUserId"),
   })
   GetPermissionOutput permissionToGetPermissionOutput(Permission permission, Userspermission foundUserspermission);
   
   @Mappings({
   @Mapping(source = "foundUserspermission.permissionId", target = "userspermissionPermissionId"),
   @Mapping(source = "foundUserspermission.usersUserId", target = "userspermissionUsersUserId"),
   })
   GetUsersOutput usersToGetUsersOutput(Users users, Userspermission foundUserspermission);
   
}

