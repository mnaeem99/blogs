package com.naeem.blogs.domain.core.postcategories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.time.*;

@Repository("postCategoriesRepository")
public interface IPostCategoriesRepository extends JpaRepository<PostCategories, PostCategoriesId>,QuerydslPredicateExecutor<PostCategories> {

    
}

