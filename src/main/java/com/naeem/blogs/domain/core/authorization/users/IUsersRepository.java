package com.naeem.blogs.domain.core.authorization.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.time.*;

@Repository("usersRepository")
public interface IUsersRepository extends JpaRepository<Users, Long>,QuerydslPredicateExecutor<Users> {

    @Query("select u from Users u where u.username = ?1")
    Users findByUsernameIgnoreCase(String value);  

    Users findByEmailAddressIgnoreCase(String emailAddress);
    
    
}

