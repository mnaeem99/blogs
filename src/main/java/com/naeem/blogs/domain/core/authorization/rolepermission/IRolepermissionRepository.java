package com.naeem.blogs.domain.core.authorization.rolepermission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.time.*;

@Repository("rolepermissionRepository")
public interface IRolepermissionRepository extends JpaRepository<Rolepermission, RolepermissionId>,QuerydslPredicateExecutor<Rolepermission> {

    List<Rolepermission> findByRoleId(Long value);

    
}

