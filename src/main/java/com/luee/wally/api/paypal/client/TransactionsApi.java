package com.luee.wally.api.paypal.client;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.luee.wally.api.ConnectionMgr;
import com.luee.wally.api.paypal.client.model.TransactionView;
import com.luee.wally.api.tangocard.client.config.TangoCardJSON;
import com.luee.wally.constants.Constants;

public class TransactionsApi extends ClientApi{

	private final String url;
	
	public TransactionsApi(String payPalClientId,String payPalSecret,boolean isSandbox) {
		super(payPalClientId,payPalSecret,isSandbox);	
		if(isSandbox){
			url=Constants.PAYPAL_TRANSACTIONS_SANDBOX_URL;
		}else{
			url=Constants.PAYPAL_TRANSACTIONS_PROD_URL;
		}
	}
	
	
	public TransactionView getTransactionsByDate(String accessToken,ZonedDateTime startDate,ZonedDateTime endDate,int pageSize,int page)throws IOException{
		DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");	
		
		StringBuilder _url=new StringBuilder(url);
	    _url.append("?");
	    _url.append("page_size="+pageSize);
	    _url.append("&page="+page);
		_url.append("&transaction_type=T0001");
		_url.append("&fields=all");
		
		if(startDate!=null){
		  _url.append("&start_date="+URLEncoder.encode(isoFormatter.format(startDate),"UTF-8"));
		}
		if(endDate!=null){
		  _url.append("&end_date="+URLEncoder.encode(isoFormatter.format(endDate),"UTF-8"));
		}
		
	  	Map<String,String> requestHeader=new HashMap<>();
	  	requestHeader.put("User-Agent", Constants.AGENT_NAME);
	  	requestHeader.put("Content-Type", "application/json");
	  	requestHeader.put("Authorization", "Bearer "+accessToken);
	  	   
	  	String response=ConnectionMgr.INSTANCE.getJSON(_url.toString(), requestHeader);
	 	return TangoCardJSON.readObject(response,TransactionView.class);	

	}
	


}
