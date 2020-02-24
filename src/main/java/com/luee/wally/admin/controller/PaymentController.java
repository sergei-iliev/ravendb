package com.luee.wally.admin.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.luee.wally.admin.repository.ApplicationSettingsRepository;
import com.luee.wally.admin.repository.InvoiceRepository;
import com.luee.wally.admin.repository.PaymentRepository;
import com.luee.wally.admin.repository.SearchFilterTemplateRepository;
import com.luee.wally.api.route.Controller;
import com.luee.wally.api.service.ApplicationSettingsService;
import com.luee.wally.api.service.InvoiceService;
import com.luee.wally.api.service.MailService;
import com.luee.wally.api.service.PayPalService;
import com.luee.wally.api.service.PaymentService;
import com.luee.wally.command.Email;
import com.luee.wally.command.PaidUserForm;
import com.luee.wally.command.PayExternalForm;
import com.luee.wally.command.PaymentEligibleUserForm;
import com.luee.wally.command.PdfAttachment;
import com.luee.wally.command.invoice.PayoutResult;
import com.luee.wally.constants.Constants;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.entity.SearchFilterTemplate;
import com.luee.wally.exception.AESSecurityException;
import com.luee.wally.exception.RestResponseException;
import com.luee.wally.json.JSONUtils;
import com.paypal.api.payments.ErrorDetails;
import com.paypal.base.exception.PayPalException;
import com.paypal.base.rest.PayPalRESTException;

public class PaymentController implements Controller {
	private final Logger logger = Logger.getLogger(PaymentController.class.getName());

	public void test(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		String json = "{" + "\"paid_successfully\": true," + "\"email_sent_successfully\": false" + "}";

		resp.getWriter().write(json);
	}
	public void editEmail(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String email=req.getParameter("email");
		String key=req.getParameter("key");
		PaymentService paymentService = new PaymentService();
		paymentService.editEmail(email, key);
		resp.getWriter().write("OK");
	}
	public void editPayPalAccount(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String paypal=req.getParameter("paypal");
		String key=req.getParameter("key");
		PaymentService paymentService = new PaymentService();
		paymentService.editPayPalAccount(paypal, key);
		resp.getWriter().write("OK");
	}
	
	/*
	 * REST complient API mode
	 */
	public void payExternal(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        PaymentService paymentService=new PaymentService(); 
		MailService mailService = new MailService();
		ApplicationSettingsService applicationSettingsService=new ApplicationSettingsService();
		
	    String aesKey=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.SECRET_AES_KEY);

		//AES encoded POST values

		//No sequrity - tests only
		//PayExternalForm form=PayExternalForm.parse(req);
		
		//AES sequrity - production only
		PayExternalForm form;
		try{
		  form = PayExternalForm.parseEncoded(req,aesKey);
		}catch(AESSecurityException e){
			logger.log(Level.SEVERE,"AES sequrity exception",e);
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"AES sequrity exception");						
			return;
		}
		
		//validate  form
		String toInvoiceMail=applicationSettingsService.getApplicationSetting(ApplicationSettingsRepository.TO_INVOICE_MAIL);
		String fromMail=applicationSettingsService.getApplicationSetting(ApplicationSettingsRepository.FROM_MAIL);
		try{
			paymentService.validateExternalForm(form);
		}catch(Exception e){
			logger.log(Level.SEVERE,"External form validation error: ",e);            
			
			//send mail
			String stackTrace=paymentService.convert(e);
			Email email = new Email();
			email.setSubject("External payment error alert!");
			email.setContent((Objects.toString(e.getMessage(), "")) + "/n/n" + stackTrace);
			email.setFrom(fromMail);
			email.setTo(toInvoiceMail);
			mailService.sendMail(email);
			
			//respond error code
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST,e.getMessage());
			return;
		}   
        paymentService.payExternal(resp, form);        
	}
	
	public void sendPayPal(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String key = (String) req.getParameter("key");

		
	    //find user to paypal to
		PaymentRepository paymentRepository = new PaymentRepository();
		Entity user = paymentRepository.getRedeemingRequestsByKey(key);
		
		RedeemingRequests redeemingRequests = RedeemingRequests.valueOf(user);
		String currencyCode=paymentRepository.getPayPalCurrencyCode(redeemingRequests.getCountryCode());

		//convert currency to EUR
		BigDecimal eurAmount;
		try{
			eurAmount = paymentRepository.convert(Double.parseDouble(redeemingRequests.getAmount()), currencyCode);
		}catch(Exception e){
			logger.log(Level.SEVERE,"Currency converter for : "+currencyCode,e);
			resp.getWriter().write("Unable to convert currency to EUR");
			return;
		}
		//check if already paid
		Entity paidUser = paymentRepository.getPaidUserByRedeemingRequestId(redeemingRequests.getRedeemingRequestId());
		/*
		 * Don't pay if already paid up
		 */
		if (paidUser != null) {
			resp.getWriter().write("+");			
			resp.getWriter().write(JSONUtils.writeObject(paidUser, Entity.class));
			return;
		}
		
		PayPalService payPalService = new PayPalService();
		InvoiceService invoiceService = new InvoiceService();
		MailService mailService = new MailService();
		String sStackTrace;
		
		ApplicationSettingsService applicationSettingsService=new ApplicationSettingsService();
		String toInvoiceMail=applicationSettingsService.getApplicationSetting(ApplicationSettingsRepository.TO_INVOICE_MAIL);
		String fromMail=applicationSettingsService.getApplicationSetting(ApplicationSettingsRepository.FROM_MAIL);
		
		InvoiceRepository invoiceRepository = new InvoiceRepository();
		try {
			//payout
			PayoutResult payoutResult = payPalService.payout(redeemingRequests,currencyCode);
			//create invoice number
			String invoiceNumber = Long.toString(invoiceRepository.createInvoiceNumber());			
			
			//save payment
			paymentRepository.savePayPalPayment(redeemingRequests, currencyCode, eurAmount,invoiceNumber, payoutResult.getPayoutBatchId());
            //create invoice
			PdfAttachment attachment = new PdfAttachment();
			attachment.readFromStream(invoiceService.createInvoice(payoutResult, 
													(String) user.getProperty("full_name"),
													(String) user.getProperty("full_address"),
													(String) user.getProperty("country_code"),
													(String) user.getProperty("paypal_account"),
													invoiceNumber));
			//send invoice
			mailService.sendGridInvoice(toInvoiceMail,fromMail, attachment);
			resp.getWriter().write("OK");
		}catch(PayPalRESTException ppe){
			logger.log(Level.SEVERE, "PayPal rest payment", ppe);
			resp.getWriter().write("PayPal exception, check logs for details\r\n");
			for(ErrorDetails errorDetails:ppe.getDetails().getDetails()){
			  resp.getWriter().write(errorDetails.getField()+"\r\n");
			  resp.getWriter().write(errorDetails.getIssue()+"\r\n");
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "payment", ex);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			sStackTrace = sw.toString();
			Email email = new Email();
			email.setSubject("Error alert!");
			email.setContent((Objects.toString(ex.getMessage(), "")) + "/n/n" + sStackTrace);
			email.setFrom(fromMail);
			email.setTo(toInvoiceMail);
			mailService.sendMail(email);
			resp.getWriter().write("Server error, check logs for details.");
		}
		    
	}

	public void sendGiftCard(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String key = (String) req.getParameter("key");

		PaymentService paymentService = new PaymentService();
		try {
			paymentService.sendGiftCard(key);
			resp.getWriter().write("OK");
		} catch (RestResponseException re) {
			resp.getWriter().write(String.valueOf(re.getResponseCode()));
			resp.getWriter().write(":");
			resp.getWriter().write(re.getResponseMessage());
		} catch (Exception e) {
			logger.log(Level.SEVERE, "", e);
			resp.getWriter().write("Server error, check logs for details.");
		}
	}

	public void pay(HttpServletRequest req, HttpServletResponse resp) throws Exception {

		PaidUserForm form = PaidUserForm.parse(req);

		PaymentRepository paymentRepository = new PaymentRepository();
		// get redeeming request by key
		Entity redeemingRequests = paymentRepository.getRedeemingRequestsByKey(form.getKey());
		Entity paidUser = paymentRepository.getPaidUserByGuid((String) redeemingRequests.getProperty("user_guid"));
		/*
		 * Don't pay if already paid up
		 */
		if (paidUser != null) {
			resp.getWriter().write(JSONUtils.writeObject(paidUser, Entity.class));
			return;
		}

		PaymentService paymentService = new PaymentService();
		paymentService.pay(form, redeemingRequests);
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

		ApplicationSettingsRepository applicationSettingsRepository = new ApplicationSettingsRepository();
		Map<String, String> map = applicationSettingsRepository.getApplicationSettings();

		req.setAttribute("isSendGCVisible",
				Boolean.valueOf(map.get(ApplicationSettingsRepository.SHOW_TANGO_GIFT_CARD)));
		req.setAttribute("isPayPalVisible",
				Boolean.valueOf(map.get(ApplicationSettingsRepository.SHOW_PAYPAL_PAY)));		
		req.setAttribute("webform", form);
		req.setAttribute("entities", entities);
		req.setAttribute("reasons", reasons);
		req.setAttribute("paymentTypes", paymentService.getDefaultPaymentTypes());
		req.setAttribute("defaultCurrencyCodes", paymentService.getDefaultCurrencyCodes());
		req.setAttribute("countries", this.getCountries());
		req.getRequestDispatcher("/jsp/payment_eligible_users.jsp").forward(req, resp);

	}

	public void search(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		PaymentService paymentService = new PaymentService();

		PaymentEligibleUserForm form = PaymentEligibleUserForm.parse(req);

		Collection<RedeemingRequests> entities = paymentService.searchEligibleUsers(form);

		PaymentRepository paymentRepository = new PaymentRepository();
		Collection<String> reasons = paymentRepository.getUserPaymentsRemovalReasons();

		List<String> removalReasons = new LinkedList<String>(reasons);
		removalReasons.add(0, "");

		ApplicationSettingsRepository applicationSettingsRepository = new ApplicationSettingsRepository();
		Map<String, String> map = applicationSettingsRepository.getApplicationSettings();

		req.setAttribute("isSendGCVisible",
				Boolean.valueOf(map.get(ApplicationSettingsRepository.SHOW_TANGO_GIFT_CARD)));
		
		req.setAttribute("isPayPalVisible",
				Boolean.valueOf(map.get(ApplicationSettingsRepository.SHOW_PAYPAL_PAY)));
		req.setAttribute("webform", form);
		req.setAttribute("entities", entities);
		req.setAttribute("reasons", removalReasons);
		req.setAttribute("paymentTypes", paymentService.getDefaultPaymentTypes());
		req.setAttribute("defaultCurrencyCodes", paymentService.getDefaultCurrencyCodes());
		req.setAttribute("countries", this.getCountries());
		req.getRequestDispatcher("/jsp/payment_eligible_users.jsp").forward(req, resp);

	}

	public void saveUserPaymentRemovalReason(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PaymentService paymentService = new PaymentService();
		String key = req.getParameter("key");
		String reason = req.getParameter("reason");

		try {
			paymentService.saveUserPaymentRemovalReason(key, reason);
		} catch (EntityNotFoundException e) {
			logger.log(Level.SEVERE, "Unable to find entity", e);
			throw new ServletException(e);
		}

		resp.getWriter().write("OK");
	}
}
