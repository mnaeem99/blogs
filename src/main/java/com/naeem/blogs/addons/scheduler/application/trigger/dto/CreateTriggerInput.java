package com.naeem.blogs.addons.scheduler.application.trigger.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateTriggerInput {

    private int repeatCount;
    private int repeatInterval;
    private String triggerName;
    private String triggerGroup;
    private String triggerType;
    private Date startTime;
    private Date endTime;
    private String jobName;
    private String jobGroup;
    private String jobClass;
    private String cronExpression;
    private String triggerDescription;
    private Map<String, String> triggerMapData = new HashMap<String, String>();
    private Boolean repeatIndefinite = false;

    public CreateTriggerInput() {
    }

}

