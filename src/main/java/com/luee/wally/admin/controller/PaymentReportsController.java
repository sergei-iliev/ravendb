package com.luee.wally.admin.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.luee.wally.admin.repository.ApplicationSettingsRepository;
import com.luee.wally.api.route.Controller;
import com.luee.wally.api.service.ApplicationSettingsService;
import com.luee.wally.api.service.MailService;
import com.luee.wally.api.service.PaymentReportsService;
import com.luee.wally.command.Email;
import com.luee.wally.entity.PaymentAmount;
import com.luee.wally.utils.Utilities;

public class PaymentReportsController implements Controller{
	private final Logger logger = Logger.getLogger(PaymentReportsController.class.getName());
	
	public void getPaymentReportForYesterday(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
  	    logger.log(Level.WARNING, "***********************Start Payment Reports Job ********************");

		PaymentReportsService paymentReportsService=new PaymentReportsService();
		PaymentAmount paymentAmount=paymentReportsService.getPaymentReportForYesterday();
		
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
		 email.setSubject("Payment Report");
		 
		 MailService mailService=new MailService();
		 mailService.sendMail(email);	
		 		 
		 email.setTo(applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYMENT_REPORT_EMAIL_2));
		 mailService.sendMail(email);
		 
		 logger.log(Level.WARNING, "*************************End Payment Reports Job ********************");
	}

}
