package com.naeem.blogs.application.core.authorization.userspermission.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateUserspermissionOutput {

  	private Long permissionId;
  	private Boolean revoked;
  	private Long usersUserId;
	private String permissionDescriptiveField;
	private String usersDescriptiveField;

}
