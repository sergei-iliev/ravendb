package net.paypal.integrate.controller;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

import net.paypal.integrate.service.UserRevenueService;

@RestController
@RequestMapping("/cron")
public class CronController {
	private final Logger logger = Logger.getLogger(CronController.class.getName());
	   @Autowired	
	   private UserRevenueService userRevenueService;
	   
	   @RequestMapping(value="/user/revenue", method = RequestMethod.GET)
	   public ResponseEntity<String> processUserRevenue() {
	       try{
	    	logger.log(Level.WARNING ,"Cron job started for date="+userRevenueService.getYesterdayDate());   
	    	userRevenueService.processUserRevenue(userRevenueService.getYesterdayDate());
			logger.log(Level.WARNING ,"Cron job finished");
	       }catch(Exception e){
				logger.log(Level.SEVERE, "user revenue service:", e);
				return  ResponseEntity.badRequest().build();								  
	       }
		   return ResponseEntity.ok("OK");
	   }
	   /*
	    * Run as Background task in a Gae Task Queue
	    */
	   @RequestMapping(value="/user/revenue/background", method = RequestMethod.POST)
	   public ResponseEntity<String> processUserRevenueInBackground(@RequestParam String date) {
	       try{
	    	  Objects.requireNonNull(date,"You must provide a valid date");
	    	  logger.log(Level.WARNING, "Task in the background started for date="+date);
	    	  userRevenueService.processUserRevenue(date);
	    	  logger.log(Level.WARNING ,"Background task finished");
	       }catch(Exception e){
				logger.log(Level.SEVERE, "user revenue service:", e);
				return  ResponseEntity.badRequest().build();								  
	       }
		   return ResponseEntity.ok("OK");
	   }
	   @RequestMapping(value="/user/revenue/run", method = RequestMethod.GET)
	   public ResponseEntity<String> runUserRevenueInBackground(@RequestParam String date) {

	    	  Objects.requireNonNull(date,"You must provide a valid date");
	    	  
	    	  Queue queue = QueueFactory.getDefaultQueue();

	    	  queue.add(TaskOptions.Builder.withUrl("/cron/user/revenue/background").param("date", date).method(Method.POST));

 		      return ResponseEntity.ok("Processing date '"+date+"' in the background");
	   }
	   
}
