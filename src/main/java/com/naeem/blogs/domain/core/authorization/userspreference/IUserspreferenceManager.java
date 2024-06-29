package com.naeem.blogs.domain.core.authorization.userspreference;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUserspreferenceManager {
   
    Userspreference create(Userspreference userspreference);

    void delete(Userspreference userspreference);

    Userspreference update(Userspreference userspreference);
    
    Userspreference findById(Long  id);
    
    Page<Userspreference> findAll(Predicate predicate, Pageable pageable);
  
}



