package com.luee.wally.api.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.luee.wally.api.service.impex.ImportService;
import com.luee.wally.api.tangocard.client.OrdersApi;
import com.luee.wally.api.tangocard.client.model.OrderListView;
import com.luee.wally.command.Email;
import com.luee.wally.command.order.OrderTransactionResult;
import com.luee.wally.constants.Constants;
import com.luee.wally.json.ExchangeRateVO;
import com.luee.wally.utils.Utilities;

import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;
import urn.ebay.api.PayPalAPI.TransactionSearchReq;
import urn.ebay.api.PayPalAPI.TransactionSearchRequestType;
import urn.ebay.api.PayPalAPI.TransactionSearchResponseType;
import urn.ebay.apis.eBLBaseComponents.PaymentTransactionSearchResultType;
import urn.ebay.apis.eBLBaseComponents.PaymentTransactionStatusCodeType;

/*
 * Query external PayPal and GiftCard API services
 */
public class PaymentOrderTransactionsService extends AbstractService{

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
	public Collection<OrderTransactionResult> getPayPalOrderTransactions(String startDate,String endDate,Map<String,String> configMap) throws Exception{
		TransactionSearchReq transactionSearchReq = new TransactionSearchReq();
		TransactionSearchRequestType transactionSearchRequestType = new TransactionSearchRequestType();
		
		transactionSearchRequestType.setStartDate(startDate); 
		transactionSearchRequestType.setEndDate(endDate);		
		transactionSearchRequestType.setStatus(PaymentTransactionStatusCodeType.SUCCESS);
		transactionSearchReq.setTransactionSearchRequest(transactionSearchRequestType);

		
						
		
		PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(configMap);		
		TransactionSearchResponseType txnresponse = service.transactionSearch(transactionSearchReq);
		Collection<OrderTransactionResult> result=new LinkedList<>();
		
		for(PaymentTransactionSearchResultType transaction: txnresponse.getPaymentTransactions()){			
			OrderTransactionResult orderTransactionResult=new OrderTransactionResult();
			orderTransactionResult.setCurrencyCode(transaction.getNetAmount().getCurrencyID().getValue());
			orderTransactionResult.setValueAsStr(transaction.getNetAmount().getValue());
			orderTransactionResult.setTimestamp(transaction.getTimestamp());			
			result.add(orderTransactionResult);			
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
	
	public void sendEmail(Map<String,BigDecimal> map,BigDecimal usdSum,BigDecimal eurSum,String subject,String emailTo,String emailFrom)throws IOException{
		 StringBuffer sb=new StringBuffer();
		 sb.append("Total amount paid in EUR: "+eurSum+"\r\n");
		 sb.append("Total amount paid in USD: "+usdSum+"\r\n");
		 
		 for(Map.Entry<String,BigDecimal> entry: map.entrySet()){
			 sb.append(" "+entry.getKey()+" - "+Utilities.formatPrice(entry.getValue())+"\r\n");	 
		 }		 		 
		 
		 
		 Email email=new Email();
		 email.setTo(emailTo);
		 email.setContent(sb.toString());
		 email.setFrom(emailFrom);
		 email.setSubject(subject);
		 
		 MailService mailService = new MailService();
		 mailService.sendMailGrid(email);		
	}
}
