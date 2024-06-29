package com.naeem.blogs.addons.scheduler.domain.jobhistory;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "job_history")
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class JobHistoryEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
    @Id
    @EqualsAndHashCode.Include()
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic
    @Column(name = "job_name", nullable = false, length = 256)
    private String jobName;
    
    @Basic
    @Column(name = "job_description", nullable = true, length = 1024)
    private String jobDescription;

    @Basic
    @Column(name = "job_group", nullable = false, length = 256)
    private String jobGroup;
    
    @Basic
    @Column(name = "job_class", nullable = false, length = 256)
    private String jobClass;

    @Basic
    @Column(name = "fired_time", nullable = false)
    private Date firedTime;

    @Basic
    @Column(name = "finished_time", nullable = true)
    private Date finishedTime;
    
    @Basic
    @Column(name = "trigger_name", nullable = false, length = 256)
    private String triggerName;

    @Basic
    @Column(name = "trigger_group", nullable = false, length = 256)
    private String triggerGroup;

    @Basic
    @Column(name = "duration", nullable = false, length = 256)
    private String duration;
    
    @Basic
    @Column(name = "job_status", nullable = false, length = 256)
    private String jobStatus;
    
    @Basic
    @Column(name = "job_map_data", nullable = true, length = 256)
    private String jobMapData;

    @Override
    public boolean equals(Object o) {
    	 if (this == o) return true;
    	 if (!(o instanceof JobHistoryEntity)) return false;
         JobHistoryEntity that = (JobHistoryEntity) o;
         return id != null && id.equals(that.id);
       
    }

    @Override
    public int hashCode() {
    	 return 31;
    	 
    }
}
