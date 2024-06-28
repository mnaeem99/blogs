package com.naeem.blogs.application.core.categories.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateCategoriesOutput {

    private Integer categoryId;
    private String description;
    private String name;

}

