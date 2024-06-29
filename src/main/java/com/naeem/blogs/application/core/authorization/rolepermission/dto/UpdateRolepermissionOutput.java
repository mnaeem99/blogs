package com.naeem.blogs.application.core.authorization.rolepermission.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateRolepermissionOutput {

  	private Long permissionId;
  	private Long roleId;
	private String permissionDescriptiveField;
	private String roleDescriptiveField;

}
