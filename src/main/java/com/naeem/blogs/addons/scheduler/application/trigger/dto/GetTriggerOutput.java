package com.naeem.blogs.addons.scheduler.application.trigger.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetTriggerOutput {

    private int repeatCount = 0;
    private int repeatInterval = 0;
    private Boolean repeatIndefinite = false;
    private String cronExpression;
    private String jobName;
    private String jobGroup;
    private String jobDescription;
    private String triggerName;
    private String triggerGroup;
    private String triggerState;
    private String triggerType;
    private String triggerDescription;
    private Date startTime;
    private Date endTime;
    private Date lastExecutionTime;
    private Date nextExecutionTime;
    private Map<String, String> jobMapData = new HashMap<String, String>();
    private Map<String, String> triggerMapData = new HashMap<String, String>();
   
}

