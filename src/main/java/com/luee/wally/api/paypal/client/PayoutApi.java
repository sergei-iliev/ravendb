package com.luee.wally.api.paypal.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.luee.wally.api.ConnectionMgr;
import com.luee.wally.api.paypal.client.model.payout.BatchPayoutView;
import com.luee.wally.api.tangocard.client.config.TangoCardJSON;
import com.luee.wally.constants.Constants;

public class PayoutApi extends ClientApi{

	private final String url;
	
	public PayoutApi(String payPalClientId,String payPalSecret,boolean isSandbox) {
		super(payPalClientId,payPalSecret,isSandbox);
		if(isSandbox){
			url=Constants.PAYPAL_PAYOUT_SANDBOX_URL;
		}else{
			url=Constants.PAYPAL_PAYOUT_PROD_URL;
		}
	}

	public BatchPayoutView getPayoutById(String payoutBatchId,String accessToken)throws IOException{
		   
		   String _url=url+"/"+payoutBatchId;
	  	   Map<String,String> requestHeader=new HashMap<>();
	  	   requestHeader.put("User-Agent", Constants.AGENT_NAME);
	  	   requestHeader.put("Content-Type", "application/json");
	  	   requestHeader.put("Authorization", "Bearer "+accessToken);
	  	   
	  	   String response=ConnectionMgr.INSTANCE.getJSON(_url, requestHeader);
	  	   return TangoCardJSON.readObject(response,BatchPayoutView.class);		

	}
	/*
	public BalanceListView getBalancesByDate(String accessToken,ZonedDateTime date)throws IOException{
	       DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");	       	       	       	      
		   String _url=url+"?as_of_time="+URLEncoder.encode(isoFormatter.format(date),"UTF-8");
		   
	  	   Map<String,String> requestHeader=new HashMap<>();
	  	   requestHeader.put("User-Agent", Constants.AGENT_NAME);
	  	   requestHeader.put("Content-Type", "application/json");
	  	   requestHeader.put("Authorization", "Bearer "+accessToken);
	  	   
	  	   String response=ConnectionMgr.INSTANCE.getJSON(_url, requestHeader);
	  	   return TangoCardJSON.readObject(response,BalanceListView.class);		

	}
	*/
}
