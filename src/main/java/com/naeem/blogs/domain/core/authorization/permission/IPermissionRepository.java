package com.naeem.blogs.domain.core.authorization.permission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.time.*;

@Repository("permissionRepository")
public interface IPermissionRepository extends JpaRepository<Permission, Long>,QuerydslPredicateExecutor<Permission> {

	@Query("select u from Permission u where u.name = ?1")
    Permission findByPermissionName(String value);
    
    
    
}

