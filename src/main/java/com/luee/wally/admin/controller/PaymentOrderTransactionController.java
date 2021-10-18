package com.luee.wally.admin.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.luee.wally.admin.repository.ApplicationSettingsRepository;
import com.luee.wally.api.route.Controller;
import com.luee.wally.api.service.ApplicationSettingsService;
import com.luee.wally.api.service.GiftCardService;
import com.luee.wally.api.service.MailService;
import com.luee.wally.api.service.PaidUsersService;
import com.luee.wally.api.service.PaymentOrderTransactionsService;
import com.luee.wally.command.Email;
import com.luee.wally.command.order.OrderTransactionResult;
import com.luee.wally.constants.Constants;
import com.luee.wally.entity.PaidUser;
import com.luee.wally.utils.Utilities;

import urn.ebay.api.PayPalAPI.GetBalanceResponseType;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;

public class PaymentOrderTransactionController implements Controller {
	private final Logger logger = Logger.getLogger(PaymentOrderTransactionController.class.getName());
	/*
	   send a report daily at 07:06 GMT with the current balance for EUR and for USD.
	   Execute as GAE job
	 */
	public void runPayPalBalanceDaily(HttpServletRequest req, HttpServletResponse resp) throws Exception{
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
		
		
		PaymentOrderTransactionsService paymentOrderTransactionsService=new PaymentOrderTransactionsService();
		GetBalanceResponseType basicAmountType= paymentOrderTransactionsService.getPayPalBalance(configMap);

		Optional<BasicAmountType> usdBalance=basicAmountType.getBalanceHoldings().stream().filter(e->e.getCurrencyID().getValue().equalsIgnoreCase("USD")).findFirst();
		Optional<BasicAmountType> eurBalance=basicAmountType.getBalanceHoldings().stream().filter(e->e.getCurrencyID().getValue().equalsIgnoreCase("EUR")).findFirst();

		String formattedDate = Utilities.formatedDate(new Date(), "yyyy-MM-dd");
		
		String emailTo1=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYMENT_REPORT_EMAIL_1);
		String emailTo2=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYMENT_REPORT_EMAIL_2);

		String emailFrom=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.NO_REPLY_EMAIL);
        
		StringBuffer sb=new StringBuffer();
		sb.append("1. PayPal balance for USD is "+usdBalance.get().getValue()+"<br>");
		sb.append("2. PayPal balance for EUR is "+eurBalance.get().getValue()+"<br>");
		
		Email email=new Email();
		email.setTo(emailTo1);
		email.setCC(emailTo2);
		email.setContent(sb.toString());
		email.setFrom(emailFrom);
		email.setSubject("PayPal balance on "+formattedDate);
			 
		MailService mailService = new MailService();
	    mailService.sendMailGrid(email);
	    
	}

	
	/*
		the alert should run every 1 hour and send an email immediately in case balance goes below threshold.
	 	Execute as GAE job
	 */
	public void runPayPalBalanceThreshold(HttpServletRequest req, HttpServletResponse resp)
			throws Exception{
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
		
		String usdThreshold=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYPAL_BALANCE_THRESHOLD_USD);
		String eurThreshold=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYPAL_BALANCE_THRESHOLD_EUR);

		
		PaymentOrderTransactionsService paymentOrderTransactionsService=new PaymentOrderTransactionsService();
		GetBalanceResponseType basicAmountType= paymentOrderTransactionsService.getPayPalBalance(configMap);
		
		
		Optional<BasicAmountType> usdBalance=basicAmountType.getBalanceHoldings().stream().filter(e->e.getCurrencyID().getValue().equalsIgnoreCase("USD")).findFirst();
		Optional<BasicAmountType> eurBalance=basicAmountType.getBalanceHoldings().stream().filter(e->e.getCurrencyID().getValue().equalsIgnoreCase("EUR")).findFirst();
		calculateAlert1(usdThreshold,usdBalance.get());
		calculateAlert1(eurThreshold,eurBalance.get());
	}
	
	private void calculateAlert1(String threshold,BasicAmountType balance) throws IOException{
		BigDecimal thresholdValue;
		Objects.requireNonNull(balance, "Balance value is NULL");
		try {
			thresholdValue= BigDecimal.valueOf(Double.valueOf(threshold));

		}catch(NumberFormatException e) {
			logger.severe("Unable to convert String to number: "+threshold+" "+balance.getCurrencyID().getValue());
			return;	
		}
		BigDecimal balanceValue=new BigDecimal(balance.getValue());
		if(balanceValue.compareTo(thresholdValue)<0) {
		    ApplicationSettingsService applicationSettingsService=new ApplicationSettingsService(); 
			String emailTo=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYMENT_REPORT_EMAIL_1);
	        String emailFrom=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.NO_REPLY_EMAIL);
	        
			
			Email email=new Email();
			email.setTo(emailTo);
			email.setContent("PayPal balance for "+balance.getCurrencyID().getValue()+" is "+balance.getValue());
			email.setFrom(emailFrom);
			email.setSubject("PayPal balance for "+balance.getCurrencyID().getValue()+" is below threshold");
				 
			MailService mailService = new MailService();
		    mailService.sendMailGrid(email);
			
			
		}
		
	}
	
	
	/*
	 * Execute as GAE job
	 */
	public void runOrderTransactionReport(HttpServletRequest req, HttpServletResponse resp)
			throws Exception{
		
		//PayPal and alerts
		processPayPalOrderTransactions();
		
		//Tango card PS
	    ApplicationSettingsService applicationSettingsService=new ApplicationSettingsService(); 
		String platformIdentifier=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.TANGO_CARD_PLATFORM_IDENTIFIER);
		String platformKey=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.TANGO_CARD_PLATFORM_KEY);	
		String customerName=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.TANGO_CARD_CUSTOMER_NAME);
		String accountName=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.TANGO_CARD_ACCOUNT_NAME);
		
		processTangoCardOrderTransactions(platformIdentifier,platformKey,customerName,accountName,"Tango Card PS");
		
		platformIdentifier=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.JP_TANGO_CARD_PLATFORM_IDENTIFIER);
		platformKey=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.JP_TANGO_CARD_PLATFORM_KEY);		
		customerName=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.JP_TANGO_CARD_CUSTOMER_NAME);
		accountName=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.JP_TANGO_CARD_ACCOUNT_NAME);
		
		processTangoCardOrderTransactions(platformIdentifier,platformKey,customerName,accountName,"Tango Card JP");
		
		
	}
	private void processTangoCardOrderTransactions(String platformIdentifier,String platformKey,String customerName,String accountName, String title)throws Exception{
	    GiftCardService giftCardService=new GiftCardService();
		//Yesterday report
		ZonedDateTime now=ZonedDateTime.now(ZoneOffset.UTC);
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
		
		//get account balance;
		BigDecimal balance=giftCardService.getGiftCardAccountBalance(platformIdentifier, platformKey, accountName);
		
        String emailTo=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYMENT_REPORT_EMAIL_1);
        String emailFrom=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.NO_REPLY_EMAIL);
        
		paymentOrderTransactionsService.sendEmail(map,balance, usdSum, eurSum, title+" total at "+formattedDate, emailTo, emailFrom);
        		 		 
		emailTo=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYMENT_REPORT_EMAIL_2);
		paymentOrderTransactionsService.sendEmail(map,balance, usdSum, eurSum, title+" total at "+formattedDate, emailTo, emailFrom);	

		
	}
	public void processPayPalOrderTransactions()throws Exception{				

	    ApplicationSettingsService applicationSettingsService=new ApplicationSettingsService(); 
							
	    //Yesterday report
		ZonedDateTime now=ZonedDateTime.now(ZoneOffset.UTC);
		ZonedDateTime yesterday=now.minusDays(1);
		   
		ZonedDateTime yesterdayStart=yesterday.truncatedTo(ChronoUnit.DAYS);
		ZonedDateTime yesterdayEnd=yesterdayStart.plusHours(24);
		/*read records from 'paid_user' table
		 *Our system records PayPal payments for PlaySpot only 
		 */
		PaidUsersService paidUsersService=new PaidUsersService();
		Collection<PaidUser> localPlaySpotList=paidUsersService.getPaidUsersByDateAndType("PayPal",Date.from(yesterdayStart.toInstant()), Date.from(yesterdayEnd.toInstant()));							
		Map<String,BigDecimal> localPlaySpotMap=localPlaySpotList.stream().collect(Collectors.groupingBy(PaidUser::getPaidCurrency, Collectors.reducing(BigDecimal.ZERO, PaidUser::getAmountNet, BigDecimal::add)));

		PaymentOrderTransactionsService paymentOrderTransactionsService=new PaymentOrderTransactionsService();
		Collection<OrderTransactionResult> orderTransactionResults=paymentOrderTransactionsService.getPayPalOrderTransactions(yesterdayStart,yesterdayEnd);  
	    //group by transaction message  "JustPlay" or "PlaySpot"
		Collection<OrderTransactionResult> justPlayList=new  ArrayList<>();
		Collection<OrderTransactionResult> playSpotList=new  ArrayList<>();
		
		orderTransactionResults.forEach(o->{			
			if(o.getTransactionSubject().toLowerCase().contains("justplay")){
				justPlayList.add(o);
			}else if(o.getTransactionSubject().toLowerCase().contains("playspot")){				
				playSpotList.add(o);
			}
		});		
		String formattedDate = Utilities.formatedDate(yesterday, "yyyy-MM-dd");
		//group by currency code
		Map<String,BigDecimal> justPlayMap= paymentOrderTransactionsService.getOrderTransactionsGroupBy(justPlayList);
		Map<String,BigDecimal> playSpotMap= paymentOrderTransactionsService.getOrderTransactionsGroupBy(playSpotList);								
		
        
        String emailTo1=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYMENT_REPORT_EMAIL_1);
        String emailFrom=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.NO_REPLY_EMAIL);
        
		paymentOrderTransactionsService.sendEmail(justPlayMap, "PayPal payments total for "+formattedDate,"Payments for JustPlay:", emailTo1, emailFrom);
        		 		 
		String emailTo2=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYMENT_REPORT_EMAIL_2);
		
		paymentOrderTransactionsService.sendEmail(playSpotMap,localPlaySpotMap,"PayPal payments total for "+formattedDate,"Payments for PlaySpot:", emailTo2, emailFrom);	
		
		//PayPal to Local System discrepencies
		List<String> discrepencyList=paymentOrderTransactionsService.validatePayPalToLocalSystemOrdersAmount(playSpotMap,localPlaySpotMap);
		if(discrepencyList.size()>0){
			paymentOrderTransactionsService.sendEmail(discrepencyList, playSpotMap, localPlaySpotMap, "PayPal payout discrepancy found.", emailTo1, emailFrom);
			
			paymentOrderTransactionsService.sendEmail(discrepencyList, playSpotMap, localPlaySpotMap, "PayPal payout discrepancy found.", emailTo2, emailFrom);
		}
	}


}
