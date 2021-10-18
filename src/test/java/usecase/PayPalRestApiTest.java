package usecase;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.luee.wally.admin.controller.PaymentOrderTransactionController;
import com.luee.wally.api.paypal.client.BalancesApi;
import com.luee.wally.api.paypal.client.TransactionsApi;
import com.luee.wally.api.paypal.client.model.BalanceListView;
import com.luee.wally.api.paypal.client.model.Token;
import com.luee.wally.api.paypal.client.model.TransactionView;
import com.luee.wally.api.service.PaymentOrderTransactionsService;
import com.luee.wally.utils.TestDatabase;
import com.luee.wally.utils.Utilities;


public class PayPalRestApiTest {
	private final static Logger logger = Logger.getLogger(PayPalRestApiTest.class.getName());

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
	
	
	
	
	@Test
	public void getPayPalAuthenticateRestAPITest() throws Exception {
	   String url="https://api-m.sandbox.paypal.com/v1/oauth2/token";
	   String paypalClientId="AafeAthS3PRG_dkpJPVTkCgVc-O9pQ6o2PldSIOceWsn7nIm0H404DHDFg4svXJa63Pe8OtM55ySzppG";
	   String paypalClientSecret="EC1ludnxStRx1M1VP9DDfz2xdTxMA8xcthmP_hRaQgWS09thWY2ihInfz5AA03DMxLjYZnR6sRQn-0XV";

	   TransactionsApi ordersApi=new TransactionsApi(paypalClientId, paypalClientSecret,true); 	   
	   System.out.println(ordersApi.authenticate().getAccessToken());	   	  
	}
	
	@Test
	public void getPayPalBalancesRestAPITest() throws Exception {	   
	   String paypalClientId="AafeAthS3PRG_dkpJPVTkCgVc-O9pQ6o2PldSIOceWsn7nIm0H404DHDFg4svXJa63Pe8OtM55ySzppG";
	   String paypalClientSecret="EC1ludnxStRx1M1VP9DDfz2xdTxMA8xcthmP_hRaQgWS09thWY2ihInfz5AA03DMxLjYZnR6sRQn-0XV";

	   BalancesApi balancesApi=new BalancesApi(paypalClientId, paypalClientSecret,true); 	   
	   Token token=balancesApi.authenticate();
	   System.out.println(token.getAccessToken());
	   
	   BalanceListView balanceListView= balancesApi.getCurrentBalances(token.getAccessToken());
	   balanceListView.getBalances().forEach(b->{
		   System.out.println(b.getTotalBalance().getValue()+"::"+b.getTotalBalance().getCurrencyCode());
	   });
	   
//	   url="https://api-m.sandbox.paypal.com/v1/reporting/balances";
//  	   Map<String,String> requestHeader=new HashMap<>();
//  	   requestHeader.put("User-Agent", Constants.AGENT_NAME);
//  	   requestHeader.put("Content-Type", "application/json");
//  	   requestHeader.put("Authorization", "Bearer "+token.getAccessToken());
//
//
//  	   
//  	   String response=ConnectionMgr.INSTANCE.getJSON(url, requestHeader);
//  	   System.out.println(response);
  	   
	}
	@Test
	public void getPayPalBalancesByDateRestAPITest() throws Exception {	
		   String paypalClientId="AafeAthS3PRG_dkpJPVTkCgVc-O9pQ6o2PldSIOceWsn7nIm0H404DHDFg4svXJa63Pe8OtM55ySzppG";
		   String paypalClientSecret="EC1ludnxStRx1M1VP9DDfz2xdTxMA8xcthmP_hRaQgWS09thWY2ihInfz5AA03DMxLjYZnR6sRQn-0XV";
		   
		   BalancesApi balancesApi=new BalancesApi(paypalClientId, paypalClientSecret,true); 	   
		   Token token=balancesApi.authenticate();
		   
		   ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneOffset.UTC);		   
		   System.out.println("Print current time in GMT: "+zonedDateTime);
		   
		   ZonedDateTime zonedDateTime1 = ZonedDateTime.now(ZoneId.of("CET"));		   
		   System.out.println("Print current time in CET: "+zonedDateTime1);

		   
	       DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
	       ZonedDateTime yesterday=zonedDateTime.minusDays(1);
	       System.out.println(isoFormatter.format(yesterday));
	       
	       balancesApi.getBalancesByDate(token.getAccessToken(),yesterday);

	}

	@Test
	public void validatePayPalToLocalSystemOrdersAmountTest() throws Exception {	
		Map<String,BigDecimal> payPalMap=new HashMap<>();
		payPalMap.put("USD",new BigDecimal(21.0));
		payPalMap.put("EUR",new BigDecimal(10.4));
		
		Map<String,BigDecimal> localMap=new HashMap<>();
		localMap.put("USD",new BigDecimal(22.0));
		//payPalMap.put("EUR",new BigDecimal(10.4));
		
		PaymentOrderTransactionsService service=new PaymentOrderTransactionsService();
		List<String> list=service.validatePayPalToLocalSystemOrdersAmount(payPalMap, localMap);
		
		System.out.println(list);
		
		
	}
	@Test
	public void getPayPalTransactionsByDateRestAPITest() throws Exception {	
		   String paypalClientId="AafeAthS3PRG_dkpJPVTkCgVc-O9pQ6o2PldSIOceWsn7nIm0H404DHDFg4svXJa63Pe8OtM55ySzppG";
		   String paypalClientSecret="EC1ludnxStRx1M1VP9DDfz2xdTxMA8xcthmP_hRaQgWS09thWY2ihInfz5AA03DMxLjYZnR6sRQn-0XV";
		   
		   TransactionsApi transactionsApi=new TransactionsApi(paypalClientId, paypalClientSecret,true); 	   
		   Token token=transactionsApi.authenticate();
		   
		   ZonedDateTime start = ZonedDateTime.now(ZoneOffset.UTC);		   
		   start=start.minusDays(20);
		   
		   ZonedDateTime end = ZonedDateTime.now(ZoneOffset.UTC);

	       
		   TransactionView transactionView=transactionsApi.getTransactionsByDate(token.getAccessToken(), start, end,10,1);
           System.out.println(transactionView.getTotalItems()+"::"+transactionView.getTotalPages());
           transactionView.getTransactionDetails().forEach(t->{
        	   System.out.println(t.getTransactionInfo().getTransactionAmount().getValue()+"::"+t.getTransactionInfo().getTransactionAmount().getCurrencyCode());
           });
           
           for(int i=2;i<=transactionView.getTotalPages();i++){
        	   
        	   transactionView=transactionsApi.getTransactionsByDate(token.getAccessToken(), start, end,10,i);
        	   System.out.println(transactionView.getPage()+"::"+transactionView.getTotalPages());
        	   transactionView.getTransactionDetails().forEach(t->{
            	   System.out.println(t.getTransactionInfo().getTransactionAmount().getValue()+"::"+t.getTransactionInfo().getTransactionAmount().getCurrencyCode());
               });

           }
	}
	
	@Test
	public void getPayPalTransactionsServiceRestAPITest() throws Exception {	
          PaymentOrderTransactionController paymentOrderTransactionController=new PaymentOrderTransactionController();
          paymentOrderTransactionController.processPayPalOrderTransactions();
	}
}
