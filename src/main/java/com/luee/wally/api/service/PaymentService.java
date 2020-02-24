package com.luee.wally.api.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.luee.wally.admin.repository.ApplicationSettingsRepository;
import com.luee.wally.admin.repository.GiftCardRepository;
import com.luee.wally.admin.repository.InvoiceRepository;
import com.luee.wally.admin.repository.PaymentRepository;
import com.luee.wally.command.Email;
import com.luee.wally.command.PaidUserForm;
import com.luee.wally.command.PayExternalForm;
import com.luee.wally.command.PaymentEligibleUserForm;
import com.luee.wally.command.PdfAttachment;
import com.luee.wally.command.invoice.PayoutResult;
import com.luee.wally.entity.GiftCardCountryCode;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.exception.RestResponseException;
import com.luee.wally.json.JSONUtils;
import com.paypal.api.payments.ErrorDetails;
import com.paypal.base.rest.PayPalRESTException;
import com.tangocard.raas.models.OrderModel;

public class PaymentService extends AbstractService{
	
	private final Logger logger = Logger.getLogger(PaymentService.class.getName());

	
	public Collection<String> getDefaultPaymentTypes(){
		return Arrays.asList("PayPal","Amazon","Google Play");
	}
	public Collection<String> getDefaultCurrencyCodes(){
		return Arrays.asList("USD","EUR","CAD","AUD","GBP");
	}
	
	public void editEmail(String email,String key){
	   PaymentRepository paymentRepository=new PaymentRepository();
	   Entity entity=paymentRepository.getRedeemingRequestsByKey(key);
	   entity.setProperty("email", email);	   
	   paymentRepository.save(entity);
	}
	public void editPayPalAccount(String paypal,String key){
		   PaymentRepository paymentRepository=new PaymentRepository();
		   Entity entity=paymentRepository.getRedeemingRequestsByKey(key);
		   entity.setProperty("paypal_account", paypal);	   
		   paymentRepository.save(entity);
	}
	/*Don’t allow any attempt to pay:
       1. More than 30 EUR (or an equivalent amount in a different currency) within the same transaction.
       2. More than 100 EUR (or an equivalent amount in a different currency) to the same paypal account / email address (based on the paid_users_external table).
	 */
    public void validateExternalForm(PayExternalForm form)throws Exception{
		
    	if(Objects.isNull(form.getRedeemingRequestId())||form.getRedeemingRequestId().isEmpty()||
				   Objects.isNull(form.getType())||form.getType().isEmpty()){
					throw new Exception("Invalid form data");													
		}
    	PaymentRepository paymentRepository=new PaymentRepository();
		//convert currency to EUR
		BigDecimal eurAmount;
		try{
			eurAmount = paymentRepository.convert(Double.parseDouble(form.getAmount()), form.getCurrency());
		}catch(Exception e){
			logger.log(Level.SEVERE,"Currency converter for : "+form.getCurrency(),e);
			throw e;			
		}
		//1. more then 30?
		BigDecimal maxAmount=BigDecimal.valueOf(30.0);
		if(eurAmount.compareTo(maxAmount)==1){
		   throw new Exception("Amount in EUR {"+eurAmount+"} is more then 30");	
		}
		//2.all payments less then 100  
    	Collection<Entity> entities=paymentRepository.getExternalPaidUserByEmail(form.getPaypalAccount()==null?form.getEmailAddress():form.getPaypalAccount());
    	double sum=entities.stream().mapToDouble(e->(double)e.getProperty("eur_currency")).sum();
    	BigDecimal total=eurAmount.add(BigDecimal.valueOf(sum));
    	BigDecimal hundred=BigDecimal.valueOf(100.0); 
    	if(total.compareTo(hundred)==1){
    		 throw new Exception("Total Amount {"+sum+"}, and requested {"+eurAmount+"} is more then 100");	
    	}
    }
	public void payExternal(HttpServletResponse resp,PayExternalForm form)throws IOException{
		//convert currency to EUR		
		PaymentRepository paymentRepository = new PaymentRepository();
		String currencyCode=form.getCurrency();
		
		BigDecimal eurAmount;
		try{
			eurAmount = paymentRepository.convert(Double.parseDouble(form.getAmount()), currencyCode);
		}catch(Exception e){
			logger.log(Level.SEVERE,"Currency converter for : "+currencyCode+" and amount: "+form.getAmount(),e);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Unable to convert currency to EUR");
			return;
		}
		
		//check if already paid
		Entity paidUserExt = paymentRepository.getExternalPaidUserByRedeemingRequestId(form.getRedeemingRequestId());
		if(paidUserExt!=null){
			logger.log(Level.WARNING,"User already paid.");
			resp.sendError(HttpServletResponse.SC_CONFLICT,"User already paid");			
			return;
		}
		if(form.getType().equalsIgnoreCase("PayPal")){
			sendPayPal(resp,form, eurAmount);			
		}else if(form.getType().equalsIgnoreCase("Amazon")){
			sendGiftCard(resp,form, eurAmount);
		}else{
			logger.log(Level.WARNING,"Unknown type: "+form.getType());
		}
		
	}
	/*
	 * external user payment
	 */
	private void sendPayPal(HttpServletResponse resp,PayExternalForm form,BigDecimal eurAmount)throws IOException{
		PayPalService payPalService = new PayPalService();
		InvoiceService invoiceService = new InvoiceService();
		MailService mailService = new MailService();
		PaymentRepository paymentRepository = new PaymentRepository();

		
		ApplicationSettingsService applicationSettingsService=new ApplicationSettingsService();
		String toInvoiceMail=applicationSettingsService.getApplicationSetting(ApplicationSettingsRepository.TO_INVOICE_MAIL);
		String fromMail=applicationSettingsService.getApplicationSetting(ApplicationSettingsRepository.FROM_MAIL);
		
		InvoiceRepository invoiceRepository = new InvoiceRepository();
		RedeemingRequests redeemingRequests=form.toRedeemingRequests();
		try {
			//payout
			PayoutResult payoutResult = payPalService.payout(redeemingRequests,form.getCurrency());
			//create invoice number
			String invoiceNumber = Long.toString(invoiceRepository.createInvoiceNumber());			
			
			//save save paid user external
			paymentRepository.saveExternalPaidUser(form.toPaidUserExternal(), eurAmount,invoiceNumber, payoutResult.getPayoutBatchId());
            //create invoice
			PdfAttachment attachment = new PdfAttachment();
			attachment.readFromStream(invoiceService.createInvoice(payoutResult, 
																	form.getFullName(),form.getAddress(),form.getCountryCode(),form.getPaypalAccount(),
																	invoiceNumber));
			//send invoice
			mailService.sendGridInvoice(toInvoiceMail,fromMail, attachment);
			resp.setStatus(HttpStatus.SC_OK);
		}catch(PayPalRESTException ppe){
			logger.log(Level.SEVERE, "PayPal rest payment: "+ppe.getMessage(), ppe);
			StringBuffer sb=new StringBuffer();
			sb.append("PayPal exception, check logs for details\r\n");
			if(ppe.getDetails()!=null){
			  for(ErrorDetails errorDetails:ppe.getDetails().getDetails()){
			   sb.append(errorDetails.getField()+"\r\n");
			   sb.append(errorDetails.getIssue()+"\r\n");
			 }
			}
			resp.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR,sb.toString());
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "payment", ex);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			String sStackTrace = sw.toString();
			//send mail
			Email email = new Email();
			email.setSubject("Error alert!");
			email.setContent((Objects.toString(ex.getMessage(), "")) + "/n/n" + sStackTrace);
			email.setFrom(fromMail);
			email.setTo(toInvoiceMail);
			mailService.sendMail(email);
			//send resp
			resp.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR,"Server error, check logs/email for details.");
		}		
		
	}
	/*
	 * external user payment
	 */
	public void sendGiftCard(HttpServletResponse resp,PayExternalForm form,BigDecimal eurAmount)throws IOException{
		PaymentRepository paymentRepository=new PaymentRepository();
		GiftCardRepository giftCardRepository=new GiftCardRepository();
		
		
		RedeemingRequests redeemingRequests=form.toRedeemingRequests();
		
		
		Entity entity=giftCardRepository.getGiftCardCountryCodeMapping(redeemingRequests.getCountryCode());
		GiftCardCountryCode giftCardCountryCode=GiftCardCountryCode.valueOf(entity);
		
		entity=giftCardRepository.getPackageNameTitleMapping(redeemingRequests.getPackageName());
		if(entity==null){
			logger.log(Level.WARNING,"No title in package to title mapping table.");
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"No title in package to title mapping table.");
			return;		
		}
		
		
		GiftCardService giftCardService=new GiftCardService();	
		
		OrderModel order;
		try {
			order = giftCardService.sendGiftCard(redeemingRequests, giftCardCountryCode.getUnitid(),(String)entity.getProperty("title"));
			//save paid user external
			paymentRepository.saveExternalPaidUser(form.toPaidUserExternal(), eurAmount,null, order.getReferenceOrderID());
			resp.setStatus(HttpStatus.SC_OK);
		} catch (RestResponseException e) {
			logger.log(Level.SEVERE, "Send Tango Card error ", e);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Send Tango Card error.");
		}


		
	}
	public void sendGiftCard(String key) throws JsonProcessingException,RestResponseException{
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
		Entity giftCardOrder=paymentRepository.getPaidUserByRedeemingRequestId(redeemingRequests.getRedeemingRequestId());
		if(giftCardOrder!=null){		   
			throw new RestResponseException(200, JSONUtils.writeObject(giftCardOrder, Entity.class));				
		}
		
		GiftCardService giftCardService=new GiftCardService();				
		OrderModel order=giftCardService.sendGiftCard(redeemingRequests, giftCardCountryCode.getUnitid(),(String)entity.getProperty("title"));
		//convert currency to EUR
		BigDecimal eurAmount;
		try{
			eurAmount = paymentRepository.convert(Double.parseDouble(redeemingRequests.getAmount()), giftCardCountryCode.getCurrency());
		}catch(Exception e){
			logger.log(Level.SEVERE,"Currency converter for : "+giftCardCountryCode.getCurrency(),e);
			throw new RestResponseException(100, "Unable to convert currency");	
		}
		//save gift card order reference id
		paymentRepository.saveGiftCardPayment(redeemingRequests, giftCardCountryCode.getCurrency(), eurAmount, order.getReferenceOrderID());
	}
	
	public void pay(PaidUserForm form,Entity redeemingRequests) throws Exception{
		PaymentRepository paymentRepository=new PaymentRepository();
		/*
		BigDecimal rateValue=BigDecimal.ONE;
		
		if(!form.getCurrencyCode().equals("EUR")){
			 String formatedDate=Utilities.formatedDate(new Date(),"yyyy-MM-dd");
			 ImportService importService=new ImportService();
			 ExchangeRateVO rate=importService.getExchangeRates(formatedDate,"EUR",form.getCurrencyCode());
			 rateValue = BigDecimal.valueOf(rate.getRates().get(form.getCurrencyCode()));
		}
		
		BigDecimal currentValue = new BigDecimal(form.getAmount());		
		BigDecimal eurAmount = currentValue.divide(rateValue,2, BigDecimal.ROUND_HALF_EVEN);
		*/
		BigDecimal eurAmount = paymentRepository.convert(form.getAmount(), form.getCurrencyCode());
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
    	//apply amount filter
    	Collection<RedeemingRequests> list=applyAmountFilter(result, form.getAmountFrom(), form.getAmountTo());
    	//sort by date
    	return list.stream().sorted(Comparator.comparing(RedeemingRequests::getDate)).collect(Collectors.toList());
    	
    }
    /*
     * In memory filter
     */
    private Collection<RedeemingRequests> applyAmountFilter(Collection<RedeemingRequests> list,BigDecimal amountFrom,BigDecimal amountTo){
    	if(amountFrom==null&&amountTo==null){
    		return list;
    	}
    	Collection<RedeemingRequests> result;
    	if(amountFrom!=null&&amountTo!=null){
    		result=list.stream().filter(rr->{
    			if(rr.getAmount()!=null){
    				if((new BigDecimal(rr.getAmount())).compareTo(amountFrom)>=0&&(new BigDecimal(rr.getAmount())).compareTo(amountTo)<=0){
    		            return true;    		            
    		        }else{
    		        	return false;
    		        }
    			}else{
    				return true;
    			}
    			
    		}).collect(Collectors.toList());
    	}else if(amountFrom!=null){
    		result=list.stream().filter(rr->{
    			if(rr.getAmount()!=null){
    				if((new BigDecimal(rr.getAmount())).compareTo(amountFrom)>=0){
    		            return true;    		            
    		        }else{
    		        	return false;
    		        }
    			}else{
    				return true;
    			}
    			
    		}).collect(Collectors.toList());
    	}else{
    		result=list.stream().filter(rr->{
    			if(rr.getAmount()!=null){
    				if((new BigDecimal(rr.getAmount())).compareTo(amountTo)<=0){
    		            return true;    		            
    		        }else{
    		        	return false;
    		        }
    			}else{
    				return true;
    			}    			
    		}).collect(Collectors.toList());
    	}
    	
    	return result;
    }


}
