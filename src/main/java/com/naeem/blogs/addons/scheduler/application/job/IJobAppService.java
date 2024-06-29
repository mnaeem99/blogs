package com.naeem.blogs.addons.scheduler.application.job;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.MalformedURLException;
import java.util.List;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.springframework.data.domain.Pageable;

import com.naeem.blogs.addons.scheduler.application.job.dto.*;
import com.naeem.blogs.commons.search.SearchCriteria;

public interface IJobAppService {

	List<JobListOutput> listAllJobs(SearchCriteria search,Pageable pageable) throws MalformedURLException, SchedulerException;
	
	List<FindByTriggerOutput> returnTriggersForAJob(JobKey jobKey) throws SchedulerException;
	
	List<GetExecutingJob> currentlyExecutingJobs() throws SchedulerException ;
	
	List<String> listAllJobClasses() throws URISyntaxException;
	
	List<String> listAllJobGroups() throws SchedulerException, IOException;
	
	boolean pauseJob(String jobName, String jobGroup) throws SchedulerException;
	
	boolean resumeJob(String jobName, String jobGroup) throws SchedulerException ;
	
	boolean deleteJob(String jobName, String jobGroup) throws SchedulerException;
	
	FindByJobOutput returnJob(String jobName, String jobGroup) throws SchedulerException;
	
	boolean updateJob(UpdateJobInput obj) throws SchedulerException;
	
	boolean createJob(CreateJobInput obj) throws SchedulerException, ClassNotFoundException;

	List<GetJobOutput> executionHistoryByJob(String jobName, String jobGroup);
	
	List<GetJobOutput> executionHistory(SearchCriteria search,Pageable pageable) throws MalformedURLException;

}


