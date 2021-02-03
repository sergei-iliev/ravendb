package com.luee.wally.api.tangocard.client;

import java.io.IOException;

import com.luee.wally.api.tangocard.client.config.TangoCardJSON;
import com.luee.wally.api.tangocard.client.model.AccountView;
import com.luee.wally.constants.Constants;

public class AccountsApi extends ClientApi{

	public AccountsApi(String platformIdentifier, String platformKey) {
		super(platformIdentifier, platformKey);		
	}
	
	public AccountView getAccount(String accountIdentifier)throws IOException{
		StringBuffer url=new StringBuffer(Constants.TANGO_CARD_API_URL+"/accounts/");
	    url.append(accountIdentifier);	
				
				
		String response=this.getJSON(url.toString());
		return TangoCardJSON.readObject(response,AccountView.class);	
	}
	
	

}
