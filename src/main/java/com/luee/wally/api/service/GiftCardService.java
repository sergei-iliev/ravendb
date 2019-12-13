package com.luee.wally.api.service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import com.luee.wally.admin.repository.ApplicationSettingsRepository;
import com.luee.wally.constants.Constants;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.exception.RestResponseException;
import com.luee.wally.json.JSONUtils;
import com.tangocard.raas.Configuration;
import com.tangocard.raas.RaasClient;
import com.tangocard.raas.exceptions.RaasGenericException;
import com.tangocard.raas.models.CreateOrderRequestModel;
import com.tangocard.raas.models.NameEmailModel;
import com.tangocard.raas.models.OrderModel;

public class GiftCardService {
	private final static Logger logger = Logger.getLogger(GiftCardService.class.getName());

	
	public OrderModel sendGiftCard(RedeemingRequests redeemingRequests,String unitid,String from)throws RestResponseException{
		ApplicationSettingsService applicationSettingsService=new ApplicationSettingsService();
		
		String environment=applicationSettingsService.getApplicationSetting(ApplicationSettingsRepository.TANGO_CARD_ENVIRONMENT);
		if(environment.equalsIgnoreCase(Constants.TANGO_CARD_PRODUCTION)){
			Configuration.environment=Configuration.environment.PRODUCTION;
		}else{
			Configuration.environment=Configuration.environment.SANDBOX;
		}

		RaasClient raasClient=new  RaasClient(applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.TANGO_CARD_PLATFORM_IDENTIFIER),applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.TANGO_CARD_PLATFORM_KEY));
	    String externalRefId = redeemingRequests.getRedeemingRequestId();
	       
	    NameEmailModel recipientNameEmailModel = new NameEmailModel();
	    recipientNameEmailModel.setFirstName(redeemingRequests.getFirstName());
	    if(redeemingRequests.getLastName()!=null){
	       recipientNameEmailModel.setLastName(redeemingRequests.getLastName());
	    }
	    recipientNameEmailModel.setEmail(redeemingRequests.getEmail());

	    CreateOrderRequestModel createOrderRequestModel = new CreateOrderRequestModel();
	    createOrderRequestModel.setExternalRefID(externalRefId);
	    createOrderRequestModel.setCustomerIdentifier(applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.TANGO_CARD_CUSTOMER_NAME));
	    createOrderRequestModel.setAccountIdentifier(applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.TANGO_CARD_ACCOUNT_NAME));
	    createOrderRequestModel.setRecipient(recipientNameEmailModel);
	    createOrderRequestModel.setSendEmail(true);
	    createOrderRequestModel.setUtid(unitid); // Amazon.com Variable item
	    createOrderRequestModel.setAmount(Double.parseDouble(redeemingRequests.getAmount()));
	    
	    createOrderRequestModel.setMessage(String.format(applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.TANGO_CARD_EMAIL_TEMPLATE_MESSAGE),from));
	    createOrderRequestModel.setEmailSubject(String.format(applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.TANGO_CARD_EMAIL_TEMPLATE_SUBJECT),from));

	    NameEmailModel senderNameEmailModel = new NameEmailModel();
	    senderNameEmailModel.setFirstName(from);
	    senderNameEmailModel.setLastName("");
	    senderNameEmailModel.setEmail(applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.TANGO_CARD_ACCOUNT_EMAIL));
	    createOrderRequestModel.setSender(senderNameEmailModel);
	    
		try {
			OrderModel orderModel= raasClient.getOrders().createOrder(createOrderRequestModel);
			return orderModel;
		} catch (Throwable e) {
			if(e instanceof RaasGenericException){
			  String msg=null;
			  try{
				msg=IOUtils.toString(((RaasGenericException)e).getHttpContext().getResponse().getRawBody(),Charset.defaultCharset());				
				logger.log(Level.SEVERE,msg);
			  }catch(IOException ioe){
				  logger.log(Level.SEVERE,"IOException",ioe);
				  throw new RestResponseException(100, "Unable to convert Raas response body");
			  }		
			  throw new RestResponseException(100,msg); 
			  
			}else{				
				logger.log(Level.SEVERE,"Send GiftCard, not Raas exception",e); 
				throw new RestResponseException(100, "Not Raas exception, investigate the log");
			}
			
			  
		}
	    
	     
	        		
	}
	
	private String json(Object object) throws Exception {
        return JSONUtils.convertToString(object);		
    }
}
