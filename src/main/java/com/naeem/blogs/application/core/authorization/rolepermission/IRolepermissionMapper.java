package com.naeem.blogs.application.core.authorization.rolepermission;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import com.naeem.blogs.domain.core.authorization.permission.Permission;
import com.naeem.blogs.domain.core.authorization.role.Role;
import com.naeem.blogs.application.core.authorization.rolepermission.dto.*;
import com.naeem.blogs.domain.core.authorization.rolepermission.Rolepermission;
import java.time.*;

@Mapper(componentModel = "spring")
public interface IRolepermissionMapper {
   Rolepermission createRolepermissionInputToRolepermission(CreateRolepermissionInput rolepermissionDto);
   
   @Mappings({ 
   @Mapping(source = "permission.displayName", target = "permissionDescriptiveField"),                   
   @Mapping(source = "permission.id", target = "permissionId"),                   
   @Mapping(source = "role.displayName", target = "roleDescriptiveField"),                   
   @Mapping(source = "role.id", target = "roleId"),                   
   }) 
   CreateRolepermissionOutput rolepermissionToCreateRolepermissionOutput(Rolepermission entity);
   
    Rolepermission updateRolepermissionInputToRolepermission(UpdateRolepermissionInput rolepermissionDto);
    
    @Mappings({ 
    @Mapping(source = "entity.permission.displayName", target = "permissionDescriptiveField"),                    
    @Mapping(source = "entity.role.displayName", target = "roleDescriptiveField"),                    
   	}) 
   	UpdateRolepermissionOutput rolepermissionToUpdateRolepermissionOutput(Rolepermission entity);
   	@Mappings({ 
   	@Mapping(source = "entity.permission.displayName", target = "permissionDescriptiveField"),                    
   	@Mapping(source = "entity.role.displayName", target = "roleDescriptiveField"),                    
   	}) 
   	FindRolepermissionByIdOutput rolepermissionToFindRolepermissionByIdOutput(Rolepermission entity);


   @Mappings({
   @Mapping(source = "foundRolepermission.permissionId", target = "rolepermissionPermissionId"),
   @Mapping(source = "foundRolepermission.roleId", target = "rolepermissionRoleId"),
   })
   GetPermissionOutput permissionToGetPermissionOutput(Permission permission, Rolepermission foundRolepermission);
   
   @Mappings({
   @Mapping(source = "foundRolepermission.permissionId", target = "rolepermissionPermissionId"),
   @Mapping(source = "foundRolepermission.roleId", target = "rolepermissionRoleId"),
   })
   GetRoleOutput roleToGetRoleOutput(Role role, Rolepermission foundRolepermission);
   
}

