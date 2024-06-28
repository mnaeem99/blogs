package com.naeem.blogs.domain.core.tags;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.time.*;

@Repository("tagsRepository")
public interface ITagsRepository extends JpaRepository<Tags, Integer>,QuerydslPredicateExecutor<Tags> {

    
}

