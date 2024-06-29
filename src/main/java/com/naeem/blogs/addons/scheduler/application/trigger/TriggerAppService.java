package com.naeem.blogs.addons.scheduler.application.trigger;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import com.naeem.blogs.addons.scheduler.application.trigger.dto.*;
import com.naeem.blogs.addons.scheduler.constants.QuartzConstants;
import com.naeem.blogs.addons.scheduler.domain.jobhistory.JobHistoryEntity;
import com.naeem.blogs.addons.scheduler.domain.jobhistory.IJobHistoryRepository;
import com.naeem.blogs.addons.scheduler.domain.trigger.QTriggerEntity;
import com.naeem.blogs.addons.scheduler.domain.trigger.TriggerEntity;
import com.naeem.blogs.addons.scheduler.domain.trigger.ITriggerRepository;
import com.naeem.blogs.commons.logging.LoggingHelper;
import com.naeem.blogs.commons.search.SearchCriteria;
import com.naeem.blogs.commons.search.SearchFields;
import com.google.gson.Gson;
import com.querydsl.core.BooleanBuilder;

@Service("triggerAppService")
public class TriggerAppService implements ITriggerAppService {

	static final int case1=1;
	static final int case2=2;
	static final int case3=3;
	
	@Autowired
	protected LoggingHelper logHelper;

	@Autowired
	protected SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	@Qualifier("triggerRepository")
	protected ITriggerRepository triggerDetailsRepository;

	@Autowired
	@Qualifier("jobHistoryRepository")
	protected IJobHistoryRepository jobHistoryRepository;

	protected Scheduler scheduler;

	protected QuartzConstants quartzConstant;

	public Scheduler getScheduler() throws SchedulerException
	{
		scheduler=schedulerFactoryBean.getScheduler();
		return scheduler;
	}

	//Update a Trigger
	public boolean updateTrigger(UpdateTriggerInput obj) throws SchedulerException {

		TriggerKey triggerKey =new TriggerKey(obj.getTriggerName(), obj.getTriggerGroup());
		if (getScheduler().checkExists(triggerKey)) {
			Trigger trigger;
			TriggerBuilder triggerBuilder;
			if(obj.getStartTime()==null) {
				obj.setStartTime(new Date());
			}
			if (obj.getEndTime() != null) {
				trigger = getScheduler().getTrigger(triggerKey);
				triggerBuilder = trigger.getTriggerBuilder();
				trigger = triggerBuilder.startAt(obj.getStartTime())
						.endAt(obj.getEndTime()).withDescription(obj.getTriggerDescription())
						.build();
				getScheduler().rescheduleJob(triggerKey,setJobMapDataInTrigger(trigger, obj.getTriggerMapData()));
			} 
			if(obj.getEndTime() ==null) {
				trigger = getScheduler().getTrigger(triggerKey);
				triggerBuilder = trigger.getTriggerBuilder();
				trigger = triggerBuilder.startAt(obj.getStartTime())
						.withDescription(obj.getTriggerDescription()).build();

				getScheduler().rescheduleJob(triggerKey,setJobMapDataInTrigger(trigger, obj.getTriggerMapData()));
			}
			if (obj.getTriggerType().equalsIgnoreCase("Simple")) { 
				return updateSimpleTrigger(obj, triggerKey);
			}
			else if (obj.getTriggerType().equalsIgnoreCase("Cron")) {
				return updateCronTrigger(obj, triggerKey);
			}
			else {
				logHelper.getLogger().error("Trigger type not found");
				return false;
			}
		} 
		else {
			logHelper.getLogger().error(String.format("There does not exist a trigger with a triggerName=%s and triggerGroup=%s",obj.getTriggerName(),obj.getTriggerGroup()));
			return false;	
		}
	}
	
	//Cancel a Trigger
	public boolean cancelTrigger(String triggerName, String triggerGroup) throws SchedulerException {
		TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroup);

		if (getScheduler().checkExists(triggerKey)) {             
			getScheduler().unscheduleJob(triggerKey);
			return true ;
		}
		else {
			logHelper.getLogger().error(String.format("There does not exist a trigger with a triggerName=%s and triggerGroup=%s",triggerName,triggerGroup));
			return false;
		}
	}

	//Return Trigger Details
	public GetTriggerOutput returnTrigger(String triggerName, String triggerGroup) throws SchedulerException {
		TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroup);

		if (getScheduler().checkExists(triggerKey)) {
			GetTriggerOutput obj = new GetTriggerOutput();
			Trigger trigger = getScheduler().getTrigger(triggerKey);
			JobKey jobKey = trigger.getJobKey();

			String trigType = quartzConstant.SIMPLE_TRIGGER;
			if (trigger.getClass().toString().equals(quartzConstant.CRON_TRIGGER_CLASS))
				trigType = quartzConstant.CRON_TRIGGER;

			String[] jobKeyValues = getScheduler().getJobDetail(jobKey).getJobDataMap().getKeys();
			Map<String, String> map = new HashMap<String, String>();
			for (int i = 0; i < jobKeyValues.length; i++) {
				String str = getScheduler().getJobDetail(jobKey).getJobDataMap().getString(jobKeyValues[i]);
				map.put(jobKeyValues[i], str);
			}

			String[] triggerKeyValues = getScheduler().getTrigger(triggerKey).getJobDataMap().getKeys();
			Map<String, String> triggerMap = new HashMap<String, String>();
			for (int i = 0; i < triggerKeyValues.length; i++) {
				String str = getScheduler().getTrigger(triggerKey).getJobDataMap().getString(triggerKeyValues[i]);
				triggerMap.put(triggerKeyValues[i], str);
			}

			obj.setJobName(jobKey.getName());
			obj.setJobGroup(jobKey.getGroup());
			obj.setJobDescription(getScheduler().getJobDetail(jobKey).getDescription());
			obj.setTriggerName(triggerKey.getName());
			obj.setTriggerGroup(triggerKey.getGroup());
			obj.setJobMapData(map);
			obj.setStartTime(trigger.getStartTime());
			obj.setLastExecutionTime(trigger.getPreviousFireTime());
			obj.setNextExecutionTime(trigger.getNextFireTime());
			obj.setEndTime(trigger.getEndTime());
			obj.setTriggerState(getScheduler().getTriggerState(triggerKey).toString());
			obj.setTriggerDescription(getScheduler().getTrigger(triggerKey).getDescription());
			obj.setTriggerMapData(triggerMap);
			obj.setTriggerType(trigType);

			if (trigType.equalsIgnoreCase(quartzConstant.CRON_TRIGGER)) {
				CronTrigger cronTrigger = (CronTrigger) trigger;
				obj.setCronExpression(cronTrigger.getCronExpression());
			} else if (trigType.equalsIgnoreCase(quartzConstant.SIMPLE_TRIGGER)) {
				SimpleTrigger simpleTrigger = (SimpleTrigger) trigger;
				obj.setRepeatCount(simpleTrigger.getRepeatCount());
				int seconds = (int) simpleTrigger.getRepeatInterval() / 1000;
				obj.setRepeatInterval(seconds);
				obj.setRepeatIndefinite(simpleTrigger.REPEAT_INDEFINITELY != -1);
			}

			return obj;
		}
		else {
			logHelper.getLogger().error(String.format("There does not exist a trigger with a triggerName=%s and triggerGroup=%s",triggerName,triggerGroup));
			return null;
		}
	}

	//Pause a Trigger
	public boolean pauseTrigger(String triggerName, String triggerGroup) throws SchedulerException {

		TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroup);
		if (getScheduler().checkExists(triggerKey)) {
			getScheduler().pauseTrigger(triggerKey);
			return true;
		}
		else {
			logHelper.getLogger().error(String.format("There does not exist a trigger with a triggerName=%s and triggerGroup=%s",triggerName,triggerGroup));
			return false;
		}         
	}

	//Resume a trigger
	public boolean resumeTrigger(String triggerName, String triggerGroup) throws SchedulerException {
		TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroup);

		if (getScheduler().checkExists(triggerKey)) {
			getScheduler().resumeTrigger(triggerKey);
			return true;
		}
		else {
			logHelper.getLogger().error(String.format("There does not exist a trigger with a triggerName=%s and triggerGroup=%s",triggerName,triggerGroup));
			return false;
		}
	}
	//Create a Trigger
	public boolean createTrigger(CreateTriggerInput obj) throws SchedulerException {

		if (getScheduler().checkExists(new JobKey(obj.getJobName(), obj.getJobGroup()))) {
			JobDetail jobDetail = getScheduler().getJobDetail(new JobKey(obj.getJobName(), obj.getJobGroup()));

			if (!(getScheduler().checkExists(new TriggerKey(obj.getTriggerName(), obj.getTriggerGroup())))) {
				if(obj.getTriggerType().equalsIgnoreCase("Simple")) {
					Trigger trigger = createSimpleTrigger(obj, jobDetail);
					getScheduler().scheduleJob(setJobMapDataInTrigger(trigger,obj.getTriggerMapData()));
					return true;
				}
				else if(obj.getTriggerType().equalsIgnoreCase("Cron")) {
					Trigger trigger = createCronTrigger(obj, jobDetail);
					getScheduler().scheduleJob(setJobMapDataInTrigger(trigger,obj.getTriggerMapData()));
					return true;
				}
				else {
					logHelper.getLogger().error("Trigger type not found");
					return false;
				}
			}
			else {
			    logHelper.getLogger().error(String.format("There does not exist a trigger with a triggerName=%s and triggerGroup=%s",obj.getTriggerName(),obj.getTriggerGroup()));
				return false;
			}
		}
		else {
		    logHelper.getLogger().error(String.format("There does not exist a job with a jobName=%s and jobGroup=%s",obj.getJobName(),obj.getJobGroup()));
			return false;
		}
	}

	protected Trigger setJobMapDataInTrigger(Trigger trigger, Map<String,String> mapData)
	{ 
		if (mapData !=null) {
			Set set = mapData.entrySet();
			Iterator iterator = set.iterator();
			while (iterator.hasNext()) {
				Map.Entry mentry = (Map.Entry) iterator.next();                           
				trigger.getJobDataMap().put(mentry.getKey().toString(), mentry.getValue().toString());
			}
		}
		return trigger;
	}

	//List Trigger Groups 
	public List<String> listAllTriggerGroups() throws SchedulerException {
		List<String> list = getScheduler().getTriggerGroupNames();
		return list;
	}

	//List All Triggers
	public List<GetTriggerOutput> listAllTriggers(SearchCriteria search,Pageable pageable) throws MalformedURLException, ParseException {
		List<GetTriggerOutput> list = new ArrayList<GetTriggerOutput>();

		Page<TriggerEntity> foundTriggers = triggerDetailsRepository.findAll(searchTriggerDetails(search),pageable);
		List<TriggerEntity> triggerList = foundTriggers.getContent();

		for (int i = 0; i < triggerList.size(); i++) {
			GetTriggerOutput obj = new GetTriggerOutput();

			obj.setJobName(triggerList.get(i).getJobName());
			obj.setJobGroup(triggerList.get(i).getJobGroup());
			obj.setTriggerName(triggerList.get(i).getTriggerName());
			obj.setTriggerGroup(triggerList.get(i).getTriggerGroup());
			obj.setTriggerState(triggerList.get(i).getTriggerState());
			obj.setTriggerType(triggerList.get(i).getTriggerType());
			obj.setTriggerDescription(triggerList.get(i).getDescription());
			obj.setStartTime(millisToDate(triggerList.get(i).getStartTime()));
			obj.setEndTime(millisToDate(triggerList.get(i).getEndTime()));
			obj.setLastExecutionTime(millisToDate(triggerList.get(i).getPrevFireTime()));
			obj.setNextExecutionTime(millisToDate(triggerList.get(i).getNextFireTime()));

			list.add(obj);
		}

		return list;
	}

	protected BooleanBuilder searchTriggerDetails(SearchCriteria search) throws MalformedURLException {

		QTriggerEntity triggerDetails = QTriggerEntity.triggerEntity;
		if(search != null) {
			if(search.getType()==case1) {
				return searchAllPropertiesForTriggerDetails(triggerDetails, search.getValue(),search.getOperator());
			}
			else if(search.getType()==case2) {
				List<String> keysList = new ArrayList<String>();
				for(SearchFields f: search.getFields()) {
					keysList.add(f.getFieldName());
				}
				checkPropertiesForTriggerDetails(keysList);
				return searchSpecificPropertyForTriggerDetails(triggerDetails,keysList,search.getValue(),search.getOperator());
			}
			else if(search.getType()==case3) {
				Map<String,SearchFields> map = new HashMap<>();
				for(SearchFields fieldDetails: search.getFields()) {
					map.put(fieldDetails.getFieldName(),fieldDetails);
				}
				List<String> keysList = new ArrayList<String>(map.keySet());
				checkPropertiesForTriggerDetails(keysList);
				return searchKeyValuePairForTriggerDetails(triggerDetails, map);
			}
		}
		return null;
	}

	protected BooleanBuilder searchAllPropertiesForTriggerDetails(QTriggerEntity triggerDetails,String value,String operator) {
		BooleanBuilder builder = new BooleanBuilder();

		if(operator.equals("contains")) {
			builder.or(triggerDetails.jobName.likeIgnoreCase("%"+ value + "%"));
			builder.or(triggerDetails.jobGroup.likeIgnoreCase("%"+ value + "%"));
			builder.or(triggerDetails.triggerGroup.likeIgnoreCase("%"+ value + "%"));
			builder.or(triggerDetails.triggerName.likeIgnoreCase("%"+ value + "%"));
			builder.or(triggerDetails.triggerState.likeIgnoreCase("%"+ value + "%"));
			builder.or(triggerDetails.triggerType.likeIgnoreCase("%"+ value + "%"));

		}
		else if(operator.equals("equals")) {
			builder.or(triggerDetails.jobName.eq(value));
			builder.or(triggerDetails.jobGroup.eq(value));
			builder.or(triggerDetails.triggerGroup.eq(value));
			builder.or(triggerDetails.triggerName.eq(value));
			builder.or(triggerDetails.triggerState.eq(value));
			builder.or(triggerDetails.triggerType.eq(value));

			Long date=dateStringToMillis(value);

			if(date !=null) {
				builder.or(triggerDetails.endTime.eq(date));
				builder.or(triggerDetails.nextFireTime.eq(date));
				builder.or(triggerDetails.startTime.eq(date));
				builder.or(triggerDetails.prevFireTime.eq(date));
			}
		}

		return builder;
	}

	protected void checkPropertiesForTriggerDetails(List<String> list) throws MalformedURLException
	{
		for (int i = 0; i < list.size(); i++) {
			if(!((list.get(i).replace("%20","").trim().equals("jobName"))
					|| (list.get(i).replace("%20","").trim().equals("jobGroup"))
					|| (list.get(i).replace("%20","").trim().equals("triggerName"))
					|| (list.get(i).replace("%20","").trim().equals("triggerGroup"))
					|| (list.get(i).replace("%20","").trim().equals("triggerState"))
					|| (list.get(i).replace("%20","").trim().equals("triggerType"))
					|| (list.get(i).replace("%20","").trim().equals("nextFireTime"))
					|| (list.get(i).replace("%20","").trim().equals("prevFireTime"))
					|| (list.get(i).replace("%20","").trim().equals("endTime"))
					|| (list.get(i).replace("%20","").trim().equals("startTime"))))
			{
				// Throw an exception
				throw new MalformedURLException("Wrong URL Format: Property " + list.get(i) + " not found!" );
			}
		}
	}

	protected BooleanBuilder searchSpecificPropertyForTriggerDetails(QTriggerEntity triggerDetails,List<String> list,String value,String operator)  {
		BooleanBuilder builder = new BooleanBuilder();

		for (int i = 0; i < list.size(); i++) {

			if(list.get(i).replace("%20","").trim().equals("jobName")) {
				if(operator.equals("contains")) {
					builder.or(triggerDetails.jobName.likeIgnoreCase("%"+ value + "%"));
				}
				else if(operator.equals("equals")) {
					builder.or(triggerDetails.jobName.eq(value));
				}
			}
			if(list.get(i).replace("%20","").trim().equals("jobGroup")) {
				if(operator.equals("contains")) {
					builder.or(triggerDetails.jobGroup.likeIgnoreCase("%"+ value + "%"));
				}
				else if(operator.equals("equals")) {
					builder.or(triggerDetails.jobGroup.eq(value));
				}
			}
			if(list.get(i).replace("%20","").trim().equals("triggerName")) {
				if(operator.equals("contains")) {
					builder.or(triggerDetails.triggerName.likeIgnoreCase("%"+ value + "%"));
				}
				else if(operator.equals("equals")) {
					builder.or(triggerDetails.triggerName.eq(value));
				}
			}
			if(list.get(i).replace("%20","").trim().equals("triggerGroup")) {
				if(operator.equals("contains")) {
					builder.or(triggerDetails.triggerGroup.likeIgnoreCase("%"+ value + "%"));
				}
				else if(operator.equals("equals")) {
					builder.or(triggerDetails.triggerGroup.eq(value));
				}
			}
			if(list.get(i).replace("%20","").trim().equals("triggerState")) {
				if(operator.equals("contains")) {
					builder.or(triggerDetails.triggerState.likeIgnoreCase("%"+ value + "%"));
				}
				else if(operator.equals("equals")) {
					builder.or(triggerDetails.triggerState.eq(value));
				}
			}
			if(list.get(i).replace("%20","").trim().equals("triggerType")) {
				if(operator.equals("contains")) {
					builder.or(triggerDetails.triggerType.likeIgnoreCase("%"+ value + "%"));
				}
				else if(operator.equals("equals")) {
					builder.or(triggerDetails.triggerType.eq(value));
				}
			}
			if(list.get(i).replace("%20","").trim().equals("nextFireTime")) {
				Long date=dateStringToMillis(value);
				if(operator.equals("equals") && date != null ) {
					builder.or(triggerDetails.nextFireTime.eq(date));
				}
			}
			if(list.get(i).replace("%20","").trim().equals("prevFireTime")) {
				Long date=dateStringToMillis(value);
				if(operator.equals("equals") && date != null ) {
					builder.or(triggerDetails.prevFireTime.eq(date));
				}
			}
			if(list.get(i).replace("%20","").trim().equals("startTime")) {
				Long date=dateStringToMillis(value);
				if(operator.equals("equals") && date != null ) {
					builder.or(triggerDetails.startTime.eq(date));
				}
			}
			if(list.get(i).replace("%20","").trim().equals("endTime")) {
				Long date=dateStringToMillis(value);
				if(operator.equals("equals") && date != null ) {
					builder.or(triggerDetails.endTime.eq(date));
				}
			}
		}
		return builder;
	}

	protected BooleanBuilder searchKeyValuePairForTriggerDetails(QTriggerEntity triggerDetails, Map<String,SearchFields> map) {
		BooleanBuilder builder = new BooleanBuilder();

		Iterator<Map.Entry<String, SearchFields>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, SearchFields> details = iterator.next();

			if(details.getKey().replace("%20","").trim().equals("jobName")) {
				if(details.getValue().getOperator().equals("contains"))
					builder.and(triggerDetails.jobName.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				else if(details.getValue().getOperator().equals("equals"))
					builder.and(triggerDetails.jobName.eq(details.getValue().getSearchValue()));
				else if(details.getValue().getOperator().equals("notEqual"))
					builder.and(triggerDetails.jobName.ne(details.getValue().getSearchValue()));
			}
			if(details.getKey().replace("%20","").trim().equals("jobGroup")) {
				if(details.getValue().getOperator().equals("contains"))
					builder.and(triggerDetails.jobGroup.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				else if(details.getValue().getOperator().equals("equals"))
					builder.and(triggerDetails.jobGroup.eq(details.getValue().getSearchValue()));
				else if(details.getValue().getOperator().equals("notEqual"))
					builder.and(triggerDetails.jobGroup.ne(details.getValue().getSearchValue()));
			}
			if(details.getKey().replace("%20","").trim().equals("triggerName")) {
				if(details.getValue().getOperator().equals("contains"))
					builder.and(triggerDetails.triggerName.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				else if(details.getValue().getOperator().equals("equals"))
					builder.and(triggerDetails.triggerName.eq(details.getValue().getSearchValue()));
				else if(details.getValue().getOperator().equals("notEqual"))
					builder.and(triggerDetails.triggerName.ne(details.getValue().getSearchValue()));
			}
			if(details.getKey().replace("%20","").trim().equals("triggerGroup")) {
				if(details.getValue().getOperator().equals("contains"))
					builder.and(triggerDetails.triggerGroup.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				else if(details.getValue().getOperator().equals("equals"))
					builder.and(triggerDetails.triggerGroup.eq(details.getValue().getSearchValue()));
				else if(details.getValue().getOperator().equals("notEqual"))
					builder.and(triggerDetails.triggerGroup.ne(details.getValue().getSearchValue()));
			}
			if(details.getKey().replace("%20","").trim().equals("triggerState")) {
				if(details.getValue().getOperator().equals("contains"))
					builder.and(triggerDetails.triggerState.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				else if(details.getValue().getOperator().equals("equals"))
					builder.and(triggerDetails.triggerState.eq(details.getValue().getSearchValue()));
				else if(details.getValue().getOperator().equals("notEqual"))
					builder.and(triggerDetails.triggerState.ne(details.getValue().getSearchValue()));
			}
			if(details.getKey().replace("%20","").trim().equals("triggerType")) {
				if(details.getValue().getOperator().equals("contains"))
					builder.and(triggerDetails.triggerType.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				else if(details.getValue().getOperator().equals("equals"))
					builder.and(triggerDetails.triggerType.eq(details.getValue().getSearchValue()));
				else if(details.getValue().getOperator().equals("notEqual"))
					builder.and(triggerDetails.triggerType.ne(details.getValue().getSearchValue()));
			}
			if(details.getKey().replace("%20","").trim().equals("nextFireTime")) {
				Long date= dateStringToMillis(details.getValue().getSearchValue());
				if(details.getValue().getOperator().equals("equals") && date !=null)
					builder.and(triggerDetails.nextFireTime.eq(date));
				else if(details.getValue().getOperator().equals("notEqual") && date !=null)
					builder.and(triggerDetails.nextFireTime.ne(date));
				else if(details.getValue().getOperator().equals("range"))
				{
					Long startDate= dateStringToMillis(details.getValue().getStartingValue());
					Long endDate= dateStringToMillis(details.getValue().getEndingValue());
					if(startDate!=null && endDate!=null)	 
						builder.and(triggerDetails.nextFireTime.between(startDate,endDate));
					else if(endDate!=null)
						builder.and(triggerDetails.nextFireTime.loe(endDate));
					else if(startDate!=null)
						builder.and(triggerDetails.nextFireTime.goe(startDate));  
				}

			}
			if(details.getKey().replace("%20","").trim().equals("prevFireTime")) {
				Long date= dateStringToMillis(details.getValue().getSearchValue());
				if(details.getValue().getOperator().equals("equals") && date !=null)
					builder.and(triggerDetails.prevFireTime.eq(date));
				else if(details.getValue().getOperator().equals("notEqual") && date !=null)
					builder.and(triggerDetails.prevFireTime.ne(date));
				else if(details.getValue().getOperator().equals("range"))
				{
					Long startDate= dateStringToMillis(details.getValue().getStartingValue());
					Long endDate= dateStringToMillis(details.getValue().getEndingValue());
					if(startDate!=null && endDate!=null)	 
						builder.and(triggerDetails.prevFireTime.between(startDate,endDate));
					else if(endDate!=null)
						builder.and(triggerDetails.prevFireTime.loe(endDate));
					else if(startDate!=null)
						builder.and(triggerDetails.prevFireTime.goe(startDate));  
				}
			}
			if(details.getKey().replace("%20","").trim().equals("startTime")) {
				Long date= dateStringToMillis(details.getValue().getSearchValue());
				if(details.getValue().getOperator().equals("equals") && date !=null)
					builder.and(triggerDetails.startTime.eq(date));
				else if(details.getValue().getOperator().equals("notEqual") && date !=null)
					builder.and(triggerDetails.startTime.ne(date));
				else if(details.getValue().getOperator().equals("range"))
				{
					Long startDate= dateStringToMillis(details.getValue().getStartingValue());
					Long endDate= dateStringToMillis(details.getValue().getEndingValue());
					if(startDate!=null && endDate!=null)	 
						builder.and(triggerDetails.startTime.between(startDate,endDate));
					else if(endDate!=null)
						builder.and(triggerDetails.startTime.loe(endDate));
					else if(startDate!=null)
						builder.and(triggerDetails.startTime.goe(startDate));  
				}
			}
			if(details.getKey().replace("%20","").trim().equals("endTime")) {
				Long date= dateStringToMillis(details.getValue().getSearchValue());
				if(details.getValue().getOperator().equals("equals") && date !=null)
					builder.and(triggerDetails.endTime.eq(date));
				else if(details.getValue().getOperator().equals("notEqual") && date !=null)
					builder.and(triggerDetails.endTime.ne(date));
				else if(details.getValue().getOperator().equals("range"))
				{
					Long startDate= dateStringToMillis(details.getValue().getStartingValue());
					Long endDate= dateStringToMillis(details.getValue().getEndingValue());
					if(startDate!=null && endDate!=null)	 
						builder.and(triggerDetails.endTime.between(startDate,endDate));
					else if(endDate!=null)
						builder.and(triggerDetails.endTime.loe(endDate));
					else if(startDate!=null)
						builder.and(triggerDetails.endTime.goe(startDate));  
				}
			}
		}

		return builder;
	}

	protected Long dateStringToMillis(String str)
	{
		if(str !=null)
		{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date date;
		try {
			date = formatter.parse(str);
			return date.getTime();

		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		}
		else
			return null;
	}

	protected Date stringToDate(String str)
	{
		if(str !=null)
		{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date date;
		try {
			date = formatter.parse(str);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		}
		else
			return null;
	}

	protected Date millisToDate(long durationInMillis) throws ParseException {
		
		if(durationInMillis > 0)
		{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			String formattedDate = formatter.format(new Date(durationInMillis));
			Date date = formatter.parse(formattedDate);
			return date;
		} 
		else
			return null; 
	}

	//Execution History By Trigger
	public List<GetJobOutput> executionHistoryByTrigger(String triggerName, String triggerGroup) {

		List<JobHistoryEntity> jobList = jobHistoryRepository.findByTrigger(triggerName, triggerGroup);
		return returnJobOutput(jobList);
	}

	protected FindByTriggerOutput returnTriggerDetails (Trigger trigger) throws SchedulerException
	{
		TriggerKey triggKey = trigger.getKey();
		String trigName = triggKey.getName();
		String trigGroup = triggKey.getGroup();
		String trigType = quartzConstant.SIMPLE_TRIGGER;
		if (trigger.getClass().toString().equals(quartzConstant.CRON_TRIGGER_CLASS))
			trigType = quartzConstant.CRON_TRIGGER;

		Date nextFireTime = trigger.getNextFireTime();
		Date endTime = trigger.getEndTime();
		Date fireTime = trigger.getStartTime();
		Date lastFireTime =trigger.getPreviousFireTime();

		String triggerState = getScheduler().getTriggerState(triggKey).toString();
		String triggerDescription = getScheduler().getTrigger(triggKey).getDescription();

		String[] triggerKeyValues =getScheduler().getTrigger(triggKey).getJobDataMap().getKeys();
		Map<String, String> triggerMap = new HashMap<String, String>();
		for (int j = 0; j < triggerKeyValues.length; j++) {
			String str =getScheduler().getTrigger(triggKey).getJobDataMap().getString(triggerKeyValues[j]);
			triggerMap.put(triggerKeyValues[j], str);
		}

		return new FindByTriggerOutput(trigName, trigGroup, triggerState, trigType, triggerDescription, triggerMap, fireTime, endTime,
				lastFireTime, nextFireTime);

	}
	
	protected boolean updateCronTrigger(UpdateTriggerInput obj,TriggerKey triggerKey) throws SchedulerException
	{
		Trigger trigger;
		TriggerBuilder triggerBuilder;
		if (obj.getCronExpression() != null) {                   	
			trigger = getScheduler().getTrigger(triggerKey);
			triggerBuilder = trigger.getTriggerBuilder();
			trigger = triggerBuilder.withSchedule(CronScheduleBuilder
					.cronSchedule(obj.getCronExpression()))
					.build();

			getScheduler().rescheduleJob(triggerKey, trigger);
			return true;
		}
		else {                    
			logHelper.getLogger().error("Cron Expression must not be null");
			return false;
		}
	}

	protected boolean updateSimpleTrigger(UpdateTriggerInput obj,TriggerKey triggerKey) throws SchedulerException
	{
		Trigger trigger;
		TriggerBuilder triggerBuilder;
		if (obj.getRepeatIndefinite() && obj.getRepeatInterval() > 0)
		{
			trigger = getScheduler().getTrigger(triggerKey);
			triggerBuilder = trigger.getTriggerBuilder();
			trigger = triggerBuilder
					.withSchedule(SimpleScheduleBuilder
							.simpleSchedule()
							.repeatForever()
							.withIntervalInSeconds(obj.getRepeatInterval()))
					.build();

			getScheduler().rescheduleJob(triggerKey, trigger);
			return true;
		}
		else if (!obj.getRepeatIndefinite() && obj.getRepeatCount() > 0 && obj.getRepeatInterval() > 0) {
			trigger = getScheduler().getTrigger(triggerKey);
			triggerBuilder = trigger.getTriggerBuilder();
			trigger = triggerBuilder
					.withSchedule(SimpleScheduleBuilder
							.simpleSchedule()
							.withIntervalInSeconds(obj.getRepeatInterval())
							.withRepeatCount(obj.getRepeatCount()))
					.build();
			getScheduler().rescheduleJob(triggerKey, trigger);
			return true;
		} 
		else {
			trigger = getScheduler().getTrigger(triggerKey);
			triggerBuilder= trigger.getTriggerBuilder();
			trigger = triggerBuilder
					.withSchedule(SimpleScheduleBuilder
							.simpleSchedule())
					.build();

			getScheduler().rescheduleJob(triggerKey, trigger);
			return true;
		} 
		
	}

	//CronnTrigger
	protected Trigger createCronTrigger(CreateTriggerInput obj, JobDetail jobDetail)
	{

		if(obj.getCronExpression() == null)
		{
			logHelper.getLogger().error("Cron Expression must not be null");
            return null;
		}
		else
		{
			if(obj.getStartTime() == null)
			{
				obj.setStartTime(new Date());
			}

			if(obj.getEndTime() !=null)
			{
				return TriggerBuilder.newTrigger().forJob(jobDetail)
						.withIdentity(obj.getTriggerName(), obj.getTriggerGroup())
						.withDescription(obj.getTriggerDescription())
						.startAt(obj.getStartTime()).endAt(obj.getEndTime())
						.withSchedule(CronScheduleBuilder.cronSchedule(obj.getCronExpression()))
						.build();
			}
			else
			{
				return TriggerBuilder.newTrigger().forJob(jobDetail)
						.withIdentity(obj.getTriggerName(), obj.getTriggerGroup())
						.withDescription(obj.getTriggerDescription())
						.startAt(obj.getStartTime())
						.withSchedule(CronScheduleBuilder.cronSchedule(obj.getCronExpression()))
						.build();
			}
		}
	}

	protected Trigger createSimpleTrigger (CreateTriggerInput obj, JobDetail jobDetail)
	{
		Trigger trigger ;
		if(obj.getStartTime() ==null)
		{
			obj.setStartTime(new Date());
		}
		if(obj.getEndTime() !=null)
		{
			if (obj.getRepeatIndefinite() && obj.getRepeatInterval() > 0) {
				trigger = TriggerBuilder.newTrigger().forJob(jobDetail)
						.withIdentity(obj.getTriggerName(), obj.getTriggerGroup())
						.withDescription(obj.getTriggerDescription())
						.startAt(obj.getStartTime())
						.endAt(obj.getEndTime()).withSchedule(
								SimpleScheduleBuilder
								.simpleSchedule().repeatForever()
								.withIntervalInSeconds(obj.getRepeatInterval())
								).build();
			} else if (!obj.getRepeatIndefinite() && obj.getRepeatCount() > 0 && obj.getRepeatInterval() > 0) {
				trigger = TriggerBuilder.newTrigger().forJob(jobDetail)
						.withIdentity(obj.getTriggerName(), obj.getTriggerGroup())
						.withDescription(obj.getTriggerDescription())
						.startAt(obj.getStartTime())
						.endAt(obj.getEndTime()).withSchedule(
								SimpleScheduleBuilder
								.simpleSchedule().withRepeatCount(obj.getRepeatCount())
								.withIntervalInSeconds(obj.getRepeatInterval())
								).build();	
			} 
			else
			{
				trigger = TriggerBuilder.newTrigger().forJob(jobDetail)
						.withIdentity(obj.getTriggerName(), obj.getTriggerGroup())
						.withDescription(obj.getTriggerDescription())
						.startAt(obj.getStartTime())
						.endAt(obj.getEndTime()).withSchedule(
								SimpleScheduleBuilder
								.simpleSchedule()).build();
			}

		}
		else
		{
			if (obj.getRepeatIndefinite() && obj.getRepeatInterval() > 0) {
				trigger = TriggerBuilder.newTrigger().forJob(jobDetail)
						.withIdentity(obj.getTriggerName(), obj.getTriggerGroup())
						.withDescription(obj.getTriggerDescription())
						.startAt(obj.getStartTime()).withSchedule(
								SimpleScheduleBuilder
								.simpleSchedule().repeatForever()
								.withIntervalInSeconds(obj.getRepeatInterval())
								).build();
			} else if (!obj.getRepeatIndefinite() && obj.getRepeatCount() > 0 && obj.getRepeatInterval() > 0) {
				trigger = TriggerBuilder.newTrigger().forJob(jobDetail)
						.withIdentity(obj.getTriggerName(), obj.getTriggerGroup())
						.withDescription(obj.getTriggerDescription())
						.startAt(obj.getStartTime()).withSchedule(
								SimpleScheduleBuilder
								.simpleSchedule().withRepeatCount(obj.getRepeatCount())
								.withIntervalInSeconds(obj.getRepeatInterval())
								).build();	
			} else
			{
				trigger = TriggerBuilder.newTrigger().forJob(jobDetail)
						.withIdentity(obj.getTriggerName(), obj.getTriggerGroup())
						.withDescription(obj.getTriggerDescription())
						.startAt(obj.getStartTime()).withSchedule(
								SimpleScheduleBuilder
								.simpleSchedule()).build();
			}
		}

		return trigger;
	}
	
	protected List<GetJobOutput> returnJobOutput(List<JobHistoryEntity> jobList)
	{
		List<GetJobOutput> list = new ArrayList<GetJobOutput>();

		for (int i = 0; i < jobList.size(); i++) {
			GetJobOutput obj = new GetJobOutput();
			obj.setId(jobList.get(i).getId());
			obj.setJobName(jobList.get(i).getJobName());
			obj.setJobGroup(jobList.get(i).getJobGroup());
			obj.setJobClass(jobList.get(i).getJobClass());
			obj.setTriggerName(jobList.get(i).getTriggerName());
			obj.setTriggerGroup(jobList.get(i).getTriggerGroup());
			obj.setJobStatus(jobList.get(i).getJobStatus());
			obj.setJobDescription(jobList.get(i).getJobDescription());
			obj.setFiredTime(jobList.get(i).getFiredTime());
			obj.setFinishedTime(jobList.get(i).getFinishedTime());
			obj.setDuration(jobList.get(i).getDuration());

			String mapData = jobList.get(i).getJobMapData();
			Map<String, String> result = new Gson().fromJson(mapData, HashMap.class);
			obj.setJobMapData(result);

			list.add(obj);
		}

		return list;
	}
}

