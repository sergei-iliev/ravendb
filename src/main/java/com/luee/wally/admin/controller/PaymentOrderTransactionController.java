package com.luee.wally.admin.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.luee.wally.admin.repository.ApplicationSettingsRepository;
import com.luee.wally.api.route.Controller;
import com.luee.wally.api.service.ApplicationSettingsService;
import com.luee.wally.api.service.MailService;
import com.luee.wally.api.service.PaymentOrderTransactionsService;
import com.luee.wally.command.Email;
import com.luee.wally.command.order.OrderTransactionResult;
import com.luee.wally.constants.Constants;
import com.luee.wally.utils.Utilities;

public class PaymentOrderTransactionController implements Controller {
	
	public void runOrderTransactionReport(HttpServletRequest req, HttpServletResponse resp)
			throws Exception{
		
		
		processPayPalOrderTransactions();
		//Tango card PS
	    ApplicationSettingsService applicationSettingsService=new ApplicationSettingsService(); 
		String platformIdentifier=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.TANGO_CARD_PLATFORM_IDENTIFIER);
		String platformKey=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.TANGO_CARD_PLATFORM_KEY);	
		String customerName=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.TANGO_CARD_CUSTOMER_NAME);
		
		processTangoCardOrderTransactions(platformIdentifier,platformKey,customerName,"Tango Card PS");
		
		platformIdentifier=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.JP_TANGO_CARD_PLATFORM_IDENTIFIER);
		platformKey=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.JP_TANGO_CARD_PLATFORM_KEY);		
		customerName=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.JP_TANGO_CARD_CUSTOMER_NAME);
		
		processTangoCardOrderTransactions(platformIdentifier,platformKey,customerName,"Tango Card JP");
		
		
	}
	private void processTangoCardOrderTransactions(String platformIdentifier,String platformKey,String customerName, String title)throws Exception{
	    //Yesterday report
		ZonedDateTime now=ZonedDateTime.now();
		ZonedDateTime yesterday=now.minusDays(1);
		   
		ZonedDateTime yesterdayStart=yesterday.truncatedTo(ChronoUnit.DAYS);
		ZonedDateTime yesterdayEnd=yesterdayStart.plusHours(24);
		
	    ApplicationSettingsService applicationSettingsService=new ApplicationSettingsService(); 
			

		
		Map<String,String> configMap=new HashMap<>();
		configMap.put(Constants.PLATFORM_IDENTIFIER,platformIdentifier);
		configMap.put(Constants.PLATFORM_KEY,platformKey);
		configMap.put(Constants.TANGO_CARD_CUSTOMER,customerName);
		
		PaymentOrderTransactionsService paymentOrderTransactionsService=new PaymentOrderTransactionsService();
		Collection<OrderTransactionResult> orderTransactionResults=paymentOrderTransactionsService.getGiftCardOrderTransactions(Instant.from(yesterdayStart).toString(),Instant.from(yesterdayEnd).toString(),configMap);
	    //group by
		Map<String,BigDecimal> map= paymentOrderTransactionsService.getOrderTransactionsGroupBy(orderTransactionResults);
		//get the sum in usd
		String formattedDate = Utilities.formatedDate(yesterday, "yyyy-MM-dd");
		BigDecimal usdSum=paymentOrderTransactionsService.calculateTotal(map, formattedDate, "USD");
		
		//get the sum in eur		
		BigDecimal eurSum=paymentOrderTransactionsService.calculateTotal(map, formattedDate, "EUR");
		
        String emailTo=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYMENT_REPORT_EMAIL_1);
        String emailFrom=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.NO_REPLY_EMAIL);
        
		paymentOrderTransactionsService.sendEmail(map, usdSum, eurSum, title+" total at "+formattedDate, emailTo, emailFrom);
        		 		 
		emailTo=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYMENT_REPORT_EMAIL_2);
		paymentOrderTransactionsService.sendEmail(map, usdSum, eurSum, title+" total at "+formattedDate, emailTo, emailFrom);	

		
	}
	private void processPayPalOrderTransactions()throws Exception{				

	    ApplicationSettingsService applicationSettingsService=new ApplicationSettingsService(); 
		String userName=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYPAL_MERCHANT_API_USERNAME);
		String password=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYPAL_MERCHANT_API_PASSWORD);
		String signature=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYPAL_MERCHANT_API_SIGNATURE);

		    
		String paypalMode=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYPAL_MODE);

		Map<String,String> configMap = new HashMap<>();		    
		configMap.put("mode",paypalMode);		
		configMap.put("acct1.UserName", userName);
		configMap.put("acct1.Password", password);
		configMap.put("acct1.Signature", signature);
			
				
	    //Yesterday report
		ZonedDateTime now=ZonedDateTime.now();
		ZonedDateTime yesterday=now.minusDays(1);
		   
		ZonedDateTime yesterdayStart=yesterday.truncatedTo(ChronoUnit.DAYS);
		ZonedDateTime yesterdayEnd=yesterdayStart.plusHours(24);
		
		PaymentOrderTransactionsService paymentOrderTransactionsService=new PaymentOrderTransactionsService();
		Collection<OrderTransactionResult> orderTransactionResults=paymentOrderTransactionsService.getPayPalOrderTransactions(Instant.from(yesterdayStart).toString(),Instant.from(yesterdayEnd).toString(),configMap);  
	    //group by
		Map<String,BigDecimal> map= paymentOrderTransactionsService.getOrderTransactionsGroupBy(orderTransactionResults);
		//get the sum in usd
		String formattedDate = Utilities.formatedDate(yesterday, "yyyy-MM-dd");
		BigDecimal usdSum=paymentOrderTransactionsService.calculateTotal(map, formattedDate, "USD");
		
		//get the sum in eur		
		BigDecimal eurSum=paymentOrderTransactionsService.calculateTotal(map, formattedDate, "EUR");
		
        
        String emailTo=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYMENT_REPORT_EMAIL_1);
        String emailFrom=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.NO_REPLY_EMAIL);
        
		paymentOrderTransactionsService.sendEmail(map, usdSum, eurSum, "PayPal total at "+formattedDate, emailTo, emailFrom);
        		 		 
		emailTo=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYMENT_REPORT_EMAIL_2);
		paymentOrderTransactionsService.sendEmail(map, usdSum, eurSum, "PayPal total at "+formattedDate, emailTo, emailFrom);	
		
	}

}
