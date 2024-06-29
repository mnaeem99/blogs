package com.naeem.blogs.addons.scheduler.restcontrollers;

import com.naeem.blogs.commons.search.SearchCriteria;
import com.naeem.blogs.commons.search.SearchUtils;
import com.naeem.blogs.commons.search.OffsetBasedPageRequest;
import com.naeem.blogs.addons.scheduler.application.job.IJobAppService;
import com.naeem.blogs.addons.scheduler.application.job.dto.*;

import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.net.URISyntaxException;
import java.net.MalformedURLException;

@RestController
@RequestMapping(value = "/scheduler/jobs")
public class JobController {

	@Autowired
	@Qualifier("jobAppService")
	protected IJobAppService jobAppService;

	@Autowired
	protected Environment env;

	@PreAuthorize("hasAnyAuthority('JOBDETAILSENTITY_READ')")
	@RequestMapping(method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<List<JobListOutput>> listAllJobs(@RequestParam(value = "search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort) throws MalformedURLException, SchedulerException{
		if (offset == null) { offset = env.getProperty("blog.offset.default"); }
		if (limit == null) { limit = env.getProperty("blog.limit.default"); }

		Pageable offsetPageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);
		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);
		List<JobListOutput> list = jobAppService.listAllJobs(searchCriteria,offsetPageable);

		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('JOBDETAILSENTITY_READ')")
	@RequestMapping(value = "/getJobGroups", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<List<String>> listAllJobGroups() throws SchedulerException, IOException {
		List<String> list = jobAppService.listAllJobGroups();

		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('JOBDETAILSENTITY_READ')")
	@RequestMapping(value = "/{jobName}/{jobGroup}", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<FindByJobOutput> returnJob(@PathVariable String jobName, @PathVariable String jobGroup) throws SchedulerException, IOException {

		if(jobName == null || jobGroup == null)
		{
			throw new IOException("Invalid input");
		}

		FindByJobOutput detail = jobAppService.returnJob(jobName, jobGroup);
		if(detail == null) {
			throw new EntityNotFoundException(String.format("There does not exist a job with a jobName=%s and jobGroup=%s", jobName , jobGroup));
		}

		return new ResponseEntity<>(detail, HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('JOBDETAILSENTITY_READ')")
	@RequestMapping(value = "/getJobClasses", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<List<String>> listAllJobClasses() throws URISyntaxException{
		List<String> list = jobAppService.listAllJobClasses();

		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('JOBDETAILSENTITY_READ')")
	@RequestMapping(value = "/executingJobs", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<List<GetExecutingJob>> listCurrentlyExecutingJobs() throws SchedulerException {
		List<GetExecutingJob> list = jobAppService.currentlyExecutingJobs();

		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('JOBDETAILSENTITY_DELETE')")
	@RequestMapping(value = "/{jobName}/{jobGroup}", method = RequestMethod.DELETE, consumes = {"application/json"})
	public ResponseEntity<Boolean> deleteJob(@PathVariable String jobName, @PathVariable String jobGroup) throws SchedulerException, IOException {

		if(jobName == null || jobGroup == null)
		{
			throw new IOException("Invalid input");
		}

		boolean status = jobAppService.deleteJob(jobName, jobGroup);
		if(!status) {
			throw new EntityNotFoundException(String.format("There does not exist a job with a jobName=%s and jobGroup=%s", jobName , jobGroup));
		}
		
		return new ResponseEntity<>(status, HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('JOBDETAILSENTITY_UPDATE')")
	@RequestMapping(value = "/{jobName}/{jobGroup}", method = RequestMethod.PUT, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<Boolean> updateJob(@PathVariable String jobName, @PathVariable String jobGroup, @RequestBody @Valid UpdateJobInput obj) throws SchedulerException, IOException {

		if(jobName == null || jobGroup == null)
		{
			throw new IOException("Invalid input");
		}
		obj.setJobName(jobName);
		obj.setJobGroup(jobGroup);
		boolean status = jobAppService.updateJob(obj);
		if(!status) {
			throw new EntityNotFoundException(String.format("There does not exist a job with a jobName=%s and jobGroup=%s", jobName , jobGroup));
		}
		
		return new ResponseEntity<>(status, HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('JOBDETAILSENTITY_READ')")
	@RequestMapping(value = "/pauseJob/{jobName}/{jobGroup}", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<Boolean> pauseJob(@PathVariable String jobName, @PathVariable String jobGroup) throws SchedulerException, IOException {

		if(jobName == null || jobGroup == null)
		{
			throw new IOException("Invalid input");
		}

		boolean status = jobAppService.pauseJob(jobName, jobGroup);
		if(!status) {
			throw new EntityNotFoundException(String.format("There does not exist a job with a jobName=%s and jobGroup=%s", jobName , jobGroup));
		}
		
		return new ResponseEntity<>(status, HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('JOBDETAILSENTITY_READ')")
	@RequestMapping(value = "/resumeJob/{jobName}/{jobGroup}", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<Boolean> resumeJob(@PathVariable String jobName, @PathVariable String jobGroup) throws SchedulerException, IOException {

		if(jobName == null || jobGroup == null)
		{
			throw new IOException("Invalid input");
		}

		boolean status = jobAppService.resumeJob(jobName, jobGroup);
		if(!status) {
			throw new EntityNotFoundException(String.format("There does not exist a job with a jobName=%s and jobGroup=%s", jobName , jobGroup));
		}
		
		return new ResponseEntity<>(status, HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('JOBDETAILSENTITY_CREATE')")
	@RequestMapping(method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<CreateJobInput> createJob(@RequestBody @Valid CreateJobInput obj) throws SchedulerException, ClassNotFoundException, IOException {

		if(obj.getJobClass() == null || obj.getJobName() == null || obj.getJobGroup() == null)
		{
			throw new IOException("Invalid input");
		}

		boolean status = jobAppService.createJob(obj);
		if(!status) {
			throw new EntityNotFoundException(String.format("There does not exist a job with a jobName=%s and jobGroup=%s", obj.getJobClass() , obj.getJobGroup()));
		}
		
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('JOBDETAILSENTITY_READ')")
	@RequestMapping(value = "/{jobName}/{jobGroup}/triggers", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<List<FindByTriggerOutput>> returnTriggerForJob(@PathVariable String jobName, @PathVariable String jobGroup) throws SchedulerException, IOException {

		if(jobName == null || jobGroup == null)
		{
			throw new IOException("Invalid input");
		}

		List<FindByTriggerOutput> triggerDetails = jobAppService.returnTriggersForAJob(new JobKey(jobName, jobGroup));
		if(triggerDetails == null) {
			throw new EntityNotFoundException(String.format("There does not exist a job with a jobName=%s and jobGroup=%s", jobName , jobGroup));
		}
		return new ResponseEntity<>(triggerDetails, HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('JOBDETAILSENTITY_READ')")
	@RequestMapping(value = "/{jobName}/{jobGroup}/jobExecutionHistory", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<List<GetJobOutput>> executionHistoryByJob(@PathVariable String jobName, @PathVariable String jobGroup) throws IOException {

		if(jobName == null || jobGroup == null)
		{
			throw new IOException("Invalid input");
		}

		List<GetJobOutput> list = jobAppService.executionHistoryByJob(jobName, jobGroup);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('JOBDETAILSENTITY_READ')")
	@RequestMapping(value= "/jobExecutionHistory", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<List<GetJobOutput>> executionHistory (@RequestParam(value = "search", required=false) String search,@RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort) throws MalformedURLException
	{
		if (offset == null) { offset = env.getProperty("blog.offset.default"); }
		if (limit == null) { limit = env.getProperty("blog.limit.default"); }

		Pageable offsetPageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);
		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);
		return new ResponseEntity<>(jobAppService.executionHistory(searchCriteria,offsetPageable),HttpStatus.OK);

	}

}

