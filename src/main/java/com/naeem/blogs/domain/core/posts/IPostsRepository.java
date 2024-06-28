package com.naeem.blogs.domain.core.posts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.time.*;

@Repository("postsRepository")
public interface IPostsRepository extends JpaRepository<Posts, Integer>,QuerydslPredicateExecutor<Posts> {

    
}

