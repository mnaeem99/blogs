package com.naeem.blogs.application.core.authorization.users.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetRoleOutput {
    private Long id;
    private String displayName;
    private String name;
    private Long usersUserId;
  	private String usersDescriptiveField;

}


