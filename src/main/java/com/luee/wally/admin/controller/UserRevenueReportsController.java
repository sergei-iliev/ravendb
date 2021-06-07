package com.luee.wally.admin.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
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
import com.luee.wally.admin.repository.ApplicationSettingsRepository;
import com.luee.wally.admin.repository.JobsRepository;
import com.luee.wally.admin.repository.JobsRepository.JobStatus;
import com.luee.wally.api.route.Controller;
import com.luee.wally.api.service.ApplicationSettingsService;
import com.luee.wally.api.service.MailService;
import com.luee.wally.api.service.impex.FBUserRevenueService;
import com.luee.wally.api.service.impex.UserRevenueService;
import com.luee.wally.command.Email;

public class UserRevenueReportsController implements Controller{
	private final Logger logger = Logger.getLogger(UserRevenueReportsController.class.getName());
	
	
	/*
	 * Activate job by get request for any date
	 */
	public void runUserAdRevenueDailyReportByDate(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String date=(String) req.getParameter("date");
		
	  	Queue queue = QueueFactory.getQueue("user-rev-job");
	  	queue.add(TaskOptions.Builder.withUrl("/administration/job/user/revenue/background").param("date", date).method(Method.POST));
	 
	}
	
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
	private void processUserRevenueInBackground(String date) throws IOException{
		JobsRepository jobsRepository=new JobsRepository();
		
		UserRevenueService userRevenueService = new UserRevenueService();
		
		 try{
			 
			 Objects.requireNonNull(date,"You must provide a valid date");
			 logger.log(Level.WARNING, "Task in the background started for date="+date);
			 jobsRepository.saveJobEntry(JobsRepository.USER_REVENUE_JOB, JobStatus.STARTED, date);
			 
			 Collection<String> unfinishedPackages=userRevenueService.processUserRevenueAggregated(date);
			 if(unfinishedPackages.isEmpty()){
				 jobsRepository.saveJobEntry(JobsRepository.USER_REVENUE_JOB, JobStatus.FINISHED, date);
				 //delete corresponding entries in user_rev_package
				 userRevenueService.deleteUserRevPackages(date);
			 }else{
				 boolean result=jobsRepository.resetJobFailure(JobsRepository.USER_REVENUE_JOB, JobStatus.INCOMPLETE.name(), date);
				 if(result){
					 long DELAY_MS =1*60*1000;  //1 minutes		
					 Queue queue = QueueFactory.getDefaultQueue();	
					 queue.add(TaskOptions.Builder.withUrl("/administration/job/user/revenue/background").param("date", date).method(Method.POST).countdownMillis(DELAY_MS));			
				 }
				 else{
					 jobsRepository.saveJobEntry(JobsRepository.USER_REVENUE_JOB, JobStatus.ABORTED, date);
					 sendEmailJobAbortAlert(date,unfinishedPackages);
				 }
     }			

   	  logger.log(Level.WARNING ,"Background task finished");
   	  }catch(Exception e){
   		  		 logger.log(Level.SEVERE, "user revenue service:", e);	
				 boolean result=jobsRepository.resetJobFailure(JobsRepository.USER_REVENUE_JOB, JobStatus.INCOMPLETE.name(), date);
				 if(result){
					 long DELAY_MS =1*60*1000;  //1 minutes		
					 Queue queue = QueueFactory.getDefaultQueue();	
					 queue.add(TaskOptions.Builder.withUrl("/administration/job/user/revenue/background").param("date", date).method(Method.POST).countdownMillis(DELAY_MS));			
				 }
				 else{
					 jobsRepository.saveJobEntry(JobsRepository.USER_REVENUE_JOB, JobStatus.ABORTED, date);	   		  		
	   		  		 sendEmailJobAbortAlert(date,Collections.EMPTY_LIST);
				 }  		  		

	       }
	}	
	
	/*
	 * Activate job by get request for any date
	 */
	public void runfbUserAdRevenueDailyReportByDate(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String date=(String) req.getParameter("date");
		
	  	Queue queue = QueueFactory.getQueue("user-rev-job-fb");
	  	queue.add(TaskOptions.Builder.withUrl("/administration/job/user/revenue/background/fb").param("date", date).method(Method.POST));	 
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
	
	
	private void processFBUserRevenueInBackground(String date) throws IOException{
	     JobsRepository jobsRepository=new JobsRepository();
		 try{
	  	     FBUserRevenueService fbUserRevenueService = new FBUserRevenueService();
	 		 jobsRepository.saveJobEntry(JobsRepository.FB_USER_REVENUE_JOB, JobStatus.STARTED, date);
	 		 Collection<String> unfinishedPackages=fbUserRevenueService.processFBUserRevenueAggregated(date);
			 if(unfinishedPackages.isEmpty()){
				 jobsRepository.saveJobEntry(JobsRepository.FB_USER_REVENUE_JOB, JobStatus.FINISHED, date);
			 }else{
				 boolean result=jobsRepository.resetJobFailure(JobsRepository.FB_USER_REVENUE_JOB, JobStatus.INCOMPLETE.name(), date);
				 if(result){
					 long DELAY_MS =1*60*1000;  //1 minutes		
					 Queue queue = QueueFactory.getDefaultQueue();	
					 queue.add(TaskOptions.Builder.withUrl("/administration/job/user/revenue/background/fb").param("date", date).method(Method.POST).countdownMillis(DELAY_MS));			
				 }
				 else{
					 jobsRepository.saveJobEntry(JobsRepository.FB_USER_REVENUE_JOB, JobStatus.ABORTED, date);
					 sendEmailJobAbortAlert(date,unfinishedPackages);
				 }
    }			

  	  logger.log(Level.WARNING ,"Background task finished");
  	  }catch(Exception e){	    	   
				logger.log(Level.SEVERE, "user revenue service:", e);
				 boolean result=jobsRepository.resetJobFailure(JobsRepository.FB_USER_REVENUE_JOB, JobStatus.INCOMPLETE.name(), date);
				 if(result){
					 long DELAY_MS =1*60*1000;  //1 minutes		
					 Queue queue = QueueFactory.getDefaultQueue();	
					 queue.add(TaskOptions.Builder.withUrl("/administration/job/user/revenue/background/fb").param("date", date).method(Method.POST).countdownMillis(DELAY_MS));			
				 }
				 else{
					 jobsRepository.saveJobEntry(JobsRepository.FB_USER_REVENUE_JOB, JobStatus.ABORTED, date);
					 sendEmailJobAbortAlert(date,Collections.EMPTY_LIST);
				 }									

	       }	
	}
	
	private void sendEmailJobAbortAlert(String date,Collection<String> unfinishedPackages)throws IOException{
		ApplicationSettingsService applicationSettingsService = new ApplicationSettingsService();
		String supportEmail = applicationSettingsService
				.getApplicationSettingCached(ApplicationSettingsRepository.SUPPORT_EMAIL);
		
		String email1 = applicationSettingsService
				.getApplicationSettingCached(ApplicationSettingsRepository.PAYMENT_REPORT_EMAIL_1);		
		
		String email2 = applicationSettingsService
				.getApplicationSettingCached(ApplicationSettingsRepository.PAYMENT_REPORT_EMAIL_2);
		
		Email mail = new Email();
		mail.setFrom(supportEmail);
		mail.setFromName("user.revenue.job");
		mail.setTo(email1);
		mail.setToName("support");

		
		mail.setCC(email2);
		mail.setCCName("support");
				
		mail.setSubject("Max Revenue Import has Failed for "+date);
		
		StringBuilder sb=new StringBuilder();
		sb.append("The Max revenue import for "+date+" has failed. please check logs.<br><br>");
		sb.append("Affected apps:<br>");
		unfinishedPackages.forEach(p->{
			sb.append(p);sb.append("<br>");
		});		
		mail.setContent(sb.toString());

		
		MailService mailService = new MailService();
		
		mailService.sendMailGrid(mail);
	}

}
