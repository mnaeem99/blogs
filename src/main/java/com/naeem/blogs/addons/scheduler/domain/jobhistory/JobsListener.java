package com.naeem.blogs.addons.scheduler.domain.jobhistory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naeem.blogs.addons.scheduler.BeanUtil;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JobsListener implements JobListener {

    @Override
    public String getName() {
        return "TEST-JOBS";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {

    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {

    	Date finishTime = new Date(context.getJobRunTime() + context.getFireTime().getTime());

         JobHistoryEntity jobentity = new JobHistoryEntity();

         JobDataMap jobMapData = context.getJobDetail().getJobDataMap();
         String[] keys = jobMapData.getKeys();
         Map<String, String> map = new HashMap<String, String>();
         for (int i = 0; i < keys.length; i++) {
             String st = jobMapData.getString(keys[i]);
             map.put(keys[i], st);
         }

         String mapDataJson = null;
         try {
             mapDataJson = new ObjectMapper().writeValueAsString(map);

         } catch (JsonProcessingException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
         }
           jobentity.setJobName(context.getJobDetail().getKey().getName());
           jobentity.setJobGroup(context.getJobDetail().getKey().getGroup());
           jobentity.setJobClass(context.getJobDetail().getKey().getClass().toString());
           jobentity.setTriggerName(context.getTrigger().getKey().getName());
           jobentity.setTriggerGroup(context.getTrigger().getKey().getGroup());
           jobentity.setDuration(timeDifference(context.getJobRunTime()));
           jobentity.setJobMapData(mapDataJson);
           jobentity.setFiredTime(context.getFireTime());
           jobentity.setFinishedTime(finishTime);
           jobentity.setJobDescription(context.getJobDetail().getDescription());
           
           jobentity.setJobStatus(jobException ==null ?"Success" :"Failure");

           IJobHistoryRepository jobHistoryRepository = BeanUtil.getBean(IJobHistoryRepository.class);
           jobHistoryRepository.save(jobentity);
    }

    public static String timeDifference(long durationInMillis) {

        long millis = durationInMillis % 1000;
        long second = (durationInMillis / 1000) % 60;
        long minute = (durationInMillis / (1000 * 60)) % 60;
        long hour = (durationInMillis / (1000 * 60 * 60)) % 24;

        String time = String.format("%02d:%02d:%02d.%d", hour, minute, second, millis);

        return time;
    }

}

