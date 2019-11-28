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

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.luee.wally.admin.repository.ApplicationSettingsRepository;
import com.luee.wally.admin.repository.InvoiceRepository;
import com.luee.wally.admin.repository.PaymentRepository;
import com.luee.wally.admin.repository.SearchFilterTemplateRepository;
import com.luee.wally.api.route.Controller;
import com.luee.wally.api.service.InvoiceService;
import com.luee.wally.api.service.MailService;
import com.luee.wally.api.service.PayPalService;
import com.luee.wally.api.service.PaymentService;
import com.luee.wally.command.Email;
import com.luee.wally.command.PaidUserForm;
import com.luee.wally.command.PaymentEligibleUserForm;
import com.luee.wally.command.PdfAttachment;
import com.luee.wally.command.invoice.PayoutResult;
import com.luee.wally.constants.Constants;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.entity.SearchFilterTemplate;
import com.luee.wally.exception.RestResponseException;
import com.luee.wally.json.JSONUtils;

public class PaymentController implements Controller {
	private final Logger logger = Logger.getLogger(CampaignSearchController.class.getName());

	public void test(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		String json = "{" + "\"paid_successfully\": true," + "\"email_sent_successfully\": true" + "}";

		resp.getWriter().write(json);
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
			attachment.readFromStream(invoiceService.createInvoice(payoutResult, user, invoiceNumber));
			//send invoice
			mailService.sendInvoice(Constants.toInvoiceMail, attachment);
			resp.getWriter().write("OK");
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "payment", ex);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			String sStackTrace = sw.toString();
			Email email = new Email();
			email.setSubject("Error alert!");
			email.setContent((Objects.toString(ex.getMessage(), "")) + "/n/n" + sStackTrace);
			email.setFrom(Constants.fromMail);
			email.setTo(Constants.toInvoiceMail);
			mailService.sendMail(email);		
		}
		    resp.getWriter().write("Server error, check logs for details.");
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