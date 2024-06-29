package com.naeem.blogs.application.core.authorization.rolepermission.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetRoleOutput {

 	private Long id;
 	private String name;
 	private String displayName;
  	private Long rolepermissionPermissionId;
  	private Long rolepermissionRoleId;

}

