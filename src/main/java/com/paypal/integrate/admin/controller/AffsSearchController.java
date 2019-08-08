package com.paypal.integrate.admin.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.paypal.integrate.admin.service.AffsSearchService;

public class AffsSearchController implements Controller{
	private final Logger logger = Logger.getLogger(AffsSearchController.class.getName());
	
	public void index(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
	    
		req.setAttribute("countries", this.getCountries());
		req.getRequestDispatcher("/jsp/index.jsp").forward(req, resp);
	}
	
	public void search(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{

		
        Date start=parseDate(req.getParameter("startDate"));
        Date end=parseDate(req.getParameter("endDate"));
        


		String country=(req.getParameter("country").length()==0?null:req.getParameter("country")); 
		//String experiment=req.getParameter("experiment").length()==0?null:req.getParameter("experiment");
		System.out.println(req.getParameterValues("experiments").length);
		
		String packageName=req.getParameter("packageName").length()==0?null:req.getParameter("packageName"); 
		
		ThreadManager.createBackgroundThread(new Runnable() {
				@Override
				public void run() {

				        try  {												
					    	  logger.log(Level.WARNING, "*************************Task in the background started ********************");
					  		  AffsSearchService affsSearchService=new AffsSearchService();
			//				  affsSearchService.processAffsSearch(start, end, country, experiment, packageName);
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

	private Date parseDate(String value)throws ServletException{
		try{
		if(value!= null && value.length() != 0 ){	
			return new SimpleDateFormat("yyyy-MM-dd").parse(value);
		}else{
			return null;
		}
		}catch(ParseException e){
			throw new ServletException(e);
		}
	}
}
