package com.luee.wally.api.tangocard.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;

import com.luee.wally.api.tangocard.client.config.TangoCardJSON;
import com.luee.wally.api.tangocard.client.model.OrderListView;
import com.luee.wally.constants.Constants;

public class OrdersApi extends ClientApi{

	
	public OrdersApi(String platformName, String platformKey) {
		super(platformName, platformKey);	
	}

	public OrderListView listOrders(String customerIdentifier, String accountIdentifier,String startDate,String endDate ,int elementsPerBlock,int page)throws MalformedURLException,IOException {
		StringBuffer url=new StringBuffer(Constants.TANGO_CARD_API_URL+"/orders?");
		url.append("page="+page);
		url.append("&elementsPerBlock="+elementsPerBlock);
		if(customerIdentifier!=null){
		  url.append("&customerIdentifier="+URLEncoder.encode(customerIdentifier,"UTF-8"));	
		}
		if(accountIdentifier!=null){
		  url.append("&accountIdentifier="+URLEncoder.encode(accountIdentifier,"UTF-8"));	
		}		
		if(startDate!=null){
		  url.append("&startDate="+URLEncoder.encode(startDate,"UTF-8"));
		}
		if(endDate!=null){
		  url.append("&endDate="+URLEncoder.encode(endDate,"UTF-8"));
		}
		
		String response=this.getJSON(url.toString());
		OrderListView orderListView=TangoCardJSON.readObject(response,OrderListView.class);
		return orderListView;
	}
}
