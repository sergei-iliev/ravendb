package com.luee.wally.api.service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import com.luee.wally.admin.repository.ApplicationSettingsRepository;
import com.luee.wally.admin.repository.PaymentReportsRepository;
import com.luee.wally.command.Email;
import com.luee.wally.entity.PaymentAmount;
import com.luee.wally.utils.Utilities;

public class PaymentReportsService {
	private final Logger logger = Logger.getLogger(PaymentReportsService.class.getName());

	
	public PaymentAmount getPaymentReportForYesterday(){
		   ZonedDateTime now=ZonedDateTime.now();
		   ZonedDateTime yesterday=now.minusDays(1);
		   
		   ZonedDateTime yesterdayStart=yesterday.truncatedTo(ChronoUnit.DAYS);
		   ZonedDateTime yesterdayEnd=yesterdayStart.plusHours(24);
		   
		   PaymentReportsRepository paymentReportsRepository=new PaymentReportsRepository();
		   return  paymentReportsRepository.getPaymentReports(Date.from(yesterdayStart.toInstant()), Date.from(yesterdayEnd.toInstant()));
		
	}

	public PaymentAmount getExternalPaymentReportForYesterday(){
		   ZonedDateTime now=ZonedDateTime.now();
		   ZonedDateTime yesterday=now.minusDays(1);
		   
		   ZonedDateTime yesterdayStart=yesterday.truncatedTo(ChronoUnit.DAYS);
		   ZonedDateTime yesterdayEnd=yesterdayStart.plusHours(24);
		   
		   PaymentReportsRepository paymentReportsRepository=new PaymentReportsRepository();
		   return  paymentReportsRepository.getExternalPaymentReports(Date.from(yesterdayStart.toInstant()), Date.from(yesterdayEnd.toInstant()));
		
	}
	
	public void sendReportToMail(PaymentAmount paymentAmount,String subject){
		//create mail body
		 StringBuffer sb=new StringBuffer();
		 sb.append("1. Total amount paid in eur.\r\n");
		 sb.append(" EUR - "+Utilities.formatPrice(paymentAmount.getTotalAmountEur())+"\r\n");	
		 sb.append("2. Total amount paid in each currency.\r\n");
		 
		 for(Map.Entry<String,BigDecimal> entry: paymentAmount.getTotalAmountByCurrencyMap().entrySet()){
			 sb.append(" "+entry.getKey()+" - "+Utilities.formatPrice(entry.getValue())+"\r\n");	 
		 }
		 
		 sb.append("3. Total amount paid per each type (PayPal, Amazon).\r\n");
		 
		 for(Map.Entry<String,BigDecimal> entry: paymentAmount.getTotalAmountByTypeMap().entrySet()){
			 sb.append(" "+entry.getKey()+" - "+Utilities.formatPrice(entry.getValue())+"\r\n");	 
		 }
		 
		 sb.append("4. Total amount paid per amount value.\r\n");
		 for(Map.Entry<String,BigDecimal> entry: paymentAmount.getTotalAmountByAmountMap().entrySet()){
			 sb.append(" "+entry.getKey()+" - "+Utilities.formatPrice(entry.getValue())+"\r\n");	 
		 }

		 sb.append("5. Total amount paid per country code.\r\n");
		 for(Map.Entry<String,BigDecimal> entry: paymentAmount.getTotalAmountByCountryCodeMap().entrySet()){
			 sb.append(" "+entry.getKey()+" - "+Utilities.formatPrice(entry.getValue())+"\r\n");	 
		 }
		 
        ApplicationSettingsService applicationSettingsService=new ApplicationSettingsService();
		 //send mail
		 Email email=new Email();
		 email.setTo(applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYMENT_REPORT_EMAIL_1));
		 email.setContent(sb.toString());
		 email.setFrom(applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.NO_REPLY_EMAIL));
		 email.setSubject(subject);
		 
		 MailService mailService=new MailService();
		 mailService.sendMail(email);	
		 		 
		 email.setTo(applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYMENT_REPORT_EMAIL_2));
		 mailService.sendMail(email);		
	}
}
