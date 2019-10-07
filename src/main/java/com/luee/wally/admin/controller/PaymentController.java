package com.luee.wally.admin.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.ThreadManager;
import com.luee.wally.admin.repository.CloudStorageRepository;
import com.luee.wally.api.route.Controller;
import com.luee.wally.api.service.CampaignSearchService;
import com.luee.wally.api.service.PaymentService;
import com.luee.wally.command.CampaignSearchResult;
import com.luee.wally.command.PaymentEligibleUserForm;
import com.luee.wally.entity.RedeemingRequests;


public class PaymentController implements Controller{
	private final Logger logger = Logger.getLogger(CampaignSearchController.class.getName());
	
	//private Map<String, Integer>  result=new ConcurrentHashMap<String, Integer>();
	
	public void index(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		PaymentEligibleUserForm form=new PaymentEligibleUserForm();
		req.setAttribute("webform", form);
		req.setAttribute("countries", this.getCountries());	
		req.getRequestDispatcher("/jsp/payment_eligible_users.jsp").forward(req, resp);
	}
	public void search(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		 PaymentService paymentService=new PaymentService();
		 PaymentEligibleUserForm form=PaymentEligibleUserForm.parse(req);
		 
//		 for(int i=0;i<3;i++){
//			final String id="Thread #"+i; 
//			ThreadManager.createBackgroundThread(new Runnable() {
//				@Override
//				public void run() {
//
//				        try  {												
//					    	  logger.log(Level.WARNING, "************************* Search Task in the background started ********************");
//					    	  for(int i=0;i<1000;i++){
//					    		  Thread.currentThread().sleep(1000);
//					    		  System.out.println("Hello from "+id +":"+result);
//					    		  Integer count=result.get(id);
//					    		  if(count==null){
//					    			  result.put(id,i);
//					    		  }else{
//					    			  result.put(id,count.intValue()+1);
//					    		  }
//					    	  }
//
//					  		  System.out.println("dump from  id:"+id+":"+result);
//					  		  logger.log(Level.WARNING ,"************************* Search Task finished*****************");
//				   		
//				   		}catch(Exception e){
//								logger.log(Level.SEVERE, "affs search service:", e);							  
//					    }
//				        
//				        
//				}
//			}).start();	
//		 }
		 
		 
		 Collection<RedeemingRequests> entities= paymentService.searchEligibleUsers(form);
		 req.setAttribute("webform", form);
		 req.setAttribute("entities", entities);	
		 req.setAttribute("countries", this.getCountries());		
		 req.getRequestDispatcher("/jsp/payment_eligible_users.jsp").forward(req, resp);	


		
	}
}
