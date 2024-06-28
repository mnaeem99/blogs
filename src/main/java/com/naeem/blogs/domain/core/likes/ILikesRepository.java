package com.naeem.blogs.domain.core.likes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.time.*;

@Repository("likesRepository")
public interface ILikesRepository extends JpaRepository<Likes, Integer>,QuerydslPredicateExecutor<Likes> {

    
}

