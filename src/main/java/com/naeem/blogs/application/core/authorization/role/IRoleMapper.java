package com.naeem.blogs.application.core.authorization.role;

import org.mapstruct.Mapper;
import com.naeem.blogs.application.core.authorization.role.dto.*;
import com.naeem.blogs.domain.core.authorization.role.Role;
import java.time.*;

@Mapper(componentModel = "spring")
public interface IRoleMapper {
   Role createRoleInputToRole(CreateRoleInput roleDto);
   CreateRoleOutput roleToCreateRoleOutput(Role entity);
   
    Role updateRoleInputToRole(UpdateRoleInput roleDto);
    
   	UpdateRoleOutput roleToUpdateRoleOutput(Role entity);
   	FindRoleByIdOutput roleToFindRoleByIdOutput(Role entity);


 	FindRoleByNameOutput roleToFindRoleByNameOutput(Role entity);
 	
}

