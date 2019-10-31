package com.luee.wally.admin.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.luee.wally.admin.repository.PaymentRepository;
import com.luee.wally.admin.repository.SearchFilterTemplateRepository;
import com.luee.wally.api.route.Controller;
import com.luee.wally.api.service.PaymentService;
import com.luee.wally.api.service.impex.ImportService;
import com.luee.wally.command.PaidUserForm;
import com.luee.wally.command.PaymentEligibleUserForm;
import com.luee.wally.command.WebForm;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.entity.SearchFilterTemplate;
import com.luee.wally.json.ExchangeRateVO;
import com.luee.wally.json.JSONUtils;
import com.luee.wally.utils.Utilities;

public class PaymentController implements Controller {
	private final Logger logger = Logger.getLogger(CampaignSearchController.class.getName());

	public void test(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		String json = "{" + "\"paid_successfully\": true," + "\"email_sent_successfully\": true" + "}";

		resp.getWriter().write(json);
	}
	public void pay(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		
		PaidUserForm form=PaidUserForm.parse(req);	
		
		PaymentRepository paymentRepository=new PaymentRepository();
		//get redeeming request by key 
		Entity redeemingRequests=paymentRepository.getRedeemingRequestsByKey(form.getKey());		
		Entity paidUser=paymentRepository.getPaidUserByGuid((String)redeemingRequests.getProperty("user_guid"));
		/*
		 * Don't pay if already paid up
		 */
		if(paidUser!=null){
		  //****DEBUG*****
		  //Entity paidUser=new Entity("3456364346");
		  //paidUser.setProperty("name","Geologia");
		  //paidUser.setProperty("type","Amazon");
			
			JSONUtils.writeObject(paidUser, Entity.class);
			resp.getWriter().write(JSONUtils.writeObject(paidUser, Entity.class));
			return;
		}
		
		PaymentService paymentService=new PaymentService();
		paymentService.pay(form,redeemingRequests);
		resp.getWriter().write("OK");	

	}
	public void index(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		PaymentEligibleUserForm form = new PaymentEligibleUserForm();

		req.setAttribute("webform", form);
		req.setAttribute("countries", this.getCountries());
		req.getRequestDispatcher("/jsp/payment_eligible_users.jsp").forward(req, resp);
	}


	public void searchByFilterTemplate(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// could come from a search filter template

		SearchFilterTemplateRepository searchFilterTemplateRepository = new SearchFilterTemplateRepository();
		SearchFilterTemplate entity;
		try {
			entity = searchFilterTemplateRepository.findSearchFilterTemplateById(req.getParameter("key"));
		} catch (EntityNotFoundException e) {
			throw new ServletException(e);
		}

		PaymentEligibleUserForm form = entity.create();

		PaymentService paymentService = new PaymentService();
		Collection<RedeemingRequests> entities = paymentService.searchEligibleUsers(form);

		PaymentRepository paymentRepository = new PaymentRepository();
		Collection<String> reasons = paymentRepository.getUserPaymentsRemovalReasons();

		req.setAttribute("webform", form);
		req.setAttribute("entities", entities);
		req.setAttribute("reasons", reasons);
		req.setAttribute("paymentTypes",paymentService.getDefaultPaymentTypes());
		req.setAttribute("defaultCurrencyCodes",paymentService.getDefaultCurrencyCodes());
		req.setAttribute("countries", this.getCountries());
		req.getRequestDispatcher("/jsp/payment_eligible_users.jsp").forward(req, resp);

	}

	public void search(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		PaymentService paymentService = new PaymentService();

		PaymentEligibleUserForm form = PaymentEligibleUserForm.parse(req);

		Collection<RedeemingRequests> entities = paymentService.searchEligibleUsers(form);

		PaymentRepository paymentRepository = new PaymentRepository();
		Collection<String> reasons = paymentRepository.getUserPaymentsRemovalReasons();

//		reasons.add("suspected fraud");
//		reasons.add("payments through website only");
//		reasons.add("no paypal account found");
//		reasons.add("no amazon account found");
//
//		
//		RedeemingRequests r = new RedeemingRequests();
//		r.setKey("11122313123");
//		r.setLink2("/administration/payment/test");
//		r.setAmount("10");
//		r.setCountryCode("US");
//		r.setType("Google Play");
//		r.setDate(new Date());
//		r.setEmail("eee@ee.com");
//		r.setUserGuid("11122313123");
//		entities.add(r);
//
//		r = new RedeemingRequests();	
//		r.setKey("9999999123");
//		r.setLink2("/administration/payment/test");
//		r.setAmount("10");
//		r.setCountryCode("US");
//		r.setType("Amazon");
//		r.setDate(new Date());
//		r.setEmail("eee@ee.com");
//		r.setUserGuid("9999999123");
//		entities.add(r);

		List<String> removalReasons=new LinkedList<String>(reasons);
		removalReasons.add(0,"");
		
		req.setAttribute("webform", form);
		req.setAttribute("entities", entities);
		req.setAttribute("reasons", removalReasons);
		req.setAttribute("paymentTypes",paymentService.getDefaultPaymentTypes());
		req.setAttribute("defaultCurrencyCodes",paymentService.getDefaultCurrencyCodes());
		req.setAttribute("countries", this.getCountries());
		req.getRequestDispatcher("/jsp/payment_eligible_users.jsp").forward(req, resp);

	}
	
	public void saveUserPaymentRemovalReason(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PaymentService paymentService = new PaymentService();
		String key=req.getParameter("key");
		String reason=req.getParameter("reason");
		
		try {
			paymentService.saveUserPaymentRemovalReason(key,reason);
		} catch (EntityNotFoundException e) {			
			logger.log(Level.SEVERE,"Unable to find entity",e);
		    throw new ServletException(e);	
		}
		
		resp.getWriter().write("OK");
	}
}
