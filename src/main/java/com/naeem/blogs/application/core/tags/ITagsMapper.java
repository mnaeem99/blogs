package com.naeem.blogs.application.core.tags;

import org.mapstruct.Mapper;
import com.naeem.blogs.application.core.tags.dto.*;
import com.naeem.blogs.domain.core.tags.Tags;
import java.time.*;

@Mapper(componentModel = "spring")
public interface ITagsMapper {
   Tags createTagsInputToTags(CreateTagsInput tagsDto);
   CreateTagsOutput tagsToCreateTagsOutput(Tags entity);
   
    Tags updateTagsInputToTags(UpdateTagsInput tagsDto);
    
   	UpdateTagsOutput tagsToUpdateTagsOutput(Tags entity);
   	FindTagsByIdOutput tagsToFindTagsByIdOutput(Tags entity);


}

