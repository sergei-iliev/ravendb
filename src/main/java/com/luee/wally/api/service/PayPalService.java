package com.luee.wally.api.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.luee.wally.admin.repository.ApplicationSettingsRepository;
import com.luee.wally.admin.repository.GiftCardRepository;
import com.luee.wally.command.invoice.Money;
import com.luee.wally.command.invoice.PayoutResult;
import com.luee.wally.constants.Constants;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.exception.RestResponseException;
import com.paypal.api.payments.Currency;
import com.paypal.api.payments.ErrorDetails;
import com.paypal.api.payments.Payout;
import com.paypal.api.payments.PayoutBatch;
import com.paypal.api.payments.PayoutItem;
import com.paypal.api.payments.PayoutSenderBatchHeader;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

public class PayPalService {
	private final Logger logger = Logger.getLogger(PayPalService.class.getName());

	private static final String PAYOUT_STATUS_PENDING = "PENDING";
	private static final String PAYOUT_STATUS_PROCESSING = "PROCESSING";
	private static final String PAYOUT_STATUS_SUCCESS = "SUCCESS";

	private static final String RECEIVER_UNREGISTERED="RECEIVER_UNREGISTERED";
	private static final String PENDING_RECIPIENT_NON_HOLDING_CURRENCY_PAYMENT_PREFERENCE="PENDING_RECIPIENT_NON_HOLDING_CURRENCY_PAYMENT_PREFERENCE";	
	
	public PayoutResult payout(RedeemingRequests payPalUser,String currencyCode) throws PayPalRESTException {
		PayoutResult payoutResult=new PayoutResult();
		ApplicationSettingsService applicationSettingsService=new ApplicationSettingsService();
		
		//read package name title
		GiftCardRepository giftCardRepository=new GiftCardRepository();
		Entity entity=giftCardRepository.getPackageNameTitleMapping(payPalUser.getPackageName());
		if(entity==null){
			throw new PayPalRESTException("No title in package to title mapping table.");			
		}
		
		String title=(String)entity.getProperty("title");
	    String subject=(String.format(applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.TANGO_CARD_EMAIL_TEMPLATE_SUBJECT),title));
	    String message=(String.format(applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.TANGO_CARD_EMAIL_TEMPLATE_MESSAGE),title));
		
	    String paypalClientId=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYPAL_CLIENT_ID);
	    String paypalClientSecret=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYPAL_CLIENT_SECRET);
	    String paypalMode=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.PAYPAL_MODE);

	    
	    		
		// ###Payout
		// A resource representing a payout
		Payout payout = new Payout();

		PayoutSenderBatchHeader senderBatchHeader = new PayoutSenderBatchHeader();

		// ### NOTE:
		// You can prevent duplicate batches from being processed. If you
		// specify a `sender_batch_id` that was used in the last 30 days, the
		// batch will not be processed. For items, you can specify a
		// `sender_item_id`. If the value for the `sender_item_id` is a
		// duplicate of a payout item that was processed in the last 30 days,
		// the item will not be processed.
		// #### Batch Header Instance
		String senderBatchId = System.currentTimeMillis() + "";
		senderBatchHeader.setSenderBatchId(senderBatchId + "001").setEmailSubject(subject);

		// ### Currency
		Currency amount = new Currency();
		amount.setValue(payPalUser.getAmount()).setCurrency(currencyCode);

		// #### Sender Item
		// Please note that if you are using single payout with sync mode, you
		// can only pass one Item in the request
		PayoutItem senderItem = new PayoutItem();
		senderItem.setRecipientType("Email").setNote(message)
				.setReceiver(payPalUser.getPaypalAccount()).setSenderItemId("201404324234").setAmount(amount);

		List<PayoutItem> items = new ArrayList<PayoutItem>();
		items.add(senderItem);

		payout.setSenderBatchHeader(senderBatchHeader).setItems(items);


			// ### Api Context
			// Pass in a `ApiContext` object to authenticate
			// the call and to send a unique request id
			// (that ensures idempotency). The SDK generates
			// a request id if you do not pass one explicitly.
			APIContext apiContext = new APIContext(paypalClientId, paypalClientSecret, paypalMode);

			// ###Create Payout Asynchronous
			Map<String, String> parameters = new HashMap<>();
			parameters.put("sync_mode", "false");
			PayoutBatch batch = payout.create(apiContext, parameters);
			payoutResult.setPayoutBatchId(batch.getBatchHeader().getPayoutBatchId());
			
			PayoutBatch pay;
			for(int i=0;i<Constants.PAYPAL_LOOP_COUNT;i++){
				pay= Payout.get(apiContext, batch.getBatchHeader().getPayoutBatchId());
				
				if(pay.getBatchHeader().getBatchStatus().equals(PAYOUT_STATUS_SUCCESS)){
				    //could be error success
					if((!pay.getItems().isEmpty())&&pay.getItems().get(0).getError()!=null){

						if(!pay.getItems().get(0).getError().getName().isEmpty()){
							if(pay.getItems().get(0).getError().getName().equals(RECEIVER_UNREGISTERED)||
							   pay.getItems().get(0).getError().getName().equals(PENDING_RECIPIENT_NON_HOLDING_CURRENCY_PAYMENT_PREFERENCE)){
								payoutResult.setPayoutError(pay.getItems().get(0).getError().getName());								
							}else{
								throw createPayoutException(pay.getItems().get(0).getError(),payPalUser.getPaypalAccount());	
							}
						}else{						
					       throw createPayoutException(pay.getItems().get(0).getError(),payPalUser.getPaypalAccount());
						}
					}
					//success completed
					payoutResult.setAmount(new Money( pay.getBatchHeader().getAmount().getValue(),pay.getBatchHeader().getAmount().getCurrency()));

					if(pay.getBatchHeader().getFees()!=null){
						payoutResult.setFee(new Money(pay.getBatchHeader().getFees().getValue(),pay.getBatchHeader().getFees().getCurrency())); 	
					}else{
						payoutResult.setFee(new Money("0.0",currencyCode));	
					}
					return payoutResult;
				}
			
			try{
				Thread.currentThread().sleep(500);
			}catch(InterruptedException e){}
			}
			
			
		throw createTimeoutException(payPalUser.getPaypalAccount());
	}

	private PayPalRESTException createTimeoutException(String paypalAccount) {
		PayPalRESTException ex = new PayPalRESTException("PayPal payout processing timeout for user "+paypalAccount);
		com.paypal.api.payments.Error error = new com.paypal.api.payments.Error();

		ErrorDetails errorDetails = new ErrorDetails("PayPal payout timeout", paypalAccount);
		error.setDetails(Arrays.asList(errorDetails));
		ex.setDetails(error);
		return ex;
	}
	
	private PayPalRESTException createPayoutException(com.paypal.api.payments.Error _error,String paypalAccount) {
		
		PayPalRESTException ex = new PayPalRESTException("PayPal payout exception for user "+paypalAccount);
		com.paypal.api.payments.Error error = new com.paypal.api.payments.Error();
		
		ErrorDetails errorDetails = new ErrorDetails(_error.getName(), _error.getMessage());
		error.setDetails(Arrays.asList(errorDetails));
		ex.setDetails(error);		
		return ex;
	}
}
