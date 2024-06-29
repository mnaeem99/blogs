package com.naeem.blogs.addons.scheduler.application.trigger;

import java.util.List;
import java.text.ParseException;
import java.net.MalformedURLException;
import org.quartz.SchedulerException;
import org.springframework.data.domain.Pageable;

import com.naeem.blogs.addons.scheduler.application.trigger.dto.*;
import com.naeem.blogs.commons.search.SearchCriteria;

public interface ITriggerAppService {

	boolean updateTrigger(UpdateTriggerInput obj) throws SchedulerException;
	
	boolean cancelTrigger(String triggerName, String triggerGroup) throws SchedulerException ;
	
	boolean pauseTrigger(String triggerName, String triggerGroup) throws SchedulerException;
	
	boolean resumeTrigger(String triggerName, String triggerGroup) throws SchedulerException;
	
	boolean createTrigger(CreateTriggerInput obj) throws SchedulerException ;
	
	GetTriggerOutput returnTrigger(String triggerName, String triggerGroup) throws SchedulerException;
		
	List<String> listAllTriggerGroups() throws SchedulerException ;
	
	List<GetTriggerOutput> listAllTriggers(SearchCriteria search,Pageable pageable) throws MalformedURLException, ParseException;

	List<GetJobOutput> executionHistoryByTrigger(String triggerName, String triggerGroup);
}

