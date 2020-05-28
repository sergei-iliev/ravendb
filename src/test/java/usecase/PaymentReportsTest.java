package usecase;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.luee.wally.admin.repository.EmailTemplateRepository;
import com.luee.wally.admin.repository.GiftCardRepository;
import com.luee.wally.admin.repository.PaidUsersRepository;
import com.luee.wally.api.service.EmailTemplateService;
import com.luee.wally.api.service.PaidUsersService;
import com.luee.wally.api.service.PaymentReportsService;
import com.luee.wally.api.service.PaymentService;
import com.luee.wally.command.PaidUserSearchForm;
import com.luee.wally.entity.PaidUserExternal;
import com.luee.wally.utils.TestDatabase;

public class PaymentReportsTest {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	@Before
	public void initialize() {
		helper.setUp();

	}

	@After
	public void release() throws Exception {

		helper.tearDown();
	}

	@Test
	public void getPayedUserYesterdayReportTest(){
		TestDatabase.INSTANCE.generateDB();
        //PaymentReportsService paymentReportsService=new PaymentReportsService();
        //System.out.println(paymentReportsService.getPaymentReportForYesterday().getTotalAmountByCountryCodeMap());
		GiftCardRepository giftCardRepository=new GiftCardRepository();
		Entity entity=giftCardRepository.getGiftCardCountryCodeExternalMapping("US","EUR");
		System.out.println(entity);
		
	}

	@Test
	public void getPayedUsersByEmailOrPayPalTest(){
		TestDatabase.INSTANCE.generateDB();
        PaidUsersRepository paidUsersRepository=new PaidUsersRepository();
        System.out.println(paidUsersRepository.findPaidUsersByEmail("mikelo@yahoo.com","mikelo@yahoo.com"));
	}	
	
	@Test
	public void searchPayedUserTest(){				
		TestDatabase.INSTANCE.generateDB();
		ZonedDateTime now=ZonedDateTime.now();
		ZonedDateTime yesterday=now.minusDays(10);
		   
		PaidUserSearchForm form=new PaidUserSearchForm();
		form.getTypes().clear();
		form.getCountryCodes().clear();
		form.getPackageNames().clear();
		
		form.setStartDate(Date.from(yesterday.toInstant()));
		
		form.setAmountFrom("0.8");
		form.setAmountTo("10");
		form.getTypes().add("PayPal");
		
		//form.getPackageNames().add("com.moregames.makemoney1");
		//form.getPackageNames().add("com.moregames.makemoney2");
		
		//form.getCountryCodes().add("DE");
		//form.getCountryCodes().add("US");
		
        PaidUsersService paidUsersService=new PaidUsersService();
        System.out.println(paidUsersService.search(form).size());
	}
	public void createExternalPaidUser(PaidUserExternal paidUserExternal, BigDecimal eurAmount, String invoiceNumber,
			String payoutBatchId,String payoutError) {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Entity entity = new Entity("paid_users_external");
		entity.setProperty("date", new Date());
		entity.setProperty("country_code", paidUserExternal.getCountryCode());
		entity.setProperty("paid_currency", paidUserExternal.getPaidCurrency());
		entity.setProperty("amount", paidUserExternal.getAmount());
		entity.setProperty("type", paidUserExternal.getType());
		entity.setProperty("eur_currency", eurAmount.doubleValue());
		entity.setProperty("email_address", paidUserExternal.getEmail());
		entity.setProperty("paypal_account", paidUserExternal.getPaypalAccount());
		entity.setProperty("redeeming_request_id", paidUserExternal.getRedeemingRequestId());
		entity.setProperty("package_name", paidUserExternal.getPackageName());
		entity.setProperty("address", paidUserExternal.getAddress());
		entity.setProperty("payment_reference_id", payoutBatchId);
		entity.setProperty("invoice_number", invoiceNumber);
		entity.setProperty("paypal_error_reponse",payoutError);
		entity.setProperty("full_name",paidUserExternal.getFullName());
		ds.put(entity);
	}
	
	
	@Test
	public void processExternalPaymentSentEmailJobTest() throws Exception{				
		TestDatabase.INSTANCE.generateDB();
	    
		PaidUserExternal paidUserExternal=new PaidUserExternal();
		paidUserExternal.setEmail("sergei.iliev@gmail.com");
		paidUserExternal.setFullName("Gerion");
		paidUserExternal.setPaypalAccount("mmm@yahoo.com");
		this.createExternalPaidUser(paidUserExternal,new  BigDecimal(3), "000234","payment907897", null);
	    
		
		PaymentService paymentService=new PaymentService();		
        paymentService.sendExternalUserEmail(null,"mmm@yahoo.com");
        
		EmailTemplateRepository emailTemplateRepository=new EmailTemplateRepository();
		Key key=emailTemplateRepository.createExternalPaymentSentEmail("sergei.iliev@gmail.com");
		
		EmailTemplateService emailTemplateService=new EmailTemplateService();
		emailTemplateService.processExternalPaymentSentEmailJob(KeyFactory.keyToString(key));
				
	}
	
	
	
}
