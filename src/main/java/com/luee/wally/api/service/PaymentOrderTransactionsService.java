package com.luee.wally.api.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

import com.luee.wally.admin.repository.ApplicationSettingsRepository;
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
import com.luee.wally.utils.Utilities;

import urn.ebay.api.PayPalAPI.GetBalanceReq;
import urn.ebay.api.PayPalAPI.GetBalanceRequestType;
import urn.ebay.api.PayPalAPI.GetBalanceResponseType;
import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;
import urn.ebay.api.PayPalAPI.TransactionSearchReq;
import urn.ebay.api.PayPalAPI.TransactionSearchRequestType;
import urn.ebay.api.PayPalAPI.TransactionSearchResponseType;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;
import urn.ebay.apis.eBLBaseComponents.PaymentTransactionSearchResultType;
import urn.ebay.apis.eBLBaseComponents.PaymentTransactionStatusCodeType;

/*
 * Query external PayPal and GiftCard API services
 */
public class PaymentOrderTransactionsService extends AbstractService{

	private final Logger logger = Logger.getLogger(PaymentOrderTransactionsService.class.getName());
	
	public Collection<OrderTransactionResult> getGiftCardOrderTransactions(String startDate,String endDate,Map<String,String> configMap) throws Exception{
		Collection<OrderTransactionResult> result=new LinkedList<>();
		int page=0;
		//iterate through paged results
		while(true){
			Collection<OrderTransactionResult> paging=getGiftCardOrderTransactions(startDate, endDate,100,page++, configMap);
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
	public Collection<OrderTransactionResult> getGiftCardOrderTransactions(String startDate,String endDate,int elementsPerBlock,int page,Map<String,String> configMap) throws Exception{
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
		    	   orderTransactionResult.setCurrencyCode(o.getAmountCharged().getCurrencyCode());
		    	   orderTransactionResult.setTimestamp(o.getCreatedAt());
		    	   orderTransactionResult.setValue(o.getAmountCharged().getTotal());
		    	   result.add(orderTransactionResult);  
		       }
		});
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
	
	public Collection<OrderTransactionResult> getPayPalOrderTransactions(ZonedDateTime startDate,ZonedDateTime endDate) throws Exception{
		Collection<OrderTransactionResult> result=new LinkedList<>();
		ApplicationSettingsService applicationSettingsService=new ApplicationSettingsService(); 
	    String paypalClientId=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYPAL_CLIENT_ID);
	    String paypalClientSecret=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYPAL_CLIENT_SECRET);
	    String paypalMode=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYPAL_MODE);
	    
	    
		TransactionsApi transactionsApi=new TransactionsApi(paypalClientId, paypalClientSecret,"sandbox".equalsIgnoreCase(paypalMode)); 	   
		Token token=transactionsApi.authenticate();
		//***find first page
		TransactionView transactionView=transactionsApi.getTransactionsByDate(token.getAccessToken(), startDate, endDate,100,1);           
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
        	   
        	   transactionView=transactionsApi.getTransactionsByDate(token.getAccessToken(), startDate, endDate,100,page);        	   
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
		 
		 if(map.size()>1){ //tango card has a single value only
			 for(Map.Entry<String,BigDecimal> entry: map.entrySet()){
				 sb.append(" "+entry.getKey()+" - "+Utilities.formatPrice(entry.getValue())+"<br>");	 
			 }		 		 
		 }
		 
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
	public void sendEmail(Map<String,BigDecimal> map,String subject,String transactionSubject,String emailTo,String emailFrom)throws IOException{
		 StringBuffer sb=new StringBuffer(transactionSubject);
		 sb.append("<br>");sb.append("<br>");
         
		 map.entrySet().forEach(e->{
			 sb.append(e.getKey());
			 sb.append(" - ");
			 sb.append(Utilities.formatPrice(e.getValue()));
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
}
