package com.naeem.blogs.addons.scheduler.domain.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository("jobRepository")
public interface IJobRepository extends JpaRepository<JobEntity, Long>, QuerydslPredicateExecutor<JobEntity>  {

}

