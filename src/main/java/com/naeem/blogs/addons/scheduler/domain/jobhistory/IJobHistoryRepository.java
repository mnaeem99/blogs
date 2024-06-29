package com.naeem.blogs.addons.scheduler.domain.jobhistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository("jobHistoryRepository")
public interface IJobHistoryRepository extends JpaRepository<JobHistoryEntity, Long>, QuerydslPredicateExecutor<JobHistoryEntity>  {

    JobHistoryEntity save(JobHistoryEntity job);

    @Query("select j from JobHistoryEntity j where j.jobName = ?1 and j.jobGroup = ?2")
    List<JobHistoryEntity> findByJob(String jobName, String jobGroup);

    @Query("select j from JobHistoryEntity j where j.triggerName = ?1 and j.triggerGroup = ?2")
    List<JobHistoryEntity> findByTrigger(String triggerName, String triggerGroup);

}

