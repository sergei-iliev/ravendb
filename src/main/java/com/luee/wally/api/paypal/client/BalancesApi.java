package com.luee.wally.api.paypal.client;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.luee.wally.api.ConnectionMgr;
import com.luee.wally.api.paypal.client.model.BalanceListView;
import com.luee.wally.api.tangocard.client.config.TangoCardJSON;
import com.luee.wally.constants.Constants;

public class BalancesApi extends ClientApi{

	private final String url;
	
	public BalancesApi(String payPalClientId,String payPalSecret,boolean isSandbox) {
		super(payPalClientId,payPalSecret,isSandbox);
		if(isSandbox){
			url=Constants.PAYPAL_BALANCES_SANDBOX_URL;
		}else{
			url=Constants.PAYPAL_BALANCES_PROD_URL;
		}
	}

	public BalanceListView getCurrentBalances(String accessToken)throws IOException{						  	   
	  	   Map<String,String> requestHeader=new HashMap<>();
	  	   requestHeader.put("User-Agent", Constants.AGENT_NAME);
	  	   requestHeader.put("Content-Type", "application/json");
	  	   requestHeader.put("Authorization", "Bearer "+accessToken);
	  	   
	  	   String response=ConnectionMgr.INSTANCE.getJSON(url, requestHeader);
	  	   return TangoCardJSON.readObject(response,BalanceListView.class);		

	}
	
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
}
