package com.luee.wally.api.paypal.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.luee.wally.api.ConnectionMgr;
import com.luee.wally.api.paypal.client.model.Token;
import com.luee.wally.api.tangocard.client.config.TangoCardJSON;
import com.luee.wally.constants.Constants;
import com.luee.wally.utils.Utilities;

/*
 * PayPal restful services
 */
public abstract class ClientApi {
     private final String payPalClientId; //user 
     private final String payPalSecret;  //pass
     private final String authenticationUrl;
     private final boolean isSandbox;
     
     public ClientApi(String payPalClientId,String payPalSecret,boolean isSandbox) {
   	   this.payPalClientId=payPalClientId;
   	   this.payPalSecret=payPalSecret;
   	   this.isSandbox=isSandbox;
   	   if(isSandbox){
   		 this.authenticationUrl=Constants.PAYPAL_AUTHENTICATION_SANDBOX_URL;
   	   }else{
   		 this.authenticationUrl=Constants.PAYPAL_AUTHENTICATION_PROD_URL;
   	   }
   	 }
      
     public Token authenticate() throws IOException{
  	   
  	   Map<String,String> requestHeader=new HashMap<>();
  	   requestHeader.put("User-Agent", Constants.AGENT_NAME);
  	   requestHeader.put("Accept", "application/json");
  	   requestHeader.put("Accept-Language", "en_US");
  	   requestHeader.put("Authorization", Utilities.createBasicAuthString(payPalClientId,payPalSecret));
  	   requestHeader.put("Content-Type","application/x-www-form-urlencoded");

  	   String content="grant_type=client_credentials";
  	   
  	   String response=ConnectionMgr.INSTANCE.postJSON(authenticationUrl, content, requestHeader);
  	   return TangoCardJSON.readObject(response,Token.class);
     }
}
