package com.naeem.blogs.addons.scheduler.domain.trigger;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository("triggerRepository")
public interface ITriggerRepository extends JpaRepository<TriggerEntity, Long> , QuerydslPredicateExecutor<TriggerEntity> {

}


