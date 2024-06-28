package com.naeem.blogs.domain.core.categories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.time.*;

@Repository("categoriesRepository")
public interface ICategoriesRepository extends JpaRepository<Categories, Integer>,QuerydslPredicateExecutor<Categories> {

    
}

