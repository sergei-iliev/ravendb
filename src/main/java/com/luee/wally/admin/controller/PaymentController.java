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
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.luee.wally.admin.repository.CloudStorageRepository;
import com.luee.wally.admin.repository.SearchFilterTemplateRepository;
import com.luee.wally.api.route.Controller;
import com.luee.wally.api.service.CampaignSearchService;
import com.luee.wally.api.service.PaymentService;
import com.luee.wally.command.CampaignSearchResult;
import com.luee.wally.command.PaymentEligibleUserForm;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.entity.SearchFilterTemplate;


public class PaymentController implements Controller{
	private final Logger logger = Logger.getLogger(CampaignSearchController.class.getName());
	
	public void test(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
		String json="{"+
				"\"paid_successfully\": false,"+
				"\"email_sent_successfully\": true"+
				"}";
		
		resp.getWriter().write(json);
	}
	public void index(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		
		PaymentEligibleUserForm form=new PaymentEligibleUserForm();
		req.setAttribute("webform", form);
		req.setAttribute("countries", this.getCountries());	
		req.getRequestDispatcher("/jsp/payment_eligible_users.jsp").forward(req, resp);
	}
	public void searchByFilterTemplate(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
	    //could come from a search filter template	

		SearchFilterTemplateRepository searchFilterTemplateRepository=new SearchFilterTemplateRepository();
		SearchFilterTemplate entity;
		try {
			entity= searchFilterTemplateRepository.findSearchFilterTemplateById(req.getParameter("key"));			
		} catch (EntityNotFoundException e) {
		     throw new ServletException(e);
		}
		
		PaymentEligibleUserForm form=entity.create();

		PaymentService paymentService=new PaymentService();
		Collection<RedeemingRequests> entities= paymentService.searchEligibleUsers(form);
		
		 req.setAttribute("webform", form);
		 req.setAttribute("entities", entities);	
		 req.setAttribute("countries", this.getCountries());		
		 req.getRequestDispatcher("/jsp/payment_eligible_users.jsp").forward(req, resp);	
		
	}
	public void search(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		 PaymentService paymentService=new PaymentService();
		  
		 PaymentEligibleUserForm form=PaymentEligibleUserForm.parse(req);
	 		 
		 
		 Collection<RedeemingRequests> entities= paymentService.searchEligibleUsers(form);
		 
//		 RedeemingRequests r=new RedeemingRequests();
//		 r.setLink2("/administration/payment/test");
//		 r.setAmount("10");
//		 r.setCountryCode("US");
//		 r.setDate(new Date());
//		 r.setEmail("eee@ee.com");
//		 r.setUserGuid("11122313123");
//		 entities.add(r);
//
//		 r=new RedeemingRequests();
//		 r.setLink2("/administration/payment/test");
//		 r.setAmount("10");
//		 r.setCountryCode("US");
//		 r.setDate(new Date());
//		 r.setEmail("eee@ee.com");
//		 r.setUserGuid("9999999123");
//		 entities.add(r);
		 
		 
		 req.setAttribute("webform", form);
		 req.setAttribute("entities", entities);	
		 req.setAttribute("countries", this.getCountries());		
		 req.getRequestDispatcher("/jsp/payment_eligible_users.jsp").forward(req, resp);	
		
	}
}
