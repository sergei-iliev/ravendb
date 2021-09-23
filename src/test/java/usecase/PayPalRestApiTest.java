package usecase;

import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
import com.luee.wally.admin.controller.PaymentOrderTransactionController;
import com.luee.wally.admin.repository.InvoiceRepository;
import com.luee.wally.admin.repository.PaymentRepository;
import com.luee.wally.api.ConnectionMgr;
import com.luee.wally.api.paypal.client.BalancesApi;
import com.luee.wally.api.paypal.client.ClientApi;
import com.luee.wally.api.paypal.client.TransactionsApi;
import com.luee.wally.api.paypal.client.model.BalanceListView;
import com.luee.wally.api.paypal.client.model.Token;
import com.luee.wally.api.paypal.client.model.TransactionView;
import com.luee.wally.api.service.InvoiceService;
import com.luee.wally.api.service.MailService;
import com.luee.wally.api.service.PayPalService;
import com.luee.wally.api.service.PaymentOrderTransactionsService;
import com.luee.wally.api.service.impex.ImportService;
import com.luee.wally.api.tangocard.client.config.TangoCardJSON;
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

import urn.ebay.api.PayPalAPI.GetBalanceResponseType;
import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;
import urn.ebay.api.PayPalAPI.TransactionSearchReq;
import urn.ebay.api.PayPalAPI.TransactionSearchRequestType;
import urn.ebay.api.PayPalAPI.TransactionSearchResponseType;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;


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
