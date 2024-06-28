package com.naeem.blogs.application.extended.posts;

import org.mapstruct.Mapper;
import com.naeem.blogs.application.core.posts.IPostsMapper;

@Mapper(componentModel = "spring")
public interface IPostsMapperExtended extends IPostsMapper {

}

