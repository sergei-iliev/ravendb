package usecase;

import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.luee.wally.admin.controller.PaymentController;
import com.luee.wally.admin.repository.InvoiceRepository;
import com.luee.wally.admin.repository.PaymentRepository;
import com.luee.wally.api.service.InvoiceService;
import com.luee.wally.api.service.MailService;
import com.luee.wally.api.service.PayPalService;
import com.luee.wally.api.service.PaymentOrderTransactionsService;
import com.luee.wally.api.service.impex.ImportService;
import com.luee.wally.command.Email;
import com.luee.wally.command.PdfAttachment;
import com.luee.wally.command.invoice.Money;
import com.luee.wally.command.invoice.PayoutResult;
import com.luee.wally.command.order.OrderTransactionResult;
import com.luee.wally.constants.Constants;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.json.ExchangeRateVO;
import com.luee.wally.utils.TestDatabase;
import com.luee.wally.utils.Utilities;
import com.paypal.api.payments.Currency;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentHistory;
import com.paypal.api.payments.Payout;
import com.paypal.api.payments.PayoutBatch;
import com.paypal.api.payments.PayoutItem;
import com.paypal.api.payments.PayoutSenderBatchHeader;
import com.paypal.base.rest.APIContext;

import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;
import urn.ebay.api.PayPalAPI.TransactionSearchReq;
import urn.ebay.api.PayPalAPI.TransactionSearchRequestType;
import urn.ebay.api.PayPalAPI.TransactionSearchResponseType;


public class PayPalTest {
	private final static Logger logger = Logger.getLogger(PayPalTest.class.getName());

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	private static final String PAYOUT_STATUS_PENDING = "PENDING";
	private static final String PAYOUT_STATUS_PROCESSING = "PROCESSING";
	private static final String PAYOUT_STATUS_SUCCESS = "SUCCESS";
	private static final String PAYOUT_STATUS_DENIED = "DENIED";
	
	
	private static final String RECEIVER_ACCOUNT_LIMITATION="RECEIVER_ACCOUNT_LIMITATION";
	private static final String RECEIVER_UNREGISTERED="RECEIVER_UNREGISTERED";
	private static final String RECEIVER_UNCONFIRMED="RECEIVER_UNCONFIRMED";
	private static final String PENDING_RECIPIENT_NON_HOLDING_CURRENCY_PAYMENT_PREFERENCE="PENDING_RECIPIENT_NON_HOLDING_CURRENCY_PAYMENT_PREFERENCE";	

	
	@Mock
	private HttpServletResponse response;
	
	@Mock
	private HttpServletRequest request;
	
	@Before
	public void initialize() {
		helper.setUp();
		MockitoAnnotations.initMocks(this);
		
		Utilities.domain = "demo.test";
		TestDatabase.INSTANCE.generateDB();
	}

	@After
	public void release() throws Exception {
		helper.tearDown();
	}
	
	public static final Map<String,String> getAcctAndConfig(){
		Map<String,String> configMap = new HashMap<String,String>();		
		configMap.put("mode", "sandbox");
		// Account Credential
		configMap.put("acct1.UserName", "sergei.iliev-facilitator-1_api1.gmail.com");
		configMap.put("acct1.Password", "TNQ7VML3C9YDQA5W");
		configMap.put("acct1.Signature", "A7UMedn1BfBoD5MMUf9mkg5VRRWMA3YJLZ40MZ6Git756rBh2hNqHxbP");
		
		return configMap;
	}
	
	@Test
	public void payPalTransactionHistoryTest() throws Exception {
		
//		TransactionSearchReq txnreq = new TransactionSearchReq();
//		TransactionSearchRequestType requestType = new TransactionSearchRequestType();
//
//		ZonedDateTime now=ZonedDateTime.now();
//		ZonedDateTime n=now.minusYears(10);
//		//Instant i=Instant.now();
//		Instant start=Instant.from(n);
//		Instant end=Instant.from(now);
//		
//		//requestType.setStartDate(start.toString()); 
//		//requestType.setEndDate("2021-10-05T23:59:59.000Z"); 
//		
//		ZonedDateTime yesterday=ZonedDateTime.now().minusDays(1);
//		   
//		ZonedDateTime yesterdayStart=yesterday.truncatedTo(ChronoUnit.DAYS);
//		ZonedDateTime yesterdayEnd=yesterdayStart.plusHours(24);
//		
//		txnreq.setTransactionSearchRequest(requestType);
//
//				
//		Map<String, String> configurationMap = this.getAcctAndConfig();
//		
//		PaymentOrderTransactionsService paymentOrderTransactionsService=new PaymentOrderTransactionsService();
//		Collection<OrderTransactionResult> result= paymentOrderTransactionsService.getPayPalOrderTransactions(Instant.from(yesterdayStart).toString(),Instant.from(yesterdayEnd).toString(),configurationMap);

		
		
		ZonedDateTime now=ZonedDateTime.now();
		ZonedDateTime yesterday=now.minusYears(10);
		   
		ZonedDateTime yesterdayStart=yesterday.truncatedTo(ChronoUnit.DAYS);
		ZonedDateTime yesterdayEnd=yesterdayStart.plusHours(24);
		
		PaymentOrderTransactionsService paymentOrderTransactionsService=new PaymentOrderTransactionsService();
		Collection<OrderTransactionResult> result=paymentOrderTransactionsService.getPayPalOrderTransactions(Instant.from(yesterday).toString(),Instant.from(now).toString(),getAcctAndConfig());  

	    
		result.forEach(r->{
			System.out.println(r.getValue()+"::"+r.getCurrencyCode()+"::"+r.getTimestamp());
		});
		String formattedDate = Utilities.formatedDate(yesterday, "yyyy-MM-dd");
		
		Map<String,BigDecimal> map= paymentOrderTransactionsService.getPayPalOrderTransactionsGroupBy(result);
		System.out.println(map);
		BigDecimal sum=paymentOrderTransactionsService.calculateTotal(map, formattedDate, "USD");
	    System.out.println("USD="+sum);
		
	}
	@Test
	public void createPayPalPayoutTest() throws Exception {
		//paypal-app-1;sergei.iliev-facilitator-1@gmail.com
		String paypalClientId="AafeAthS3PRG_dkpJPVTkCgVc-O9pQ6o2PldSIOceWsn7nIm0H404DHDFg4svXJa63Pe8OtM55ySzppG";
		String paypalClientSecret="EC1ludnxStRx1M1VP9DDfz2xdTxMA8xcthmP_hRaQgWS09thWY2ihInfz5AA03DMxLjYZnR6sRQn-0XV";
		Payout payout = new Payout();
		PayoutResult payoutResult=new PayoutResult();
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
		senderBatchHeader.setSenderBatchId(senderBatchId + "001").setEmailSubject("new message");

		// ### Currency
		Currency amount = new Currency();
		amount.setValue("2").setCurrency("GBP");

		// #### Sender Item
		// Please note that if you are using single payout with sync mode, you
		// can only pass one Item in the request
		PayoutItem senderItem = new PayoutItem();
		senderItem.setRecipientType("Email").setNote("Thanks")
				.setReceiver("sb-jldmv4787464@personal.example.com").setSenderItemId("201404324234").setAmount(amount);

		List<PayoutItem> items = new ArrayList<PayoutItem>();
		items.add(senderItem);

		payout.setSenderBatchHeader(senderBatchHeader).setItems(items);


			// ### Api Context
			// Pass in a `ApiContext` object to authenticate
			// the call and to send a unique request id
			// (that ensures idempotency). The SDK generates
			// a request id if you do not pass one explicitly.
			APIContext apiContext = new APIContext(paypalClientId, paypalClientSecret, "sandbox");

			// ###Create Payout Asynchronous
			Map<String, String> parameters = new HashMap<>();
			parameters.put("sync_mode", "false");
			PayoutBatch batch = payout.create(apiContext, parameters);
			//payoutResult.setPayoutBatchId(batch.getBatchHeader().getPayoutBatchId());
			
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
							}else if(pay.getItems().get(0).getError().getName().equals(RECEIVER_UNCONFIRMED)){
								//let it pass as success!
								payoutResult.setPayoutError(pay.getItems().get(0).getError().getName());
							}else if(pay.getItems().get(0).getError().getName().equals(RECEIVER_ACCOUNT_LIMITATION)){
								//let it pass as success!
								payoutResult.setPayoutError(pay.getItems().get(0).getError().getName());
							}else{
								throw new Exception(pay.getItems().get(0).getError().getMessage());	
							}
						}else{						
					       throw new Exception(pay.getItems().get(0).getError().getMessage());
						}
					}
					//success completed
					payoutResult.setAmount(new Money( pay.getBatchHeader().getAmount().getValue(),pay.getBatchHeader().getAmount().getCurrency()));

					if(pay.getBatchHeader().getFees()!=null){
						payoutResult.setFee(new Money(pay.getBatchHeader().getFees().getValue(),pay.getBatchHeader().getFees().getCurrency())); 	
					}else{
						payoutResult.setFee(new Money("0.0","GBP"));	
					}
					
				}else if(pay.getBatchHeader().getBatchStatus().equals(PAYOUT_STATUS_DENIED)){					
					 logger.severe(pay.getItems().toString());
					 throw new Exception(pay.getItems().get(0).getError().getMessage());
				}
			
			try{
				Thread.currentThread().sleep(500);
			}catch(InterruptedException e){}
			}

//			Map<String, String> containerMap = new HashMap<String, String>();
//			containerMap.put("count", "10");
//			
//		    PaymentHistory paymentHistory=Payment.list(apiContext,containerMap);
//		    
//		    
//			System.out.println(paymentHistory.toJSON());
	}


	@Test
	public void payPalTest() throws Throwable {

		PaymentRepository paymentRepository = new PaymentRepository();
		Entity entity = paymentRepository.getRedeemingRequestsByUserGuid("ffff2675-a072-4b6b-ab66-cb599a29147d");
		// ****comes from server*********
		String key = KeyFactory.keyToString(entity.getKey());
		Entity user = paymentRepository.getRedeemingRequestsByKey(key);
		RedeemingRequests redeemingRequests = RedeemingRequests.valueOf(user);

		PayPalService payPalService = new PayPalService();
		InvoiceService invoiceService = new InvoiceService();

		MailService mailService = new MailService();
		InvoiceRepository invoiceRepository = new InvoiceRepository();
		try {

			PayoutResult payoutResult = payPalService.payout(redeemingRequests, "GBP");

			String invoiceNumber = Long.toString(invoiceRepository.createInvoiceNumber());
			paymentRepository.savePayPalPayment(redeemingRequests, "GBP", BigDecimal.ZERO, invoiceNumber,
					payoutResult.getPayoutBatchId(),null);

			PdfAttachment attachment = new PdfAttachment();
			attachment.readFromStream(invoiceService.createInvoice(payoutResult, 
					(String) user.getProperty("full_name"),
					(String) user.getProperty("full_address"),
					(String) user.getProperty("country_code"),
					(String) user.getProperty("paypal_account"),
					 invoiceNumber));

			// mailService.sendInvoice(Constants.toInvoiceMail,attachment);

		} catch (Exception ex) {
			logger.log(Level.SEVERE, "payment", ex);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			String sStackTrace = sw.toString();
			Email email = new Email();
			email.setSubject("Error alert!");
			email.setContent((Objects.toString(ex.getMessage(), "")) + "/n/n" + sStackTrace);
			// email.setFrom(Constants.fromMail);
			// email.setTo(Constants.toInvoiceMail);
			// mailService.sendMail(email);
		}

	}

	@Test
	public void paidUserExternalPaymentTest() throws Throwable {
		//create entities
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Entity entity = new Entity("paid_users_external"); 		
		   entity.setProperty("date", new Date());
		   entity.setProperty("country_code","US");
		   entity.setProperty("paid_currency","USD");
		   entity.setProperty("amount","22");
		   entity.setProperty("type", "PayPal");
		   entity.setProperty("eur_currency",21d);
		   entity.setProperty("email_address","mm@gmail.com");
		   entity.setProperty("paypal_account","ss@gmail.com");
		   entity.setProperty("redeeming_request_id","346461");
		   entity.setProperty("package_name","mlsafkj.dsfsd.com");
		   entity.setProperty("address","where are you from");
		   entity.setProperty("payment_reference_id","3456");		   	
		   ds.put(entity);
		
		   entity = new Entity("paid_users_external"); 	
		   entity.setProperty("date", new Date());
		   entity.setProperty("country_code","US");
		   entity.setProperty("paid_currency","USD");
		   entity.setProperty("amount","82");
		   entity.setProperty("type", "PayPal");
		   entity.setProperty("eur_currency",78.8d);
		   entity.setProperty("email_address","ss@gmail.com");
		   entity.setProperty("paypal_account","mm@gmail.com");
		   entity.setProperty("redeeming_request_id","34646");
		   entity.setProperty("package_name","mlsafkj.dsfsd.com");
		   entity.setProperty("address","where are you from");
		   entity.setProperty("payment_reference_id","3456");		   	
		   ds.put(entity);
		   
        PaymentController paymentController=new PaymentController(); 
        
        when(request.getParameter("type")).thenReturn("Amazon");
        when(request.getParameter("redeeming_request_id")).thenReturn("34646");
        when(request.getParameter("country_code")).thenReturn("US");
        when(request.getParameter("currency")).thenReturn("USD");
        when(request.getParameter("amount")).thenReturn("20.6"); 
        //when(request.getParameter("paypal_account")).thenReturn("mm@gmail.com"); 
        when(request.getParameter("email_address")).thenReturn("ss@gmail.com");         
        when(request.getParameter("package_name")).thenReturn("com.gametrix.app"); 
        when(request.getParameter("full_name")).thenReturn("Berlioz");

        
        //paymentController.payExternal(request,response);	
	}
	
	@Mock
	private HttpServletRequest request1;
	@Mock
	private HttpServletRequest request2;
	@Mock
	private HttpServletRequest request3;
	@Mock
	private HttpServletRequest request4;
	
	@Test
	public void distributeLockTest()throws Exception{
	   List<HttpServletRequest> requests=new LinkedList<>();
	   requests.add(request);
	   requests.add(request1);
	   requests.add(request2);
	   requests.add(request3);
	   requests.add(request4);
	   
	   PaymentController paymentController=new PaymentController();
	   
       Collection<Callable<Void>> tasks = new ArrayList<>();
       ExecutorService executor = Executors.newFixedThreadPool(10);
       
       for (int i = 0; i < 5; i++) {
           final int j=i;
    	   Callable<Void> runnable = () -> {   
    		   when(requests.get(j).getParameter("rid")).thenReturn("100"+j);
        	   //paymentController.payExternal(requests.get(j), response);                       	   
               return null;
           };
           tasks.add(runnable);
       }

       Collection<Future<Void>> futures = executor.invokeAll(tasks);
       for (Future<Void> future : futures) {
           // Throws an exception if an exception was thrown by the task.
           future.get(15, TimeUnit.SECONDS);
       }

	
	
	}
	
	
}
