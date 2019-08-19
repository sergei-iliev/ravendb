package com.paypal.integrate.admin.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.ThreadManager;
import com.paypal.integrate.admin.api.route.Controller;
import com.paypal.integrate.admin.command.AffsSearchForm;
import com.paypal.integrate.admin.command.AffsSearchResult;
import com.paypal.integrate.admin.impex.GenerateCSV;
import com.paypal.integrate.admin.repository.CloudStorageRepository;
import com.paypal.integrate.admin.service.AffsSearchService;

public class AffsSearchController implements Controller{
	private final Logger logger = Logger.getLogger(AffsSearchController.class.getName());
	
	public void index(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
	    
		req.setAttribute("countries", this.getCountries());
		req.getRequestDispatcher("/jsp/index.jsp").forward(req, resp);
	}
	
	public void search(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{

		

        final AffsSearchForm form=AffsSearchForm.parse(req);

		ThreadManager.createBackgroundThread(new Runnable() {
				@Override
				public void run() {

				        try  {												
					    	  logger.log(Level.WARNING, "*************************Task in the background started ********************");
					  		  AffsSearchService affsSearchService=new AffsSearchService();
					  		  Collection<AffsSearchResult> affsSearchResults=affsSearchService.processAffsSearch(form);
							  
					  		  try(Writer writer=new StringWriter()){
					  		    affsSearchService.createFile(writer,form, affsSearchResults);
					  		  
							    CloudStorageRepository cloudStorageRepository=new CloudStorageRepository();
							    cloudStorageRepository.save(writer,"affs_ad_rev_search/search"+new Date());
					  		  }
					  		  
//					  		  for(AffsSearchResult result:affsSearchResults){
//								logger.log(Level.WARNING,"experiment="+result.getExperiment());
//								logger.log(Level.WARNING,"totalAdRev="+result.getTotalAdRev());
//								logger.log(Level.WARNING,"offerwallRev="+result.getOfferwallRev());
//								
//							    logger.log(Level.WARNING,"Records #="+result.getCount());
//							    if(result.getCount()!=0){
//							      BigDecimal avrTotalAdRev=result.getTotalAdRev().divide(new BigDecimal(result.getCount()),4, BigDecimal.ROUND_HALF_EVEN);
//							      logger.log(Level.WARNING,"avrTotalAdRev="+avrTotalAdRev);
//							      
//							      BigDecimal avrOfferwallRev=result.getOfferwallRev().divide(new BigDecimal(result.getCount()),4, BigDecimal.ROUND_HALF_EVEN);
//							      logger.log(Level.WARNING,"avrOfferwallRev="+avrOfferwallRev);
//							    }			 			 
//							 }
					  		  
					  		  logger.log(Level.WARNING ,"*************************Background task finished*****************");
				   		
				   		}catch(Exception e){
								logger.log(Level.SEVERE, "affs search service:", e);							  
					    }
				        
				        
				}
			}).start();		
			
		req.setAttribute("countries", this.getCountries());
		req.setAttribute("success", "Job successfully posted.");
		req.getRequestDispatcher("/jsp/index.jsp").forward(req, resp);
	}

	
	private Collection<String> getCountries(){
		String[] locales = Locale.getISOCountries();
		Collection<String> countries=new ArrayList<String>();
		countries.add("");
		for (String countryCode : locales) {
			Locale locale = new Locale("", countryCode);
			countries.add(locale.getCountry());
		}

		return countries;
	}
	
	private Collection<String> getPackagesName(){
		return Arrays.asList("com.moregames.makemoney", "com.coinmachine.app",
		"com.matchmine.app");		
	}


}
