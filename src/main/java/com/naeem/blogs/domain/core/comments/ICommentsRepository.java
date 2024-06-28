package com.naeem.blogs.domain.core.comments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.time.*;

@Repository("commentsRepository")
public interface ICommentsRepository extends JpaRepository<Comments, Integer>,QuerydslPredicateExecutor<Comments> {

    
}

