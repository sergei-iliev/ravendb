package com.luee.wally.api.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.ServletException;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.luee.wally.admin.repository.GiftCardRepository;
import com.luee.wally.admin.repository.PaymentRepository;
import com.luee.wally.api.service.impex.ImportService;
import com.luee.wally.command.PaidUserForm;
import com.luee.wally.command.PaymentEligibleUserForm;
import com.luee.wally.constants.Constants;
import com.luee.wally.entity.GiftCardCountryCode;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.exception.RestResponseException;
import com.luee.wally.json.ExchangeRateVO;
import com.luee.wally.utils.Utilities;
import com.tangocard.raas.exceptions.RaasGenericException;
import com.tangocard.raas.models.CreateOrderRequestModel;
import com.tangocard.raas.models.NameEmailModel;
import com.tangocard.raas.models.OrderModel;

public class PaymentService {
	private final Logger logger = Logger.getLogger(PaymentService.class.getName());

	
	public Collection<String> getDefaultPaymentTypes(){
		return Arrays.asList("PayPal","Amazon","Google Play");
	}
	public Collection<String> getDefaultCurrencyCodes(){
		return Arrays.asList("USD","EUR","CAD","AUD","GBP");
	}
	
	public void sendGiftCard(String key) throws RestResponseException{
		PaymentRepository paymentRepository=new PaymentRepository();
		GiftCardRepository giftCardRepository=new GiftCardRepository();
		
		Entity entity=paymentRepository.getRedeemingRequestsByKey(key);
		RedeemingRequests redeemingRequests=RedeemingRequests.valueOf(entity);
		
		
		entity=giftCardRepository.getGiftCardCountryCodeMapping(redeemingRequests.getCountryCode());
		GiftCardCountryCode giftCardCountryCode=GiftCardCountryCode.valueOf(entity);
		
		entity=giftCardRepository.getPackageNameTitleMapping(redeemingRequests.getPackageName());
		if(entity==null){
			throw new RestResponseException(100, "No title in package to title mapping table.");			
		}
		//check if gift card already sent
		Entity giftCardOrder=giftCardRepository.getGiftCardOrder(redeemingRequests.getRedeemingRequestId());
		if(giftCardOrder!=null){
			throw new RestResponseException(100, "Gift card already sent!");				
		}
		
		GiftCardService giftCardService=new GiftCardService();				
		OrderModel order=giftCardService.sendGiftCard(redeemingRequests, giftCardCountryCode.getUnitid(),(String)entity.getProperty("title"));
		//save gift card order reference id
		giftCardRepository.saveGiftCardOrder(redeemingRequests.getRedeemingRequestId(), order.getReferenceOrderID());
	}
	
	public void pay(PaidUserForm form,Entity redeemingRequests) throws Exception{
		PaymentRepository paymentRepository=new PaymentRepository();
		BigDecimal rateValue=BigDecimal.ONE;
		
		if(!form.getCurrencyCode().equals("EUR")){
			 String formatedDate=Utilities.formatedDate(new Date(),"yyyy-MM-dd");
			 ImportService importService=new ImportService();
			 ExchangeRateVO rate=importService.getExchangeRates(formatedDate,"EUR",form.getCurrencyCode());
			 rateValue = BigDecimal.valueOf(rate.getRates().get(form.getCurrencyCode()));
		}
		
		BigDecimal currentValue = new BigDecimal(form.getAmount());		
		BigDecimal eurAmount = currentValue.divide(rateValue,2, BigDecimal.ROUND_HALF_EVEN);

		paymentRepository.saveUserPayment(form,redeemingRequests,eurAmount);

	}
	
	public void saveUserPaymentRemovalReason(String key,String reason) throws EntityNotFoundException{
		PaymentRepository paymentRepository=new PaymentRepository();
		paymentRepository.saveUserPaymentRemovalReason(key, reason);
	}
    public Collection<RedeemingRequests> searchEligibleUsers(PaymentEligibleUserForm form){
    	PaymentRepository paymentRepository=new PaymentRepository();
    	Collection<RedeemingRequests> result=new ArrayList<RedeemingRequests>();
    	for(String type:form.getTypes()){    		
    		if(form.getPackageNames().size()>0){
    			for(String packageName:form.getPackageNames()){
    				if(form.getCountryCodes().size()>0){
    				   for(String countryCode:form.getCountryCodes()){	
    			        result.addAll(paymentRepository.findEligibleUsers(type,form.getStartDate(),form.getEndDate(),packageName,countryCode,form.getConfirmedEmail()));
    				   }
    				}else{
    					result.addAll(paymentRepository.findEligibleUsers(type,form.getStartDate(),form.getEndDate(),packageName,null,form.getConfirmedEmail()));
    				}
    			}
    		}else{  
    			if(form.getCountryCodes().size()>0){
    				for(String countryCode:form.getCountryCodes()){	  			      
    					result.addAll(paymentRepository.findEligibleUsers(type,form.getStartDate(),form.getEndDate(),null,countryCode,form.getConfirmedEmail()));
    				}    	  		 
    			}else{
    				    result.addAll(paymentRepository.findEligibleUsers(type,form.getStartDate(),form.getEndDate(),null,null,form.getConfirmedEmail()));
    			}
    		}
    	}
    	//sort by date
    	return result.stream().sorted(Comparator.comparing(RedeemingRequests::getDate)).collect(Collectors.toList());
    	
    	
//    	PaymentRepository paymentRepository=new PaymentRepository();
//        paymentRepository.findEligibleUsers();
//        
//        Collection<RedeemingRequests> r=new ArrayList<RedeemingRequests>();
//        
//        RedeemingRequests req=new RedeemingRequests();
//        req.setAmount("23");
//        req.setCountryCode("US");
//        req.setEmail("garcia");
//        req.setPaypalAccount("serto@ggg.com");
//        req.setType("PayPal");
//        req.setUserGuid("3453434567547567");
//        r.add(req);
//        return r;
    }


}
