package com.naeem.blogs.domain.core.posttags;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.time.*;

@Repository("postTagsRepository")
public interface IPostTagsRepository extends JpaRepository<PostTags, PostTagsId>,QuerydslPredicateExecutor<PostTags> {

    
}

