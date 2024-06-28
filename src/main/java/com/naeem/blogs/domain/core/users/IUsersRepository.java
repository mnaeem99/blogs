package com.naeem.blogs.domain.core.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.time.*;

@Repository("usersRepository")
public interface IUsersRepository extends JpaRepository<Users, Integer>,QuerydslPredicateExecutor<Users> {

    
}

