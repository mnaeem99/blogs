package com.naeem.blogs.addons.scheduler.domain.job;

import javax.persistence.*;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "qrtz_job_details")
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class JobEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "job_name", nullable = false, length = 256)
	private String jobName;
	
	@Basic
	@Column(name = "job_group", nullable = false, length = 256)
	private String jobGroup;
	
	@Basic
	@Column(name = "description", nullable = true, length = 256)
	private String description;
	
	@Basic
	@Column(name = "sched_name", nullable = false, length = 256)
	private String schedName;
	
		
	@Basic
	@Column(name = "is_update_data", nullable = false, length = 256)
	private Boolean isUpdateData;
	
	@Basic
	@Column(name = "job_class_name", nullable = false, length = 256)
	private String jobClassName;
	
	@Basic
	@Column(name = "is_durable", nullable = false, length = 256)
	private Boolean isDurable;
	
	@Basic
	@Column(name = "is_nonconcurrent", nullable = false, length = 256)
	private Boolean isNonconcurrent;
	
	@Basic
	@Column(name = "job_data", nullable = true, length = 256)
	private byte[] jobData;
	
	@Basic
	@Column(name = "requests_recovery", nullable = false, length = 256)
	private Boolean requestsRecovery;
	
}


