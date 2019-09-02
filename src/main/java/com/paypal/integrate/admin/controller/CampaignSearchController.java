package com.paypal.integrate.admin.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.ThreadManager;
import com.paypal.integrate.admin.api.route.Controller;
import com.paypal.integrate.admin.command.AffsSearchForm;
import com.paypal.integrate.admin.command.AffsSearchResult;
import com.paypal.integrate.admin.command.CampaignSearchForm;
import com.paypal.integrate.admin.command.CampaignSearchResult;
import com.paypal.integrate.admin.repository.CloudStorageRepository;
import com.paypal.integrate.admin.service.AffsSearchService;
import com.paypal.integrate.admin.service.CampaignSearchService;

public class CampaignSearchController implements Controller {
	private final Logger logger = Logger.getLogger(CampaignSearchController.class.getName());
	
	public void index(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		req.setAttribute("countries", this.getCountries());		
		req.getRequestDispatcher("/jsp/campaign.jsp").forward(req, resp);
	}
	
	public void search(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		 final CampaignSearchForm form=CampaignSearchForm.parse(req);
			ThreadManager.createBackgroundThread(new Runnable() {
				@Override
				public void run() {

				        try  {												
					    	  logger.log(Level.WARNING, "*************************Campaign Search Task in the background started ********************");
					    	  CampaignSearchService affsSearchService=new CampaignSearchService();
					  		  Collection<CampaignSearchResult> campaignSearchResults=affsSearchService.processCampaignSearch(form);
							  
					  		  try(Writer writer=new StringWriter()){
					  		    affsSearchService.createFile(writer,form, campaignSearchResults);
					  		  
							    CloudStorageRepository cloudStorageRepository=new CloudStorageRepository();
							    cloudStorageRepository.save(writer,"campaign_ad_rev_search/search"+formatDate(new Date()));
					  		  }
					  		  
//					  		  for(CampaignSearchResult result:campaignSearchResults){
//					  			logger.log(Level.WARNING,result.getGroupValue());  
//								logger.log(Level.WARNING,"totalAdRev="+result.getTotalAdRev());
//								logger.log(Level.WARNING,"offerwallRev="+result.getOfferwallRev());
//								logger.log(Level.WARNING,"Campaign Records #="+result.getCampaignCount());
//							    logger.log(Level.WARNING,"Affs Records #="+result.getAffsCount());
//							    if(result.getAffsCount()!=0){
//							      BigDecimal avrTotalAdRev=result.getTotalAdRev().divide(new BigDecimal(result.getAffsCount()),4, BigDecimal.ROUND_HALF_EVEN);
//							      logger.log(Level.WARNING,"avrTotalAdRev="+avrTotalAdRev);
//							      
//							      BigDecimal avrOfferwallRev=result.getOfferwallRev().divide(new BigDecimal(result.getAffsCount()),4, BigDecimal.ROUND_HALF_EVEN);
//							      logger.log(Level.WARNING,"avrOfferwallRev="+avrOfferwallRev);
//							    }			 			 
//							 }
					  		  
					  		  logger.log(Level.WARNING ,"*************************Campaign Search Task finished*****************");
				   		
				   		}catch(Exception e){
								logger.log(Level.SEVERE, "affs search service:", e);							  
					    }
				        
				        
				}
			}).start();	
			
		 req.setAttribute("countries", this.getCountries());
		 req.setAttribute("success", "Job successfully posted.");
		 req.getRequestDispatcher("/jsp/campaign.jsp").forward(req, resp);			
	}
}
