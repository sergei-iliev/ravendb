package usecase;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.luee.wally.admin.controller.ConfirmEmailController;
import com.luee.wally.admin.controller.ImportController;
import com.luee.wally.admin.repository.PaidUsersRepository;
import com.luee.wally.admin.repository.PaymentRepository;
import com.luee.wally.admin.repository.UserRevenueRepository;
import com.luee.wally.api.service.ConfirmEmailService;
import com.luee.wally.api.service.InvoiceService;
import com.luee.wally.api.service.impex.ImportService;
import com.luee.wally.csv.PaidUsers2018;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.json.ExchangeRateVO;
import com.luee.wally.utils.AESUtils;
import com.luee.wally.utils.TestDatabase;
import com.luee.wally.utils.Utilities;



public class UserRevenueTest {

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
	public void getExchangeRatesTest() throws Exception {
		ImportService importService=new ImportService();
		
		PaidUsers2018 user=new PaidUsers2018();
		user.setCurrencyCode("EUR");
		user.setDate("8/13/2019");
		
		ExchangeRateVO exchangeRateVO= importService.getExchangeRates(user.getFormatedDate("YYYY-MM-dd"), "EUR");
		System.out.println(exchangeRateVO.getRates());
		
	}


	@Test
	public void import2019Test() throws Exception {
	
		
		ImportController importController=new ImportController();		
		importController.importUserRevenue2019(null,null);
		
	}
	
	@Test
	public void createInvoice() throws Exception {
		InvoiceService invoiceService=new InvoiceService();
		Entity redeemingRequest=new Entity("affs");
		redeemingRequest.setProperty("date", new Date());
		Thread.currentThread().sleep(1000);
		redeemingRequest.setProperty("creation_date", new Date());
		redeemingRequest.setProperty("full_name","Sergey Iliev");
		
		redeemingRequest.setProperty("full_address","Baba Tonka 7");
		redeemingRequest.setProperty("country_code","5900");
		redeemingRequest.setProperty("email","1@1.com");
		redeemingRequest.setProperty("user_guid","01923456789");
		redeemingRequest.setProperty("type","PayPal");
		
		PaidUsers2018 paidUsers2018=new PaidUsers2018();
		paidUsers2018.setCurrencyCode("EUR");
		paidUsers2018.setPayedAmount("23.89");
		paidUsers2018.setDate("8/13/2019");
		
		paidUsers2018.setUserCurrencyCode("USD");
		paidUsers2018.setUserPayedAmount("25.00");
		
		InputStream in=invoiceService.createInvoice(redeemingRequest, paidUsers2018, "000001111");

		
		File targetFile = new File("D:\\demo.pdf");
		 
	    FileUtils.copyInputStreamToFile(in, targetFile);
	}
	
	@Test
	public void createCSVFile() throws Exception {
		ImportService invoiceService=new ImportService();
		Entity redeemingRequest=new Entity("affs");
		redeemingRequest.setProperty("date", new Date());
		Thread.currentThread().sleep(1000);
		redeemingRequest.setProperty("creation_date", new Date());
		redeemingRequest.setProperty("full_name","Sergey Iliev");
		
		redeemingRequest.setProperty("full_address","Baba Tonka 7");
		redeemingRequest.setProperty("country_code","5900");
		redeemingRequest.setProperty("email","1@1.com");
		redeemingRequest.setProperty("user_guid","01923456789");
		redeemingRequest.setProperty("type","PayPal");
		
		PaidUsers2018 paidUsers2018=new PaidUsers2018();
		paidUsers2018.setCurrencyCode("EUR");
		paidUsers2018.setPayedAmount("23.89");
		paidUsers2018.setDate("8/13/2019");
		
		paidUsers2018.setUserCurrencyCode("USD");
		paidUsers2018.setUserPayedAmount("25.00");
		paidUsers2018.setInvoiceNumber("000222");
		
		Collection<Pair<PaidUsers2018, Entity>> entities=new ArrayList<Pair<PaidUsers2018,Entity>>();
		entities.add(new ImmutablePair<>(paidUsers2018, redeemingRequest)); 
	  	try(Writer writer=new StringWriter();FileWriter fw = new FileWriter("D://file.csv")){
			invoiceService.createCSVFile(writer, entities);
			fw.write(writer.toString());    
			
	  	}

	}
	
	@Test
	public void findMultiUserTest() throws Exception {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		
		PaidUsers2018 user=new PaidUsers2018();
		user.setDate("8/8/2019");
		
		Entity redeemingRequest=new Entity("redeeming_requests_new");
		redeemingRequest.setProperty("id", 1);
		redeemingRequest.setProperty("date", new Date());		
		redeemingRequest.setProperty("creation_date", new Date());
		redeemingRequest.setProperty("full_name","Sergey Iliev");
		
		redeemingRequest.setProperty("full_address","Baba Tonka 7");
		redeemingRequest.setProperty("country_code","5900");
		redeemingRequest.setProperty("email","1@1.com");
		redeemingRequest.setProperty("user_guid","01923456789");
		redeemingRequest.setProperty("type","PayPal");
		ds.put(redeemingRequest);
		
		Calendar cal = Calendar.getInstance();
	    cal.clear();

	    cal.set(Calendar.YEAR, 2019);
	    cal.set(Calendar.MONTH, 10);
	    cal.set(Calendar.DATE, 3);
	    Date utilDate = cal.getTime();

	   
	    redeemingRequest=new Entity("redeeming_requests_new");	    
	    redeemingRequest.setProperty("id", 2);
		redeemingRequest.setProperty("date", utilDate);		
		redeemingRequest.setProperty("creation_date", utilDate);
		redeemingRequest.setProperty("full_name","Sergey Iliev");
		
		redeemingRequest.setProperty("full_address","Baba Tonka 7");
		redeemingRequest.setProperty("country_code","5900");
		redeemingRequest.setProperty("email","1@1.com");
		redeemingRequest.setProperty("user_guid","01923456789");
		redeemingRequest.setProperty("type","PayPal");
		ds.put(redeemingRequest);
	    
		
		
	    cal.clear();

	    cal.set(Calendar.YEAR, 2018);
	    cal.set(Calendar.MONTH, 10);
	    cal.set(Calendar.DATE, 20);

	    utilDate = cal.getTime();
	    
	    
	    redeemingRequest=new Entity("redeeming_requests_new");	    
	    redeemingRequest.setProperty("id", 3);
		redeemingRequest.setProperty("date", utilDate);		
		redeemingRequest.setProperty("creation_date", utilDate);
		redeemingRequest.setProperty("full_name","Sergey Iliev");
		
		redeemingRequest.setProperty("full_address","Baba Tonka 7");
		redeemingRequest.setProperty("country_code","5900");
		redeemingRequest.setProperty("email","1@1.com");
		redeemingRequest.setProperty("user_guid","01923456789");
		redeemingRequest.setProperty("type","PayPal");
		ds.put(redeemingRequest);
	    
		
		ImportService importService=new ImportService();
		Entity entity = importService.getRedeemingRequestFromGuid("01923456789",user.toDate());
		
		
		
		System.out.println(entity);
		
	}
	
	@Test
	public void removeUserReasonReferenceTest() throws Exception {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Entity reason = new Entity("user_payments_removal_reasons"); 
		reason.setProperty("reason", "Unspecified user request");
		ds.put(reason);
		
		
		
	    Entity redeemingRequest=new Entity("redeeming_requests_new");	    
	    redeemingRequest.setProperty("id", 3);
		redeemingRequest.setProperty("full_name","Sergey Iliev");
		redeemingRequest.setProperty("reason",reason.getKey());
		redeemingRequest.setProperty("full_address","Baba Tonka 7");
		redeemingRequest.setProperty("country_code","5900");
		redeemingRequest.setProperty("email","1@1.com");
		redeemingRequest.setProperty("user_guid","01923456789");
		redeemingRequest.setProperty("type","PayPal");
		ds.put(redeemingRequest);

		
		Query q = new Query("redeeming_requests_new");
	    Query.FilterPredicate projectFilter =
	            new Query.FilterPredicate("reason",
	                    Query.FilterOperator.EQUAL,
	                    reason.getKey()
	                    );
	    q.setFilter(projectFilter);
		System.out.println(q);
		PreparedQuery pq = ds.prepare(q);
		List<Entity> list= pq.asList(FetchOptions.Builder.withDefaults());
		System.out.println(list);
		
	
	}
	
	@Test
	public void encryptStringTest() throws Exception {
		TestDatabase.INSTANCE.generateDB();
		ConfirmEmailService service=new ConfirmEmailService();
		Collection<RedeemingRequests> entities=service.confirmEmail("sergei.iliev-facilitator@gmail2.com");
		
		
		System.out.println(entities);
		
	}
	
	@Test
	public void compareDatesTest() throws Exception {
        UserRevenueRepository userRevenueRepository=new UserRevenueRepository();
		List<Entity> list=new ArrayList<>();
		Entity e=new Entity("affs");
		ZonedDateTime now=ZonedDateTime.now();
		ZonedDateTime yesterday=now.minusDays(10);
		e.setProperty("date",Date.from(yesterday.toInstant()));
		e.setProperty("id","1");
		list.add(e);
		
		
		 e=new Entity("affs");
		e.setProperty("date",new Date());
		e.setProperty("id","2");
		list.add(e);
		
		 e=new Entity("affs");
		now=ZonedDateTime.now();
		yesterday=now.minusDays(30);
		e.setProperty("date",Date.from(yesterday.toInstant()));
		e.setProperty("id","3");
		list.add(e);
		
		Comparator<Entity> comparator = userRevenueRepository.createDateComparator("date");
		Entity ent= list.stream().max(comparator).orElse(null);
		System.out.println(ent);
		
	}	
}
