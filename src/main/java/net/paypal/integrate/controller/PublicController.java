package net.paypal.integrate.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import net.paypal.integrate.api.Constants;
import net.paypal.integrate.service.PayPalService;

@Controller
public class PublicController {
	
	private final Logger logger = Logger.getLogger(PublicController.class.getName());

	@Autowired
	private PayPalService payPalService; 

	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String start(HttpServletResponse response,HttpServletRequest request) throws IOException {
		    //mailService.sendMail("sergei_iliev@yahoo.com", "Hello man", "This must be my first test");
			return "index";
		
	}
	@RequestMapping(value = "/payment/cancel")
	public String cancel(){
		return "cancel";
	}
	@RequestMapping(value = "/payment/execute")
	public String execute(  @RequestParam String paymentId,  @RequestParam String token,  @RequestParam String PayerID){		        		
		try {
	        Payment payment = new Payment();
	        payment.setId(paymentId);
	        PaymentExecution paymentExecute = new PaymentExecution();
	        paymentExecute.setPayerId(PayerID);
	        
	        APIContext apiContext = new APIContext(Constants.clientId,Constants.clientSecret, Constants.mode);
	        apiContext.usingGoogleAppEngine(true);
	        Payment createdPayment= payment.execute(apiContext, paymentExecute);
	            				
		    System.out.println(createdPayment.getState());
		    
		} catch (PayPalRESTException e) {
			e.printStackTrace();
		}
		
		return "execute";
	}
	@RequestMapping(value = "/payment", method = RequestMethod.GET)
	public String payment(HttpServletRequest req, HttpServletResponse resp, ModelMap model) throws IOException {
		   
			
			Amount amount = new Amount();
			amount.setCurrency("USD");
			amount.setTotal("12.00");

			Transaction transaction = new Transaction();
			transaction.setAmount(amount);
			List<Transaction> transactions = new ArrayList<>();
			transactions.add(transaction);

			Payer payer = new Payer();
			payer.setPaymentMethod("paypal");

			Payment payment = new Payment();
			payment.setIntent("authorize");
			payment.setPayer(payer);
			payment.setTransactions(transactions);

			// ###Redirect URLs
			RedirectUrls redirectUrls = new RedirectUrls();
					redirectUrls.setCancelUrl(req.getScheme() + "://"
								+ req.getServerName() + ":" + req.getServerPort()
								+ req.getContextPath() + "/payment/cancel");
					redirectUrls.setReturnUrl(req.getScheme() + "://"
								+ req.getServerName() + ":" + req.getServerPort()
								+ req.getContextPath() + "/payment/execute");

			
			payment.setRedirectUrls(redirectUrls);
						
			try {
			    APIContext apiContext = new APIContext(Constants.clientId, Constants.clientSecret, Constants.mode);
			    apiContext.usingGoogleAppEngine(true);
			    Payment createdPayment = payment.create(apiContext);
			    System.out.println(createdPayment.getLastResponse());
			    model.addAttribute("result",createdPayment.getLastResponse());
			    
			 // ### Take Payment Approval Url
			    String redirectURL="none";
			    Iterator<Links> links = createdPayment.getLinks().iterator();
			    while (links.hasNext()) {
			    	Links link = links.next();
			    	if (link.getRel().equalsIgnoreCase("approval_url")) {
			            redirectURL=link.getHref();
			    		model.addAttribute("redirect",link.getHref());
			    	   
			    	}
			    }
			    
			    System.out.println(redirectURL);
			    return  "redirect:"+redirectURL;
			    // For debug purposes only: System.out.println(createdPayment.toString());
			} catch (PayPalRESTException e) {
				logger.log(Level.SEVERE,"error->",e);
				model.addAttribute("result",e.toString());	
			} catch (Exception ex) {
				logger.log(Level.SEVERE,"error->",ex);
				model.addAttribute("result","->"+ex.toString());
			}
			return "payment";
		
	}
/*	
	@RequestMapping(value = "/payout", method = RequestMethod.GET)
	public String payout(ModelMap model){
//		// ###Payout
//				// A resource representing a payout
//				Payout payout = new Payout();
//
//				PayoutSenderBatchHeader senderBatchHeader = new PayoutSenderBatchHeader();
//
//				// ### NOTE:
//				// You can prevent duplicate batches from being processed. If you
//				// specify a `sender_batch_id` that was used in the last 30 days, the
//				// batch will not be processed. For items, you can specify a
//				// `sender_item_id`. If the value for the `sender_item_id` is a
//				// duplicate of a payout item that was processed in the last 30 days,
//				// the item will not be processed.
//				// #### Batch Header Instance
//				String senderBatchId = System.currentTimeMillis()+"";
//				senderBatchHeader.setSenderBatchId(
//						senderBatchId+"001").setEmailSubject(
//						"You have a Payout!");
//
//				// ### Currency
//				Currency amount = new Currency();
//				amount.setValue("20").setCurrency("EUR");
//
//				// #### Sender Item
//				// Please note that if you are using single payout with sync mode, you
//				// can only pass one Item in the request
//				PayoutItem senderItem = new PayoutItem();
//				senderItem.setRecipientType("Email")
//						.setNote("Thanks for your service")
//						.setReceiver("sergei.iliev-buyer@gmail.com")
//						.setSenderItemId("201404324234").setAmount(amount);
//
//				List<PayoutItem> items = new ArrayList<PayoutItem>();
//				items.add(senderItem);
//
//				payout.setSenderBatchHeader(senderBatchHeader).setItems(items);
//
//				PayoutBatch batch = null;
//				try {
//
//					// ### Api Context
//					// Pass in a `ApiContext` object to authenticate
//					// the call and to send a unique request id
//					// (that ensures idempotency). The SDK generates
//					// a request id if you do not pass one explicitly.
//					APIContext apiContext =new APIContext(PayPalService.clientId, PayPalService.clientSecret, PayPalService.mode);
//
//					// ###Create Payout Synchronous
//			        Map<String,String> parameters = new HashMap<>();
//			        parameters.put("sync_mode", "false");
//					batch = payout.create(apiContext,parameters);
//
//					System.out.println("Payout Batch With ID: "
//							+ batch.getBatchHeader().getPayoutBatchId());
//					System.out.println(
//							Payout.getLastRequest()+"::"+ Payout.getLastResponse());
//					model.addAttribute("payout", payout);
//				} catch (PayPalRESTException e) {
//					logger.log(Level.SEVERE,"payout error:",e);
//					model.addAttribute("error",e.toString());					
//				}	
		    try{
		        Payout payout=payPalService.payout();
		        model.addAttribute("payout", payout);
				} catch (PayPalRESTException e) {
				logger.log(Level.SEVERE,"payout error:",e);
				model.addAttribute("error",e.toString());					
			}	
			return "payout";
		
	}
*/
}
