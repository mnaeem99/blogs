package com.naeem.blogs.addons.scheduler.application.job.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateJobInput {

	private String jobName;
	private String jobGroup;
	private String jobClass;
	private String jobDescription;
	private Boolean isDurable =false;
	private Map<String, String> jobMapData = new HashMap<String, String>();
	private List<FindByTriggerOutput> triggerDetails = new ArrayList<FindByTriggerOutput>();
	private String jobStatus;

	public CreateJobInput() {

	}

	public CreateJobInput(String jName, String jGroup, String jClass, String jobDescription, Map<String, String> jMapData, List<FindByTriggerOutput> triggerDetails, boolean isDurable, String jobStatus) {
		super();
		this.jobName = jName;
		this.jobGroup = jGroup;
		this.jobClass = jClass;
		this.jobDescription = jobDescription;
		this.jobMapData = jMapData;
		this.isDurable = isDurable;
		this.jobStatus = jobStatus;
		this.setTriggerDetails(triggerDetails);
	}
}

