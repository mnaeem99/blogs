package com.naeem.blogs.addons.scheduler.application.job.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class JobListOutput {

	private String jobName;
	private String jobGroup;
	private String jobClass;
	private String jobDescription;
	private String jobStatus;

	public JobListOutput()
	{

	}

	public JobListOutput(String jobName, String jobGroup, String jobClass, String jobDescription, String jobStatus) {
		super();
		this.jobName = jobName;
		this.jobGroup = jobGroup;
		this.jobClass = jobClass;
		this.jobDescription = jobDescription;
		this.jobStatus = jobStatus;
	}

}

