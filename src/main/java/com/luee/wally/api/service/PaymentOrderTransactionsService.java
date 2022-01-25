package com.luee.wally.api.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.fasterxml.jackson.core.type.TypeReference;
import com.luee.wally.admin.repository.ApplicationSettingsRepository;
import com.luee.wally.api.ConnectionMgr;
import com.luee.wally.api.paypal.client.TransactionsApi;
import com.luee.wally.api.paypal.client.model.Token;
import com.luee.wally.api.paypal.client.model.TransactionView;
import com.luee.wally.api.service.impex.ImportService;
import com.luee.wally.api.tangocard.client.OrdersApi;
import com.luee.wally.api.tangocard.client.model.OrderListView;
import com.luee.wally.command.Email;
import com.luee.wally.command.order.OrderTransactionResult;
import com.luee.wally.constants.Constants;
import com.luee.wally.json.ExchangeRateVO;
import com.luee.wally.json.JSONUtils;
import com.luee.wally.json.JustPlayAmountVO;
import com.luee.wally.utils.Utilities;


import urn.ebay.api.PayPalAPI.GetBalanceReq;
import urn.ebay.api.PayPalAPI.GetBalanceRequestType;
import urn.ebay.api.PayPalAPI.GetBalanceResponseType;
import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;

/*
 * Query external PayPal and GiftCard API services
 */
public class PaymentOrderTransactionsService extends AbstractService{

	private final Logger logger = Logger.getLogger(PaymentOrderTransactionsService.class.getName());
	
	/*
	 * payments for JustPlay are tracked by API server
	 */
	public List<JustPlayAmountVO> getExternalTotalPaymentAmount(ZonedDateTime startDate,ZonedDateTime endDate)throws IOException{
		Map<String,String> requestHeader=new HashMap<String,String>();
		requestHeader.put("Authorization",Utilities.createBasicAuthString(Constants.PAYPAL_JUSTPLAY_PAYMENT_USER, Constants.PAYPAL_JUSTPLAY_PAYMENT_PASSWORD));				
		requestHeader.put("User-Agent", Constants.AGENT_NAME);		
		requestHeader.put("Content-Type", "application/json; charset=UTF-8");
		requestHeader.put("Accept", "application/json");		
		
		
	    DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS'Z'");				
		
		StringBuilder url=new StringBuilder(Constants.PAYPAL_JUSTPLAY_PAYMENT_URL);
	    url.append("?");
	    url.append("startDateTime="+isoFormatter.format(startDate));
		url.append("&endDateTime="+isoFormatter.format(endDate));
		
		//List<JustPlayAmountVO> list=JSONUtils.readObject(map, clazz)
		String json=ConnectionMgr.INSTANCE.getJSON(url.toString(),requestHeader);				
		return JSONUtils.readObject(json,new TypeReference<List<JustPlayAmountVO>>(){});
		
	}
	public Collection<OrderTransactionResult> getGiftCardOrderTransactions(ZonedDateTime startDate,ZonedDateTime endDate,Map<String,String> configMap,Map<String,String> currencyCodeMap) throws Exception{
		Collection<OrderTransactionResult> result=new LinkedList<>();
		int page=0;
		//iterate through paged results
		while(true){
			Collection<OrderTransactionResult> paging=getGiftCardOrderTransactions(Instant.from(startDate).toString(), Instant.from(endDate).toString(),100,page++, configMap,currencyCodeMap);
			if(paging.size()==0){
				break;
			}
			result.addAll(paging);
		}
		return result;
	}
	/*
	 * Page through TC 
	 */
	private Collection<OrderTransactionResult> getGiftCardOrderTransactions(String startDate,String endDate,int elementsPerBlock,int page,Map<String,String> configMap,Map<String,String> currencyCodeMap) throws Exception{
		Collection<OrderTransactionResult> result=new LinkedList<>();
		String platformIdentifier=configMap.get(Constants.PLATFORM_IDENTIFIER);
		String platformKey=configMap.get(Constants.PLATFORM_KEY);
		String customerName=configMap.get(Constants.TANGO_CARD_CUSTOMER);
		
	    OrdersApi ordersApi=new OrdersApi(platformIdentifier,platformKey);
			 
		OrderListView orderListView=ordersApi.listOrders(customerName,null,startDate,endDate, elementsPerBlock,page);
		if(orderListView.getPage().getResultCount()==0){
			return Collections.emptyList();
		}
		   
		orderListView.getOrders().forEach(o->{
		       if("COMPLETE".equals(o.getStatus())){
		    	   OrderTransactionResult orderTransactionResult=new OrderTransactionResult();	
		    	   String currencyCode=currencyCodeMap.get(o.getUtid());
		    	   if(currencyCode==null){
		    		   logger.log(Level.SEVERE,"Missing Tango Card currency code for unitid="+o.getUtid());		    	       
		    	   }else{
		    		   orderTransactionResult.setCurrencyCode(currencyCode);	
		    		   orderTransactionResult.setUnitid(o.getUtid());
		    		   orderTransactionResult.setTimestamp(o.getCreatedAt());
		    		   orderTransactionResult.setValue(o.getAmountCharged().getValue());
		    		   result.add(orderTransactionResult);
		    	   }
		       }
		});
		return result;		
		
	}
	/*
	 * Reuse data from previous PayPal call
	 * Check  for discrepancies between PayPal system and Local server(PlaySpot only)
	 * 1.0 difference is a discrepancy
	 * @return - list of currency codes with discrepency
	 */
	public List<String> validateOrdersAmount(Map<String,BigDecimal> orderSumMap,Map<String,BigDecimal> localSystemOrderSumMap){
		List<String> result=new ArrayList<>();
		for(Map.Entry<String, BigDecimal> entry:orderSumMap.entrySet()){
		   String currencyCode=entry.getKey();
		   BigDecimal amount=entry.getValue();
		   //local system
		   BigDecimal localAmount=localSystemOrderSumMap.get(currencyCode);
		   if(localAmount!=null){			   			   
			   BigDecimal diff=amount.abs().subtract(localAmount.abs()).abs();
			   BigDecimal rounded=diff.setScale(2, BigDecimal.ROUND_HALF_EVEN);			   			   
			   if(rounded.compareTo(Constants.PAYPAL_LOCAL_SYSTEM_DISCREPANCIES)>0){
				   result.add(currencyCode);
			   }
		   }
		   
		}
		return result;
	}
	public List<String> validateOrdersAmountInPercentage(Map<String,BigDecimal> orderSumMap,Map<String,BigDecimal> localSystemOrderSumMap){
		List<String> result=new ArrayList<>();
		for(Map.Entry<String, BigDecimal> entry:orderSumMap.entrySet()){
		   String currencyCode=entry.getKey();
		   BigDecimal amount=entry.getValue();
		   //local system
		   BigDecimal localAmount=localSystemOrderSumMap.get(currencyCode);
		   if(localAmount!=null){
			   double number=Utilities.findPercentageDifferenceBetween(amount.abs().doubleValue(),localAmount.abs().doubleValue());
			   
			   if(Double.compare(Math.abs(number),Constants.PERCENTAGE_DISCREPANCIES)>0){
				   result.add(currencyCode);
			   }
			   
		   }
		   
		}
		return result;
	}
	
	public GetBalanceResponseType getPayPalBalance(Map<String,String> configMap)throws Exception{
		GetBalanceReq getBalanceReq=new GetBalanceReq();
		
		GetBalanceRequestType reqType = new GetBalanceRequestType();

		/*
		(Optional) Indicates whether to return all currencies. It is one of the following values:
			0  Return only the balance for the primary currency holding.
			1  Return the balance for each currency holding.
		Note:
		This field is available since version 51. 
		Prior versions return only the balance for the primary currency holding.
		 */
		reqType.setReturnAllCurrencies("1");
		getBalanceReq.setGetBalanceRequest(reqType);
		
		PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(configMap);
		
		GetBalanceResponseType getBalanceResponseType=service.getBalance(getBalanceReq);
		return getBalanceResponseType;
	}
	
	/*
	 * Split 1 day in chunks of 4
	 */
	public Collection<OrderTransactionResult> getPayPalOrderTransactionsIn24Hours(ZonedDateTime startDate) throws Exception{		

		Collection<OrderTransactionResult> result=new LinkedList<>();
		
		ZonedDateTime startDate1=startDate;
		ZonedDateTime endDate1=startDate.plusHours(5).plusMinutes(59).plusSeconds(59);
		Collection<OrderTransactionResult> first=this.getPayPalOrderTransactions(startDate1,endDate1);	
		result.addAll(first);
		
		ZonedDateTime startDate2=startDate.plusHours(6).plusMinutes(0).plusSeconds(0);
		ZonedDateTime endDate2=startDate.plusHours(11).plusMinutes(59).plusSeconds(59);
		Collection<OrderTransactionResult> second=this.getPayPalOrderTransactions(startDate2,endDate2);	
		result.addAll(second);
		
		ZonedDateTime startDate3=startDate.plusHours(12).plusMinutes(0).plusSeconds(0);;
		ZonedDateTime endDate3=startDate.plusHours(17).plusMinutes(59).plusSeconds(59);
		Collection<OrderTransactionResult> third=this.getPayPalOrderTransactions(startDate3,endDate3);	
		result.addAll(third);
		
		ZonedDateTime startDate4=startDate.plusHours(18).plusMinutes(0).plusSeconds(0);;
		ZonedDateTime endDate4=startDate.plusHours(23).plusMinutes(59).plusSeconds(59);
		Collection<OrderTransactionResult> forth=this.getPayPalOrderTransactions(startDate4,endDate4);	
		result.addAll(forth);

		return result;
	}
	/*
	 * Assume less then 10000 result quater
	 */
	public Collection<OrderTransactionResult> getPayPalOrderTransactions(ZonedDateTime startDate,ZonedDateTime endDate) throws Exception{
		Collection<OrderTransactionResult> result=new LinkedList<>();
		ApplicationSettingsService applicationSettingsService=new ApplicationSettingsService(); 
	    String paypalClientId=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYPAL_CLIENT_ID);
	    String paypalClientSecret=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYPAL_CLIENT_SECRET);
	    String paypalMode=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYPAL_MODE);
	    
	    
		TransactionsApi transactionsApi=new TransactionsApi(paypalClientId, paypalClientSecret,"sandbox".equalsIgnoreCase(paypalMode)); 	   
		Token token=transactionsApi.authenticate();
		//***find first page
		TransactionView transactionView=transactionsApi.getTransactionsByDate(token.getAccessToken(), startDate, endDate,500,1);           
           transactionView.getTransactionDetails().forEach(t->{        	   	    	  
    		   BigDecimal fee=BigDecimal.ZERO;
    		   if(t.getTransactionInfo().getFeeAmount()!=null){        		          		   
    		    fee=t.getTransactionInfo().getFeeAmount().asBigDecimal();
    		   }
	    	   BigDecimal amount=t.getTransactionInfo().getTransactionAmount().asBigDecimal();
	    	   
        	   OrderTransactionResult orderTransactionResult=new OrderTransactionResult();	    	   
	    	   orderTransactionResult.setCurrencyCode(t.getTransactionInfo().getTransactionAmount().getCurrencyCode());
	    	   orderTransactionResult.setTransactionSubject(t.getTransactionInfo().getTransactionSubject());
	    	   orderTransactionResult.setValue(amount.add(fee));
	    	   result.add(orderTransactionResult); 
           });
       //**is there any more pages    
           if(transactionView.getTotalPages()>1){
        	 for(int page=2;page<=transactionView.getTotalPages();page++){
        	   
        	   transactionView=transactionsApi.getTransactionsByDate(token.getAccessToken(), startDate, endDate,500,page);        	   
        	   transactionView.getTransactionDetails().forEach(t->{
        		   BigDecimal fee=BigDecimal.ZERO;
        		   if(t.getTransactionInfo().getFeeAmount()!=null){        		          		   
        		    fee=t.getTransactionInfo().getFeeAmount().asBigDecimal();
        		   }
    	    	   BigDecimal amount=t.getTransactionInfo().getTransactionAmount().asBigDecimal();
    	    	   
        		   OrderTransactionResult orderTransactionResult=new OrderTransactionResult();		    	   
    	    	   orderTransactionResult.setCurrencyCode(t.getTransactionInfo().getTransactionAmount().getCurrencyCode());
    	    	   orderTransactionResult.setTransactionSubject(t.getTransactionInfo().getTransactionSubject());
    	    	   orderTransactionResult.setValue(amount.add(fee));
    	    	   result.add(orderTransactionResult); 
               });

        	 }
           }
		
	    
	    return result;
	}

	/*
	 * group by currency code
	 */	
	public Map<String,BigDecimal> getJustPlayVOGroupBy(Collection<JustPlayAmountVO> justPlayVOs)throws Exception{
		Map<String,BigDecimal> result=new HashMap<String, BigDecimal>();
						
		for(JustPlayAmountVO justPlay:justPlayVOs){
			BigDecimal sum=result.get(justPlay.getCurrencyCode());
			if(sum==null){
				sum=new BigDecimal(0);
			}
			BigDecimal accumulator=sum.add(justPlay.getNetAmount());
			result.put(justPlay.getCurrencyCode(),accumulator);
		}		
		return result;
	}

	
	/*
	 * group by currency code
	 */	
	public Map<String,BigDecimal> getOrderTransactionsGroupBy(Collection<OrderTransactionResult> orderTransactions)throws Exception{
		Map<String,BigDecimal> result=new HashMap<String, BigDecimal>();
						
		for(OrderTransactionResult orderTransactionResult:orderTransactions){
			BigDecimal sum=result.get(orderTransactionResult.getCurrencyCode());
			if(sum==null){
				sum=new BigDecimal(0);
			}
			BigDecimal accumulator=sum.add(orderTransactionResult.getValue());
			result.put(orderTransactionResult.getCurrencyCode(),accumulator);
		}		
		return result;
	}
	/*
	 * given a map of currencyCode : amount pair , get the total in target toCurrencyCode
	 */
	public BigDecimal calculateTotal(Map<String,BigDecimal> currencyValueMap,String formattedDate,String toCurrencyCode)throws Exception{
		//get exchange rates
		ImportService importService = new ImportService();
	    ExchangeRateVO rate = importService.getExchangeRates(formattedDate, toCurrencyCode);
	    BigDecimal total=new BigDecimal(0);
		for(Map.Entry<String,BigDecimal> entry:currencyValueMap.entrySet()){		
			if(!entry.getKey().equals(toCurrencyCode)){
			 BigDecimal rateValue = BigDecimal.valueOf(rate.getRates().get(entry.getKey()));			
			 BigDecimal paidAmount = entry.getValue().divide(rateValue,2, BigDecimal.ROUND_HALF_EVEN);			 
			 total=total.add(paidAmount);
			}else{
			 total=total.add(entry.getValue());
			}
		}
		return total;
	}
	
	public void sendEmail(Map<String,BigDecimal> map,BigDecimal balance,BigDecimal usdSum,BigDecimal eurSum,String subject,String emailTo,String emailFrom)throws IOException{
		 StringBuffer sb=new StringBuffer();
		 if(balance!=null){
		 sb.append("- current balance     "+Utilities.formatPrice(balance)+"<br>");	 
		 }
		 
		 sb.append("- amount paid in USD  "+Utilities.formatPrice(usdSum)+"<br>");
		 sb.append("- amount paid in EUR  "+Utilities.formatPrice(eurSum)+"<br>");
		 
		 //if(map.size()>1){ //tango card has a single value only
			 for(Map.Entry<String,BigDecimal> entry: map.entrySet()){
				 sb.append(" "+entry.getKey()+" - "+Utilities.formatPrice(entry.getValue())+"<br>");	 
			 }		 		 
		 //}
		 
		 Email email=new Email();
		 email.setTo(emailTo);
		 email.setContent(sb.toString());
		 email.setFrom(emailFrom);
		 email.setSubject(subject);
		 
		 MailService mailService = new MailService();
		 mailService.sendMailGrid(email);		
	}
	/*
	 * PayPal email
	 */
	public void sendEmailJustPlay(Map<String,BigDecimal> map,Map<String,BigDecimal> localMap,String subject,String transactionSubject,String emailTo,String emailFrom)throws IOException{
		 StringBuffer sb=new StringBuffer(transactionSubject);
		 sb.append("<br><br>");
       
		 map.entrySet().forEach(e->{
			 sb.append(e.getKey());
			 sb.append(" - ");
			 sb.append(Utilities.formatPrice(e.getValue()));
			 sb.append("<br>");
		 });
		 
		 sb.append("<br><br>");
		 sb.append("Payments for JustPlay from our server:");
		 sb.append("<br><br>");
		 
		 localMap.entrySet().forEach(e->{
			 sb.append(e.getKey());
			 sb.append(" - ");
			 sb.append(Utilities.formatPrice(e.getValue().negate()));
			 sb.append("<br>");
		 });
		 
		 Email email=new Email();
		 email.setTo(emailTo);
		 email.setContent(sb.toString());
		 email.setFrom(emailFrom);
		 email.setSubject(subject);
		 
		 MailService mailService = new MailService();
		 mailService.sendMailGrid(email);		
	}
	/*
	 * PayPal PlaySpot email
	 * Remote transactions API and local data store data
	 */
	public void sendEmail(Map<String,BigDecimal> map,Map<String,BigDecimal> localMap,String subject,String transactionSubject,String emailTo,String emailFrom)throws IOException{
		 StringBuffer sb=new StringBuffer(transactionSubject);
		 sb.append("<br><br>");
        
		 map.entrySet().forEach(e->{
			 sb.append(e.getKey());
			 sb.append(" - ");
			 sb.append(Utilities.formatPrice(e.getValue()));
			 sb.append("<br>");
		 });
		 
		 sb.append("<br><br>");
		 sb.append("Payments for PlaySpot from our server:");
		 sb.append("<br><br>");
		 
		 localMap.entrySet().forEach(e->{
			 sb.append(e.getKey());
			 sb.append(" - ");
			 sb.append(Utilities.formatPrice(e.getValue().negate()));
			 sb.append("<br>");
		 });
		 
		 Email email=new Email();
		 email.setTo(emailTo);
		 email.setContent(sb.toString());
		 email.setFrom(emailFrom);
		 email.setSubject(subject);
		 
		 MailService mailService = new MailService();
		 mailService.sendMailGrid(email);		
	}	
	
	/*
	 * PayPal to local system PlaySpot discrepency email	 
	 */
	public void sendEmail(List<String> discrepencyList,Map<String,BigDecimal> map,Map<String,BigDecimal> localMap,String channel,String subject,String emailTo,String emailFrom)throws IOException{
		 StringBuffer sb=new StringBuffer("Payments for "+channel+" from PayPal:");
		 sb.append("<br><br>");
        
		 map.entrySet().forEach(e->{
			 if(discrepencyList.contains(e.getKey())){
				 sb.append(e.getKey());
				 sb.append(" - ");
				 sb.append(Utilities.formatPrice(e.getValue()));
				 sb.append("<br>");
			 }
		 });
		 
		 sb.append("<br><br>");
		 sb.append("Payments for "+channel+" from our server:");
		 sb.append("<br><br>");
		 
		 localMap.entrySet().forEach(e->{
			 if(discrepencyList.contains(e.getKey())){
				 sb.append(e.getKey());
				 sb.append(" - ");
				 sb.append(Utilities.formatPrice(e.getValue().negate()));
				 sb.append("<br>");
			 }
		 });
		 
		 Email email=new Email();
		 email.setTo(emailTo);
		 email.setContent(sb.toString());
		 email.setFrom(emailFrom);
		 email.setSubject(subject);
		 
		 MailService mailService = new MailService();
		 mailService.sendMailGrid(email);		
	}
	//Tango Card
	public void sendEmailTangoCard(List<String> discrepencyList,Map<String,BigDecimal> map,Map<String,BigDecimal> localMap,String subject,String emailTo,String emailFrom)throws IOException{
		 StringBuffer sb=new StringBuffer("Payments from Tango Card:");		 
		 sb.append("<br><br>");
		 
		 map.entrySet().forEach(e->{
			 if(discrepencyList.contains(e.getKey())){
				 sb.append(e.getKey());
				 sb.append(" - ");
				 sb.append(Utilities.formatPrice(e.getValue().negate()));
				 sb.append("<br>");
			 }
		 });
		 
		 sb.append("<br><br>");
		 sb.append("Payments from our server:");
		 sb.append("<br><br>");
		 
		 localMap.entrySet().forEach(e->{
			 if(discrepencyList.contains(e.getKey())){
				 sb.append(e.getKey());
				 sb.append(" - ");
				 sb.append(Utilities.formatPrice(e.getValue().negate()));
				 sb.append("<br>");
			 }
		 });
		 
		 Email email=new Email();
		 email.setTo(emailTo);
		 email.setContent(sb.toString());
		 email.setFrom(emailFrom);
		 email.setSubject(subject);
		 
		 MailService mailService = new MailService();
		 mailService.sendMailGrid(email);		
	}	
}
