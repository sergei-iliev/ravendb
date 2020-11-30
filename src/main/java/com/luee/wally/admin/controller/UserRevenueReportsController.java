package com.luee.wally.admin.controller;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.luee.wally.admin.repository.JobsRepository;
import com.luee.wally.admin.repository.JobsRepository.JobStatus;
import com.luee.wally.api.route.Controller;
import com.luee.wally.api.service.impex.FBUserRevenueService;
import com.luee.wally.api.service.impex.UserRevenueService;

public class UserRevenueReportsController implements Controller{
	private final Logger logger = Logger.getLogger(UserRevenueReportsController.class.getName());
	
	/*
	 * Activated by cron job on daily bases FB
	 */
	public void runUserAdRevenueDailyReport(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		FBUserRevenueService userRevenueService = new FBUserRevenueService();
		String date = userRevenueService.getYesterdayDate();
		
	  	Queue queue = QueueFactory.getQueue("user-rev-job");
	  	queue.add(TaskOptions.Builder.withUrl("/administration/job/user/revenue/background").param("date", date).method(Method.POST));
	 
	}
	/*
	 * gets called from the task queue in the background via Router
	 */
	public void runUserAdRevenueDailyReportInBackground(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String date=(String) req.getParameter("date");
		processUserRevenueInBackground(date);
	}
	private void processUserRevenueInBackground(String date) {
		JobsRepository jobsRepository=new JobsRepository();
		UserRevenueService userRevenueService = new UserRevenueService();
		
		 try{
			 
			 Objects.requireNonNull(date,"You must provide a valid date");
			 logger.log(Level.WARNING, "Task in the background started for date="+date);
			 jobsRepository.saveJobEntry(JobsRepository.USER_REVENUE_JOB, JobStatus.STARTED, date);
			 boolean result=userRevenueService.processUserRevenueAggregated(date);
			 if(result){
				 jobsRepository.saveJobEntry(JobsRepository.USER_REVENUE_JOB, JobStatus.FINISHED, date);
			 }else{
				 result=jobsRepository.resetJobFailure(JobsRepository.USER_REVENUE_JOB, JobStatus.INCOMPLETE.name(), date);
				 if(result){
					 long DELAY_MS =10*60*1000;  //10 minutes		
					 Queue queue = QueueFactory.getDefaultQueue();	
					 queue.add(TaskOptions.Builder.withUrl("/cron/user/revenue/background").param("date", date).method(Method.POST).countdownMillis(DELAY_MS));			
				 }
				 else{
					 jobsRepository.saveJobEntry(JobsRepository.USER_REVENUE_JOB, JobStatus.ABORTED, date);
				 }
     }			

   	  logger.log(Level.WARNING ,"Background task finished");
   	  }catch(Exception e){
	    	   
				logger.log(Level.SEVERE, "user revenue service:", e);
				jobsRepository.saveJobEntry(JobsRepository.USER_REVENUE_JOB, JobStatus.ABORTED, date);

	       }
	}	
	
	/*
	 * Activated by cron job on daily bases FB
	 */
	public void runfbUserAdRevenueDailyReport(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		FBUserRevenueService userRevenueService = new FBUserRevenueService();
		String date = userRevenueService.getYesterdayDate();
		
	  	Queue queue = QueueFactory.getQueue("user-rev-job-fb");
	  	queue.add(TaskOptions.Builder.withUrl("/administration/job/user/revenue/background/fb").param("date", date).method(Method.POST));
	 
	}
	
	/*
	 * gets called from the task queue in the background via Router
	 */
	public void runfbUserAdRevenueDailyReportInBackground(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String date=(String) req.getParameter("date");
		processFBUserRevenueInBackground(date);
	}
	
	
	private void processFBUserRevenueInBackground(String date) {
	     JobsRepository jobsRepository=new JobsRepository();
		 try{
	  	     FBUserRevenueService fbUserRevenueService = new FBUserRevenueService();
	 		 jobsRepository.saveJobEntry(JobsRepository.FB_USER_REVENUE_JOB, JobStatus.STARTED, date);
			 boolean result=fbUserRevenueService.processFBUserRevenueAggregated(date);
			 if(result){
				 jobsRepository.saveJobEntry(JobsRepository.FB_USER_REVENUE_JOB, JobStatus.FINISHED, date);
			 }else{
				 result=jobsRepository.resetJobFailure(JobsRepository.FB_USER_REVENUE_JOB, JobStatus.INCOMPLETE.name(), date);
				 if(result){
					 long DELAY_MS =10*60*1000;  //10 minutes		
					 Queue queue = QueueFactory.getDefaultQueue();	
					 queue.add(TaskOptions.Builder.withUrl("/administration/job/user/revenue/background/fb").param("date", date).method(Method.POST).countdownMillis(DELAY_MS));			
				 }
				 else{
					 jobsRepository.saveJobEntry(JobsRepository.FB_USER_REVENUE_JOB, JobStatus.ABORTED, date);
				 }
    }			

  	  logger.log(Level.WARNING ,"Background task finished");
  	  }catch(Exception e){
	    	   
				logger.log(Level.SEVERE, "user revenue service:", e);
				jobsRepository.saveJobEntry(JobsRepository.FB_USER_REVENUE_JOB, JobStatus.ABORTED, date);					

	       }	
	}

}
