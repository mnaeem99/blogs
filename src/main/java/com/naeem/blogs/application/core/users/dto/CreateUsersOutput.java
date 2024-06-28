package com.naeem.blogs.application.core.users.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateUsersOutput {

    private LocalDateTime createdAt;
    private String email;
    private String passwordHash;
    private LocalDateTime updatedAt;
    private Integer userId;
    private String username;

}

