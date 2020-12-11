package usecase;

import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
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
import com.luee.wally.command.Email;
import com.luee.wally.command.PdfAttachment;
import com.luee.wally.command.invoice.PayoutResult;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.utils.TestDatabase;
import com.luee.wally.utils.Utilities;


public class PayPalTest {
	private final static Logger logger = Logger.getLogger(PayPalTest.class.getName());

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

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

			PayoutResult payoutResult = payPalService.payout(redeemingRequests, "USD");

			String invoiceNumber = Long.toString(invoiceRepository.createInvoiceNumber());
			paymentRepository.savePayPalPayment(redeemingRequests, "USD", BigDecimal.ZERO, invoiceNumber,
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
