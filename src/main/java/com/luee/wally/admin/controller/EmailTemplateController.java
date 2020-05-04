package com.luee.wally.admin.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import com.luee.wally.admin.repository.ApplicationSettingsRepository;
import com.luee.wally.admin.repository.EmailTemplateRepository;
import com.luee.wally.admin.repository.GiftCardRepository;
import com.luee.wally.admin.repository.PaymentRepository;
import com.luee.wally.api.route.Controller;
import com.luee.wally.api.service.ApplicationSettingsService;
import com.luee.wally.api.service.EmailTemplateService;
import com.luee.wally.api.service.MailService;
import com.luee.wally.command.Email;
import com.luee.wally.entity.EmailTemplate;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.json.JSONUtils;

public class EmailTemplateController implements Controller{
	private final Logger logger = Logger.getLogger(EmailTemplateController.class.getName());
	
	
	public void getEmailTemplates(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		//inject email templates
		EmailTemplateService emailTemplateService=new EmailTemplateService();
		Collection<EmailTemplate> emailTemplates=emailTemplateService.getEmailTemplatesByContext(EmailTemplateRepository.SEND_ELIGIBLE_USER_EMAIL_TEMPLATE);
		Map<String,Object> value=new HashMap<String, Object>();
		value.put("result",emailTemplates);
			
		resp.getWriter().write(JSONUtils.writeObject(value));
	}
	public void getEmailTemplateContent(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String key=req.getParameter("key");
		EmailTemplateRepository emailTemplateRepository=new EmailTemplateRepository();
		Entity entity=emailTemplateRepository.findEntityByKey(key);
		EmailTemplate emailTemplate=EmailTemplate.valueOf(entity);
		
		//set content		
		emailTemplate.setContent(((Text)entity.getProperty("content")).getValue());
		Map<String,Object> value=new HashMap<String, Object>();
		value.put("result",emailTemplate);
		
		
		resp.getWriter().write(JSONUtils.writeObject(value));
	}
	public void sendEmailTemplate(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String content=req.getParameter("content");
		String key=req.getParameter("key");
		String subject=req.getParameter("subject");
		
		
		PaymentRepository paymentRepository=new PaymentRepository();
		Entity entity=paymentRepository.getRedeemingRequestsByKey(key);
		
		RedeemingRequests redeemingRequests = RedeemingRequests.valueOf(entity);
		String payPalAccount = redeemingRequests.getPaypalAccount();
		String email = redeemingRequests.getEmail();

		GiftCardRepository giftCardRepository = new GiftCardRepository();
		Entity packageTitle = giftCardRepository.getPackageNameTitleMapping(redeemingRequests.getPackageName());
		if (entity == null) {
			throw new IOException("No title in package to title mapping table.");
		}
		String appName = (String) packageTitle.getProperty("title");
		
		ApplicationSettingsService applicationSettingsService = new ApplicationSettingsService();
		String supportEmail = applicationSettingsService
				.getApplicationSettingCached(ApplicationSettingsRepository.SUPPORT_EMAIL);
		
		
		Email mail = new Email();
		mail.setFrom(supportEmail);
		mail.setFromName(appName + " Support");
		mail.setTo(email);
		mail.setToName(redeemingRequests.getFullName());
		mail.setSubject(subject);
		
		if (!email.equalsIgnoreCase(payPalAccount)) {
			mail.setCC(payPalAccount);
			mail.setCCName(redeemingRequests.getFullName());
		}

		mail.setContent(content);

		MailService mailService = new MailService();
		mailService.sendMailGrid(mail);
	}	
	/*
	 * Job task
	 */
	public void redeemingRequestEmailJob(HttpServletRequest req, HttpServletResponse resp) throws Exception {
  	    logger.log(Level.WARNING, "***********************Start email reminder Job ********************");
		EmailTemplateService emailTemplateService=new EmailTemplateService();
		emailTemplateService.processRedeemingRequestsEmailJob();
  	    logger.log(Level.WARNING, "***********************End email reminder Job ********************");
		
	}
}
