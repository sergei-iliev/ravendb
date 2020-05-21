package com.luee.wally.api.service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.luee.wally.admin.repository.ApplicationSettingsRepository;
import com.luee.wally.admin.repository.EmailTemplateRepository;
import com.luee.wally.admin.repository.GiftCardRepository;
import com.luee.wally.admin.repository.PaymentRepository;
import com.luee.wally.api.EmailTemplateMgr;
import com.luee.wally.command.Email;
import com.luee.wally.constants.Constants;
import com.luee.wally.entity.EmailTemplate;
import com.luee.wally.entity.PaidUserExternal;
import com.luee.wally.entity.RedeemingRequests;

public class EmailTemplateService extends AbstractService{
	private final Logger logger = Logger.getLogger(EmailTemplateService.class.getName());

	public Collection<EmailTemplate> getEmailTemplatesByContext(String type){
		Collection<EmailTemplate> result=new ArrayList<>();
		EmailTemplateRepository emailTemplateRepository=new EmailTemplateRepository();
		Collection<Entity> templates =emailTemplateRepository.getEmailTemplates(null,type);
		for(Entity entity:templates){			
			result.add(EmailTemplate.valueOf(entity));
		}
		return result;
	}
	
	public void processRedeemingRequestsEmailJob() throws Exception{
		Map<String,String> variables=new HashMap<String, String>();
		
		GiftCardRepository giftCardRepository = new GiftCardRepository();

		ApplicationSettingsService applicationSettingsService = new ApplicationSettingsService();
		String fromEmail = applicationSettingsService
				.getApplicationSettingCached(ApplicationSettingsRepository.SUPPORT_EMAIL);
		
		ZonedDateTime now=ZonedDateTime.now();
		ZonedDateTime yesterday=now.minusHours(24);
		Date from=(Date.from(yesterday.toInstant()));
		
		EmailTemplateRepository emailTemplateRepository=new EmailTemplateRepository();
		Collection<Entity> entities=emailTemplateRepository.getEmailTemplates("confirm_email_reminder_paypal", null);
		String confirmEmailReminderPayPalContent=((Text)entities.iterator().next().getProperty("content")).getValue();
		String confirmEmailReminderPayPalSubject=(String)entities.iterator().next().getProperty("subject");
		
		entities=emailTemplateRepository.getEmailTemplates("confirm_email_alert_paypal", null);
		String confirmEmailAlertPayPalContent=((Text)entities.iterator().next().getProperty("content")).getValue();
		String confirmEmailAlertPayPalSubject=(String)entities.iterator().next().getProperty("subject");
		
		entities=emailTemplateRepository.getEmailTemplates("confirm_email_reminder_amazon", null);
		String confirmEmailReminderAmazonContent=((Text)entities.iterator().next().getProperty("content")).getValue();
		String confirmEmailReminderAmazonSubject=(String)entities.iterator().next().getProperty("subject");
		
		Collection<Entity> jobRecords= emailTemplateRepository.getRedeemingRequestsEmailJobs(from);

		Map<Key,Entity> jobMap=jobRecords.stream().collect(Collectors.toMap(e->KeyFactory.stringToKey((String)e.getProperty("redeeming_request_key")), Function.identity(),(e1,e2)->{return e1;}));

		//redeeming requests
		Map<Key,Entity> map=emailTemplateRepository.findEntitiesByKey(jobMap.keySet());

		for(Entity entity:map.values()){
			RedeemingRequests redeemingRequests= RedeemingRequests.valueOf(entity);
			Entity packageTitle = giftCardRepository.getPackageNameTitleMapping(redeemingRequests.getPackageName());			
			String fromName = (String) packageTitle.getProperty("title")+" Support";
			//prepare variables
			variables.clear();
			variables.put("full_name", redeemingRequests.getFullName());
			variables.put("paypal_account", redeemingRequests.getPaypalAccount());
			variables.put("email", redeemingRequests.getEmail());
			variables.put("redeeming_request_id",redeemingRequests.getRedeemingRequestId());
			
			if(redeemingRequests.getType().equalsIgnoreCase("PayPal")){				
				if(redeemingRequests.getPaypalAccount().equalsIgnoreCase(redeemingRequests.getEmail())){				               
 					String body=EmailTemplateMgr.INSTANCE.processTemplate(confirmEmailReminderPayPalContent, variables);
 					sendEmailReminder(confirmEmailReminderPayPalSubject,body,redeemingRequests.getPaypalAccount(),redeemingRequests.getFullName(),fromEmail,fromName);
				}else{
					//paypal				
 					String body=EmailTemplateMgr.INSTANCE.processTemplate(confirmEmailReminderPayPalContent, variables);
 					sendEmailReminder(confirmEmailReminderPayPalSubject,body,redeemingRequests.getPaypalAccount(),redeemingRequests.getFullName(),fromEmail,fromName);
					
					
					//regular email
 					body=EmailTemplateMgr.INSTANCE.processTemplate(confirmEmailAlertPayPalContent, variables);
 					sendEmailReminder(confirmEmailAlertPayPalSubject,body,redeemingRequests.getEmail(),redeemingRequests.getFullName(),fromEmail,fromName);
					
				}
			}else if(redeemingRequests.getType().equalsIgnoreCase("Amazon")){
				//regular email

				String body=EmailTemplateMgr.INSTANCE.processTemplate(confirmEmailReminderAmazonContent, variables);
				sendEmailReminder(confirmEmailReminderAmazonSubject,body,redeemingRequests.getEmail(),redeemingRequests.getFullName(),fromEmail,fromName);				
			}
			
			//delete record
			Entity jobRecord=jobMap.get(KeyFactory.stringToKey(redeemingRequests.getKey()));
			emailTemplateRepository.deleteEntity(jobRecord.getKey());
		}
	}
	
	public void processExternalPaymentSentEmailJob(String key)throws Exception{
		EmailTemplateRepository emailTemplateRepository=new EmailTemplateRepository();
		Entity sentEmailEntity=emailTemplateRepository.findEntityByKey(key);
		if(sentEmailEntity==null){
			logger.severe("Unable to find record in 'external_payments_sent_email' with key="+key);
		    return;
		}
		String email=(String)sentEmailEntity.getProperty("email_address");
		
		Collection<Entity> entities=emailTemplateRepository.getEmailTemplates("external_payment_cashout_notification", null);
		String content=((Text)entities.iterator().next().getProperty("content")).getValue();
		String subject=(String)entities.iterator().next().getProperty("subject");
		
		PaymentRepository paymentRepository = new PaymentRepository();
		entities = paymentRepository.getExternalPaidUserByEmail(email);
		Entity paidUserExternalEntity=entities.iterator().next();
		PaidUserExternal paidUserExternal =PaidUserExternal.valueOf(paidUserExternalEntity);
		
		Map<String,String> variables=new HashMap<String, String>();
		variables.put("full_name", paidUserExternal.getFullName());
		variables.put("paypal_account",paidUserExternal.getPaypalAccount()); 
		variables.put("email",paidUserExternal.getEmail());
		
		String body=EmailTemplateMgr.INSTANCE.processTemplate(content, variables);		
		
		sendEmailReminder(subject,body,email,paidUserExternal.getFullName(),"pl.time.app@gmail.com","PlayTime Support");				
        
		//update
		sentEmailEntity.setProperty("status",Constants.SENT);
		
		emailTemplateRepository.createOrUpdateEntity(sentEmailEntity);		
	}
	
	private void sendEmailReminder(String subject,String body,String toEmail,String toName,String fromEmail,String fromName) throws IOException{
		Email mail = new Email();
		mail.setFrom(fromEmail);
		mail.setFromName(fromName);
		mail.setTo(toEmail);
		mail.setToName(toName);

		mail.setSubject(subject);
		mail.setContent(body);
		
		MailService mailService = new MailService();
		mailService.sendMailGrid(mail);
		
	}
	
	
}
