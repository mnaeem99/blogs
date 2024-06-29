package com.naeem.blogs.application.core.authorization.rolepermission.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetPermissionOutput {

 	private Long id;
 	private String name;
 	private String displayName;
  	private Long rolepermissionPermissionId;
  	private Long rolepermissionRoleId;

}

