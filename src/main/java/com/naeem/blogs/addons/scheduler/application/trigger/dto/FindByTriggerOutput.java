package com.naeem.blogs.addons.scheduler.application.trigger.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FindByTriggerOutput {

    private String triggerName;
    private String triggerGroup;
    private String triggerState;
    private String triggerType;
    private String triggerDescription;
    private Map<String, String> triggerMapData = new HashMap<String, String>();
    private Date startTime;
    private Date endTime;
    private Date lastExecutionTime;
    private Date nextExecutionTime;

    public FindByTriggerOutput() {

    }

    public FindByTriggerOutput(String triggerName, String triggerGroup, String triggerState, String triggerType, String triggerDescription,
                          Map<String, String> triggerMapData, Date startTime, Date endTime,
                          Date lastExecutionTime, Date nextExecutionTime) {
        super();
        this.triggerName = triggerName;
        this.triggerGroup = triggerGroup;
        this.triggerState = triggerState;
        this.triggerType = triggerType;
        this.triggerDescription = triggerDescription;
        this.triggerMapData = triggerMapData;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lastExecutionTime = lastExecutionTime;
        this.nextExecutionTime = nextExecutionTime;
    }
    
}

