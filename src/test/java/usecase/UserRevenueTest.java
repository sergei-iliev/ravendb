package usecase;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.luee.wally.admin.controller.ConfirmEmailController;
import com.luee.wally.admin.controller.FBAffsSearchController;
import com.luee.wally.admin.controller.ImportController;
import com.luee.wally.admin.repository.FBUserRevenueRepository;
import com.luee.wally.admin.repository.PaidUsersRepository;
import com.luee.wally.admin.repository.PaymentRepository;
import com.luee.wally.admin.repository.UserRevenueRepository;
import com.luee.wally.api.ConnectionMgr;
import com.luee.wally.api.service.ConfirmEmailService;
import com.luee.wally.api.service.FBAffsSearchService;
import com.luee.wally.api.service.InvoiceService;
import com.luee.wally.api.service.impex.ExportService;
import com.luee.wally.api.service.impex.FBUserRevenueService;
import com.luee.wally.api.service.impex.ImportService;
import com.luee.wally.constants.Constants;
import com.luee.wally.constants.FBAirConstants;
import com.luee.wally.csv.PaidUsers2018;
import com.luee.wally.entity.PaidUser;
import com.luee.wally.entity.PaidUserExternal;
import com.luee.wally.entity.Payable;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.json.ExchangeRateVO;
import com.luee.wally.json.JSONUtils;
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
		user.setCurrencyCode("GBP");
		user.setDate("9/31/2021");
		
		ExchangeRateVO exchangeRateVO= importService.getExchangeRates(user.getFormatedDate("YYYY-MM-dd"), "GBP","USD");
		System.out.println(exchangeRateVO.getRates());
		
	}

	@Test
	public void getExchangeRatesAdRevTest() throws Exception {
		 PaymentRepository paymentRepository=new PaymentRepository();
		 BigDecimal usdAmount=paymentRepository.convert(20, "GBP", "USD");
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
	public void createExportSummery() throws Exception {
		ExportService exportService=new ExportService();
	    
		Calendar cal = Calendar.getInstance();	    
	    cal.set(Calendar.YEAR, 2022);
	    cal.set(Calendar.MONTH, 1);
	    cal.set(Calendar.DAY_OF_MONTH, 1);
	    
	    Date startDate = cal.getTime();
		Date endDate=new Date();
		
		Collection<Payable> list=new ArrayList<>();
		PaidUserExternal user =new PaidUserExternal();
		user.setAmount("23");
		user.setPaidCurrency("USD");
		user.setType("PayPal");
		list.add(user);
		
		user =new PaidUserExternal();
		user.setAmount("3.2");
		user.setType("PayPal");
		user.setPaidCurrency("CAD");
		list.add(user);
		
		user =new PaidUserExternal();
		user.setAmount("3.2");
		user.setType("PayPal");
		user.setPaidCurrency("CAD");
		list.add(user);
		
		user =new PaidUserExternal();
		user.setAmount("3.21");
		user.setType("Amazon");
		user.setPaidCurrency("CAD");
		list.add(user);
		
		user =new PaidUserExternal();
		user.setAmount("13.21");
		user.setType("Amazon");
		user.setPaidCurrency("GBP");
		list.add(user);
		
		user =new PaidUserExternal();
		user.setAmount("6.07");
		user.setType("Amazon");
		user.setPaidCurrency("GBP");
		list.add(user);
		exportService.createPDFExportSummary("internal",startDate, endDate,"0011111","PlaySpot rewards","0","100",list);
				
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
	
	@Test
	public void fbUserRevenueReadCSVTest() throws Exception {
		//demo affs users
		Entity redeemingRequest=new Entity("affs");
		redeemingRequest.setProperty("gaid", "92c656f9-a94d-4845-8b54-ee7d2932791f");
		redeemingRequest.setProperty("date", new Date());
		
		FBUserRevenueRepository repository=new FBUserRevenueRepository();
		repository.save(redeemingRequest);
		
		FBUserRevenueService fbUserRevenueService=new FBUserRevenueService();
		String date = fbUserRevenueService.getYesterdayDate();
		fbUserRevenueService.processFBUserRevenueAggregated("2020-11-14");
	}
	
	private Collection<Key> createAffsEntities(){
		
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			
			Entity token = new Entity("facebook_package_token");
			token.setProperty("package_name","mega.compot.io");
			token.setProperty("facebook_app_id","mega_fb_id");
			token.setProperty("token","456456456");
			ds.put(token);
			
			token = new Entity("facebook_package_token");
			token.setProperty("package_name","mario.coffee.io");
			token.setProperty("facebook_app_id","mega_fb_id");
			token.setProperty("token","456456456");
			ds.put(token);
			
			token = new Entity("facebook_package_token");
			token.setProperty("package_name","tripod.coffee.io");
			token.setProperty("facebook_app_id","mega_fb_id");
			token.setProperty("token","456456456");
			ds.put(token);			
			
			token = new Entity("facebook_package_token");
			token.setProperty("package_name","geronimo.coffee.io");
			token.setProperty("facebook_app_id","mega_fb_id");
			token.setProperty("token","456456456");
			ds.put(token);	
			
			token = new Entity("facebook_package_token");
			token.setProperty("package_name","miko.coffee.io");
			token.setProperty("facebook_app_id","mega_fb_id");
			token.setProperty("token","456456456");
			ds.put(token);	
			
			token = new Entity("facebook_package_token");
			token.setProperty("package_name","merri.coffee.io");
			token.setProperty("facebook_app_id","mega_fb_id");
			token.setProperty("token","456456456");
			
			ds.put(token);				
			for (int i = 0; i < 5; i++) {

				Entity affs = new Entity("affs");
				affs.setProperty("total_ad_rev", 1.4d);
				affs.setProperty("date", new Date());
				affs.setProperty("country_code", "US");
				affs.setProperty("experiment", "preview_images");
				affs.setProperty("package_name", "com.moregames.makemoney");				
				ds.put(affs);
				
				if(i==1){
					Entity fbUserDailyRevenue = new Entity("user_daily_revenue_fb");				
					fbUserDailyRevenue.setProperty("aff_key",affs.getKey());										 
					fbUserDailyRevenue.setProperty("rev_check_date","2020-11-09");
					EmbeddedEntity ee=new EmbeddedEntity();
					ee.setProperty("mega.compot.io",Arrays.asList(s1));
					ee.setProperty("mario.coffee.io",Arrays.asList(s2));
					ee.setProperty("tripod.coffee.io",Arrays.asList(s3));
					ee.setProperty("geronimo.coffee.io",Arrays.asList(s4));	
					ee.setProperty("miko.coffee.io",Arrays.asList(s5));
					ee.setProperty("merri.coffee.io",Arrays.asList(s6));
					fbUserDailyRevenue.setProperty("app_cpm",ee);
					
					ds.put(fbUserDailyRevenue);
				}
				if(i==2){
					Entity fbUserDailyRevenue = new Entity("user_daily_revenue_fb");				
					fbUserDailyRevenue.setProperty("aff_key",affs.getKey());										 
					fbUserDailyRevenue.setProperty("rev_check_date","2020-11-09");
					EmbeddedEntity ee=new EmbeddedEntity();
					ee.setProperty("mega.compot.io",Arrays.asList(s7));
					ee.setProperty("mario.coffee.io",Arrays.asList(s8));
					ee.setProperty("tripod.coffee.io",Arrays.asList(s9));
					ee.setProperty("geronimo.coffee.io",Arrays.asList(s10));	
					ee.setProperty("miko.coffee.io",Arrays.asList(s11));
					ee.setProperty("merri.coffee.io",Arrays.asList(s12));
					fbUserDailyRevenue.setProperty("app_cpm",ee);
					
					ds.put(fbUserDailyRevenue);
					
				    fbUserDailyRevenue = new Entity("user_daily_revenue_fb");				
					fbUserDailyRevenue.setProperty("aff_key",affs.getKey());										 
					fbUserDailyRevenue.setProperty("rev_check_date","2020-11-09");
					ee=new EmbeddedEntity();
					ee.setProperty("mega.compot.io",Arrays.asList(s13));
					ee.setProperty("mario.coffee.io",Arrays.asList(s14));
					ee.setProperty("tripod.coffee.io",Arrays.asList(s15));
					ee.setProperty("geronimo.coffee.io",Arrays.asList(s16));	
					ee.setProperty("miko.coffee.io",Arrays.asList(s17));
					ee.setProperty("merri.coffee.io",Arrays.asList(s18));
					//ee.setProperty("2merri3.coffee.io",Arrays.asList(s19));
					fbUserDailyRevenue.setProperty("app_cpm",ee);
					
					ds.put(fbUserDailyRevenue);
					
					fbUserDailyRevenue = new Entity("user_daily_revenue_fb");				
					fbUserDailyRevenue.setProperty("aff_key",affs.getKey());										 
					fbUserDailyRevenue.setProperty("rev_check_date","2020-11-09");
					ee=new EmbeddedEntity();
					ee.setProperty("mega.compot.io",Arrays.asList(s20));
					ee.setProperty("mario.coffee.io",Arrays.asList(s21));
					fbUserDailyRevenue.setProperty("app_cpm",ee);
					
					ds.put(fbUserDailyRevenue);
				}				
			}

			Query query = new Query("affs");			
			PreparedQuery preparedQuery = ds.prepare(query);
			return preparedQuery.asQueryResultList(FetchOptions.Builder.withDefaults()).stream().map(e->e.getKey()).collect(Collectors.toSet());	
	}
	
	@Test
	public void fbUserRevenueByKeysTest() throws Exception {
//		Collection<Key> keys=this.createAffsEntities();
//		FBAffsSearchService service=new FBAffsSearchService();
//		Collection<String> acpms= service.getECPMs(keys);
//	    
//	    service.calculateFBUserRevenue(acpms);
	    
		
	
		String queryId="63445ae4-529f-4e0e-965d-0960f87e630b";
		System.out.println("Sending="+queryId);
		String url=String.format("https://graph.facebook.com/%s/aggregate_revenue",Constants.FB_AIR_APP_ID);		
		
		Map<String,Object> input=new HashMap<>();
		input.put("query_ids",Arrays.asList(queryId));
		input.put("access_token",Constants.FB_AIR_ACCESS_TOKEN);
		
	
		String content=JSONUtils.writeObject(input);		
		System.out.println(content);
		
		content=ConnectionMgr.INSTANCE.postJSON(url,content);
		System.out.println(content);
		
		Map<String, Object> amap = JSONUtils.readObject(content, Map.class);
		
		Map<String, String> map=(Map)amap.get("query_ids");
		
		switch(map.get(queryId)){
		   case FBAirConstants.too_few_ecpms:
			
		    System.out.println(map.get(queryId));
		   break;
		}
		
	}

	String[] s1={"ARF-2SEJbGmwruerdW9JItCDc3EyTsZfz_GFoy7RdfuLesK7jnL41UKB5gNIU4qKj_FhMFPEucIWJp7Pa5QSwMBerbu6QKvYMcsdZHQSiFaJ","ARGqL3V6kyoV3dt6qU9sQ921BbkMPr-Vc2CM8w2X7L84kMmBKW61t91USIgxSkH8VF46EZtM95ESq41VpVFH9fK83b5iq-6p3ZyW0Aill3w","AREUbTNFkmOgpG5QhD3rAObJvuxFUGNuqgPhyrnIjxLi1ldzzK6HNeaTHcf79v94Z8sNFo4279tuCQoPU7142NhL-MFtjddd_BgOvQ0","ARHsrdHWq-RTygdJpaFgIU3ycDyTawSZNBxkgttnwQjGqx6SLdUdQ5kiZ5wS3LzcIQ31FVYLAOsziW1RgwRYExdw7RD3uqQcdjqikzk1d53C8eS9wE_Sqw","ARE4NtTejAg1avifm5IR5uFo4OjCSMQ8jMXFJQUHHhiIGqxS_sgBNBXWPKtpaBR7XgdZsS-ENE-XnPEL8XLR6s_ySahUnNljkhMZAJgRASZdj2A","AREoI6rBZ5ZC7tKzHs7jAK8QOk1fbXktaPQYBOce9WKkcRpY7p9JRTWqL1gmfT3NfNkcaDl_FMK6VQy6J_AywLZNk-o_mmtSo8upuc2trNHcyn-hR-w","ARFXac2rQy7HxsJZYQd9jCQxQJssmd1gXQAv9BOVja1nCSmV_MdarkfAmDjfADf96ZRua1ns8M4IgRuCBxYjgHHeHAby6RCy8ux9SchugEMJ5w-Fg7P7hw","ARGht-xbOd3PuJ_JwLBXyGk4DY3YdLX4wVPzE_bmqUSCEnUa57BaNq8pYcrY9aHjiK3vy0l0eB0ax9i0o7L5MZx1UODHWE607NSyAprNRE7r0ikh414","ARG6NmCjK9Ul9_USt0cENh-GBPxv1fPbR59aQ6AT64oURRRz7ZDVeQbtbOWxwWh8G1R2p8fDcT9Sjehz0R2l6wKidGH76d9LbUuffol5hG5-qdPrSqydKQ","ARGitk656zbxBcZEcoN2fFS0jLf2SSCPVdSg0tBi_B_0Q8PYNooNdMO_hWrMGaSK771iYjqJUITtIB8duJJ-ep_VzXLNM0poYxZzuR7BLGZe4fuo0FSObg","ARFZBFRFztv1sXK5WWX-F_p0YO0c1f3Uba2OMV04UbgItXbQNhlrsHWfnAkV94LjT41DqyltSVrU6bqU-9g4ic5w75kSNhdO9MhTWAR-n1c","ARGtWVQekkZLC4ZdQL3xK68RIz5odHLE2sQD2mTyBUOq9epPgHwAHrhTUP0U8S4ZIlubZxxnz1dKnryd5wWCT_VTFhnChtLEMycoAXsx_RY","ARH7QMkZtwJO4IxiGxCa3XJPjLaOlmmpceYJnEfGCnwnWDkIybAiibwWcP__wufj031yC5OgDIJmUOeVtbVsLuaE4Ah3zQ-sdHTurjc","ARGB8GDORg_mZ0M-OB9YIqsuAZxyxV6oRPln4Ag94NmHthHr9xOZ4Yy8ZkMrpjuKYs7umePSRiWW0d7tKCvLqVSqz4NCcsuq-ANXYL5G3w","ARH1qvCdu4ZzuTMZG6FViT1OQqRMyF2IhwkvOD6YpNVus_kv8x5uyoXgzln94nuiDxIzLdEbQcFTqJ42ltj15zXBttm3XEfeCu4VTXpsYkXc","ARHSlvsvUA0wvqoeYceyhX4pzKl0ocB3UfUuH_PUedmfLMIZ-M6ThWPXDJ_WtiBUvDufhYu6VGVPG4L1gBa96J8MKKRyn5t9D6PAfhBOw1Z3","ARFNbIifwQBIIuUnq7PFcS63u5Q_4lommQ64cDunJZ4g1qx_2KgE7-6RkD-mY9Mo25uNKjajU59DbLoTsitQLYw2z77qGZzngjicR_zWZEY","ARHYX4rn9C27hCc3BZrL_GThhkcE6-G3SKMnPYtOceCQ1-jwJCNM4GbkacohUAKj_DCGkIWUsU_RFJPQLFeVFRVjatLHm8-_ipTM6IYd0-0","ARHaV5GU469JniJQ_22F6HL9LyEFy8hXtGwoUraS4j6FoTKbrxdAxuHSYKv1b1CDVuhC8LyDRyLpkWJ4BnJzmeDBvEYM00zL1D6qoRRtGHs","AREHEU8ulvC-lC4ai15zDiJYRIMyz8YWoWb6m9ozLfuV9_a2yet93f4zkFriVgmQd5cDtBEua4CbRJheLXJm4PpeME7la62kQxum4FAA_NQ","ARHp4uuxZis2lMHqK3wI93FOxJwnUQ0J_tCg8P4FuZ1dcp9H1oN-PTCescX_GWOj2HRV2qc8bBXMNsCV7gmXDl32WOJQJqaeaEk7Jg4-hJ8","ARHNbIfIlaz50INayvF3ut946Bbm9cf9vpwxas9E4J2K2hI7crLgV3P_5Wa2nQy3w5QuEJw3SO73uceNEuRwOkcFyZrzOjX7_u6MGnwerwM","ARGH66JUPoYSg9csugff1bsW0FBuXsJY5rmy5BHmvZIgCAzg6h7ciTGih72G9uSngMJ67oWlJGBriXJHh36uWxO3WgwmqalcDVyT6EZsajg","ARHAdZeagyLN8v4k0Z882LrO-FCs32uYgtgQ6oI2OM3GAdnNo5HKC08SZ2IsFMu83szjW4XqF375bKp3tUfaaePB3m9rFxin7JF0Duty2rE","ARHR5WCzf-TzW5D4YfI1LQ7oiSPi4by_q8i82aJ2xO33lZc2BZsUahn_r0-3nqOHyY1shPVewMNxMUPWpxqvFhZkZwnnh41NBGjimrnfzViZ","ARE7jnb3MVTp57323ldbZrkvhfBsK1QUTJp9DCvcdOd7MjGQBUtd67i73PDegHIDXIPfiQFrTlv9Um7F6YCJ2052P3YyiSzFMnhG3zs8kkLThrgttVY","ARHT89oW3BypDjwmGa5F-STP-EwZf255pD23MAFQn2RMYMoqR1vF0d6mEHDVO1xrAjzXK7KC-h9FCCPikBgZUqL7ougRWLUFnshvjJQSEg","ARHRY_iUZ66SJ3T37L2zUScLorZLZM6-onPn0DBNOloy7yB8v4YMmiyyTOA9GsviYPlFxgnR6KPNcfOckwD1Q_wdGt4-26jjpEsb0JcwzN0","ARHGsUxJDFdLLR_S90J99UknJDmbE1QKD3X8nsp5iCJoq-pH5mqtFBMV5Y19Px9ndhiri43IY4KLCPgJIu4osZY1lLTAaVS7CPp-0L11lDV0ugOHYLQojA","ARGS61Y5Xz-gpgK3yC3ION-XuInv81mC2PpF-Kd3F40pD_e__f3UwwGrMGMxOicTdVo0Cn7Zdlg6J5Ce6UxEaGko1zbh-vDyUOMcchN0QpoYcrkWut3oOg","ARFHiORroAG0D25Z30VcHpoCUQOFz8bTuJqSC0Zssyr6H2MZmJVrBkpvYeTfozxWt-Cxx0gg8KCltr9pMB97-oLFbUP3hUPg9VZaCg","AREgqRvpQ34G_MCaJgqx1dZi69KlxWeMOgZoXKDzixhS8G075S9Z8B6RqIW-tXpOhAOgXzaZHhgd2YdmY8tVL-mXcU9uWhEF_5e4s7x8S2s","ARHGlhQxBs8-QNoOMQI8oPM3n1h6YQPV53c31jacF1oJHyLrBBsmIUVJDucpPonr3w7QB_0oyL5-pBvmo6_qfjcUELE2Clk56x4w10MD9r_W","ARHwVopJSdrq1SvTt46ZToByLBNRoZNH-DhOFpuqPOgGP5czrJcmvb2Jf-XPqcCqwVsBwIs_RwoEzoMSSbE0EYlm2fQmb1f4d6lvOtZK4rtT","ARGAgXU6sS_JSRQejvXkc0rm28vDLyXRFmb5lgr2zKC78wKpLshaAJ-2uVixoWBnBerLzIxwRVsUkFVCZaH5H7RAn3XGmgVZy9YwGmahP01X","ARFW8a0kQcGQ2vRJB1C3YnAKpuM-fPXhy0Z_3PLMP6LjkfZZwR84DZGfUoPlyeTQ9E8QhuHg4fvGTAUKrnTmHoE6DjaYnCgLEii4z2zL_kY","ARF7ABQfnu1RDMU0IOHZU15pn7f8h319xJ-51Dhh3AxXnoPupa_KYQFUxOJ8AH7ZoxACCOIQICjgr3IUOdMrOrv7aClexRbYcVECRNUJiY17","ARHxYZZQwLPpDP7aSsFovISt1hKwjitX5buOIYE0eBpuJfV4q1flOMrYFBg0lLR6FCbNxDMrH42kVppci6kUmlnmPpIQ2H5dtFHu94s","ARFAFt02BdSancDP55wYvrs9xwO1Sg9EUwzT5c4ryYt89m6o0aPg0boDfq7ujFzo0OkbSE1KxoMpra6yvs0FaVtKEl1x9r89CJG3S7O_3cxVjgqfVOLsfg","ARGycf4wh9hviyNfENGXByJxhG1Lew8FHDiv16AxObJvrMvroDCSedVwzeRJI6POmpxiXsTuB1IPSMK5HGo0L8a32GJebZCc19dmz0xVGg","ARF61rmnXVAYEWApIe8cQ8UtAUUQNkFgdm-_vdadiHA_G0j5RTEyyVtZbkTnOuq8eXUAei7yoFVOkP1iO1r2iJxfkrdO5QRezOPurdTMPkgk","ARFsfhhWtKGo8svx4BEwPDfc9MW005mqHH1IyntEEmxZI4M8iYHHck_8rfbhRyIuXMZtpe1-QRiIbrT_sYM_V5iLBr02NGLCw_8dfrIR6L0T","ARGEeSZQNAB5VJnoNc89eV4sQVP8-dlL5DojZduYkSc-ZL6a8VVkvUoZoaOfJOnu7xt19Ckw2ugRPHLigAKmkar3OQouxf1eTsvgQSBXFunK","ARGkJczGidrOdpwggX7UQ_7HRfscfIhVnyBsj7m8N-zL6yV5YxIZbGThDYc1k_SL6OH2bFXF6_X9FOsll63Wfpu99SaOCFs5xx6k683inBBCmaOOwL_j_g","ARHh7ErhgGYTzTbEAr_zOweIy4g-V82mvjvWdvq2d2ZieMo_BzfhfDGor-wM-aQWbE4NjKupIS6gCkNuLzcsDe23Zc4SvJD4u7KUFOQjr8dZ","AREJALMkS5LXknssbvqzMr_XuD96aDVwbCCixl9L2G2ZYoSaWoBFXmjis0C3nKjm3ZN23_SglJqCluUtMu_SNTBhQjx4ESM_xsHy-6lnVI4","ARHXJBzj8kw72V6vxQ4llZ-X4i2cnb0BnurQ08hSWI90ATp0bLsXwK_6wZhIM26d4yWLdMqYZ-vDoriJAa3VPd3dSbFImkVYdy7Z18L5kKda","ARHrI8gTFf_SY5Snrb0V26WGJ-WENM-CgUNS1aVr07O2vaS9Qd0SB-p5UYCKi1yTouOhNlu_Y1yJQB3gOa-1LDHd42ykFJjI0zO5XDwgIgs","AREoC2qQ8aiOZkUOSBW0Yd9CEzVRhl5wXt3tN_twwK1jfMl8aBnqJZEUYR97Xmtg2f_6YgwIejC3xjAvTKsxLL4G5nDkn-8E6XCZDm2ln_vO","ARH79gmgau2wc54Rckz8CYA36ba6YrML5vqYIWKJ6xbYVqA0pUGj598puGlFNnbrk1GPzwd20gMzxYc-eZyB3A1WKJZlyxFiFUoDvWMKrSh6","AREZiX_nDhuNa2xpZiFXGLaXBCKaHXbTxrBZDpxsALovj1Wiez_6MCnaZ0AP6LsXnD7lBz_QP0EUMnEDWjcDJ1zEhP4kaZFqmLdALjz7ZmRF","ARE2qc3dUF_EQWbYj_XUvQdO4tZfYqxqRQsIMRKfPOJB0tSm3CHydObWPGvPytlfV_-SzqDiCWAjJI4KCP5ZVAQrIdMbJeVKuKt3bEdpSQ","ARH9VTgCkYKNin-C6gJeH1nV_CbG6jLVhMchXPJcFj8_Obow80dYrYs7rFVErUIpLOe-q9MgJ-OiGEQiZnua5-JAzHvjFG52YJm16l9v4VY","ARFWUwjITkGDCd598Im_6L_E1v4fEQJ_ACzWsBxjR8hFpClAWvT4zgkliBgzQl4EL7_jDWGnty5O7HeGpxaQiXXOVNeVMSW1_Al03oVuekbc-iBDqmC-Aw","ARHYX21OFTI5hW9RxowA9QjEuTc3qPz762I5nCAAkHPOoQmL6eJShYvSkGrVK36KQ1YeSTwAy7AhKd1bI7gHEucqVdkoSwJZjFYnlq6hhX68ssyRhVjlUg","ARGnCttigsbPyctcPesTQDdxzKEvq7WsTY5TzyG5vonZhf8fX_ZjS4KZ3c9a-aX6Z1p1GZ7JnqsU0OpBb1ZGqGLxK0yM9m6DHsS2Lab2XGrl4LywMW8n","ARFGsSc_gKwP0YseGyMU0nBXfY4L4wZ9vCt8OKgPHuOhPsEOSRhKOJDidxKbKjL0xBBJITEj3l1b__dyZSEXrVPhGbB5Ptnvbc17ZJqyAMz9AYGpE2dttA","ARE3IgEVAt12lcA7M4efFORSYUInYBBaZ_7ujepybVa9cDJX7c2j-nC4VkUzimXGUcC-XBdwtb4jIXWtmkiaLaJmxATtcWb9Qhkl1LBO_MDBhj21Yt4jiA","ARFUlU7yPt_gcdZltPI6jiIZRv5xkJRb-hiIl3svHvUDBslRPUHlFUgdjgRswagmETH2P_8JaATzkwzYnhPTqXQNNkfQoO7L7D6td5JEVoW6","ARFyD9ltP6YWl8gD9TtforTKXGHcJAe4aHx_sAcWE5sE0zmznyEqeQCSvQ0edCT18eNjpJuLHxVtxfAttIB8TGpRbRFhJzqscfLN1qmM46Q","ARGchwmY3H9Y8qH6AYbllPNbOmijK2s0oYlOQKpZQ1TJ_BMnpoms6n1nd0p_th3rqAAZdAiJrP6xTG3tYTZ6EpyYDpqBkJgK3JsVLkT1ECVM","ARGKaaUt0SdRrx4abBklBvoo6RRS4Y8kGSrUWxxlGIwIp48iyxseB7Z0-lKYD-SzmqGtLvkE8Jfm4LOGPofFi5-WalN6KDbZhvAF1KwYTDoO7OtdLWs","ARGCVwnbKWcU97-xZ01_VdDUBrkHha7B0Nakp2LCga8J-LU_svbtTKzsZe-_8MalfmEM8Uwp2CrPky-Kf3Nsfri28bM7Sfm9oqV3zxyBPJL_H9YWKlmBCw","AREjQn5a6HJEYLTHdDeiIheUQ-kXMUE5iUUxB-eNtEhTSlZn7TUyoqh_4FWIllnF3lBQbsOEMvzm402UFqP0BBxrMQoxxw8fZA6G1LavgFQ","ARHNC1XohnSDJQxixr0AHrmC6uYuBHJWm4nf4-thBKlH1_jrR8jGHrPNUBEuQYAy9M_jAyfV73hCL3XUMDopr3pz0j_mNIh-Jy0UiH7ibWgs","ARFiISM3cgDIBpsstK4-MMxyq0QG-2iYvAs070SOeW-aYWyxp_bAjDkbOYj5Xk0BB5vW9Z1OEUdUuq1a3IboJTvmFrc0tA9qOXcYXYS34w","ARG6GU5nnIX8191hYLiZQ3sEElfPI24lTys-zxkyrODvSgVNS9BXm9LQLbZWuxl4Ai2xMtK_skHSKAlwkSOLUvDkvk6lj8tYJ7uk3ega_f13spSqm9Kn","AREUtxwgl1RLxam8MG0Am8RyHkkVx-cZ8uskX-mrEOjnISbCZuAEUMRU9c_Bv0ZxBsm2mS6wsgmvU1qgfFRMp_QvG6FYZ8fTuRcnyWyjrSQ","ARHx5NKlOmOkZYH0cF9ATipoM1a3Ir27SI7ZUYV6A_5oO4S_kj8D3uGIA1w8jkC069HMZBWgVl7g-NUfC1YVd-6qBAe72JN9jISqfYC0tg","ARHAhxANGWb6M-O2hUCcgl0rbj1PquGfrwUoesom8zKLbnVymzTOw34oXHme-Lth0XL7ewcYQSVczveWytWd-0YddZHyJ4b_Hv9oAKbflgLO00s"};
	String[] s2={"AREAswnSdbW37f8WxjswtO4S-YGz59q2nDUVbgzQFO-8gf5-gSckKcjNZtZNbA9IQftDEIhEGac_ouTrzQU50O2_WhajsZA08hLm__hGNV60","ARFndcl0JabbrLAlScgahc8g0nLhv2phasiAVyeqP8hRibbVd9D7A659H4WedDyMV_yJczgNp-mqQbIw8NNtH7X_BgUQQZ1uy7brPhNIoLBY","ARGmg-446viFT0KPj3IaUEAe0qm30XhY8ihXrYW3Z4AwArzgl51q18VNof-O4NcbFkcjYMYu_xODxKUakPg3UTP_8xWaC-NcSvQQ0_VLAkKH","ARGCX_0beO5dwZKBKSKiXqIoczEVH6d7HZc1t8U8haLTd-z8_DHI6k-y0RC0ZolEkcRrc40ne4_FNz6PTE24Ak6riIkjr36uGMee44KCMyXXzw","ARHglBY8LVXY7kvch3E9c-afdU0aMVybfN9VUolpEdc5erINTuZuhPOH3TAwJd2p6ybVpVLKMDSSe_SZTklKguAknf6lsnAzY1-3tfZfAA","ARG8Rj2OVzfMRTgtdxB1iH4T1uq5FIRfFZD1JCMAYjvirDupYOgV3snY5EXAV9u0eB5qq9BNhwTlWDf5grQjajDiZ4v7BDk64r3I076i4_9noTIcrF3L","AREoYusZZu-VIoH-CaTw-qVdEM3W981xfrRHY6Nf6LkRMCEokHIzECLp7zSfavOWrs-6TwfIa6BqMkiFzu-Jp1taYBERFfmx8xCi1iR1JdIa8A","ARHs4jhO1-u1EVGoI0EnAuIptNY-AkGkLgZ0qtjR6k-0ncB-OnR-XwRJirks5x1MAp8Pkx9Vw3jlKgFWf23RJuK6yD-QBzOCZXLmj_6nlQ","ARFuTCslbW9C00-HY-E3m4GA2q3OVEAFmlROFiA3DugOXWrClBTFzQWt-VSsqH7brwPCRu1VLB-bLN2RW_SxS-2ATkT-f5EVSqGd0eSUXYydhdlFu_8FKA","ARHX4NW7Zs5IkIRYWddZjrrm9Vg4uHLQqnPUtBIqPQhtw5ey52RaNN9dmpBw54kg8_ZAoiDE4EQXnmMLh7m3-yvImaekaAoWWVEfrH3a4sE","ARFw9TkmIBufcCuzz1AvvFdF1PWNh7pVTqQwb4jh3Ngq2daVs3N0zZc9KNi7no5f7I7dQVRxqaRItcv96Q2NhY94KlAyA1DNafzvYxsggMU","ARGcwgJZMsuQv85Mbq08qmNcoZPkDgNK6HabiK1t0REm8detQQZhgyEhg6GDN5x9GOcZYGPlGGwhhjF1tDwrBMR1iExf-JdaymLxtrO2lw","AREXsWfbxm4dVuaG8_6HHf1JAwdJWv9rZT_wxrtQUFVtiu5ed2VkBJn4pcAVOMmsHlq0gDCCWjpllnsJH4Xj_zHuA3QEV-WhoYj6goTbsUde","AREgWLDDlldzKkKj2hVG34y6GAhYbo0i60IQRnEqWWVFl3UzrGKChEKxcMjbQb4kQSZ0nXfvKMuRcH3yyYieSSp0kic6iVvtn0vcVbaCBVp10AQAJsratg","ARGcuGoslZpkWMprb9hnY0g2EeBz-XA-Qee1NdLGi4kEQpmg_8yIuxNlEtywHZ45xI8DVpqcMoiC4L2sQrxxi26slU6B8kRx1x2zGnDfyZxBz6pJ91btzw","ARG27DFC54tsDqSA3GUgUDswZQUs_4O3xvQROF-xorrAECU1nz2sfyX8Jr_WxpT_hWLiuHPG8gbMqHmhAIoUzdOUVziOUN4t45gQCoHowA221oL3zgWLtw","AREN_9WZ7wSjbnx50bflvFt87V1zm3R2emIitmyAmIvpxweb0ldTJJtAFP_0BvqK3_Itm8XT4PY-U-dofJR41utBVJn4-fsxK_VkgFAzNNKI","ARG6Ee8jZAIn9ZHfcIbqypCfLuunREIm8cSI55lj6-YOeZH2SGpiyWA_QXp8l3u9zfxX4BsXdhOH5lEbMIFUUzn55NSU98uhVkL1UYOWH13n","ARE3enbdR-GV7z0en4T0KASmhv2zh_xdePDWMjDehl1KGa5MGufFgy2b04jXagdZbrNrG5P9DOCFprTQlD2SQ76mlZSFCQIVTjCIF003WiOs","ARFoOomQq7PEUsljBNZG24euoCHHNJmqq2RBv3pAL7aM96tTv78-M-xkXIR8CYl33yS3rmE5jbjeiY7ezIYLYLVA3a5ePCmCEo0FxQiNMNqW","AREnSjEPjdUJs974dDcuB63Ur3EzTGy5LUQ1Dj7ZrzamyXWofqliUBkJi5ceT_ILPoU0nw1jJnJLaoMX-_UYWG8sTDgV6giyAe3VZqeeUR4q","ARFEtkwLaDjztzKH4mMMnEgwIyNyVVBa3s2Wkag80yH4Q85dSo7eWLugHPu8tLhiRSOQd-Tu--TLN6051wNWL3pVt4L3Nhk0gTjUmfYk6bI0XB9Dn1o","ARHCc_46dMcPgLnfiX6VBmCtVAlVsFxHkkVuFfFhsFvPg9kXKWCf2JiwDg94R4dH1MoXlJtGonU2r5ZbC_IZVs3vtatND7dhjHfXsgl1pJA","ARHdLEEOs7wSyYSblocgbfWFot55aspKzHVD-I69Tb1uNxDjmetPbdUk3OQNpWILZTxC4L0qwIi_Mr57m4auDM24ye465a2UYzglm-EH6sgN","ARG8ApTfQs8jJPfG72dRaOo577YCBCeXnV4t5kpNr2RJp79yOKhtZoQBFjVWe26JBzSNw1ZT8noJ0F1g-5BUlj50FznCUQ1zOby1qR0V2XXinBbmHdkmVQ","ARGuCzQ10M9ZkcXuQaQv8L_-tIQhPqQahnHzf17Xt41CZk_e_aEkp0yHd7kd0nXsae1TB9rgTO-el728VcO_3jqIQ0oZF3nXycDg5MztmJmD","ARFfzIZ_laD-BZt1ggBtSmBXdlMlmjfF10gjGzakSNRf8zBH92_SoNRYJ4NLHswZyceeRUQMgbnDRA_HLiOcVAwTLrYYFKI1aG4eBsHZWsiDGntC2bCVZw","AREHWvCs9n0f42SP-yd0My0_v6sNSAE_ckGTY6YZcgG2NbvNKABsn4w1ODcmr5x165Ixr-Eph6-z4RiYBfiDRMx3LWOihqccn5dbmkuJhZPW","ARG-7XMTddO06Go5I0XMqXn7mzBIidilaZ_vq7paSzpyuZeU_m5aThmtv5_9NFmZJwe2APF4_48lgZvnN-yKlQEwG6mvBk0iNS1mAHDE0g","ARG8FE4xtD1V6S7gUJEPNJSpyTMgUKEIP__JzYB8E2VVC_V7fml3yQS_wP9IdM-K8utCWr_p7i8x4yZ82TUrRpQMbY6HqrbcenZYyElAWVRI","AREZR5y-2F9mk0I9pYhXQp5qbd-q-jBQl6YgiyjgcxPTv1JylV5z9zVKrxFwT-5JT5zOelAJ6QIC0HS8LCEZCt8rgyvFXKzXAC9ULTrFjmYk","ARHBhSUOLD2QzQdkgore7CVzLac_khqPcs1BIAfat5N36AI7dIOn4jwq6lPlbLwK0weAgJvDgE3iR_7UL4YtlCHh2z3Ao-LaShO6c_qJ1TH2","ARF9V5iivSk1HWmaO_2nXjGMeYXLfeGjUSlSwAg9TAXdlmpSS1Lg4NcWYhIoKXDLdT_HDQ8C6KWyVwzIdZEIytFwbWRVPvdiwN38fL4xuCc","ARE58Pao8eOsyiy6lPkC_KUlgh-9B8caOFCalEAK_TysbEgj8W3FaCmkbwDLSmh09ZOTasVwXg-7JCknJ4eMRF6r6u4hlIZrJBhOIg","ARHi170aN6LLxc3WLsXW3lYosfidm4TBocJ-AmWP7jX9lfP_TTaltCIw8Q669W819NoSa1dRjJZvBHvyX7ib-E7XQ_aRhlazecAl6Cpz8IAH","ARHE4Y7uYaTlTCI0Z6-zWuTDwsRDXmcYnHtW_KhcLEvzTiY6dXlcbMq7UhPFOm0LkL9jvx8OYheqqbQxLnOSSSLW4fQm58RpUy8ZqQxPTAI","ARFDeRAeVKaMToMv8Hr1YUcgCr7eEsvU17k7C3AxbzBkysuaKBCkhhOAdE0TYqULjsEqq4dbjYO7dlNANSbzhpA2c9fcRMTy3fqux6xT1Ho","ARFlEQ8qligLk2n0IlCzoyvhSMrDCybaY39bgtsfoC4v1MtE2qp73ZGfENnUIXMWfJA6jxFvpYokdj8iL4ONH9zmmbCxbhVRXzY2_vI43nYt","ARGWy4THnQ_dNfPGoH5NimEkj04_h3KdXXMFSLESmOsZKTQsaQVcUOhnxVCZjXwxGErWEY5k1oLzLh_Y-tEHOWheqtOBEc6GhxkXIgDadA","AREu-jGaCZfVJTL3idkoKtTCkth7gKLUbhpW55WbfmAIR_NAcfsNFBOvfYBv_m3U2zljahJHI7tV55hg1yAxknIcR_wGa1JsCTlIFEkKpoE","ARGbMQHufK9imCFAEI_R3qrVgG1x6gvsv4cHyYTLhrqQ084Sojf4JmB1WZsNvyUT6y5sJEE709M0rkcQTBznLXWoHbkDNUxU7UYyVs5XwNI","AREEDX8FWVfPxdU0Kc6J66qG4YS3JoQ42zcPGmLi33L52yzC0zrcsB4XGiSVG5Np4dyMUSaQnHOWj5asWD1Bgxde23B7LakuPyEFmrZAsQ","ARFO20f6vZYEebRfx5FluqFPANcTjj4maNpxVNv80PcW-4JjBZQoGb-uvm5iN32SfYB286NSH6ikToVgNm3aVDjgqoHDKtekljzRXyK1H1nR","ARH7Cg91z2Oc81Zfe_uxlcPwefYul7krnqF3Q3ihthFwy271Wq-Y0kALOKv5SJNPVgvleZPmocByQCBmCv1SjXtarhqMOZBFRlRYoyDDhbaz","ARFFsuro1bLQ17h8x447d2x6ydFFKlziSgI-yJjCQ90gQlugboCzXMb4DmjtKCt951WEz_d7DqMO09ftKoxIjw--9qfuwWFNEp6EtFnxkExM","ARHlsimP_P6RU2dVfpSIs9UflsE-MCMLG_5Br6MuMt0U2XwO0CRT8AH0wnCDoljDmKwcA4IcnJF4JCSmI3RDCmWOqnUErQeesD_sk6j0ac4","AREai-cfYUjNSHECHPoj72JO0DsQb-OZFeILrKc_IJFogaPLrvQFPCnU9HWyaoheYDVSqWjVsGxPrNL2a5A37pUhHSZY17OZB12Q6Q","AREV8NWs3olUBQL-W6_7xnWSyJQtr2TAcm1-DRFV9ij9Hg56x604URVxorm1wIvn_qbXQ_nQ3uAD6JHp1UhnoDnyCMimrliHOaWM91mX","ARF-t9LcYMQnPGi2Hla6v1G9obxQ-JVSAsvNPNh4SbatdlFKHOx7OZ9tAPsskJfFECTS0v_94CfVsUjkqJmk7gZfazBCyAQjPPLcFGvfpi-Kypsmsg","ARFA8mCx1GoFH0c5AIaIR9Nn3MHNhOobLnJ0iodv6K9ZR8c-wez0NoUhYISH-hdV1rgVBwr5jwuUNHUKJOruAfcxy6X0W17ihB1bNkSNWAqILTj9ApUdbQ","ARHNGM0eXTD11Q2BrZyzkoSdnbWq2D8ly1X8CXp0_4LBeex3foGTawsQyBKY6iHsp7EyYZsR7Yl77lGtXc081ndQ85OYj9iS8KTy5tMufEEpDw","AREfX8gx_PDQZuxAZLZYwQDeVqkaJ9Ti116gBwgPIH0lCHA2RLtmnG8hnA_prupKXnZnbOTaxkoFoJdpYCINQnNL03aKEg0kfoHFbg-52jHjmQ","ARHiZOydGCtI_WMjyqZdFrx8qs8VfgtyDjH9mloPISZmORwK0jQ_9IlL6wGc1eZesUuC8KsqkZShocTo-2R3AaFg1AzAzSBELYd21SL98YoIMw","ARHpk8hK6U55a7IX999NfoyRnjGPoVwhHyxN0dX6d5VERQlaCOQ762-BQY0VE8nm1dTLjIvR1-2t6yeTQqpyz9AYLC2O5GqFP7jtd2DF","ARFVQKpvVSVBDe3iZVAXxY1IdckQi4vG2SrdQiQHOpet98kYAV8OPWvULTkPkiasPzjVbS6SRXPDksV8BWN7BagayvrbREMSbcT2cTO-Ylc","ARE_WVP2eL7BXzvi0JB5w3bqz-BZQt8FJtuotmsG1T72nKWrDc852fC6-4hkJauu0DSgkKFQfwY9hntWyAK-B0vtsCQU-V4LPbghcLxTPywG","ARHDK-92G2fHQICcEIAj0uwDL7Fxa_WY1LsKdLyctbzcefhXB4RhEL_I3lUMz0lBNGVlDw_Hxh_fU84nZcOB4OINmAR7IVqQPjAFeL0NqsNbGaEJFNGbIQ","ARFFsiuomuCVpeEoCKq4lJlUcSE-WhXIqQXyxZSW_4s0IHQIUbL4ZaoyfzrqES7bZpszmgEr-X2qxd6htTNDwfzuLBDXrGq5QnaJrjIwU7jHoQ","ARH-3YCrCGZVj6wI1uI2mbRRSpcJlIbetcfqfRSVwWqjHU5ZQLmOsPVjH7ivX1J5gJgFE7kOFF0kUcEu5CzvF6-BN9Unu0Bn1MWaRWEnl10","ARFGQtRPEgSHOB8Fo77FZLjTlNhX70sN9x6SM7d_zaP4Z-FC3068njzwbQaA-qyCLYaDqg27ZXbzXgkd878oGdo2mUCUYBRIGSOhUTwfRdkF","ARHQXr4LcOcl_C1bqKChDgGgYETcHov9DPohObPG93bx3ADQYn3I7V4BawOhXNj8viDJ-iGkbKvr-D_aAN6fUN_PblTVJrvikv0PIs3Gp650","ARFDeG_Ztur9_KA7he9WbcRIKvAO2aptvoINt22ykpsrwbmmm-TmFO_tqaL4CA70D373okeCL8x2YEdc7t3gH-y8msrB0JfifQJXFoCTxJ__","ARGCPK4SxBAgz_q93o5XIU0SPoKhyAR5K__wgGE_PuryIl57BRFmPH3AGXC1ksBOYnjgvOXYUSDXHHehVPCYyKeway01cN3K1-Le_omYhWMg","ARE2phSA2R9-We8vu8RXqWBDgGpnN-JgI_QHgFCtoOLNtSrPIgnlm_RFlb-pUtddgOqaZ7MEPdfPwFS1fCmNbYYBXe_ySbQboCWwko9buA","AREF5xuMEeESWB0JCVisjRDT4b_iZEeJs01dLGgfgaPgOw-231HPeSUjBiOHdg56OiDgQbU6bMQnTGyPOuVHSj-CefrAxRzlR8ekBSvMa2cV","ARFmn5Wo93NvjF1AfBV0DRxAWLKpbeRAYeBTjJ5yR4rdBOGfr3fYh18bRgAfNUlrwQ2Ad0jdNn-8OZZLJHVy8wOK_FmQkFSHVayqDO8jR-8","ARE_SFslK-u8IYY23QOJZ3-PI7advhLucBT2mdRqk_UValWhrtlKcR_WpVAT0XzzrYZuEYwHOtYeVXbDZCCwdMbdc4Bz2Fo7hS9Hbt5EwjhY","ARGdFSSlUOkbmO1yr9lYWa4lPv-dbvEwY_0MNHCS6E2FhVqTOyqFinSPqQ7juqwHZdZ3qd5_xHjLQtDawkr2Q77u-GyeB3nQyT6RCsz7QXj4lcxcfSvdtQ","ARG8giSalqNx5OL7COD4Z7ZZF0805ILlaJ4e91Z-8a24W-XppDf2tNqFRTxHtV74RKNuKt3VLuWss1WLH-CjdTpgvLsoVizWrYSHgddIMwU","AREiR7UbtcffeFGf_VaRTCXu1ce6_eo7OGHIY07IyWRUHpDcxy18jofdoJ_YIsju00tXJizWqnocBgBMSl-5DksThPdwqhcC-d-yPcZjguA","ARED2AYnrFWap9spHt3tYPNmXTsDI1_QcCeFLLYYuriHF0vCZdT03GMKof2FBNkxEzimhMw6KNJQqapIAGD5WoIek4XNu2CXd7INpjlX_h4NyUerxV8D","ARF9dYsao4X0l0HMFjH1wv9y3kxVQTVPPvyTUSsRPG3FO4eraCqNIF23ov18gUMmJgfsrSPzECDKuM4Umnzh_xEkiH_uxumhnAG-io_gOgfRnHZ2m2Xz4A","ARENtFXlHBdA8tXhDunE35Ong7M8CbI1spn_AwvX4xM58ZXQOtEMbEVvoTUE9uQ_nK8pDXXcxRjF1NJpgDxYEz16X6SRHDjojI5A98Oid13SyPePiJcVqA","ARFdTijZ772QWp2oQyV5Jah9spfFv0KwPg2exTQP969ofpmakNof-OP3Bf3UGcF2QyqdqhvSQzcUfSStUYPCS_dT3hK_tfYbmpnKK91lMA","ARGrt2MaJ50-eBNs6x5oJZdzCL68W3Je5DrZvXDsnawm8R9wg1evZOoqswiYTs6UIdL0YsjuFJ3YR3PK-jm-xjlpyBQQCYNOd9fhFI3DnWM","ARGaqqstFyyeUcp_ACRThSBdC7jDGb1GS8shyKciEqoLACuX8R3JoTuAvLl5X_rBgmkcUwUf_vDRXglb9TKW-apoTFy2t9SkBbAPXA","ARFrAblKlYgUQIMGb41jTlh250VyQ2CxTX9KCtjfVYzDPioXDdGCYG2H71vkWDIpYlaOCpFmr1r9m2Bun-ZIc7vaQyel8nq6hhndaaVd4kEgp3z8d0CjcA","ARE0r5sKahVggXamJ4giP_zyT0qm6b9QtF3Lh84x3xRYxTTg3lUIlXFVfRzPERWD7Q2uJCw1gP6HvsH_edrnxGG0J0iyZ-nhLUbrnjIaRZt9W7bL1wZfZA","AREO8el6u2riblr9KGQ6AEAW3W1cd2QJsghm3AO3z5vIBbd4OyWyL97XcKOwm_pEWIWmdi39PcE47QBYPoTFtdNNPrnU5naHUx0jjFRgrg","AREHsv1tKHZW8NqaduthT5hG9rQnmEup3B9UJ2RAD-k79s5MrtNTIH27RUPuoqy74eHEtC50msfesDlntXMstojbwwHMzvSKgTJDxjX3lAv0LCWbuWpjzw","ARGV9PBNV-oe74kW0gtxymVyBJ2OVY9H7q-8xzG1m1eaCSQ43HTzUDUM8CuV_MsefUMb8vDuwzHaLgP1DcLb-EIWcydSso9-VfSLZxJDt2XH1In8kdRE","ARGA34zjHHdZP1TUrAwz38ZnRTrmGjougjC8pISm37rFuFASl5YqRfbieAuQ5_SGM1KV3CzSyDdFpFCi2N_7TX5QmqbpOxP7_UKAPMctPSM"};
    String[] s3={"ARHaO4RAsUu7xHQbbi9uJdjZ7BYu_5RTQ-EvPOCIBxuFvM9aSXvK05OePZXzMp0XeLJErmhP8AFKLJWnZKS3Vp_g7hajVQuFR4J0MxSLqw","ARE2yu58alz-mwhPCKGc59-Su4YRjVXTkBbYulXNfLXWFQU-YgLkNfeSneJBYTI9cLrsw-SregrKpfAV9O6kOMVLwBvfAA8r3csrqHfjhPse8oFY-t4","ARGfUSiUX3QIuhFykL9Y5i-H8_H7QeE-oTioGNA70IGaM4mAZYSVWu79T0Z-e11-WvwTAN2ciZWqZ5lrmtA0BDGnvSjG2r-7UtlqKjFymr5V3SFzRtPR7g","ARGhBroel_Plmb0SJbPisSlPled9biu9TsUZt3_f5WkJleMs6AJZh4qiNldO-CffJTHB96b6C78EIQGtCr31IGEVH2wB5mMCAvNLJWPlIkdh2uBqIYojkw","ARFTebcqXcgoOh2ZSb1qV5rxoNgPfc_a7sBEOrHfGA9RdVoxSlFacJKhqHUfcuOA4PUrldTQImus4-F3duy1Ctn3Qw7XarTGt0hvvOHwqawvswXmV3BoIg","ARGg1hHJW-l0_VrFWjtCfV3v9qfWklOUvykFbyRRfMAepaGImqUEewIBRsDgO7nYKhiIn1_tc7k8ytXYqva1AlRVecpQ6h9Wls2XEsR3-FRSI8t2","ARET1G1SSQ4H3_SwjP4xBM8BZBo0SULndk4NqDfQXB4mNrHFDm5kC1l1PX8WAXVdX2OnQ37x-rXUIypN9pKCvPCHCvzdV6So5jU8iOaNff6PA5ZkN867","ARFGG4H8WMW9f8U09L3lbXB19SBIUTCptl64GvCgl6FoRw6skaCk3iTJyLix2pIP3kpW6TCgCXhDPDmMFNmpmw-v7dhFZIlyBJqLjzEo-susMojWHA","ARGgdy-DtfGraUN5W24R8kiqFUDvJl3aldyyJCXDX6qyg2i0EcR1wstDUOW47bBteYporZmSvfPo3-m70mhhuDJgEigiG3VqsN_ZxqIUxcTUxb_w3N7f","ARGO6tgAL1KRkmZJMHfbt-lPFSrsfDGQbpr__3V1yNwsKWSUk5M0zrUunAiXlzRZa6tc_2GIb5mtzutMqBVDJK48kafah4tAykmZ4KHjJpF3KvAWQ6Rowg","ARGdoEaaTBN8VMZLdxKvJ-gkm4eJ6XhIt9QLe2cVYwrRMw5Bwlf7x8XHY1kJodtO9A4f_VpKJSvecxCRmvQ3kfl8covw1RsDZiaK8rBYNTMAYthxTjW4Sg","ARFih4jwLFc3Krw2s3VdjSpm36XvS-UD_wwRginUPpUCN5UmGs7bmpp1nQdokNHwEOspRT4Ls--lNWDBshYFCU8L0HbLqA0sftpe6bLtgSmBK4sYhTU","ARELECABdCgs9t1gcBNZMaSZepoTl33zsvZO3CHlsMogT83q560oJpv6DeBIE0xaHBO2zF5V7wVgKGNvvgge_OFxkAMskv8RbXZHkFKFjr6_fOFfEdSX","ARGuYCbB7U2W4nYrB2dQ8vFs_0s-tsapWr7tRsT6h6gn-V4z9sqtpdhFKSeM2t4jGfQLKoiBdqas6n1B4ls2GczVxTqAh58KLXVVa_PAbA","ARHQLllm7drUL3XHen1CUz0mP-jKbDWtYe7S2-E6SrEzF85srB2-70LP9kM2sDETgLAlAY-cGHKSjNev9PfZz4_kW8yvbyFS6FkdFb8_J80","ARGgPoC7Qy5iGzDfMvE4wDuVkpwq7LHVHPL1mumaTpYfEZ0Zf3rrqwR_MTrsOddbG76jRkU1lit72EVfuK9Ym_H8TmiOW4eqX1jijD-O4TUrdpr06GJsrg","AREC9-I87bC5MBeuiPsPlgber7LNfQxQNlQNntYFqVEo-uyG7ILrpSpmmJj2q3OAM0MHbNqGXlKxda3-0mCQ23b8MsZeaigRhTbLMM6JJmE","ARH4LWFUK_dqmF47O96fZY6-QPOazV0qCniWxgWejeylyDyfS9zkdQ3VIvSu7TADjeqx1I2mlwtLhRvtEkzYIGY7SpzCuKRCKsa8zuITWCiMpjHMQJrJrA","ARG3kqyY_UL5ck2Bd-Rhow9T8usjlNQkCHt3TwFLQ4OCTNBDNMa4-T7RoaGsVdKN6u4nARA-q53i2OUZjAqruY1VFJIQ0rzJhcyDtXu0xvjTSMF38_k6HQ","ARGionqRie_CrypskXhJ-sP7uX88K0zZvVNPUDA33hiveL9t0YlENFzEDGVmqycFbt6W7fVeisjKVNUsWAZDhTvoe3Dw_8gJQm33sJ3jFPQ","ARFgAomV46i-BGgsZyE3yqyGEhN2tq9Oacp5o6WcphBNY272gwN8_fdQ4ZzGnrrd2zuoZYfJLoxVHVAlfsg9VaILNmRPKXcep6IzyDiuqA","ARFXuqXtVGxYwgkjMUm5dbI0yiYWVM9Rrbb9Fw3034Yk8p0TPUXHAgVpDwX89LuHxUzQGWIusI7g7YidSHyfgcVyJAT0vtah5xrybcMAndW0","AREspqRAgZ7p8Hd7Hx8Qdm_An_sbQTm8IgBvXiJXkBoxeDp8TMMN74Aw0tIBXFIqAuu2oRRT1H-UCFzhkjMhdTFoeRD_XFgfgkpiFmOZOCZX","ARGcB5y0QgrsZwYMBn96i89zQr1GmKA6QYRmuar9_yp2US4lgwL5z7F1d5oO6I53JfuD0RLyHynEB0qutnhu8BC1cKKbOTRFTUjvEy-Eb0rj","ARF56CVu74MfW2_5pwqYJ_TQKJ7JuKBG5eY2FK5rGSue9iR4Be2G4cZgyFEWlGovFi1H1G_dfuLq0EJpJtamTLZEQ_-Q3L7hAtLWQJcFIkVV","AREu8Y1vyUnu_Pq1-2mKI0k7zeDXatthvLqgNNXkE4HFGi4lb41oa4XegNS7X7hSmRCmCSKBxwLhE5Wlu0KxnOAQ-KET8iN58XRd9YZ9RdR0","AREym5d8nFcOweVj3VA6RbaioDXIeGimctPuSYjPiRtY5uFBonwVv-esG8HEcizl-2ktEVjgQritSwFZitHPMZ9xiZFifUoMCnXUmMlEbTI0","ARGiW2bdfm_Yc_PWVcciinDpalxaPRM9Az4XdwqlAwKf66jFkKrOJkuCC6zk9sReynDT2wsVZmuqWIHI2A5XjfXRv2imxYmE7MbvqXPJWhIUANvBMzsv","AREuRrJLQ3r3M2TqKWpjpP7oGyJXXN-RjdbjtBRKGLUSqEbU8cIuo36n2eSfireF0v_iyVEIk-zrjpGfcD8b-ykUeUoaSloOTHaJ2h1PmDdw","AREQT7nxvj46kUaiPWEvwEdz1-O_dYYjgCUm5ePCFbZ4hCJyX96PcLOmspqIpuEZvmUO6EpUUeFftejCEEjxjcm7vMv3i6K_ImoVYRY","AREkzik4Gy_s9UF4FpD0iNy3kr7M6mKAIhIaednARMaW-pjfVRO4314Qk09FaKgdmS2vTyR8coaBWtWCl6c5xvcxDNpBxpkvKF2h1gyXRZA00VqNPxau9A","ARGptQjrbSb4uuCxFfsJ_P80qvxlP6K90PzuEWLs7drx7rxnEdsF9Yqg4cq_Cb7SjYQDhhKlOBEt1X4x9yvdGSSIz0dldFGtbmV_ZK74I9A","ARFbLwXys4fGY7qPE_JRZQ8Gpq9rV5-bywGUSO6FBFP0zivLLBv1j6J8JNsqZjZe7qaSwcgkFNhzzUE0Yp_kST_prvlcwQCamlthE1QaDg","ARHprJFZrgBy7uA9H867XYADfIcB98-oelqbYPAN7ZcrPWhG-7As__QhKxYzJGYD6TM18NtfnVyCPftx7Df_5GDXW2tLlaxSt_h2hr-RwDM","ARFKkgnxbqOxQig740dGCVcbkNESylpjWdzCxrcDT1OSrP9izTIo8zqRq4nZnaBj6p3d4gi76RwqMHdsLe1AETd5qNnAvgSw-lkx3_S5Kw","ARE-6fX8m34EzkGDHEk0VBuuSsab22-ZETcw2W4APif5Iq8Ot04LXDvm3XP1m1Eyat--VJzLkgZZDOiRO045hrss6DBDVxUp3jtinEO8N87Z","ARHO_R3JwoTGqfF_CIOLaVKj1NB0bdYliShTZm7F9MPdVVb8fOrJM1dYexqsRtdnLBfE9J2UIXKn7SMXfPZjrD1Wd5KU-hbgoy9EDJ8JG4U","ARH2PhmeBC2Ea1UyevqEh1PgRD6-rTwh98s6u5FQpOvKshvaxUWLnVkC7_a0I_pEbnGvjPHWBvW8AjVMHBaj6e5IsNbkab1I00vbEMhL52vVlQ","ARHzT_ZDr5Y26jzk7D1IJ5mSbaGjONPdn5BDSqRMmcME4yWeGNOrzolt33316iQ2_TVkQ0F_ShgjvIahs0GyUpRHqG8od4ckeI7TT1BcTQ","ARFpv9T3iB35jNykxmUvj4mKTzkRtSPlg-pF8Xcxg7C4IFSg2BOUmpoBLXPnp_-yfYEE5tj-4iwK5D0pUBFVrfQlPNXT1t4Ogu3_nOcb8sa6","AREg3A580BP94japDbyVXDVa0fmDwG7osWn3KIGIrIB1TJgezwLJC_SaYO_HVqpnHW38Ut36vEdyydZf2pVhllxkkXYhV4In_uzraEufIdlu5FSM3znU6A","AREF4d0GX6Jo-LoCISM4GLp3qICe1de1kaFZDHW_oij7_OQ0F0OpH3jlPUzuGtw4Gdp5uwSAfkdd_1wLqSuMZPZjBB6Tqa0mvICTKsgh-JHDKZmtQB8","ARFmBBgBTz7cU1COnigpwC_RljnrvSrDIcJGsuN4fK2E9_8R9sDOizJWMDZl6pYeLpMLTuB5J5g_NpZlIR9_QvT2kyNQkZ5avgFnp2_VdeAiPNzksZ0Ppg","ARHzFSt-RTLB3VTGmwUQiVlmjniqgdYfaOd4ZXRPtuhpxoTfya_XCSEvbTer6qThTsbXNCgexHsRgN-F0zk3ZtMUuRbH9e0axvNygnMyMA","AREEIIT31_k3c1YSl0gOuJ9YRxEP1_iT4Oa9v86S2sqTP38YkyL9pMQwQQTeM0deo0rVtqpKMYDT3w-TNprFrBOOaTuO2m9rYGawkMwshbFwJxBpHA","ARE0B5EiWcSXE3Sft8l_Bznf_PNXVEssUO6KwgsrFUex4naf2MbBImu90OgwB5mN682x_yuaGyZiywHOxk3n06_mIxI_CpxUanU1J1LcdRS-RML9aU0","AREB6kEtDecSXDLuwGp-BEh9Zmbu7OwwAItfClqZmS-vNveQEtn5XRvmNtGbCZjC50hp-3XoQygAnx3MBuv3ry3QfD3t6-ZvBRqR15pw","AREjlHmxSPC4NG4Q2p1t0BQFMMKGUJI3SPn2JrIQ4YpiS3snx7v5MESVdOHRdzsejnkce1eIEdavyVb1VchUf4Vg9vC1iHlXzYlwZNtf","ARFt7l3ylXKcP0AoE74stMr06MssdEQJAs8prq8HBeXgxLXO3F9IQpBht43gI2n2rqWKfDGXs-ZztKshFF6_qg3i1F0xkX3mZvLSWIBQGz4giwv_Dpdt1A","ARFVBi-wUkxOjDybTJl2kG_5rpFBaGK1NAQH0HCmhBbEh8KDukdUOwN6fInNSWChT8MsaA00jX5duNhXJcvH6Q5KY87MvMISF7NI66AgjHqqq1twWHNzIw","AREBqWsMSKRsTnQyiIDWWZ1_QS0dDXiSMvGOezh_1_1zVzIMNYroAhezTKkZi_UVEr9wgrO4OI5HOT3xN1-lgFref8FmFK2-fkWC8g","ARF2OKLkzRrcWdzh_2oO3_M2AdbXNnH9X9VuHXUYK-soNrbvVOy3Y-FZ6AUfZYKkWU2ykYweNQWm5hA5MKhQtbNUPLnGYKKX-MXByN44aChCSlCyRw","ARFQ3-V4ArM0VXVPzwoULoVPr4DnbJJafK8MdgpsZNSE2Og0SPofIpG4_cU1lp_Z_Ig0RgeMLvUvFkrtDeFM4dhmGRHqR7FfKgda8lKJDI5IYzbmK6NkGg","AREvkRSME_GmG-96lCpw3F8wWxOoD96WCUo2si1yIbXk6x3AYdrCceLd-Zj6jPrGS6I_8hnxzGfrjkVrxuwaikP3Gv-hJ6WixJhh0dcsvQR1V5SH","ARHNHHrWWRf6hl0MudbSmmnUIfv-gpk2m-U_MeSOAOBD0yzn0bplxnth4RJ-erNbvmwNrYWqz_d1pqQrc0IumuOqlkLitFYqXqH0Doco07HtDv3oa3_l_w","ARFQQ76OrxCfJJrd_tka8qcIfZ3mYc_Ts8SS7SUlwpg7mgEoKuGFaMyLUktJ5dFaC1-CGwYCxQC54rVKIfZnAIlILK23eHCkqlfEAQclG8je","AREtXR9HTJouE6gftNciFzoRV3i9QutPTxIuunoqmra5FE0wW4TlZ90H-EcfcVxTiwNKS64mCN-4zBV8P3bV6iE5PPX2b6uR4e7Yb1uAS4A","ARFtiJtEZ4od0ZkXNkewHzhttIVUNS_OHma4G88juE8KqRCmOHKpeN__8dg-hILi-6nbgsFUgCDct_scnaTElcCAV9HLIaPNrHeXcq5bhZY","ARFqxEqRvCr5sI_Af1k8F1OufTLMO6wwN7aJgAiDTfOWlWm-woItQgTii2hMNMCSCyG9trbcvO-boeP_dMH1imRpXSXvPT6Boq3t5aKkpvlCmRszwQ","ARF69blxBequCnzP210JkeYh2s4SZIzBUmLtG6QQdTOFDpPFipX2-IwFdhiWKBenlZMG3AunT3wVZOdL5UYEbpGND3WeMJuEYkf2fFj691U","ARH9ET7cFrGTS6He1eBcLUUs-Au336BrwaL5QoX5WMJkT4vh-I7ftbZZEm-9WJxqXr1t1x3Lua5w1HPdJtXvfQ6OXY2TziYLw1sZiUzg5w","AREO48Dqb74QaKZWiWRY3SyigSDU7bF47RqvZwRwlyzfcw5FjVnTZZsnXOnjMhmUmzSAmUlU42AZYgsWN9it1tFLhtpY2xcAi53McnTw7Nw","AREfkbwJ_zD6L5gPFI4BVp9rncTd8iGXlHLlqAZss-6i-iESm3ADirvuyls1_c8hFK9VCLkgOWsm2t72UydRQnLDdTSWx4ZUKsOTfQ-6rV4","ARE3QA36TjVYaf0QY101fvV9nCtUrjgl9QqmujxhY4XqrYSvK6y8MEp8VJ_LgoVPzEBAVMmJ6_t2t4s4EYXvSDwmmaAzQKLrPf0cDw6dvQ","ARGtpDsSw3-vuK4d5ql4UXL6jSsFUSuJrCKHAoQ1gP9_O5xBqCWIUqfUQP6R04SlkBJ-5NeRH4671KR_XoeKTjpm52HLNYWQGFJZVKTyDZo","ARHiIBKKvuQOIMqdMZ9w6YhLbLVBSpMaIrxciD1xT5gC3S_HQ1TDKHUJqOdQDH4_4QkCJBZYcm1AWKgPco52lLif-7yhhoyBGPxk_PWQrD-ifceoC_7N1g","ARGsR5D9XxO87gk4OcFOT1qTOVjqg5ciTfRY_7oP0t9BDEzUJaDtsHpqRm5ygweCNcXUtoeMhH_YdqZhAvQIuiMTSczsF2bBO-j8ZH3suvw","AREfXxEw-k1LaPpS70_9rQRSq__-r-IFP2dnqzxbgPmi12vPf00mfJM2YTVF5XfGV3T_mxhzBTxcJ-O2TbZ2HCEoTu-VC7DHgJy_ITB9_A","ARGO3OcRBxORMzsIIrJQfCtcxJ1d6fVcZWRG8IJ_3U4xPmrCmQ0iGihqQFF_MJypDfT0cZQuwQXo8PTSG4fzUiG-_0G3Y7D2P-syOppgfUE","ARFih2x7AQlj_FntCx9TvBDZ7B-nuGhJdxLSRpLv3mrmiGTsWzKPjppjqkGfjTQW36nIPh-HCYWeYNHuPbDLUbww0xN5IxWm6JA8FCxXU_8","AREB0itO6dV3OV9ZIHfC3Z1lEeKa321-DNiR-iqXXRPA0dXmeyQ2ZKcMKY6yvevg2O43g-TRlO45yKl2UJ7_QwznC4vMxh6nzN3G1YXel0c","ARELBzQ8WfeVY7rAU-Y03T_UmAHHu3hDvM-kNJnWGRDKbxQAO-DyazzyKdI1YE_bs30fKSX8jHqMplc3V_6_TnQpYjGn16rqXJhDso6boLqx","ARFOzDfeA9eZ25iaeHNfKmH4uB0hMjs3MEkP3UeZZ1yIYem_jqHGjnYbNrhzX0zVDcNjS6pA7u2uYs8yBCdynGIwx7aXeGpHjtT038-6wdMj9wPqew-VZw","ARESFQ3nI_vwzJsg2xCRQjbH3I2SgXum4axJjRn4wvvmgoX8XJFNGwB3xtclbU72Vr0pxw6CnNLcSE1gY4CPDJYE_HwKoBR7PJ5FUD8kSmRD1mLq44-0kQ","ARH6CGKBL6qry2u8tRv4JMXkvracrYBfhwpOduSk_vny3GUfmwCoyJgI5VTAdXe-ZatpdtSnlRknEkqiyjL4G3LsyruBxF1yUy0WcXu9Zwvv","ARF0Lm8dIfj-XwXpgCpb9qM95Ntt-TMpGwoNoOiRyOlNEHzr0VCvy48LUa2IVxljszm2CcNMx6bRnCxJxT-5St3poLJvegoZeLXDwpUuPgrl","ARGPIcTUKVkU5pszXqb69tSj3erfngWT03oi5eNkhqbzs6iApukH-3klymtUuB7rUkS5qBC6WGitF0zqlOQfq8C9O9E8YL6clMT0kNs","ARE95tZfwC344Vbhflqxtafep68jiR5JhylKW1RNipeN7ob39WgJkzmh6i2JRwDHrS1T2TNHqEyxymb2y_hI_43YIOm5CX_bnmjUG7zwf3ME","ARG2TJDXkwcQd2I86CDxBUggJ4dHwABRrGHZaFboSQMgwoLFpK2z87Q6-9NKzYoYelYgfACvQ-loku9Sivn8NjFiwz1SHFVazCB-UtJhEe9JcKtEv_DwJA"};
    String[] s4={"AREJKRrq5Q6JnnQpGxf7vJJiw3_ubSZ7j5GRD_p2cDkT25SWJarBXuJE3cZEQ9tyBIihOpp6C2MFp6tbIRzRqhEK_VObmwWWYkJMtA_Umo4","ARGYgwxzGGLWShhP_aGszZcCUHvXe7jPANRxU_V0K4uAwczuIKF-apdE70evReXgL_JGGt-6ClFj21aTRgqQfrn_ykvF2GaxQ7dyQ0ykwtY","ARF7MHnV4s65v3IxtojfYuxU90AQkeJ6zCbUsBiKDTkba-Kdlv54rxubZF2kP27XDLk8Bk-E_ydT6nht9yOZsgVIYmi7vBGWRot5aGNyRGoatzVkBHRo","ARFhLZtYOFCzRHlEh4YDK8NgWktq4ZwNF7yB_5TH4EymBqnnRXdVe869A86TZ4nInaGUUJx-Aa3jLAZkBkrhCgwXxuzYrd1Ik3E5aFv2ulBd","ARFvN_JixnJ2OHYTYmn_oSl_y6XCGpo_AMgbWS2qLbR_sZRtpFQbmykRLx6lEpxBRLXXxkHPo33XnEV3hfzZUcQ_65K28PQafCeQuDOKY6ciCyIrsVIlGw","ARGBYKvhuNCB2fPV2b1yUs3CwJzXImEk8VAUfaEAJEfAvgU84C8fK4MwQHVX5GjBGiIQ-DafiI2AiqokTmBP5byXURGwO2DyV9VbJiqid1MXvFGbBQFpew","ARGwjH0hrCcm7QOJttqze92FAOYaIZKhSAA_liR0Dc0LnZ2kd2WdEi_l3SD9LytUC7LU4jQ092oQnSDjSqpWoC14NuU_txIyxkrsbyKxr2gs0H-UGMoeEw","ARGJFC11qFBPlQDHawh93Gc2sRNm4e7e2P-qpdMdWM-pSzC0uN0z6BgrjtfzryUzigpECkPu5eRYpcvuj39H5znWTyc3zNbGDN1e6DN28Os","ARFxmVAPZNnholHqZxwtcQjX4tXRnOV2tgJ3O1CST_nLsUXOdkl-zJ2leTcKYClbZsmmK0mIDdh7RCNZe27TpvelAyf26eVJOOvH7KNjRe6AQAmsaa_9","AREdvVSlKfSHMPvePmf2rVf2YeeGBC8ssHsTEmSvcMAbDLuCXOJjpwyjgNTRdPFG3H0KZP1e3UZCywQpfGDvtUw4TQGzTjLZ8AyDmbuGxwY","ARHeqLJkg3t2VqTF90pjOAeSZv5M07ombae3CzCM_Ok4u5zB_3EXA671PybbQTi0yU6VjSaUN_TucBtvRIg69JJNGNbacBe0DxDRcyTohA","ARFD3giTH4Az02bluZRyRByRRRDlB9UJcLD9KktapIRDCOJ-R5-ikSeJjzROOzVZdiVz7zVtMs92AVNv7JyrWcLqQqtW2G6XbbZgf6g8MPzt","ARFgmdJgoriQfdN0n5-crfXoI3ZJC-MyycqZ3E8ZFaCLn0UT7FhsJ6J2J5VwA0yvublHg_Vyxj5CwZzHSxrMT-CyDnPN0r8lV7zAc20u2ou_","ARF6826_TPKxRSg9os4vK-vYxP3L74Op4p4b5ZCWv0XklR4XhrHvgBL8mew2NQdbSrIOln_j7SJSH8m0EkaNF87HVroz44EWcs2CIS7meTiwgFItt8NV6A","AREc3Go85P8BsuT8o6JoonCkNv1wOxYqDf7snnEcQxRL3nriqlyiBbhpgE5d3tF6G9jFU_7n5nL5MCLZOCmlEY8Nm69Z8kDLELKCJaCPOFn7CxeaYSi3dA","ARFWkiXeWevnxOHL7lyeB9f9KCKgvg8lXDy-pR_8R1AmdBo_tJLtkXl4Zta3sJVCEgNGooZjwCqBqR0GZWXn_u3l42b6IuiWw1oebzKa5G4","ARGd2GSxG0CbxLxK4bmptMlveislvsldYdUN88KeGdv3Rm_8ziPMPoEN_hIvUsLeANQ1qfeDPlYd3nTXNiXHB3YydJrCjVl0NEVEmwdkvWTu8w","ARFtYpHFuxr7-cFAol8aWqK-DjPA_3-zmofHETwFOKI3mEnFv88whJ12plshLt-gipWNE-o3kXiNf6-4M8U6T8uCErDb2j4rzis4ZsJPu-s","ARFtFlKrRCjStSFCbmBs_TjueGKQRBq9_E0kJrhf1-wZBDf7RnSD8B_b7wA8H32pIFbHD_-peaMzQ8zkps6DMK2bFKLn6G1XoNcYyaGm","ARFT3_kcbRzkAjVrTB3krjky5omK_VI2c4fUtsTgOjpYllUNuLvdWflOILPOpvgaFYLqB37YjAXgOlFIACXZbjjQDlBRr4dmfgXE3cwOjA","AREBcqEQbbZrnKPoCK_QKCo5TR3YhxiFyWUAodqerSRYePjACXGGNGwZqeF5aWlBJEPeijie97WwqaGA915a-m_OqIJ79ML9sqRU","ARHBK3_ePvRnkS6S4otWID3YB9LZO98unAy3jNaEnI8oTbNmxEIXe4dKilv8Cnq446ZuhvRr5vUXSTHx5-J52CYf_6ncpxmYxEk7ltnaTw","ARGXqa9u7MLsVv7hcZKtp6awRBIr2roI5O80Ea0L00ln3bPdQrz53vTeaPHScqcDcfgBM27nGQak2ScC9Eyyc8hXz_9_osRWxTm_w2g","ARETEAdaZHlOxb_cBg0yc1JPJ1diqIMmHppWvbjF90KpT4NIxTG2eJA6C_-JYJW64heHMl18DXWBNkA9ex5zM0JIeHanqR8u77bZy3I","ARFnqm1h0saOy-c_0ZANBIpVmlbWGqKZBIejC4BbHv7hUoIO_xt6mEbIX12TWM-uatyR1-pGXZDR0PD-YiqU2efkqyBXHM_bvF7NR7vxaJg","AREiHz2Wrd93ik_oMBvVMeCx0viOaODAOCz2-d_zdPJnduEVP82GY1KOO2-HLRJnGUftizp1kQZwFYHIrm_GeaXyuQFple81W1OYh8W9gMg","AREbSaRFAyoAsJxmQarrArAfHdRHBmrCVnLYnpDm2f-VmBGoKoGUJ2MwykHIDHI1I5HAOA0dQ2aHCzbBWGaPHzSSjpnPkTmztxZEn3v3p9ca","ARFwUXD1JVdhh3W8NIcCQKkYkjgMlyqSwn9wJm3pI_FKNex4Z1rAXDn494-dXMc2vpE1kkFHOfqjc8zWfpJZkhbG0v6oF3YK0zLxhtaoo5A","ARGehH1ZXBtGkIKsbFzhWcUd2bJxxir_WKKSg569xw3h7FfirYg8oiYjR76FEiRIFsZ00aNlAe9R851YuD8dVRcN0FlQ3g9MUcdPHeM57ieEYwdbKxh6pA","ARG3TEBJohQoeWI2UEUWXNKEjsh0bF8g0U5ySxS0J5FnycrgW1KJDLuUr2y0npg-PGjt3c5zrvP6Qyg2zm7gk93AlMnoEn23vjv064Gp","AREjlHh5ZG-WVt24oedfgj26xF_NtQhxk3bLVxhMp_h3KVCYrVn9yN6jZAm73jQRcYa68PEdvFSDKRtuzXkmddCeGdDApMUTtb_KaA","ARFOUNvTiQ678Xxo_Jmq7Yi8XB2KJekRk1rsI0F9fAEEYJRFuyz2tC2g8-PxNHMBwxh5fAEFxoNR3F_aEjC8La53yVDcajJXYXqb8eR5xKM","ARHeNW5ukiWrQrmkKoHA7O17ToDsMfKpsnv_v2NHgHpSsm3XVYgOhw6YdZF-FzKKckfGBs0Jy4_RF_ng1FB_vcy4pqkmuoaQdRYs_gjr3g","ARH2fTezg4hyqMT8A3iaMCcT_vRAIkiMo1xavk5MSAzQAjVlIrzI6edXp0sSgFtzQrbIMOFh11sULlTVJeWVZundlRoPg-ly-kbFq2hc5uPRU3MyOXCX6w","ARHtV9U639DCSolQ-v2stx040w5vIV8S4JxBw9H3_vIVcQhibPDYtLsM1nFq81Ppr-ajpL41tqR46mfOYpKtx9qjhohQ8TEsPFpHV537aeJl","ARHWLBeV02IhghImGo59b1kC0exuPDBvmPw8TtoVUx6mb7bCuIQjvCUWwUw6g57g-q-88W-rlwZ9LK54sAUUPYf5k7RYq2xNwRDlCwkcxVg","ARHtyOd0mbzwV_8E0U9umQiiTk80swReOM-Hz3HpLsym3C1j_zrSVgVhG3OGIDmgS5nOr8jl-F3_hn_Jpdb7l371QuC2ZCQY57PUr5IJfr-P","ARF2HeggQUsMpNNbeU47vHSwWVEBokiKK65VMN2mvp_feP7yqHWJF7CnxhYZHzMpCcKwjRwUqd4ENHVcE8FYWTiYOSh7pqQDetVBpcER-TE","ARGMJv5HHlg7cv7ywFjUWgLb55Sc5dCk96zK7VZlCTM2MZ2xv221ewJhFKKzeZykweHVSbETu1StijxVmUw74F8_ij9JXHksRrAWsBiuNlU","ARHH-Np3vs_aoQdqOy5x8-_NJwJ4B5U9qoyuFiXl1fnfAikNySWf1bOVEPHrLV15t76mzWOjeBYbeng452Te6qXgRAWkJxAcxu4KnvHzmb8","ARFHdve_xuMeqaJVulJCk_rSZF87dsZyDf1A4g9Eopax0toeOz-5HjR_k-1VEI9crsyO-Cyu55jjwTZnbqvbg6CUux84J7eSxNrdkYhA0t0","AREhv4S_u7OpS20GVMlKMK_WvHAdeWyIj4wdKeDYgMWJwj7VGbTW8q0wNEhDaBlEmxKkWTQTd50u5XJkBfy5TPlt4GUq8ZJ-lOBy28nXWZ4","ARFuJDr-kBPcik7B43GejX2pb8MEI1cqmW8DvfRBaIE_sUweoXf0oyL758NfL2udvaNo-PdAUTT_zZ4zBWL_gTouQG_brJjGMq37dA0UBnq-QLYp9w","AREI3JibaVtc3YFuSYpCzjeRbJURXc4isyLxQ1u4g93k1PTZ8ze1m8s0rpg-IDlNvdhJRDS8KcZoeN7iHXqk72XyxkKEjpv9exY3sWQMLJs7Fg6R3xM","ARHeImfibyfvDI8gAeR3Pu-l24J4yBzCuyIA5MhyBTg88eemWqXpULGsRkIQkNcbDPakeJiShuCPY5SYEv-SBxyHHIF_LrckQRIk5RtrJu3Sak7bgHn6iw","ARGPtM_kgjzkKa-ga7IIVCbUzFciuABD8CMomeOxMeUkMNcnnWA_wgHpWxsHbGhKXPTYl9PlkXJxFui5KMz-_E_5338zdWmO26BRpjhnXCZPXNPScFw","ARGoH0NUvj3B4fovRwODs0EatkTqVnkCl1_Jce7-a07enJTGK7McukjkgpzjVSjxZQoRn2IgzH-AFu7XTP8unYbnZ7fZPGduDb7_KTLmlCtoMcTd0fYU","AREoTOdy_OTPgb4CsPScJKyihtOgqyiies6R8gMgp3BfwIbpSi8JwkVkADakjnpG3CRcUcxyL6nfDfbJQMCoh5eep5AEZXKF1rqaq_fuzTK5uc6Cqkg","ARH-jn3FHOdLauHl-_aq6A9SoNd_F6YUK_w07ZWwIcXDp_E4TO4xgo4NF1mMhfAIe8XOPmxL6zJBMEUVYRFTNng0bUJtLoN3PPYQ6A0wDebIDIR8PSw","ARHCIB2aJEy5iH300w3JoWq2XZteRpz_gQPcnlKMfGOirLB9wZPA1eReUU1TxwO4TdXqjduQUEIjRCfRFPOapvk2NHNCTItj0oZLbetSi0FVtJQ0Q6SmRA","ARGjyE8aoWHmCoMKHm1EA7RpS-ootG6ulL8jqoePjQidt2jhXaj-u5CcC8ZBv5nT4QLqM0NCR_NfAatJ6cWtBbpF6Hh69pervaXYoHA","ARG71jWsg8XJuyGxrj0pz4HPHP2CJ9dSX4gEooOwWSf3JJY4O9A0lwErv4GGI7mdKZoON_Wix2xeNYG_-Cvd4RdpIDBTO4-lQBrtrinYXAg","ARFUwKNXgozZAb_SllL5RsuRBxGi86rg1GqVzJ9J6_SrrbYVhx4GrYjM2ARjxDTZmu9zQ05iT-izE_r66elwfpyN4y9Ekgg5PH3QnNSJ8Ctl","ARGJd0Iy-gz1hitxlvgC-NqRwlbUQQgfcrNmpIuMtBePMhqSMR2pBKVugWehXmIQ_2lMTZ4YLi2G25dJjW5WOO0kpSzmRuA7-cr8uA6Ld54B2A","ARHlZ5GkhGWjWY2df4ejFpv2bmD_s0Yoli-KwFzO5HMBcnqzYZpjvJI9xslpIA5MFXQTN1TFsNjgu7bMLv4b7k7B6VPCOofstUbY0xpxnIYh","ARFV7i66w-YCk6CLJcXfNISIt85DcALJD7Dnta7onh6PccRWGsdWQWKLQ0JUYITHkv4e2Jn54MiIsfo_t6BnRBe6UA4bUo6m1iS2DadOJr8","ARH1_UTHgvFFOckOw4B5R23RyMS2YhIYoqYGK1EmgmkG_HNLoTlZPPLqM7U1oMwxzbJcA0jiz_sMyWYPaUjmXz_iR08Cniu1Gpp8_c1LpcasJ09T0jpfUw","ARFSV7Q20BKK0VcjU2sY448O-aLK12TE43hW_JX_LzMunVIHDYdyIVxDSTODokxu2Pw2DNbOtVgXGsFNROlfN3wUE1IHpjIaVOKMHnXQ","ARGaGSa8jBN9VtyYYj_oSnaV3RpozicfIlOehK2_XoMztLQYSYG7mPNeJ3NFOuBfnhqlWZJ6Nqg3d97D9um-RViSz1TliVZwquTR73zXxA","ARFK9m2H1HHPWih1og8TVv40SwIrslPBC48dg3CmmJkU_AHTkLRq7RNozo1p06-wWTgFe1pB0qYflTeXUQQJF3ed2lKaNtXOs67kEvee","ARHq3yuchrzQUxWL1TohnHxWJDhcWzjil0NucXAR8axZM49vBymnjj70EDwqB2NMIE24WxS6J2RbwyHhVwFtqeh3FdziNeRJ-sTUPuFOtd0","ARERCtlfy4bbbRdYGNQWsgbSZ9LP2-oBT8Qiqqvzz0BTTGL24hXOyRHqm5j8sji81UIA45kFjjps0m005QkAXCuUYrnaC4WIeycOkrXbeKk","ARF5nBFOAkM9D1oBgqNnmx767CDa2_FAuRFV1c67y35rhSYlMJtkyyqUnYO1hrcnyW9qlSDD9TdyIuTSgFI6FYiz_pRC_BOUMSwQMzGj_n0","AREtPkK2BLHnDWSHLYiHL7FBiyz8XeRkdyYSOiYQkEOm5T6l6QIxEXPUuueL7vNnKxUOnoFTBUhr9uhI1uXHGqfMycdn5PTDNDBdVHYzEYs","ARHWlGMnYOwy-OghEWxn0jeIeaR99oj_OSUNlYeEsNBwBuxEEs19pWowySxVgeGBi9j6jtRoJL7HGzXVbQh79S8feFR2RenNVcqhsTOzUw","ARF0W3ts9X5r0toxlE5IotWflgOUmd0xwez3UM16Dtv2PEV_CIFiJUkl9366lCt5bo4siUg0m-SHosmczsriws1CW7E_rMiguC0ym0Y7kw","ARFMF27zASLnByZaH7LLZK6rKwZUdtJ_iigqg1jlQCJBAtHSe2kXPHf7RWCG3PVHU6J3bpFJ4pzgZPZfZdL_AzdWQT6TVJ2RAqD2W9MvYTw","ARHwveHBTJVF0IdnwXo5p8z0nk41kgNU1Hi_DbcWzGUdn5QBqvHUBkL-b3h4jRPJOo9djrl8rKwvGvb2OB7AQuJUMZFWfk4vq3NuwKNcdw","ARF54wZJTV83Vde5J5aM76cRhs_BWE_SwX06d2f2LeVuSrugI2UT0qq3P3yDyi1x4hDXCw6-MxPsa06Sw_u9hl4bcWZC55FkVDgexWGsRg","ARHvXYKL_XE4I732OXEqhg_tv0z2bsMSDfrqea3si_4YHntslCYE-hxvYUhLZGaTPCydEsphMRsyKsgZbyZAgjR0XF-r_aRJqBtZvaymxmM4","ARFM5C0WR5ZJf5G_rF0DeoG_icpE0Aa2BI73Lqdu7jQrZItbfJd13NJcbEucoSR45Yuk-6NpX2TXpLENxUJPmVgb_pc_BOCB_vdrVj79V74q","AREu9ZOU4iwi-JrFD1LYYh8b6LlMtUc6m9E5VK-_2BOSMjojU5BbrMh7vDt0YyjSI1DJDOK10fRDm2vYh2ygCLzS7WctH_G-HMUZ_pI6I5o","ARHnJaotYiraipdhlxU8ho1VU9J5xw0EmhVUYQ1MHC3PBVjtcd7SlFdGeA9RJctwKClWfr5JwN9SQqRKN-uAn_vQQkDVqyiJAxLqUzREMDw4","AREtkTBP129SV1as-L50JFPPhjiQc_pe63TH6R2S7EUdCeb8_87jF_5PC7JZBwB7dW1g45u2uKkyqfVEVOnUVh0wgKwpjVl8aJ2g2r61rpA","ARGsynwBQaU8bn63GE0zSP0pGOrLqu0c52IrQQWW3cGWPMXBRBCsfoYTZxk3m7Q63Ibi0QJ8z_VbE2l6cQIQ06_W4RT6Jvvmu3PmurMEEw","AREfyhnvdVDinaBmYVjK3UcrTbS1WJAagEjCZnnHaPhLvxjv7B1HfxrgW3SQmoi1g5RpjyJlVGtJexkMfl0CdQLn-Gc6xX4DRqXpRb0ewNYN","ARFR4GVH9etqDIFfchVBPkL6YoJyzH8VReLbxdTS2qZ0Cdgjr6pMJBuCN-8RaGayWsS9c_ZZuT9OmLCqbcSG3tpifURYPqa4TcQxWVW5yA","ARFVMEKdGNf1I8QpUZqex2Eu9qRlrFr_sP5r8SiXAWgEvQo9oajk3aZwc8LkEVDEFeE80JXJJ3oZ0x_82nfIpQU1e5O_TbHVf6VgwH4VKA","ARG-9v5vbGJAhmb08Jtze4hRjMlGb4mucA7DaDCULCcf9vOmK-0H94jLA3aRX6dTEhfE_YaA5Oivc0NYOUR6BFC6RRGLGfej1xPcg6_kIaNoYrkeNh4iBA","ARHnWbhMXMTcpvwpRutmkM6YWoh6MAn5pkW1V-Mlb8vvkBKqITHdUvglXmZCMC9ZH6xiDLsJdF7bi9Ea-GjnD450CrcXVcfnrzo13aXbBBls","ARG2-4XGjN0iRqI2T0UHoNyrEpAJAepV8MZ5NvQGw6EcY61f3rT5Ef8JuJi3rq_dz7plt_H2Fi5SNPbapzxO8CArq2pZCw6PKSFO3o-4q-dq","AREE2xXPO1LSofwq3lFMCSU40Or3m5NIWe3JWxzJk6nHIeX9YjZilUxh9eiui9Wmt18oL5bM8Q2mqU7FbcGvIt3yi6MMaBgopGFyn6j8Pw","AREMu0-ymx3VdSUgqKSwgmndayTXVYVT3PhQJfas5g4rLRr6HQRsKyIR-jtKkLl1kMAnsm0BECbfOFMUlSucFCMKAOeS3FjslxG_QwBZ9Ow6","ARFzFqVR1x3s5Zp5mA8RPfjpLMl_tT8w93iS7qDpV7Fmrx4yaK1EIQ8adejVL42yoNer1kKAt6tusVm0sh5hIJ4EZtIUgU0kpG3bvad87MI","ARGObERI7YHvGMLcM52pubJNOaa25NSeY--9X8sIHHJPM2_atY82G053oCyohWHMeJt64I-V0ibkGmdwTmrCOlgZGF3INz2nTHkInHj1Lng","AREC9V3hGj4Cq88rEGISP1HvPpyGxzIYwzdLmlRfbt7zq3yobEW8sxgpjGREQZp_dXN4fMACXYjk9xzBCcsR0uKn7FEgf_gguW4zzHlZnTQ","ARF3s-JqE1j0c7CGnwwTJ9tR2LE-c8PpPox1OAE62EI7r3LfBnTNcTGNFHPENYZPf26pQR0Sp9hmLMoXkRE0f3GlyNo3hQxiPETm5dFz743OGyRINzDCjg","ARGusj1zeP8FUzoXMw2Nqm6No16PPo6uqc-3KgUip6tZdEB0oGPnjoawQMJperoUZJlP7YErBEYwll9xBuWY4VznLZkBxCQRc-7x4598MXltBbIZkpx0FQ","ARF1Rd2cdgi4SVQcR5y8mJrQJXWdv7VvYSa5YobX1kiD3oX5vb2G4vnc8trwdQGO5igQyqV-iw6bXsMOuKzeSDdrKwX0M8Hmt6aSfrAiLC92YhwCXLHjlg","ARHimpHlXVj_RHynXXi29kKG2IUngYJfAwsikTCbE3GXmywrPLJ2ULe3VFMxOsLvt7Q9MP1_paA2E_3wxMcnHnvMn2paE7anxDolNcPaSLD2yyqVVaqP"};
    String[] s5={"ARHWMJVkMrZlku7S0XtN829QiH-MDw6hSznb4yOe3JWgd4b5T6DNc5VMfAzigpiPD5mCIrKUAxEOQGz-z0kDxlFdtMLWYHffyZClnz3FD6JQZg-PfaJsqA","ARFk-luF35-wFheRQYg02gYfUTptqFPr_xhrE0JTJD8WD5-plXqYyJiT5ugWCPLEi2n7nqF0A8mwdf3tq56xGCbCEhvYlLYp2WQJqseg1rtlpeqkS_H8OA","ARFj6L7Ac20PToC248-Yrq4AO81q9HA9vgCW_MM0pss7obpQqhIHZU1EH3KHFt1yJEyiNZwdew0a6I4k6aEmvxRd1Lu_0LL-2ALgXI_5Sg","AREW4NH9v5j-cD-zXK2BdnRnI4AbPWVPJ7vF2sRkj3OS5CDrhtYAcKoYJBNiY0m6oyJj8Ro0VK4ASinY0Pg_IdDmE4HYvy2HaKEKj2DJn1o","AREAEfNQ9NXo9dyYvHu0iqHV8foTF4QgZVgnazpYTE2KMaEZKkqho4np7umw6wObybGqeu7m8dTNlRDJLxFVmkS_6rlVhmd45ByP2-6MqUQ","ARGjs_eYKWePhn2v_m6tpWSycgtZ36sLetX5poBtMNN_IE_EdkiYkipp9ZF8l9_bYpyH75P7Wp-fuwNW65rNal9JhlrJU9SDSC1aq9TYXz7lHgoWxw9y9g","ARH2MH3IRBPhU2GUJIuOKrGVyLLor-4yroV4djovGQKBgmbWjcWtAvVqYKEFrB5KQqe0dxxHENvjET_pMsGwUgop9U_sCcvjEmShN6zW","ARFPy9zDM2MF53_oAOwmR5nv6EW7kpG9dhKbGAgUInfsOSQncgz8qDIUDaO5vtZpO6EYgzYyGHXEiaDJFXZkpsjMf9_tF1yCUo2c5fmRAdxl","ARHGMkn3w1mJohnyYDSVRjBOykRPavEFKmDpbYK7eyOJv1Ceyvk7e6PVDlc9cpezeXSuriL53aV13neJRHVrhuu6jxT-hrAGF7ZMiWKz0g","ARGNj5j0MKKqRI6-6QoRaITrwXgNBjgQrrKzhSI1_Ch6qs8qUa9dgLxn2cdzmqdoQ5QTlZ3gVU10DdPN4jNI1fuKJSjoVwAqxyP7W5DgLA","ARFx4e8vY0P4uZDigM5khqKYqK5NK3RNwL0EZEwfK_-uqr6ag2JG_ewykHku9BFDiek27T9gZKFPNlYY3_lbQJb_v62Y4JnQl4Ok22zLca9lkkj9xzMb7Q","ARGTtohqja116qLljBIyFQH7EoMXJgNRfMWr5Ki7T2lVb55_pXwXriuxvnXXKG47pi78EfpVl56q3OGTmD7WmfPNpqRaBCWc8uQ4Yhaj3wF2y3o2x4vNSg","ARHShCeGRTqGua-c724jglgKKP8dLU2Rtc9j9hSUGrBleEv6TJhkUHgzLikX9A_8ZbB-OsZGllISvCfzKWpMyC9EQeUVzYiiCNenOeAQIhyS","ARHAFtf7Ed4N_Wd3w8pVawE5yxYvlG1fyxJZbXep65yP2oSeqzpZGeaM0JgRMjEhJB5XzUwBqgdyMXGUjEvYuqkN_spEfMzJMqV0JEfB6Ok","ARFi0F9WyclW14OU7f4N4SMqEJgJBiOtI4zui8NvGt-lJORzMpUTfXeDRs4UbVosZvRLSmctpZf9hoEEDeEU2abyqlbfHkbsZQJP9nnNFg","ARHk1yR7VUtyWzu_PtmeZlsybs7Nnio_l91R3Oyb7GQGtXoE6aH53P9auZ2mfEsacAIda4TIOUs7vHJB2dxPBnGYiJ1J6pVpuaEmKZnaGSxz","ARFr9fKkUw_iaOksCGbkTCg-j5sIIyaQN9J6aeSG3ZQo6OatOeiEAsSgRyw6Z-U2XhumN3HMaAZb9HzduX1fIYXPT8fmqKgZwpm9WdCIam4D1hmgAzDmNg","ARF8qgwiLgq0G_T5C4rqOlvH3CvHiqkd3EsbWGBHSU0lWnlqtATIXwPVfedTuGrU2ok4wFmfALYunoEJ-9rpNFmP-pDzJx7V_-_Q8xoDkNnh","AREhRqU-d1qo9UsfdLT__QKU7Q-IEr_6Rr5cgbdwQgkiIcjvulJoMNtcNRvUz4Xdsw3NSSN6wPRTP7Q64-CebF4NMICRjwGyw-zM_h9DmIc6","ARFVwoM4YAbyN-UfF2dcy8pZMoNvg2l96J50kwrXXQYng732ljVgGNwSNjSCj9e5-eMilT8bmC2JZ6XwLSZD6bcToXTZtcgBMPWn9FVNPro","ARFAgQOVHl_qYotRSaOE7yGawonsIap1OQEjBnDApAnvD5wt3Vv6S1N-fiBFdeT1Hz_yvuA3_dvu7rgHOnmzPazIR3qlDRKorqfJOq5r","ARFJUPB_q9OKR5At5pI5sC9Yr87h-BWDh42qVrWvfeRUn7T3BeX8gtwk4i6j6-Ys6qeYSx63qctSFHCkO_4RJAC1VmwjRaf7F8POx6eASkrf","ARHDUT6OAXa_5y65yv_EfNrBJC9IFPINTEWS-2zKwHa2Rvu8i_1JOm2Qn15IyBKewppNL46h7vAf8zq6i92JhyHKzAPTTQ1pqvPSz1vtFYgi","AREtemXTfg3kXNtxuncHU1i-oQ00cGie5tFOO5Viv5xrLIFsesXrhB3uu7ruK-een-NOBEWFKh-XACPGdO0T0Z-rJOUnHrvE8XGqb2x7","ARHJpoibcaS_-g6BvK_-dZdcczumbQYDIYliBjh3nMX03bXQcp9xHLS_w1HU2njH00IfpmGsJ_a0lPafGbOIZ1hNOXQyxzngTTQwxio","ARE4EGmAfiYy6mV30a2dGpJOqjm6_IidQWGtiGw0pZwZ_8nw_dVVPlxqjuEIxIviK-O4fGGuFZvUwFUpYClnQLXCd53SWKGnaQ7f5xZX","ARFv3ItTfaoFCFh4dKhu3VySAFusjl3AQsRVsC1wm3KSKCo1mn9t9GUobE1MpL-QfDQbwukqIDvoxQ1TMawIMRWBKti41qN_y_1Kt14C2_w","ARHyhFcKwAf4qiB7e6wWoB5FUfMTRRkFH7WtNTCzvW25gMDX4ffVGzkb9gmGppIUX4agkrX_IHSRKsrDgfP4FPSMCdCQb5f-PMcfRKI4F4yy","ARELHnv_RfHWaI3dOR9lr5khDeUrUWNBMBEN8y-iYvpVoFnBxYH16gyaZgdDFvya1Su-cadNzp5nNbtA6zVaD-sAExoCK5IrKzZzGDko","ARHvvLcWYXkL6tNboDod-GS8rugQsANdVfYv86TkmOfmwXLSWE1Sk91tduWBgaebcL1sM0p5a82DOWVbH9c65rvlIKzDvmM1dYbmPsck","ARErG6UnBgNOHBPWM8On_PL3nWvJ86-Lt_yRQpcA5TlYc_qIMTONFDSEMRRgAMBEKO8l6cW-XGWWgOz-N5gGdTm_f9hsVzGKp6agGoL3ZyFmnQ","ARF7mG7OoA7jiq0UmWubVJyhPF7Yw0SsW_lgawtNkTbrE4Nfe0RgO_ChbjnkyB3rYwo8AC0RK9qWSevR62-K8Nku_MUByDWPu5WhfT--ItfBTE3MRQ","ARHuQUobvvLG_o3tj-44aLcn-QOAPXtVJc8wESF5P2ClXMnBDwGCYrO39rFhYN1smskO-svdks2OgxSg9731AeQkiJnBP2MN-6ypIAzZ","AREakENdoc-_cyeHXAJz9UHyV8xGRnTGK1bJk9d3KmHQ0ZghX0hM7BKbPJnnot2RVdXtldr1Je-GKyQBnrgg1-zpGxjnJwnIRpaBfktS","AREkWdCzeRwhB1iSDEyqFoGLCB3PyLEhq6vHUSgWu9ZPyTN6Gn2bWM9lo_D9QtaIlHiPkXw_a4ATduGg2hnwhvt-odzDK3WHFYGc72dzyJDEKA","ARE5wnvSd63tp9oZc0uBkkoU3BUTHjKSTxxucObmzwsC4Ncw5-tCq3SeHaItHXzkXWBj52O_fomPVbkp4dmHUtkfVaeimP7H8TPpBR2glqE","ARF2lIaLtB7QpGybA3zCYmQXXCZ4pS2v4y7GY2LqjPgZRaZ402_v43mc39MUm9zB9mI_BclVPWRi6u4UFcdIW_LOiOdd3hObSfgM-iY","ARENxayFNYUcTRZTUaxLQImuXn3ai36whXj3zwQ2uLVR8LEVSwRPXeFpRm14XeHDRQ-JVKa1oPJttOE1XvbdjIrEtQI8bJjSxhYGQA61DY0","ARG2hC5aIXG-yF_XFlTSQ7rD_4U28XhrUl4NhzoEQx-Ppqy8OfU772GC2AkQW2utjeA2h_iCFZBMlj2_BAJItttk8D1tXO4dQvGzbuC4B9HK","ARFXhVj3w_SGFnyZbGCX-SzYKci25ebeUZDXG5gSFWd8yHNmanJIDsUqMzjgk8jjixz2ZN7mCtw01vmV71lnJnXRXzKLUaMi0rOZIMl7xg","ARGqpla5LC-ojoGm4mYNrfus3ZHRa3pr1CuuN0sPUqFc_JojnxJ_lFA8-b_PvWvCQbvPaoettKPA-fCwefmFEUxM3nCaeXa7HhnDBAyLR7Rh","ARGwoQO_L-uI9iS_s59IewGTdkiC2yiVnvqmcA6GeG19ETZDsyuSiUAvFbU0DVE0x4GEeW3UUR9KoDryC98IjV0OIunbdJW5yaBOvPSeIRj-","ARF95yENUMRm9Cn502bKZ0mJndSlP9QbPjHdGDEZJEwKKTxyipRKtt81Y6FEk142M9OrjLMtX8K6GwHjX0WAdiGQyo4LC9lJgqthx44K_56s","ARE1sOF2DXO9xdbrann9cfF6BM5M7f1MOcHJrMvOJIufdo31X-SswLeeOvs8jHitqm9Tj0QX3SpNMsldU9372RrePoeqkUGALT6NbCo","ARHVMpiyD1iLpHKdV3Ne_yJva3aOgEstJA7KjhO0NCjjHlXDk7EcnTWfkII3KMp9KXzAo2hNy9lkJAXpL3DGNw4QUbRg1LhMxYnOzTriE3Z7","ARE-9qfdOpCVm9xmtcDTXD9_G9QCc4Nqil_2tPjne1s3RIM4SjYOsvlZQ6Dqv1-wGKFLu2iwotNQLOiW42H_FT81RQwcqtIUbThZ8RThUdzX","ARFaYfkO8uTG3hArqFSVbk3Zj3JpvmTzaBJnjKGlsARn82k1N4yfjFpqvpBntF1oSGJJ2yefY-Zj0nsPSncXW5RcHEELkQrxCH-isvJnJ7qd","ARFSpNVlaKJyl6eY2iNIx5uEFvj9yJPKQZlvuJpLnrWKnrpA_SDzp_eZ5PaFEp7m4L2WMp26bvLDnTh2NhcwPzsPsbZGrxlEDYNxPBjicVQ","ARFAF8Rdx45bF0NcFmrdeCJmUZ1ZdTNS44we23IfKOFYEbQg4UAheGyQ2EmqjoBBbdRLLdmtgieLG0OWxDaUkzEoV3F8XtTV8GocQxuyFFIAkjBW7tbXLQ","AREzJI6XUc1E_v5zQWvzny3KqECnH6Kp6n-jHAvRES057ymAeVQfXjQg8PRq7Rql_b3PZUXDRMY2CGZbFh5ARuRQlj5drYJReE5raO5jI9ALMnYisVZ2OQ","AREnDxKIo_5e0_n0gmiULEHzLdnMBtoxMioKbzfoyFtPc4w3IU7rkCrpPQFCA5B1NhMfB3sOZnhDVwPdDUxh0XE-pW2fdriIeUxr6SfJOH4","ARGWDOb0jFXs6Fk2Aa2ngA1l9rjumDOVsnOQ__u1hHbyxXr5USCBCgV1y2iHk99WHVQSDTHgWOAgMKkU3Q0IUtpKVt2xw82lMTvRlMcEKQ","ARH-p0GPx5MdSrHQin2g0IWtvadF54zhRL7ZGXq33WNjARwlc5rE4Yxuz0G7dITecoVsMAqmy0_FgpHvbB29HdKRwV6eiQsf8v0FONaFPVvHRRneBIE","ARHNhp2ki8YmzWd5y2GX8mfueIo761Enom5MxPkZve8u0zsIUAss-R1sVsnaKIdD0Xv9QDZ8kzkmezTbk8wWnlLFxUOuaMq9glYD_dPtwRMf96-kp2kRsg","ARH5l6eaPE3UMOwdnVVR6h7-2zvIvyZQNEPlIcyRv9-bGEKWjPAKDW-On6_vS4byDV92c7BMIWa3bXikz9fpip6k-5SNX9eETvgvlfx34bEQ","ARHwH55o8U2Xwx3PyZRVWUTR_wRIf3LthrSslYmOfH_JGJ3EyQm3gNrvKpgiOl7QxsmzV1qg9wjKB_RCsr5YpduGvt_L6aZBQirybA_ssaR9","ARHvJG5NKMSNpzLTfHbNuwoZEluM25dv6omiDg8fxa_HfFFmKEDRhBY1VCLmV9Px8GwkmTdQQsbgoZcl02OBdXhr8ENXn3nsBHPByFwLi3f-7A","ARF3m6PPCtelytQ6mgh95Qjk51EBeazrarR3U5y8gntT7CYMpRzwud6GNOUOQ1JX3fV2L7fsKzeDYbi-ucuB9wFFAjeC_WBK95xNSPBNGPO1l1d2NzfSMg","ARENDuF4h_0WREy5AVPqGM7Er1zS9uwDOvHh3WE_gG_tQ1fxWS9UuV8mb9m0m-aXuXcy85oyZLs-x7Vez4amqV4k51lJliPMce3bv0A6yiim","ARE3QwKTIHUlDJgxuQ0nv9FeYaM7RonzglSW1g--xlBclVsVzvwxUPNY14HlXgFKuekk8DQaC-9ZZcQr0unX7a6D9xnNMQiTQrZFOEAWRTDEyRIK5JvdLg","AREluO0zbeibHO_KkVcu8J67V_fz-OPxBQiRwYaT-jlgbTQyc2n5_bHP7NmMEv1AFWMKnkXgc9rPIPG0mh3OwruUyyUsH-frqhwfXAa64dJ1","ARHVjq8SAuS52KGatiMtcvAub-DAKGbiNL4KOjUaRYP5dF3pjBIMrIRklbdgdw5QznMPLmPNxRLw1idIMwMb02RxQSYXc8gI8tzc5-1YtLb1","ARFS4UpydvF7ASCdg1UqPrVCBRXkSPmVBWMUcLiafNUKflN5D5YMRQH6YNDLqRO4D34cMzkOU7chCXm7ZKNmNxOtAcyThUPD61jvJv5BtD0","ARHJSAbw6SdvjruzUqeKo9KgFmsDcnMBomWyLhzVL_li-XoqW51aRJuGHVEQFiRopHV0XpzCQKxGwKmVBROkvk-T9kGYoWcy2aPkloQudm-90gqQNBWIGA","ARFuKddfMgwyGFStVGi0ryqc_Ruww7MTxj5qxHEpvXUNOD76atlsXLHpYevV8H3Bd8WZByrwKPQGJydKF7mBhdvBBFMSgplXpbyWsIYzZYM","AREhSwipRb29TzhwzjSCEHDsjfbU1rUs5Olu0XVPX6t9i41e6LQe6doEprWGBWS5y6YhqCJTl6fNHBN-3tORJoT1oNeZvR_JC-76gfcUSpE","ARHm-zl37czU7cGapG_8f6zuKHcSkCxLfENyFj-zjBmg9zvBSxXgrWlVtYQ7Spq9oZMKtP5XjuWt6ja_YOjqfozaGikKusKNX-aVtJmo4A","ARG8vnwiG0U9vBYE1nNTdGKjlnoa0xEliZLee9tsIgPOTra93s6CqkIh5RaLuC-ERcBXTGr7c26D62GmiBod9kBXmm2Qyf191uKNKnPZoLP2","ARFxu_ybNfQsa2AuBOCbNq7iDcCkGIVTp-XyBNH0GXC-3qvJiMtiiirey_Hx5YOssVMgMeFzFN5wmXwoT5gqRN8jnhg4b81mJur4b8P5TwIBgUdCPh4","ARE9hZYRf-06GNnJmmHoA93yZxwASoCqZPV9lMdnMhQ0eLlRJdQBwSRBU6Fb9G97pj0Dniu_tNrxxt11t127zrB7cczwcbIyoK6L9y9qGHJYSP88Bn0hmw","ARG31zhXKyZ__P3HAy_B7cWhL-g7VxIPoP6cDmhNlTxWmusJsNbzG6f8ntXyfbSolD_OeaKvzMfh48q6VL7q5S8GK7LaXmvC7c3w668","ARG2teF5YL1M31SPnZ45rzlFeW2Fq4wGGgFwmfX5HJxi63L4YAdFmazSzYN9AccijCs7qmf9p4t2TdE06Hm9i1V59W0YxKu4BLxNbnqm4rdijXHwkqsP","ARF1lFs5YxxceiBZ5g6bZxrJuWyRCllN2HCjOIeyUQszXvuWvFspnI3mAHeDK6lKkfbDp4lMOY5V0_NK0Ep01Egs-E8tobpQHWOUqzPwPjKpiwjh2P84Jw","AREhrXF4PuWWBkLux549-lgvX_7-skXt7rbJ2qftM-Ou0XbFEB2kTRvw6lhKpuzBfB4YQrkNIzg3KsYGnu10yjDETBaK3-ZqGAPX87MQ9hOA6Ewn0nqY3A","ARGPNlEVkV305V0FaAVzY9-eiRIJa3YbXW-_dQryxWRQkUPCs7Ea5wJIa9om7BoD0P_jqknxrYqr0ZCYaYIAjKltb0dn-QBe6Ko2NUg","ARF52RVfuGBQw27DdDLnYueQbP7348LkopHndcy7Kl8KEiJect2XUdFmEsTq1rxOUovTlzyorLCgde_1KND09GOqw64048u-YzY9AG4z4eDm","ARHaRHhU8ZWwfVNPINfGSS1w9w2yh0cI-HvA9NfpdNa_Tp50dy9NZfZENTzCQbru5nC8_r7MoFanlBbpQy86HEuVHvSLqnQtP98qOzsXXTEq","ARE46XsRGpKhOzZdAgWELfu4KcG0SkGxuSI5RJJd24cMGhnii09yVPedKK-CYlZK04vNQqDZA51UNGJL4-nPSALpHLEdohVaRKfjm9Lb","AREajf0OH2WBbYLd3t-9FdyegPnAhqIe5AGt0nrJ1X6pp8HzztdyaKXlwyEc4A6jpY4U6FkjDbXBvJEfpy_EqZDPoZdSr6r-TrdauKF210YT-C9GnjicbQ","ARGbnAMbJpCSWqgUi4_LhgAq2QbVUYsQir4X0b6TX-papx6qaKlOAJj4T9S1Z0Bx2BskDo8UvLlkVIAsGxJQW3yhCchIngLIyl96yLILIAoH","ARFcOTkXU6O8tSGWizPGYxMB0SgSxZV7o0y2ZsriGXQV4N4ElMIadDX8M2aQTrgpoRWsmpw_V8iTMKwcHRazPrNNQvSEWKkZgq0ucFQvaoU","ARGGLADrZDE67ssyL3G44HV102N7N_D5t6BuakX0Z5DOURGLVhNl3HcW_poj_wygqT81g8Lc54cvQvFgBOyjmOGh7eZHTiu69WNxPf77IvuR3ljCC9vq-A","AREJz3yxtjBZhp7L1xwiywd_ivZ6PgGMOnHhUaJo0g6fb3jRbgUAQioHogYHR1Zelps4mOVA9A81Q0RKHGGj359S6ZZY0HaRGa8u5LcO28sJG_Z4MUjRTA","ARHkRTWmT0_sWTEqA2iwGlShnfgMGN-5BdJ_8vjTrq3pWqiOxuFNRyA8wwyfErVwk-lNJ8LWjDuZZXcyFQs6XNjCfeHZMq3X9ByAX8wJlw","ARFRpc1pUZ9f64y0i_Isl_fRsgEiiDXhsvyNSFyK9bT66q83fs0umwrgHTRYMNXPXn9UW6eCFtUIrSDkV8qoE1xkKhiEc1-vm3upaR7N53oA","ARG7qJM8M7a38b4ACA5fr6tYReil2_BdDpiGt9tXT7GE021cE0UatNZzztOtXVt5Oj5wGSQZ3GZklYsAvU8m494QbJ6linJ0eTEkU2oOik0","ARFZjcRwn235PX1pBvZlX-lRceV1jQPgdv0SUlt1aoXR1aF63Mjj1_HzN2se9F9tjcSkOwTyagXOqadYsO9d99wgD8LQEHw98eE33xvvh_Ya","AREkUz40IDopmasguUI4RY7MjN_bjPJPZ4XV10TtIYojqZDCFTxKpH-DLtXB1J4_8lehbJD9u6HqLnU4v2BoEb5OaiAJ7ml5s5xadI5yG_Y","ARHF6ZLLRU_2sA2geO33i-u_MAyOXO8sR-x2mOy4yz4UlK8xqrNGGw017UacsWePM4aHdtXFgejKbtKkKvgTsUnw4ROdm9Ro_LKUn9ZfHw","AREikitbL2ePMU8JbPCJYpPkRB9iqeG--hPGV4kPCXyy1dty-b632dUYTjx5BOo9_ZEfQRTTvd1qQbspHuwA_d8KvR6etzs55MA1cmJxQvI","ARH4viEqTbyWjLZz8fjuywQ-aJ7pPXGwwIcayZ1A5e4WTRNbVtAumnp9pIsRW8uH4u7opZdbWoAzs088XHUE-xEu7IpSf_b0nAHw84Ijqq5Hn4qY3jOuFA","ARFv36BplBbeVqPmfDQ5H6VqDxx6V-aG8R3HIk9b2wTVaZ6GmuEb0raAA_qVf9njocd-UlW6FI-WVKKPkBB-FX2_PQJtmCfR44De5xN3b2u-bbtskx3DCQ"};
    String[] s6={"AREjXHMs3aLi7Gl0bxS3TW9kK3ifdXSaKkQTnC8RJbmBC3AOVSI7WlkzqhR9xiF1t2lYfCG9DX8XLDzDbr73RodW3O4yz_OBSZN9hmcfXFM","ARHsxYmwihoY7CrJes9T7TBpcaB4LuRCMFqqAMSPtvmi63M9kefJAEzfKcVQWgj1jJ8dCNLGunB8c11XaUIZYjXg-nSRRDclxKbBVhi5ijo","ARFpE8Z8XDqvhCIdp-ZezcUQjb8GZGBVQCGbh2MRSGkXJ1krnc92O7-OWXwOIm1Scij8g2LnZHCelP97D8CvOAlYBOrrGuSgXHtNn9mfrw","ARH-j7pn9jmct1js1rOsDCzxnzfu8cUcTcHXST4Z8F3sNIvIF3W8E-SG2WD1gVfZ8C3QryPQ3nWXBOZGBWeiUnZMpdggfSf0ieOoLk0","ARHh4orSu58VX7iNwLWspVDLpoEGnBqt-4ioBnGxn43mTNOmS-MxYxX6GRKrB61nVrTZ_R3czo_vJ_1b_DYrQ9rwkPl2-is72B-s66f2AaU","ARER6islH2o_WCwbK2M9h6c1LF6viF5KzS527TRmFZifNBJNNlgcMIkHNBoC2irR-4Ix857buvyqxlMKnosowhhTqHCyqfiV9U8u_ImhzU5X","AREc5_F_9MdEkoGGd0A9143crUdTuJf_TipwifNpguc_GPyRTSpnFLZJxSMFCz3VsKHD4_A09Y8u1K3q_1VqJTTHr-AOqk3fHqaItPJf","ARElzixWnbUCLycWkQDEBCHPbV7x-UGhKIdGep1xR3XpscS-VyoMDZIuuPLmbplDYaEvBC6-oL2uslvrESTJ2-7o34l_Uw2Yr5PYLA","ARELDE02u0WTQPBhlIAfdlD28imJL9v0YITiz8QE65Ya3Oov_nKNGlth7VeIaoQo8FXMBzWK-Cip5VJQzyuEQqDuyUyVPm05r7YEXeJM","ARHlVBA9nRT0T7MCBKHMp20ZgeeITE8yisHltRtd-pFucxu5swg9WjdouBg0cWtoGEE-KtwacDeQQ41N8zvH1cfU7BQjoLbwcs0f-UvtbrY","ARGr0sj-IHnsaaSV_VonPZ4WwrSKJR-C8ojxWo6dOgv9q16rpDwokQjFYYeSJ7WCOFchzc9RAInn49xb0EL_nGr_cEK0NaFg1r9BqEiv","ARE5uJbi80JvpBeURVT_xkrG3mPh757sNyzh-su7G9ycuXvAU_1_u4F22SjJrr4_3jQJbJ47kowa6TbadYhBkty8GEX0EgbGRbOEVZgp","ARFDvOGoXgqiLLi9iDwA8FfuDJMi-i4Zta8nr9HN61NmOp8vkBgyNJ7s5J8FyzOVVsaZk4Uog4Xg71XMqblj-wDhvVw7r0pA0zuw4MQ5yg","ARHOOI3igdUamzzvj0gGM79tAwlqB9pEMfiz5o6DKwn1O2NyyGYNXIrMeJn5dWyqQiAVDrezHOyb31vYJqTiAWvWDU-dhiNJrWUkI5htlg","ARGawf1ibFjtVJ4Rf3lH2SX9AroyqnLMK8zQlrpRikVRJJ6K80hiqWtAiSEQTGri4pRpabAI0v6eP-_57YnPMXZfuk2sCI-RHmxQKsXx","ARErxbErVHgBmI2R8OPTC5wdgzSJOzPh_xtkAGjwiVhYUYsMMd1saBtqlIPijQxK9FvUDWuU3hsg2PNKzjiclY5tWPwVLzZlmO2UnEPj7OQ","AREjFHeK3IN8kNbvU2vECj9ueKqUIKjJoo-n5DLvpGG7ecioMbHK9uEzkpBWJrHKU7sCwku-RI-t0UBOPKqJwFyHhepBPO8h4FkdsIX0","ARH8LeWcY4Ms4gBHZ8MN5SJsACSO2Dn2DUy8YRpD4hv-szLlj5zAsKMxBGVTz0gRtlZISTIGfYcMwWkmJGp4RxWTA6A7D8Bg3xKmC0Vd","ARGnQXU3gPurTHIvarOczWJ6NdkEdb18mroo96nnz8y0lfS8G44zkFy_Jqw4OzRJpFY4KL2BU0f1fYUVl11u6zAL4qYxMkhhqf6FWw","AREm7wlLjGdkOafKeDu6BP9lzY8ixBLEVtFSLJnERJ_pkoX5Jwi7yn3hOsZvIe2V_Gk1svTUPAkex8PuZb6r9kLpXatb4LeQQstDyFSCbA","ARE6VBYhQblLQqqfXy0OI-LnUeCph0P-52_4wNaammUAUhAsHvfb7qX2-YdV-i4nN_jIKR1dWpBrRKszYRq713edDq50LFSMR0EsRJEe","ARECYzPd7DPOHxTTcIuNaJLD7apLn5dWBWPqZtM8DBOYVyZ0CoG59YHCg4UxKtf5dofrUiSNqVBGi8zuPUwYYh7dPHJ4vWT3skWfIsis","AREnNE8KJH_r2cE3yzXGKD9rj4DFxdAd2rauLOvNu2kArQDX54_U0HIc5hB9iQFfc0JL3Y2cNDtXIjvpYT98m6-9a7cIReKudFubT_eFCZuSWLTB2iMC","ARF8RpG_W-GQ7-NcIfScVK2QlaK0TgUwO2SM9KgcNb8EZfknjDbGHXuooL9_ERV2NHZVH19tmrKRJMM9K0TlVvg6SPWaFVk-VRLViZLC4mAC","ARESHLIfowweirb10PBb0aslBG61s4IaKCMhBpojrHhSC6FjzoN8pmSVEzGqZQjr2-8BILLyIb6ODjXIb3Z2NnuHGFZBOgEes-D28CLhcLo","ARHR0W8uxpjDOOJJf3mIM9gBQZhSvWlYQfaFpLaQbkNeyGnVlrMmAKGFC9O0aR1jc23tz-IYNqHl0DnXhQD1c9hKkFpX1SEh8Ds3SJPGHJo","ARGOpzcSiGIzqZmwCNpkQQByaWZ-CZT2SAcVOQdq90beMR5NIGp1G9_KIJOOfYDpt7ioy6ELdW-L4XaQ5syqSJPpt83sznHPYMj1dXoIGdwB","ARHgLpu890NRfaFZudqb5gz7JlzeI8gNwrv3FO2RgF6AXuYLF7sG4l3gkiPhwUO2AscgOY-0wgEaej7qigTb2x1I9Cm6BzYKNPk","ARHina0_Yw9FM-pnQT1Zbq8wXKKhtJMZogfxr0EP7VUIv4MsOrg0n1kU8YBPUitwRHboStR75qwNHt2o9ZLCd_i233OHoeSaOGWo-aAKqTA","ARGX5EIRl1f_KbMKWZZbNilJgqRkzuLf40k2U9cEuiJSUb7wXtT2JF13_0iQ0XQLjNyEEaqHJ1U5BvgUKWXw8U-Rs-H3syv9BTD8q-NPeWQ","ARELlCg-IRE6ZSE0EoTDF3pA-lGlpVKp4iFUsrzGJFCEAmy38W7-UJ6O0oL9hBqiB4qrQZwyWSQ6Q3OQyzl8ioeIVMdnRiR879V3IaH-zyNt","AREI-dd02yOELal5AXKas5aj4m0nCyI9fiHns2tl_-Qaf5CUbMD4UC8cYMbdpH2lwjD2KxQ1H9rnVP58RldFJyPkmW6trrKORKoUbqVOU_fS8Tyw3gB3VA","ARGEM7pPcoMXwO5zkwng15yRQ87UMVP7eryjvCkDY3IYbjjAknBRvHtpgzzkMvUFtquczMJOlbzETyz8tZ_I9bSWs9wzeaejwVj9DiH0rUup","ARF8Ozr31ftQTNyMgvLH8jFmj2MtJCkjbhXUa-CbCl_dBrkD63iVOp879sTsZbUt4c5OM8OFq5Dm8wpkEyLTB7DjG8bwCfRpHRZ0Osf09tJ0","ARGn2bEpwKBr_KHflGq8yp1Ekqsr0ol-utfgPCIpH7GEzaTT9gi5LeHGG_I_6m9FMgtkgiN2ZPq6mdLDoY0-48TjElApGs9_c8knU-KrGZ0","ARHG3m7qdxSbqQ2JvAiUVJ2EbkWmJPfzCW37ykRm4yq3rltC3DSurPnHkrPgBlKTyV6Hv-v33iao1jpq0e7QzD5KSu8ftGOs2h7lt-L_VVO-","ARF2B6sgf1Fu1SQ1NsRYpkHfWg_uL3ELAX4w9JqUc-hB6-l6jK8yaM7Hi7w8ePBR1q75hBnQyjq9fyC4DSGHHZomkG_gWDkzQ51IADgLyVo","ARG7q6eMKpkJLArZs87I2OKhTfD1z_CshxhnU6Dd3npXJlk4q1L9uWWMMpYmA6wGTUW8oHOuHnMGhozHpkMdCh2_McKNMZJFYPypXmPn_kR_","ARFUSq1REiCg9uGnfhrKrTJtg__xqClzyhqBMTtf63XskljzpRgyXGignlYT5Nue7GlTmMirvYR1ogB9NPqUh-bu5jRBn9pOUpOx4HYVal_c","ARE_Xsh0kreDbsas-uZrLg_sEEN1H7tQoOvgWlcVdQ-JPFYuKvtE0ttSQA0LEvyz28Lx5ZnENUitifKSVS1PdyPgxXEBwsAG-lzxwtIFaIxK","ARH5yChDE_KyHSXxb2Rn4sk-Ei4GxJLeXopbTO4Vi7sUh594wmFmuUWz3Mp980P3-A5k9V24M3bq6PsOqz2gfR2GPys2XuN7JktlBi0fm5xS","ARGtz0X8JxN6so7KweKxMv_qU8M2lspmIvOiqjgXsriUVfjBziPNdcsWLMJ8xAtfQJkLdla8g-jKyyeF2tn9tkCsqax95JUIZdP6_YX4c5R_","ARERcGA2HYTEpKIX16ZfJ5roJTycsNNxtQrNAfouvB2YLnCDKwqGdOaOVMAGId6TMBK_dOXGrV582Y9JiWF8PR2GK3X0pFcWFo_xJ1zmAaPzrWM3NFRQ2w","ARFKbrbJGLklwaBaXa-CNFXVW7TSOH2JFZWStxmrttT8rT21SRk4r_-MO3Ih_k2PQGC5UtedkNSiLbCP2_PRsUvMwJ_ZIbHrfP7tErurCvXXP5VOYm9jOA","ARFOmpE9JljYH8Z-V9EXwQ1S8spASiMS7s8GdFAjjVx3WfMqGKctmo9zYDynDAuL-9N7kHidFCDS5QGE1tkaP-8hkkaSwaa7BalGnm_-74b16ZvjGmW_SA","ARFGWRo7oPcB97Ij9eceBFc7EZYaQVxJ5OB-fAw6arv-RHDWCqOYHN-maVU4OJbIPItOma5Kis5Vrcy0lK-q-lEPCnfB7ITK7r5-_Ja38A","ARFaGwE6FhBw3_QIShDaaN_EdgdpCFnrHRyiqg_wq5e81iPwjUohKrj1Z5-9qPZBVCGu_IIbG3GG7uqLoTooCf4Bkgl9nczRDU76WONPoTAaAtH0wV3hiQ","ARGs8X3haZQYLlnyeeeWVzUooCozWC27KBjcLjZcQxmJ3sajczZ5CU2QT8x7VEwINmdQFUNVucy-GjlQ0wghmdw-QpKA_yNWM3KUGhdezw","AREkP3nPl8PbF_5FGo4Su08IbVeQRzZ0r2DGNYfEwtHigHY1BLTxJFng1GuXYWNCBZgJe9fCL_qrFCw-sxB_EpSg_-_eSvkR-df_hI0zGg","AREdaOytTUF-479OB1WEVwgdsAXr8PKsX3c0LDgjzTuQPhgkOAkPKA82xWILkhkFgm4aKYwBcIYkt34DDglBUwZT5hNYcTDdOeSWG_1QWg","AREZ5AQvFsiOM3_TMMMepa4sb7MNBmPHSMQrHwpvijYPAUE0MhumppomFk5Sk88Z0Li3YtB--IwvT-n42M0juORSR-4hOwphq2FBhGPaDeA","ARF1r9PE2pOdvlXSlnjE0bCYzVo7EODw_vReQt3mxfAnuF-0JmQkfJ11S9jGdlFTob4vu4gw6yH1vB0_CDZl-6KusvBgVoBEezUNDBN5jvQ","ARF_FcYy3DI9P9OCJbgiXG1rxoxLB2z-5jCEMJJBQVyvTU2znmDNP2tiyilllgCkGXTHb7GnYe_ceuTTSM10B0nYn8rOn-oDI_I","AREzzof5hok4YUi3TRP4VxOfAflsnS5GVxm39qQCVFL0mWZRbNv8U_0_yPcB1GqmOY25Nycfz558-hF_hOkTQgopGtijWPzqYlNsC2vh","ARG_7d1iOB3UQwbYmuCCMPDSudvKfeWNLjPERuZ3TeyTcAvetnVsxKZlvoYTS2gSE2jrds1wyFUDi0N_IAjSAu6A-nWBCaG8-Q5Q9pmgXgs","ARExqSD8_cmvNNAPwknF7jm42Z_04n9TCNNBq8KcOE01B0E5Bpo6iHRfANgXKvbKWhjFc-w0G80DRZcqVn8iXP7i-6CgxCd0R2A58LyYMQ8","ARFZ4cRvdwcN0kx3H2zUp2dNZ5P0cXksD8nmYZIkZUepIOVfZ_H29bJXMCl3SwFyQrqD6wrouKBIt5wd0ygX4UdPfVkg0JLT2CBSbiJx","AREheIXYxIM5AnQW9hfo1t4YmRHlLqIKSeaEgVGEdzHSshrfH60_9-zGAaYnQrROuVGRFsI4pWKUkDa9HdPj6cjDUWHPsZPFE3JCcnjObRE","ARE4IpYoqMB_twaukzEY6URJzIjJN58cvipF8y1LOoSya-MG6AWyQ6P6RWlLLSZzTmbfzc_8jGTmlvebaDP4dRT0BNetUIx77xF1cEM","ARHbSlkQ6o4eD4S0e54Ivhhxd36PawJIlB0Xzt4zz0tL2qNnKCO0-ky33GKVmqG411crgyWKgB2rQsEt_jZus3EdY5rpNwbINRZLqgZ3ilY","ARHuCKX7FqqNhvbCK-Y2bhPn6eJEe8eSqnU4_vDZPgaryIH_UCy-j0u1qEvbSCsXlOspa6jr7Rgpg0wh0K5fpdHxpiExQiiXLmYW9XFXIQ","ARH6mE3t_gef-N8PZRJfnDtGF-aNI3P9691aT1CI8RSK6ag8DDPxcHkX1T_eLK_9Julf9AwIgSQ2qxeJHVPG8aoY1yRv5bQDX-plqrBfpw","ARGtbsExvA6vbMiYFu6e5AuLFRTtT_tZIM2DKs5gOY6sJBMQTR9JVaCvMqnZMqhLKcleSXlUgWgfKAUUNQn3eWSHlFXvyuqSH3jJ3jZreBnE2FW3Gz3hjA","ARFMW0KmdpXFMKdHAThkVMlJeT6pYmzztVAZhXqkFTDp7QLJFGKlueH0QWVDebm6SFrVMbd6aFLcgPhLhDax_02_8mKWcBNVy0RO1vXsfQYj","AREIx_WHqUwX6qgVs0WA0fHIlrwU-0kS7Ir7g5lCcCRoD7YErURkd2Qg-gdrtVNlO2YF3OqBes10mDAWiWhL7V22VotJlzC4CTqTnUhgYZcA","AREzv1weHJHk4rmwQ4rf71UBEl8a9WsIck9UL1slrNz_DWUC-VPNLYan3D3mEbOZKCwv1YYwNTaulq0QVNr_7lB_nOri-bFtnleeU9AC73k","ARGOot_pY3MJfu75j3eDDGbd07Dc9u6MLGS7IYoiWrj38tBedIPMEtUZio9Q7nByYKsCfKkqSNZUF_NrvV4UcnNhvZdim0ROTv8hpzMPkw","ARHGJXk7RBPJVZ8OyMHfFLfhWoU8pyV0jlYzAPm8iN6-MoUWiWz8Hl-Akj8d6eVqYg-Fw4UwbxqcQeDSEqH_wUyP7BgG4t9HYntESZ6j6U0G","ARFcwZe8500a1_rzckMoBAtX9PwI_FPOsvUxhuVL56sRAPDOIC6poFObYsSJHtkX5R-E0z_5JkBBT6edCKYZzf0nL3OsvvcfQ3lgq5JZHjo","ARH5FI3QI1J2MLWhvMFCTl9taQnCvAbkTSltA-0yeIsmGeeRKSrgZGiRuc80EN-pEc9q0LZlwLM0vpOAzIwbeHVXrdMNOT9P0sxBFOKqPA","AREwRGZxcEJEfqPcKnIvq-pyGACdH9deKfGfdLH0gK-1Wb0-PjEYKcFQ8pQLTskyEfieNm2Vk8Z-6rM3pWCiUweB_g4srqwnn1fzp2meJg","ARHoUwwsGeLfBf1lB6gsJd2X3vtvo7ZSiCJW60jWM8KHMf4JSjnxGAP11GIhq541cmkDnAjyRspk3htYICXuOvMlEcgExjy1ewzWLfJfMpSz","ARGk1kwjzEmmU3jCYWcBlyeHwyvdRJeQ8_CQpX7M6-Hi6wYGif5LMX7h5AWjkhwAtE9uOR7EMWQkk2frXQj7Wd_Wt2qDFsrvd3ALClfnRIA","ARHvsR5KmDrpVXUtP-lWV-gTCXgxuJOqcI1_McJ7xIFRgBEzQUnvpocMDWkMttvRjbVsdRxaMh2SauN6iU2pBWfpAZ7kwGbRiH1FZUE8_Q","ARHtx4WHLbj5V5wpsLPQmIP_qXNtizKS9c2itKbuLkouSmnW-5-uaEUXX42RXTFDtzGLRb17VziN4fFPNoncl1YYF_2aYY4c0eXlztyGw6Q","ARHPchoDfaoiipuNOskrWnGlS_1DqSwCb5okuQQGfudCmshNje6AlVlb_IIPdky4b8Kk-XE5dI2H-Evj2_eT5bx3CFybhS93qeJ4-UaU0oH4eUKRNOYKEA","ARG1v_qzB10LONTzDeb_50QhmPrlw0deX-dUSxcon9u_531XvgX2MxQBP6R5X1VnFOh2Dclyxl1Zx0-183IdHKyK10K0Ss5DaNX73VyKM0de2ttSlAux","ARFBo6aVv-STiYMj0iMFuZeicBwpOVmFPOxY2hdKv7haW0F_0aq1cREZ715_rcmZLOyF1itmImixwjk9VgL_5vc-S9W1AqcqcCaKgRzHeURMvEGMe7-H","ARFmbrZ1jdgB4vKLW55yYlOe5YGfgBn7Tf5N9wmHtcqFaXXmkjX1wW1S3K9Jk2uqzGy2q7mhc_NI9ZdZdoxkrTJQzxIoW8KhGnfNBbKlI1U","ARGW1eaYqO82UVa6AwhwUOhli5nAy3xYopNEh9pXPmZ4amj6yDONYxCAR2HztBKR8NNVcf4tjj3mUTsiYrRKJ7N8VzunDaE3tatZejY50glvZfkZhR0ZwA","ARG2l4r0i7jzLozSxNI3RqjFkRzRvGemBfCTmdjStlbvQ0jD4KhUTlgBWFVl-imDyuQRMYrUaz39orRXoSEWmccSCu4yQbFKMyoR8ksw5w52","ARFK8sgPb86EwU_uYGFhFvcs_dHwD22ky4pERvg_YftaULuWi0o8iaaMy4hSVnASbJ2lAJt3fUgSUgGnohmHFw6fmsrMl629icEFhgb0A-g","AREXNE5Q4JnnZXk20NZjTni_JNXbvPYPgMwYRLEPABmm-sCNev4P24JOHm4Q2NBp9k75Z8jdRGmUL84EYmZL5T0JVice7l9nF7Qvh-XQm8oQ","ARHm8WQMA61eKy-D6SwfXyMsLG-5odt8v8AfDI7AoA-v-aLah5efw3XiuHGLmmJSttZaDvikZ0byMtTBf33lkZpcdO1qGwRDJRK-BOPyEgiuZc1Z6Bs8aA","ARFvMpLWyQNmISuyVbKH1GsiaoppZ8WAd4Z92XsldCkTxdhYtRXAaXGk9NlF-QWo16ZqqtP8l86GK2zG_1s6M5n5fT-Y25KsBkcvIe5CGlYD","ARHWKaQAp7AOkKrUFOVJv3FOrt0Or_yTB7aBiv2KKA2_UQZIejiiAECoQWD6RBTr73hq5HdNCNvp9N-f3RDv00xqa0U6b_7t3DO9mw","AREsnvpajC4cxsipQDcva38_tD2-IQW3PisWD0JHOWCJ135ygdTLNu0aWTQW08t1wWjvQGU_bW6iz891Yh3faHVOhKiIJXfGflEk6rbMdY4v","AREeKnsbQasW0X7ruQ4NjYjTdPMXGFZckyJMjRehG_MLuOzTvvQpUK7mE1Y5xfqhVYDKKPPjCn6JiRm_wQPIK0rkgaArLRgbrOEP9_w3RvlS","ARFQ8V0Uk9Glq6EIQk096x0i84jGyVPu--qZhBPdnF2G12crt9spNcYC1NJDe--IarD99BxH15hjfrvLC_nVCGp3LNxCFsP_IPJs8aBWNIDK","AREwJWJS_n2ZNDMJCat4p1H5YW33brkWw3o_-N5J8s9mKVM1vFy1PEL02kmJoKQ_b4zJp7zq9wlGnryZARPI14I0EltOpQTZUgYPhixGh2JS","ARHJh-BqQKTQoaMFw4KqVqcUJCdz-A_kx81fHBEUXVXGDxXnUVn51ayr5M_Vf2EMBl7hVgur4uLmw4dPeRXd9CypkutPnXOOqVIEjnzj8Vft","ARFQnnAtWVwWWAh8jw-WyIoCPGNmzOVt6atvIv8HqOUTgs-D8otIjTwWl444fml859S6Xi9c0JYFG3T_PGcGXOqGeGi6U1yRL9bKWQuiASQ","ARGcDjGnJBuE0fhT6eY_82Q5zirkFfujI86C0dlH4I57YJJ5SWvV9WhJ_bIfNyRMi0gcThrr2YN1uT-KFGBWNGlTmO6bDaeLIwGGLMM3SYLO","AREs8opJDzQ74V0tvGRCQQcGhhGyEzJij76oqqo0wQf0vOpm0PgjefSja9XiVi1NCfpTFsGYUvCHPCTWYRpJKOU_J7pi74Kq8lG46di1JSBv","ARHrniq8EzUUSy8lzgjMzpPkS17Y40c-3sEeLfGlifprnosUo1TqreNi2cou7dV-nHYMuj8G8FTfUsu5bCgIoP2KWFWqlNInLo_Aqg","ARET3KltIstRQsPleUbpUVkzLLVe9_Wk68TjwLCC1jOCeQ8QG6zS1b-N-vWx1akvAN97lbF6k6NBQJEiDQXXV6_kQu_L5BrdyyhpHF1ZDcN1","ARG2NwIcfadm6K9zLVTTedeE41C5FoNwgUDBC9WGWIACzUg_mAjgZ8UKRgIeYrEa6rYlLz2OjyHWbtVx4weXGLqpEzLn0txtE-eDradr0A","ARFgcrepGdDcodgSvk3qW3kGtAZExYCRyTPbB49zsok_XvP3ojaY6daNSfMUxxNeM9gjX_HnvoScZz1PLxajQJX-iSstYpA4IalqmOKsmknpkRm4cxuC5w","ARHOOxZQxR0eyqqbBT7uOhhlSMPx5tHmqLTue0EyMoaYMbjQUbgwpgqG3ZIydat05-JD-6tN4mBgCd7xxcNRCxPVdolezVJaII-Egdc","AREgtwY6iNAKu-Sx6jCerULFEAVFcZuAYc3dPbbWqAT3rcJ7WUocQ6RyD5U00xGP9xLlhoOZqhCC45PjMnQbIWF06_dJ4dCqGj8fyAXlKJA","ARGpNcBMHyowYoo1MxZynA0F9ydAmzBH2l4bYijhgZNu6F4OcpqzEw8wYF1ohmRn-6W-kbC9bg1ouMLLBHkrSQkvnr0XG6S5yTGL7_dNmFy8Xp2Q--wLbg","ARFHKl06PSP-uoCleGSQ_YZMMDwjmv_Vw9FNeX5SYMZaQw97Vw3CPiXdylrwETGz01ye599m-iyfk8eQ55QhRrzqfwhT6Md7Fq1brD5Fas0","ARF-o3AZ6m7SZQmjixvJow0uwcWVHyqhk8xns8EvdtVhH1GaiKXSSuH-9gCKLwgECDMPlNhGJvcl__4RjsoSZPKkOZWRjhlk9J8jVJAnlpc","ARGWh_O8EIOKHjWdYm0zdMdnsiK1HwmyrCskgLQENE_A_TLXXKAi9Tp5BAcmfzSNWQrYrHlQ_-SH4AY_mdYvhTVYlztOxLDFKBNJI7sg1d4ZNS2N7J1b","ARE8gf6BFwCcxoVEOKqJ4QQtP_D03fm-DghtsOsQn4w24HbuHLu8UB0r2R75a4uyA0t6rgoiksMP82TxM86RB5MwyAtqmklhOBh5vtRDEtUEIMwe4G7R","ARF8GkHALsVxkSLNv7jv36X3qMqdpFgpt7-f9hsIeAzcCWB8OGfZwOJKw6UMIAELykUu4T76fYZNSE67lpPgAnY0C6EUarwjj-JzVeUxDofuvmVII-w","AREDndXtiwJYktGCT6IAtqT9ZRfdPuEjv_kgYnQ74q--N9T9e3KPEtFjOfg3d0lxldWGVpBgVu7df7RQdeQ68j3wFWOmsJmW5Dh0z-eekLOYZDRWlg","ARF77lHSSjUQDnY9-kGsFqEpljmRsI6jnUhAk_8HxcJ2HIkcA5sJGRoEb04hgIFtWYG-T98klUjHpDwloVxdu07LXHel6VZMF2-KLdxCaF_W7TmxJP4","ARFYqRczH1iIj3kn3UdmUf1YFWU3o6cHN3D-eE61g6W2uLT2pqXyNnoUT88XpPLBqyvmIK6DsGv5aeNSD3KL6ojjMh8YNyj1a9XbSPxbgVuvgGQC","ARFgsFUhHgeRT0AZzS9u_7NIN_ZFRLZQfKqgp9E4YaWmWDpKqVhbbvQ61G55OeC2wegm94JZ8I6qLy5kQ-stfGY49fyTcdgi095hoGLNo_U","ARF2oEcmrXuOf9P6RR_06N8lCu0UPFcJgav3Tr7bDsffvca0ZpgQi7w0GnsBWkdxp1eUvVgD-KXCW4KS0nR_D0Cvs-twJ5f7PQWIyasEWa_rSWx_RmNRjA","ARHSxw9IQt-J_qovostKnwgEge3Wx3BVyx4wS1lkBE_oBN0H6Qm_Fo7lc1MxGVzgLfJ6eF0UQyJ3lw45e-oF_BXT6gqqu2TJwz8ND5FdmFO0h6pYRw","AREX3cu0Z2ZkIPCE5Xthgc0yX_5LJnLYLust8Wp-t36bXt2OT8WQG4VAQ22SzLARqq3X_puUGwfeBBBbhHviQHQD3plepui2s2zdtpIg7U3remfrKA","AREoum2-fzj90O_Om00aMTgLScH-Es3tW6-r3Ue8svq_TMGwv2EgsAnmf6xZMi3LOcL5_vs84r85gGeI32mnWPGDUfNFb2CDGKjvDkLNtxk","ARGZtCs1FO65xMucEtgGpOtiQB6vNv1gfBh4UgsG3md4Jp_lhQMJRj3yYoHYsZV5OOMdSb5iK20hYz_i5Qnzc2qJVWwaR-2M0yxz3Eq94HRL","ARF_PjSBuPtH4rRdl14e6LBPoU0r5vzE87sCCpD5CBVPFmSwbYaBGULYdobxV9WX3JbUJPkScrGxJUEbbGd0ks4zxL0wiC8796uJCKM","ARGtrp15Si6HJet5piKG2bAGYxvSaqxRU-dgNUW0tk4fQMAYv82uw4jdTGzGfifB7t39FFIjJ75fAYNYRYdhaqyUX7MQE7w9bU4rWQ","ARF5VnO0v3V98i9gkUPkE3dIH8ACmKChlvp5OdEpXyFy1y79x9qwkCd1MC6WmQYkRngHTQhDTjqIWZjOQVWDnBgqxUNBhKw1W8RVs42c7Vgm","ARFgzSVUjlHs0rptaGRTk34rBt5i2Q-QIM99SKku9vWp0KKatQh_7igK8--nlDHRACTswu8eY24jQnGzewRAtDnzqt8VO68baR197w","ARECjmZrXMFhIypbUZOPCxQQca0mNerqawjW403t9bt9jomM3BwiaRNZ63Ql_OsEEtmZE0szYRtNWNJJDaMI-ZGZejcJG4dMetYWZ_mISQ","ARFMkkpsPP-0OXppEEnjyFK_7ZEIZYQuBZzDXeij7XonydM7kIiljh97VspymkAYHedDmJbm-Hamu6AlRsVJlMKXlgRoBhhapHlzZN-d2w","ARFwMx3Mu0w4TxJm-BqFHkj3J_QpOFnZtS2UiFUjp_ROjVXTloKDUhupvaalA9gKTsTV_bfaDycdJ_qeUDwtxFG5yWAnT3SINuVm5a3G0IPPsPikJQ1n","ARESfZ0nsA2lad8SRMSpZ80hJtBc3R333B1iVcaNzwupc8uCLewMQQj63yobDbaRGvcgujGluuPLVz_L14NKfOj5QY4WnOOzEr1-CKbqbFo"};
    String[] s7={"ARF8jCBEuigwKQiI9MfydaBamztKxA-pnZgGDF71aVZyrWANSdJhONBwVTye-dKH596ZsZE3-Qm2jHJQlgIEZGT8_6nO0A5PahpoPMWL","ARE1C6jEiclJxSgwy6JwfKEd0P688yqJOuU8IBLJwqM3lYa9LCibEMcL-Yo7yN-HiIh82ux_WqIehcTK377Ag4aubkXUek0RgH4cqd1s","ARGbonyz3wjM1qIHEO-zLSVjEIC0FFDCZnWXk0f6gpIn41jrqJmlHJXcNeU8NGYZ3PtcRsmX_wO-iT0h7WfzhjR_zNXNCzhReNFWs34l","ARFORYjaNU08CDL-8qzSMxWKCeDHzXOi8FUIMgwT-TvCjpdN_DGBSK02j0ciJ_bh8KwjCkyAhhKjrruvHQKRs_0Mz7spz1PLqBeqKGPI27w","ARFe5wWTUKVitp-RHEZUVCOwRY7wCWE5VeiMrvwTpVSTJrgtRd99tVkUHxMt8FfT4zRvgYN6ii4pPgw6Km3pzo2k4kkmA26v21gfQmv7Fw","AREFxlqKF9dx901sKOBzNf4FT8iG_vR7LZDUPlXTwRKZvX4VlbqBUsp7y7eUEcZXoylx8ss3hZVsyViNtOTcRm-xX5I_GIba5X6PGEnr6Xo","ARGzoc90-hBgjM-WDn6JeyLZlr2BTvzGAsVthtDjwy_4snu0Xoqc-MaiK5Hn0Dw7oY1EfFIgj9wdOuPxcTybGJ7VGK4uE73cw4E5TIptdzA","ARFUUnKe4Qj0TnHoa7hfbsj9v7BLpbyLTh_hjizj9xb3Y2ulAG1BwdrzGIiovJmmN2OAkkuHelBWIioJ8ImSx5_42qDGa0j8y7-D499qn48","ARHmjc_GZKDuAAqwUNhVk6fFQwVaHeJGH42qOT40GcjB0EjJf7x_ml4IhKQ6TRLg2XxuIQBaJjjaHvknSQtiwVzJfMUEkcfbUET3J7NIeJE","ARFonyBJsE5n45OzCn9CfLjnxuxxjUtmu5GJSTnP_vXC9avsV1L8oT-bQEL3lnezK8Noq5sIGVOXakHe11x9E6rhueVT0Tv8iIXutuQPaw","AREJ6zAIO0N0XYWCvidofC7DxOoRXVIQPE5Jk2pFsSR2ScALAv-aIFlhqVGvNQHM6L6dhBFI44FTz8aAmFQscDmVposMne0EN4b1avyeL6m6jI6ooo4x","ARGoJuFrkIpui-t3YHbCp0W-wL3pjfPGeV6FWE4N51k9sFbt0jpEkhUpChzC3259RDCNYNJLQEUkq9oIj7Bb4Prayw-xuN7bU9-5Tu3HlDs","AREWO1uKweYO7U6xZMjDVggoCn6PKcLHxIxRxo6Qzbq9bI2ETKCOp1BA_Foaoo0zvv0A49AAfHh0b39kGjc1vy0VhhXcKOFQlkMccc7c2w","ARFa4uBxlk9Ivudnc_wLSkKjvBm6UcRNk87Uuqa3DlIJRyVJ_y4BzYXvBHWQ6aJWfJQWv5oZR7KfrgP7AQmH2MVg6zeBU4_uduLfqbMnyEo","ARGHC1wfWGNGwdqLUISwl2ud4wF_wZrQSezPVMQZYRzJo9X03IwOtHu59lY-D-KS9PByPG1GJh2X6Nv_zYM0AfIkiMMmUAtY7TYLty8UgA","ARGHV9ixV6WJSNAP5V0goBm2KMyRCGgqHTQo-38vNrRlp39LW4NBn6KKMk3O01CZ7liJ7t__p8yDAsjZrSeu0Kah9Grtaiqkl7qPfzcLlq8","ARHIWTg3vCzHefqFSU5YEEyteV03atnhyKdkq1MbAAJ-T5L40WUTgBVaFNnn643oUh_8PCsCertdoSWEJekTvnIMpmHG9Kf88S4ezsxyBXw","ARHbCP84lPX2BAYBl2LKUX2iY2_D9oI5merDul5ZhsWwCF7Dgm2ulre73ZW5vblXhxhrXJGML8MmP6BFY8Cq7z-lucsMy8SqOm5G5Q2g","AREbVK0J0l0PPyUCKMoPWK1_qxty0ASVVA-4auhgfyUODJoxdRvo0qqpNNqrkYIbs3KdQsb4y1AX969C9YRCSJTYSo4-1HHdUB3qIKS-xOw","ARFdHUT8Sa4f3ZfeP8rfjMB75DE0rn4Eu0N4ByOFPVjRemUS3m9UZJZ3gunRNvPDEcKAzV1s1orM5fHjgaN7Ya6pnJKNPpYky6CJQw","ARGtSltVFUAnoAH6q3g-Om8Il5Saftv_hJ14LSBP2vVZZCtHxxKey0ocGu24rOQB4Fu_vc-RhBQMFRWrDQTvkFy49Ci_yNOnJSE73w","ARFdpvTj6G8XihjzrQwE0nsuCmE9Qk1X6OKjccWTHiTReP2VcvO8zVJT4LEMA7dGAjdIcsBaAPqJvKN73TZsxOLPu6Fjvy45Zx9U_A","AREXr6-CM8ZdgzdHiGxeCT-Uk9z5S7CnhJmVTut6xKHDb9_TW79_A1d14HLyi0XC6AHG7VGYwcXTlH6DFu38tYfijvS78RNatCU56DwcQ0-Zt27SgMGyRg","ARFrTJoQ0eecVsw0yj__pkAOqa42H0bwkdwCAVekfW0kmuU3XTlH9v493teKHjjqDTyfUUEF57poLy0pNB7N9DUX0PsaAfy69WmXrJxVXGdKSZLGJXr2GQ","ARFqk5UoLyaLs12hkCJk8nHs8UBIKfDqy08HIUmj_oHLaiaVmf8N0vqyK31yyMvakXW7Th5wQuXCUVlj1v61LX-qOS7xuy1opmGkj25ld7uODvRk2JHu5Q","ARE6SOXvWpRo5U6TCKOG9SHjIoHR9Kpfcc055JxAYthjdl1Pf87i_0PAqwTiulk4yt5tj5oldfJ0J9cB5eEdVVox0BYllZcTKoxK0Yf1VwwCx1ZLeTnMjA","ARHcbG0rAty1T_GCHiezQ5HP9Gae8uRcH-Lng-gKk0Kzz7hL-a0LU8gZJomq2FwswBjL_FhFn4Px3UuW8y1cZAL2XZTQ11kMG6ofGQ","ARG7UncJ699bVqdkprI5cUuPRGjr1-YC0IVe4ghOF4H2wNmzhnceis7LtpqFRDybESSo9ldvM2Dk-Bj6dJdashCQJdzPLz2uW4unXwCFvaGh-_ki6Xg31g","ARFtQZDGERveyNMM9khJt-TDVeT8o5ZQQM1NJRqBL8uMLvgTc1ykVtUHeVDto3tW9-X0LHE05yjNX4Qa8Tku3jp9yExiK8q2n6eU2u-GEN7iVubhYrNS4Q","ARG2_rf5zbWkHRQdQ9cKWtapi57GYvvEonsiMIy5Cwnf1N5nIjSeyHWHU9UgeqkS0UcR7QOJhM_JpJEs4H4dXZRbLkSJtxmPGwLVSFcoGst6PhlrWXAk","ARFNhehpq4X-l2a0_GyV8CVp5kE5ymTwgT5c0id6F7-PzMZ8c3NrB7hg7lwNY5NqH4TUv_sdfP2WKnU9DazPRXzmr8KHG0mTVo7mvlQrT_t-UL1QNnEc","ARHsUc3azSSA9hApUwHH5QLuQ_JIYpxLxCNszxckXwQGlJweo4INbNqqxI4eFf4fOt8z6-ij2HDF6I7w5R6MgOExF5LIfk7fSP-KjRg4ygE3-ZH9HbZT","ARFhhlx2TIZ7NHH9m3OCZXMYlN55V4nL_7TJZeKZjge4xIgJwnadJ4s1G0gtrY63cvwIIukNGrpcEd4jBCsA43pckJDfyeHOC32pSHKZtg","ARFR_N_J7PuWhYQXGUwUd8RNPUlq8r1kjUWEATbmKBbVR-kP61-6q6EMSPiEMleLTUr_CPlfHOM7sNhHgJI2II8jPF2M8rtowu6p5Xr7IQ","ARFejt-RFk26uJFYgo3BaUAl9ugzcGGw-biI59oLmSRokWVQeJucAwPECm6MjCC4wbAnjCnwB6qG6HOF2sAk9w5OCyUA2GITamI32LU","ARHQvTQUIC7py20oywFIpKTCBazMw7aZwa8Oe0hhUwe99N1nsXwXy2mTgS6WkmPZmtBkJvusy6Ga219VFRObUs7uhdvftTZTaCjelTsaCXE","AREhgzJW39xitc0L1K1LBFwXFg0arbS2mdOIw-cJevkry3NDXPSE1ZXwwX354NhG7qcVSBUPYFbhfBzYaqihk_npmPpHfoi772mypkSk5FW7bR92biUwsg","AREofO5FSP-Lc4BQKrGXs2Iys6H9JPwFIoXTJaelReANhVevzuboQbMI28z5_jPJl32SO7_IDuJPXoVXuviQi52yMNo3_E5k1j1N-67dvWoC","ARHjNEzBx0kXbVv7wWd14B0bv48e1zJmhgym8xIctQdWXEl4bnmerqSLmXwRtfqSd4a4VSbVYGgF22c9bknv6f_MfrxiKitp-XOfIGB04YM","ARHrbrUrt-Qg8VqvGOIO1WoGcjSVBxBKfrjf5eiv3CaC7XktIuoJ6hLzxk5zoa-qPHsGvl0ai4J9xuVq0ZrvEn3rCz_ydyjIy5eiLoP2ioI","ARHaW-0YlkYsDwgovr2U5pIj1iPdfaCp22h6FGwhshszfraF72HYARMkTsSzfZxvAqjx29nBo_YjxLX7VoK0EZ9UoCsMYKdVLUtDQjytY0I","ARG8o-GHEVc0aCRJeJFiIkiCd5RyliGj90JtIUtOtJloteYfaLza6ZaHCB8nGnauSjt46OYDcNz5_Hmk7P535_-tHNp2N_qKIDg577V8bwnagZpi2CiGFQ","ARF1QXicMEPClPj9b253DRlKGjHd1p_wUB1n0FUM86J1nGHy9C2H9uvtjt-SgQaT2BQ4GK4FE4VGQWqYcJj-oGfYCi4a1n_FqlyF-3O76CU","ARFWMCvvQ3tjL1V_83cgevJyCTezeaJw_lVTBXt2tQLjuaqXRYsjce_sk0VeY4nsrM1yV-GDtudQjHEfc7roMlfKb5_Dg4ImgO9oc_Y","ARE4wL5CgarvZQV4eh4EZUW-Nip4sOzaXiLOLeY-FKvca-aKgVBdVzfvydLXgv_TIC_PxCz_pqK_16FfHERPCBJTejnpfv6dFw9ebJqj","ARGe-AbePIuiGtzJT3Pmyp8BhMI7jz66EK0_RfjNr8y64o9tXzkXTs8cyCHebcqBNaEE2uT7pvWUj7Qs4EzyTVAMleriMUJUK0WJRV9E","ARHX_ZzXID8L0mJ4KQjDsastpWB1Ym-LpvjJagJteTWE81XFjy5LMltF6s1vr0_agEdJOYAlLTX_oaDmVY194X4Pl30L57nLQwyb-51yEt8AADF_0VKhBA","ARFOAndp5ug7IVoh9Iz6Gq_ZTaHSpdQ4cnjPh7uXukdFQPKRV_Bgt9YzafwnELczKOYL61tUJ9R_AyKXEGnGLL30cUpI2zlbFWi6gymiUak","ARHxhd74ezZokKjRH0Oxujrwp3wpFkzRl5_FiwhcgqXqMRX1zZ6F0xl8O0Cn5HhuV-Onk4OgTxhTqfdmMF8otMns543zjLRtKf8B_7PV","ARGsKB3bYy87YU1MbUHwdW22zuFAiuOBpp59NZmilBJjalg4CfMvwseul0f-sp1WKbRu4wVroUcSft8oMKKWogh5uZcT_6bUInlUzfsq7Fo","ARHLUji0MRzzbbENjXFq_dD0M6qZzEgqQ4-JoBvN1fpiyWGUIwDSp6VdFED7ebEK3KydyKY3x-f_SY9TSNOandIlQwvv-XC1xPZw6EYyGbI","ARGcD29_Z87aZ9eB_2eYtboCBAXQ7pLWd4guelBOoK7qvJOH1E35A5IayMCnEFZ8v33orihTZqK-dNOLsC2PoKxd8qq41_Msoy1nyNaR","ARFEMPvKUwl4gcD3ym3jGaI_aMLsU8eTXGmC1xNkWostd3hjO_j_CBffghAuq1bksnCmsauVSEK3IcwzsKWB_w-nz8N7g23wdLU7hA","ARH3YGCUb2EV4OmOPa2A60MxBPFEBmDFmNWXXHNkcLb5bT-HwWSBTnzuwSexlyGpY2idW18PHjZ7NwO6caH-ncXYzyyP4_4bGEf1WJYXg7B0emljXS9p","AREyHGYgJkdnjfJUKO-hvzvV_fcxeL6V_ofob6y8DZTeR1mNV8oFiIcPQ8oEQQxTdxeR_qJToiscql_wbxMzybV8HSHybsGX5gout-6EDg","ARFNyoy0PB9xH9Dpz82cLbuon7j6LWaz6kzZq6ezp0-8eTnRWDQ6jJhRPB2ZAZ9ormAD2kGwSpaVQf1Styh5D5cD_I05pv8FEi3A1HA_2Qs","AREa4Ik5Fz5igJYOrIJZo56CY20T-KyPcQ0J77va0GEpLetA2_JObHB-FAHLVDHcJJlIc17vsqU6rlUN3MrF_3isf6AyeqPzniwD293mcbDphCcP5oz0Vw","ARFAFq7Ab1TxmGHM4X3uSvubEB8vfC-QNeh2j7-t1cGMdxV4C8IR6r3tawtAl--nwf5vTSrznDQkdCKug0MxVQbJgHEYulD7__Z6HZwLMVE","ARG9VSsum16-lNTNdaybIEEV3IcAqgAKyLDBTfZUTauK-lSGl27fSW3JjLn-8h-O3Nnfcb_XTJt7aZdCt4attmYdKt3ZEcQNcRpEWg91QShmhaoRrOZpBg","ARElqPcARyGthuhW0_ZO4bz-q3emGa0rh0ygs8VV6fV8fQ-kBiwqyaF1lfuuReRbDcem_KacsgRgLvkHbsEqAPxnzhreZwVPrXZyNKkJ","ARH_25z33aKz42SHjf61AeO1KSEFgM9lI8kpo2ZNpzEewzbxrEExWrUuYdQ5Is8focaXW43Yvz8Hs0HMRF84mQJF71pyZc5-hokyy66nag","ARG3RFT4ThHLvnIaCuBOueiPO_cDLor3DnNPo7LqvJPJYzy7iICXXfzinV9-mrNJSLxsSbw4hTdONSZBMl5LmUUn9gIAKQyUsMigdiI","ARF1_fzOfAjqMqy3z6rt2LeHzeMUZPJGLzbek2z5dVR4Me0y--pvY75ImVThdi5KDe6z_zha-Pm4HVakL_V_KqWfYnVMZa7nXJfcoClSsS4","ARG661-90OZbHQi8NK29jk5WoCTdwhCW7nw_q4gIEMAtb1-ci2XJt7f3nQX-MdoabirXHqsQl5Mo-8m9aYvXUXbv8Bl0mnoHF3kKizjN","ARF44zSWr5VO5w08lHLsoBVoaLUEchrV_3fOo9cAS3aUm31I7p7OOoAp7jEZsuQ8hOLybawj5lKcbbBK0bX9Yf2PPuCDFKyqKSZSWfq_X0Y","ARELsdGxDOfpn049v8Wwp9cDmDYIm6G02Iuzp6l9-JgtQ5aDeyFmsh40Qm3fzhaZWIeCf68M6oxzovT_mGvEh5l01sRNCcaG-4QnfDWHJZ4","ARGXDbvJjhLAeDZ4q0bKrdIsHfjAaXzNajo0JP-rrk5I3rfPvuPROlioFcPri1R0xwOImeA_RMj40_gNJnuVBVtifbAydKHv2JU6TQ","ARFO8I0hh0COZyg0nrY6vb67Xrw_YfsyvdXwu8o7LEUXYiBnGfMk_imaFn7pojh8VJafaRR3M2keWcVSph8svLjAglpXoakcsV8WiHGTN1Eo","ARGN17dXl7z7xm_RAUhILm1OqXuD1_q3gbbT0qoj06tjPYJXbirrtKUshefSj04qyxgsWuo2AwEUcHzNqA_zHSEyyEmxjMDnL96H-A","ARHz33hztj-Tx3Hb06vFx1tPLmlZNqwOQNvIhWjavyLdYAd8uuIR8KSIiMQbD7Jvon94GhcyOLUxpjv1G1iFNiQ-RI7QbF6Bcjmxf9s","ARHr-X0FsiC0ooUqG2JA5u7-_LDdyWGZ0_yhKuM1HmGzTtOlV1FAKRRoxlIgQU6Czsywb1yECk2avaw3Sb9XIBr6S5vwoK5CLDNXC6dFMwE","ARFgnS-BeOHLSji6yP1n4Ynf3Kdj3LPN_Oozi9w4bY1Pgc0jaj_2Lj9XMVsOLGZPX4gqLGSIS-qksCZbxUCVMWrUY_7VCySTpzsh9Nzfu-axyFBKTjQdNg","AREsotY9RjxhD9fugKMsxG5385rLKaanrydp9a-r3gF7OBBB8cA1qsul4v9nI0-NRzCaoRptOgO25C4hRkuz8Mbn6xGYCRHSNOWwLSjlqg","ARHEaW5WRSNl1fxMPHf-ha0_rORnH5kk-67tzhj2D-nmQFGCniXpOzKGmn3dQWZRxIAK5j3XvLUypCFjkoZaMfNd9sZKALxG1k5mcodNfw","ARFvdRlCivBKlr0CclCVeGyWza8JCwivXacZzH7sFkaZS5-8aGhVToyTaFYpupRv1ybsrdSjKdgbndsAVPV_kUgisBdfs_hLADhZL69P5w","ARHaPStOfiD4MbXu3Hrp63QppeBpSZMuZCLOzab8wes4G9IlBdffsuqXrg9JbPItYrmp-DQp5TEcKSmER3T0OggLf8sz4t-Bb_g7qSKo_Us","ARHKm9MzohYYvGQ_V8HakDKAfihodpOmjuS6ageW5A3ejg--EqMGhkrbNpNrCMygipIBvwibUdVgA2nK-FYye9yb4-PuQpm3nD6T0lCvA0i5","ARFKFS9K8op7VZbbSVDcU1RA8K_ELClicPjrQ1nrtdAxr8vpIanNKQ9hTMPWlfv5pBaFmwTPPNG5PUJY02Dy6ArqW7m71xmwUI9eKr9iNgO1","ARFATilDjLi1M74Bnl1BHxRUeSrQsyXhi9CZ-wUNXGEMQ_fv06hPuNzYpaqIdwVTTovQz7CL5Qz_oSEh_JBCUAkhfJ-LdxunCxhZ45AVKLs","ARHy47tZCdT2wTrWytNXVrsxa5X45NjpZWpJ5lywHhbwy-PzbQU9Scslm90HyhpRP8fVDdDB_htEK78kBsmsW7MotIQovaCztyBzztO2EiY","ARGGjNjK4rUPyO9YWa0uJSNdrja3TABipMkNeTLpXlH-A9TfML_XNusGtBf1pPaNw1Pd4obO9snsyZwdsFE6Wx9LMKtn_17K_8NMONY3fJ5q","ARHPxZjq9Y92OrZZkyJeHN31hdAPHn7IQr1JWsEy3Cn36R6bXTawhRFkK_u9xay6-bVKuhs2cCgMsW7TCWjswIOUSzF1E2YlISZ_2YUb9R_q","AREH7p_cgiKuTEnYUf4sJHePoNruu_YWwGl-9lvL816dfU85SZPzGcZPAqLMQuB4YQPfIx4eYncwD6AldFEUOEsMP1c5JMIo80clhDfct13OBx8ZgJAiNQ","ARFltUHVlzV9wqjdcmFEoO0VRyV7Ks9RhhEjVJfuKRyfGT--TXoBlrZ1kSDC7l16DvMApPK3Q5vdvfPpXiVEs8pt2MSkxGVG_ErlR-IH","ARE12lO9W2mqZ2ZW5L05h7j6BexyRtw7gFMQJfizkH6VYKhbkyX-g5DSWf_gFtzw4fezOAJH3euCzwjv7T5tvd0AmOndra-OSmti0fy_Uyo","ARG-EUmfmDujLhP7l7k3o35WZc0Cp6juLqKc6bWMJDywf3LYDE2ryZRJ5IKOR5XYJRk_hTDnO0DIycobQ3pVsBSVFlkn39Iztw6nBSIO6BcEFNjayjeDrg","ARH8SUPCdT1pgi0CsIIRVPA2cYsatG6DsnU8dr9_srfV4drnk06DwxpaY17YwR5dqhohm333mVTOM1p7pugWnz9qrgDRL5moUTQT_q-_iSI","ARHGe0yZAEAUynl7cvyODu_2y_u47y_jg3r4wdSP8VbhoR4RF4Q8vFM2jukhcasYctdaOfMsgSHjHi7R0QAN6Saw3NDG7Ub7LEev8FigIbLV","ARHSwgZzUFE1WaT9aRqUWJEubqS9OcMTFXWKcnELHcqbCQL-vzkKXs91_MWhv56CowCpKEV1VxWbNvPln7W0TXxh2ozrhKfTMikwsXVuPA","ARGP3pl4Bo8kyqoAW0jf96RTKjHt6YAL5qk7ix29nP4c5AEcU9MyAfBX-cDhpaGzQWxJ7K1IgANb2Ne1Na0VeS7NItv2lnF2vSOc2Yjw7ok","ARFsSrr_hixLf4SchLO2-lNX8NgtryBvxiaxYfO9empe1pSasZkcpN378lxYW0lLxQD0DCeXd_AC1C8ztfTsbBppopNvkTLWvH893C0sMw","ARHz7QBeEhxxGCw2hnfjLC-6uxx1_U6dvzsEQPtpDdhM7jqwgA4g4l8EDxe9K4ilfyB4eAX5XOcnHBy00EEl2VHf1Zg7W_zWAiOhr7NI8XTI","ARHoY7Uxa7um1ozJqRoLAgNzCFbwTa9YetTP6RHi7HPBf2LaqTwe2402hamgY7b-DbDjSjyShmINmZghrrhiwby7YRGpho6PXHzhCIcHAQ","ARFH9ZOBxkLMAzKd8Z9TZoyLytmPVii3DszvW1CDp9MZdGhBAp6lWVSELTwiXjslT2Stmub7sBrADN1jJn7vLCkZ-SpeYCTPLZR94_3ayA","AREGMqq2Cs0jEKyZXNqxlXUWkTin90rhOmEZxberfzqeh5lEnEsR7NJj6pP2F0DW8fkiFuDijUErMAAIXrtrmhjpEoAqA8EX-YC3C6r8BqA","ARFJBFu8_tu-MRhdN6nSsPScmVRzZYCLqS9PHUPZ2EKlbg5dMRLtScfaQoULuc8ZYmj0cn4Ld_r0M0p1JsgD-gLohCgZrLLwE-D2XdnkVUo","ARGdfgzRVKeP9vfoHx1hvsdHeDv9zxudivsoxaYmllrW4ElmabhhedRKZ6c3AHCJWf-aIabhzMv_Ihos9yzmRBuwgNOcO9n8tyiI-gNSsls"};
    String[] s8={"AREU2sCSG72jgGtWDnvNxqrWtCATIEyHvM-TEdwGSnEuIov8w-NJITEuvh50JKXcLtUS9ACqW39287mDqn1PwqNmtL9ZGNFyjKglRpmjNQ","ARF1YlupATkWqT7Zkqn8XrWtze4qUiFMiqyqPAg3ewWFYVd8UtT9FHGHo_o-Q37Uyum7au7PlR19IGUM-pEdIx05umkgPSifgptozXGQIg"};
    String[] s9={"ARFOEJ_Fm_mi0P40E--uXQmguyoKxP_TCXTmGIsx_aVTaY7nUX4U9ZcaLIk4FhVnyW2VS4NXlcjoyyJAOrrR1xbSXuIKKKdj8m-RSG6BLx1Sy2k1RWvoXQ","AREbIZSto9MYZkry4OpSRrFwX6ZemQr7lNrn102Vp7V6TrYmHxXfDca6MSiZcZoamFzWdmHHyjdwY5r6VfvnnTmafRkVq7v7DlFzSXxuTNClAGr_kmjxcg","ARGrwo-bZ3JE8zLbYD9xjrydLIdFZthEuerkfp4UPyRt-o0iZYxc9Ak2Y5efzNvrmcYsF1-v1g9kYmTLm71bzCyHVxKhZJgTkE4Q2jDZMqDhH9_Z2nBfOg","ARF3ZP0CkQJ39csI9Ye0aVa4ucSZSW335mcjKZrQfzZNg1l_fM8eZUdP7wEZOcIWtEahkK3FQWONnfKjxq-GXwNGqKJuQUP-_BO0ZdJGH-H1WPPqcKX_ug","ARGEziRgb9pyYvEZB8PLOUgTveY_edu0HJ9Qs1TbETl2-Qw-p9sT5yHzEsB-R04t2KD8Nw4aEhTJN6vU0dxwTtvsJ02_xs2Zh4Jgtu-749H9RAH30NofQA","ARF38xpzhi4mRhiqzzLuZPQ7ZPDJ98SWW_WgzLBP-2cBjWY3N4CJJPCqK9WEiKQcLfZhOaFOq62WUP2Ar6it5RpC7k1uMYjgRfcJXNw1J1Orfdhv9nzzTA"};
    String[] s10={"ARHJ5NDzp-MIFhTxG1vLEcQfvIIiDjFj8xqRsvSPitgsqepYKCWyQpxPY3SMAhYy3TWuJpX9jyF28Vl8_QMmkSHg5k4jjKZbmZ0T6PyClQFx21WvhLKqhg"};
    String[] s11={"ARF7lrNalGeZ-XgvUPwKKvdzE7W_sY0uaUChyF4gGvV7RjkBGhtcomcFK4CpRSZeULMkp-jD-MDV6qBP4booab5mC5_HQZEzdOwCsuX1EyU"};
    String[] s12={"ARE77SWCgculxVLNdXOmOM7VpqKjcD7LoipHJARFCxhvHhuzVPNmIiF15ie22SaT56PtxUMQYbUOJKbV3dxUpkH5YMRQmx7fECCCRzY","ARFM8yB4gtpnlHo04dq0w6sfXolE9bf07iCfBQMk9UOMSBiGJVyicdS-Of2GRI4f5-irVndcaYXt7j3HSjEoumTeLPG3hpduud_Tckb2YA"};
    String[] s13={"ARFv_DsMxN4xIpJ5n4NhzOfTDhQxlOumSB3CUXm3Y2EH9I3Chc0FMXggcVO2ZA81a7ZtFKHcllMA8G69K7NWndSAjaVYxgboa7myBXZYJaxf488XnR0"};
    String[] s14={"ARFB4Hj16E--ThzwVkuiS2YwWczf-TZoLH9l9LeJGx0vabKq2ha7r6CaBM3TiuQPdiFdXgzkuhvHC3PLBnx0nkp_J1ba6--IJUuwwe1G","ARGnei7UpSRKIbFOqFpjHZQlVaspJxrK4ZhNiASRKLjQ6Ejk-VqhNojgJRewrUjHulVmh1qQb9TmqNRbMeNRTduwPyixaq1slPopdjLidg","ARERrODkOYdXGuk6cjerZwL0Zmwmqg2NwD7D2ZpeqF-NooxCAs72aft50ufWmfzHOlbcOnviybMYLNTXXvdFq4JVL_jUPBlJYR8FywQmpw_pyZvrRur6","ARFAoIoB4ZER6JD6DI6Rdc2DKIr4UtJJ2YEJyhnVPkXEOg8BwE3LaF4zygdN7h6EQ4xnLKpkhE23qyYoR-KM5tHD5Y4pF6QqtmEUcPcbeL4","ARGDS2JhAu11pJ5K43DYRf4zCZu8MGS69duGcV9paUTdXhrDuhqD5Rr8m3aBT9uVT1Gp5b3rL_3qj3m1k0IdrYpfkBj-WVFhmgdZ1g","ARHzn9xiKNRsq6QZD1cutXUY8LIkfVn6kVYNK1WF2A-x9xQ51bfs6ww3bOImvrWthObY-9lh5C6rpsuqHMNRux6fpc5PYb_eMVR7W2nCpnC2wj3-hATB2g","AREbpi0uRDmBAGNzJI4C4gq-z4rjByMHGZkcbsqpCIqugj1qC_rjn1dVz88koSvKsN5bWCbnKqGwfE2OB5_sqUaRqCnBtin4o6e1WBUDZRtYt-BV2Yan","ARE4qCnDhG_2Oin_kn6gFuTfcoW_crg7_dGLr7b5qPnE-f4Z9My_RKK-_aDdIMjJXWlTBqgUacgIYj7X0BxDnJvq8LBDfBYizvHcCfXEsHSNyDQWfoTJSw","ARHWEjgGoh4kvpm-AvovXj3sDTjqXx5NXGxRa7SURJrAeGLANgz_mDK-sH9JeDo9SnXig7ASWS9buBwh8SuLCpSnJBGzu__AGsfhS21paYOd4JgJBXAK","ARE0KXwhySzHoq9Kkfcz7F-D8mUM918fUrOb90En1ljNhPWDbQ0f1qsXeOFVpfpsc6rOPwnhyhHp-PmLBVb1vVM0zyGQ0PQIIJepVm6TKzw","AREIcXMQLo8qEeK0pZgnX5hyp8TTIym2klARfTDKIHsSdMJHquOTtrUgspzJwcmJWvNjeLTdShdtUeZ5vhTaJhudFhQWSMc6Pv_RegGIKgceX9A3e5IlBA","ARFMxNyjJwEqYXdERepL1XEY5UlOYKsIcZzZmAT4uonymJAZExFpxSgtaNlIiroJc_MENZP3GhsjQdpv5Pc5YlfQ9y5-YuqsQp7SEU3Kky0","ARHZbSf3mP-2ia5repYtf3663H-lpt_GVuz3PkfNbUqQApQYDpCTQdAx0gE39CBmTP-RZFUuUSkA-D5J1GLepX0DeRGhF0vs669BZQ9E","ARF8WkSaRKFM2ewg4lYqxWO5Ak5KrP-NfjevJW8rEUz1VPjb10dvJXWz6t-fG2T81Vfeyr_PquLJMMgQ8jlTw1pORZ_n4Lwjk1LdTDEBeeqn-tV9mmI","ARGr01k9zsoyhFV3--BXOLpO8zPZAn7o--eweD4ab3qS0M0pXkYFeeo3b1tD83Lri9jeUkSiBJFtEB2fQIwF5wSxFjuaeSbY1eDUfdxqEo5EXhZsPpGN8g","ARHxh7WMfW0CtPflHMYhPShxGYpB0Hywd-dWuvkIYGNa1awBJBzUDBZpr0lCaphNs-cCmyestKVvI43IdrMnbnGpseLWTMrR1cE-l22dno8","ARHsD_-LQ9JWkKMJkGZ_xkE6lhFNm1bMdF5v09E95HJ-sJVG52Ggr3P2xyDsBLfqN09SOrLfIE8HrI3GGSJDWUpstYIW-c219exXw0naGddNvLle4Zk","ARGbPLu4DpjAmv54asZWkET547NDm7vtUJBZmBqrlodsU2pN8CWMUf1R0mlglnp7NGDNo0mLdxAkPNrmy0rWL_FR8GSLFENzz7D04MEO2kY","ARFBpCxCcfLj_VG8Z9qKWG3kQEOdgeuxaiitGgXiZuDsirC4UIr1dWb_O25ZDyRTG0CtZNfNyl6PTIu1D2O8HU1r9GDnDCZnpdZOUmUKFw","ARF63SHneqwG4VoDJUGT4T9g0Y2qZPugsWxQ40Z1j1TbXLf6qVDq5eRAPaDVKtdsC2lFotDL4SIBpywLMT-aOehPWxnYOx6tHURFOmKnBiKxq5D_XTfxdA","ARHj8ICSgR5Fwdne5Nc6l8uvHxj3J8-3mKR90JjQ3BpCWbwof92H-wls48zuSZUCKKDVWMyI6mF4lHRLw5AJ-9Ad0jxz_O2BLenG6e3JCQ","AREUFcWCkanVN-KAOCJYcbtBiLxJ_sohvzafIjmEveiyfYfejOWDdWzYN18lidDp3_sEXzEjYl3MZtzuPAUBLjn30BhkAECROhgJ7g","ARGtJEpWxmXBCjxuiMen1z0dtaTv10tS0O79YGaD2um-wABBSssz5FsGgDTx_06JbfO-C0bvFEZORmOGxardFtjdwhVl3KxuRHFiKpNROs4"};
    String[] s15={"ARGpEdUiiXQ9NNUPERnE8nmokm5yapU5nqrG4p7xM4QLVufYJb7Acr8nT_Gr8N25Yj4VtFzvdVuP1sLmGw0xOEtUQLk1fTIcCeFSObu_FVTj","AREMecAT-Ek05R4BgcYhXLD8ud8y8SznQLYPIcaanfoT-UrlvPbzZUo1qixpCP0_YMhCrj9YLMAUoLx9nsEmdEZRyGAJ4NgKtYmaIz_rSzvd"};
    String[] s16={"ARHLNZY7nqQbCwinNf3SKVf4l_NXxK7xm5_Y7ymYbLk-j4SaQs-8LiRMs2U-l8G3sOKIDe0s_W43tIP_o5qWFr7qNQdiOeg2-iwzoyjgAeAR","ARH_ezz-UP0MOYozhZk7TYQE-9m8IEGd4h3LJpnYA7iYUSp0DM7rpxLsJFz8BAqLQZPz99ej3rlZiZYICd28fPCRbIjOAXlA0UGcg5OUU4y8WpMTf6MfBg","AREl2-uePEOpM9n2B_W7yoKyhqcbt8j4AOk5-e456rHIK9_N3dIic22aGuU4xOBBaWLMt4nyeiBXHSUjShbnUedwcRdK2y7xxzw5PuPLFs8","ARG0IFgK3TuLuaMmGq8C3pyhxo8ygl53PAeXS_Ffe-rt_6geNDw-36Gi7q6OuDF2FpLl5pEfMSct8OVWdVYaBFGFUre46N-srQUA4Q","ARHqOxI4dx6R4wOrZ-njY2VAslKQSxb9GzPo9yq1uPEsRXCzrr5mrKTvBlCP0i1tixymg004lY0y1RFYbrercPwVhAx5fH7ExKD9yYE7ka_ros2yhPn-Xw"};
    String[] s17={"ARGItE-loTbE8IEZ27syAzBd4oUMiFP08RTj6aZ66knd5I2zGf_fhEN5vXz6NaaM4U5KNchHKSKsSDFPumbHm5jKSiOrbHPeCxivcvz1ng","ARGA6x2i00dExRsLdidcnHbJ7cdtHQJaL5jXgk2q_uIUvarV5vJwk5ozQ10j5GIa_R_eaITJgIOaOscp3J-gqqNwrzHCyUfRr8VgabV7","ARE3waIp0OIxLOJrgepF6pUXOSZR6SocZXQVyl_pwmLUmUzO2iWCMQ60ia3gKvDOLUqYSPaRcyBYepwHU3zUd9ZsTkxWtb1Er4ZD-1w8EUd772C5sdWKdw"};
    String[] s18={"AREMPsh9hm4yNO-0K5GPa5qPVIA24GZ82zbjcKbMvz-aF1PxjmsRd2Awh1nPgahRgtmjLLjCR3STBflH3-6DHfyTEVhLbQRqBnLh_V8OUKI","ARFdZDYVyucuWQnCtAC41fiP59gKZdZHXNj8al4SiIUn2AzkRq7Hrx17BA9K8TTtg5FZDn07QlzNwxeEDfPpDfdRCtGBiUxuNtQ9T_WVyUvG","ARFxgiMCCmZ1IPGB5wP4E2ekvfUNhNtGwCBarHFUdximUtaQa_hYdxr8XGse8eM3FtVvKb-Wl-zixHoGykysqgx73B-HmiUDNcAV5HNrGA","ARE6IuCMBDSViutUrNBXfwkghrd6fzaqnhAOsEnsQDY4vRAIj4xq_s_lC-4vBTYcoYyYJ4VFCyjusRm4bl9Fz_vKlyrcfi1VBN4-bYcmOr8"};
    String[] s19={"ARGRvkusE-jedvzFu-e-iWYgn6XyfEXCUzznkN6MLa93asafaLFtCz7EZI1Y7EDKOUXZ6JAMrHadJAeD5XbAvDBhk4B5Vx8TTvENs5XQblcq"};
    String[] s20={"ARH88n5d8PTaqL6D1xASMv2PzRqjY8UqKZt0n1OmhFjCxbxNwQ6d02fYGpFriNnCuY87PxPAb9t1LyzymrbIUL3cUBZKZJOpahDHysBNXMY","ARHLJ9Yo48azTroh72wzbUM1UPu41grfvr2x_qVpqEod-YaW47kZiHSniUMPqASpUHi4WLCPg-rTNJTTGRJJm0gxTGhRPcIekgh3PbgMvlC3KkFb_Svd","ARH6-Xt6fuJbwJ3F27SYbYEocUHwuvveI7aeOG4wqS5RRJqcjxa0qf-SnBX_vY5chZRfBz3aTQqq9fxxjzbkGJyKE7rZXaJKGIld2YFelMux"};
    String[] s21={"ARGn3OmiX2Slky_tPL0p6Yjl7kaYveImaBM7KcIIDwZnjgKM0nyp3f0SOx6_yDPVmq3efxicWOcCpzZjDHPSWP9iLUTT2Qwg1bPuoMlluTg","AREAhiaCkC3FPFNcStL67kw0NN0Id3vL482ISKKZWcU1qnYSgFrNde0NFvqhCCyKflbFpNjgNHEpwXHG4rG9LJkgDxfYCaMLiir1L7Dae5Fd","ARHoNsBQBCVvaf4hDr_YvRBDWGQq4B_agN52J8N-5xhZmrnrX8TlQ_3MoWjxNSVM3Jv5Ps_LSLUGSBRiEe6V3iWqyVfwkCuCOQaOz_GCa7nJ","ARGr0dLBodIh5-8PLdpRaPqb3R7nf_nrnmRgHlvpWlUwK4TappFKzIJa1CI54bLW59VKRUnO7esQhhfN3SdjaDJo1dG9GWQFQuqy77xo2U6y","ARESCqI4Pku2_8o6d3kXhJ08KXPPJ46wcUaS_SuwBgcsUrZ5svzmlUQYbe92ebHlK1YTR9rdjV8nqEUB1hDrdXMtW5-h5urNZ9O2uXl8BziT"};
//	ahBzfmx1ZWUtd2FsbHktZGV2chELEgRhZmZzGICAkNzg5LwLDA|"AREFPw7JX2oiMFzOcI_X-ynWiTbF4Dz8nKKnkqNZnra9EMm6TwSx5_CmIqikoq56bdqu9AGo6ktXIl5aF-kyELK_1_u9WwNCrUjOvZro1Q4d","ARE8Rllnk9vl-pe7ljAvxkSkqsblCix2_s1tv3lIyDZGi_OzjRBBdS_zT_jI_tDK7-OWC2F0QZ_A_VnKhHBJ3fAcxQ90nWUpvQC-eiS4C6Hwb70hN95Pqg","ARFBNDW_jDfKM-IKGqNgKryPpHBWB8SkTBuez58FVaksCC1ntKAHN5GkDD7t39bACxxFFrenUZ5GtjEs2YEAqM0eZYkW7Wp4RoBJfoVWWw",
//	ahBzfmx1ZWUtd2FsbHktZGV2chELEgRhZmZzGICAkMKe4IQLDA|"ARF4QuCrujeiQmEtgr4kCTbpBPLms_wBGW89_KXxLMrdp7m2WrJrRLqNsAXVgHMqV9Wn-RJOKH2-k79o801BwUAVZ7a5mOBIFOQ3l3JOMNoK",
//	ahBzfmx1ZWUtd2FsbHktZGV2chELEgRhZmZzGICAkMKT8u0KDA|"ARGMAOOR5uyDM0gy7pGAouUZjujFkM59niYZne2cqoVKzLDUL-Mw3XTm0_J1sQcF5cuBpcqpZJmRjkO8rfdkHWLXH9xx7MsNvrV1LOMl_bcD","ARH-qoSPNxjdBsXeL_ZE96DnJdFHv3soIjljdP36hCkVguxYf29YncDX8JQPJYQ829dAaZeg5Ug7z2Y3bfaq86bcDpiDrJ-vW6_BvYU4WMs","ARGIjFxbBSmHY6gfqMTCMXBBRvs_knrKwu3bdlzFjuUrUT_e2yDCaYOm0qQRPw5Jw5KThbCCeLcijafEZIxGA3HUC5KuAg0-1KDVTUrVO7M","ARFIWAcPgdHvXdPhnilWhXlaHOSo8BAa3bqRLd7IB0JTDgWdnGoNrqFWAKje4qlEJjho_FlMrZ5fQdzH-rwvDibdUN_O3LGVU0dZmlpL8J8",
//	ahBzfmx1ZWUtd2FsbHktZGV2chELEgRhZmZzGICAoM-irrgKDA|"ARGuEqQo_Y3fteAT3YBXODeNMdNuua7L1IZLlegdN6vJ7hdfFDYivu8QSTzzKuUyXsd1mZBrc3k9-38TxhXcKhQqtXNeRRCQvryE0eIaFsugnm7YbFAwSw",
//	ahBzfmx1ZWUtd2FsbHktZGV2chELEgRhZmZzGICA4Mvwu80LDA|"ARHQ0vtg40LJu1blvWaaXPO_a-OZXdspGnWdi5xYPvKRxmsHzSYzJJlQ0kNE9aHZqu7i34lBOrQib051p7aIVMUKNacgUQASDPoVlAU_Zgo","AREMq4k2_r1iPrlpIyX32XsSDn9QTPqjWnl-Ury-nZrUZ4SjzuAdFdbSbsC9p_UO-DyAibsepL5KeITblOV7P0R4QBWyEoGdCTUuEmQ-5ww","ARHMIOnMfH97YMNR_MrtyZ3PLpGxHIvtG647i6K_-EvkmkLu9c_Vs2_CNP6NCz5qNjMh3BCfwU4TIlKkSpCcnxV6Sgp9FQThwKptonBieWvHh_LsLZPHUQ","ARFr120Tdi2XJdYoNdTB-AqDP4YPQyQ9IqZabkIdILFqENpCb9ZU70v9fyP_5LIqYynX7xEci_T6Xjaf268XQLSFBwOVDtjnJm5ZJjw0OH0","ARHN0mYA4Rs9Zx01wdmzzfabX6x0uka6bHUFVZP9BdF8CY5qhMmBeLXYYMpjQRvyJFZnnZR1CxVCAMazc_JrqtaA5-hvVsSVTx7994ADKDD4jA4Cxbtq","ARHzbsCwexsasF3XIm207_LMh3BXGmL757iyjW3heZttbfp66ian6ugnt_Jf57973Ruu3N-UMyt3XtXanf1o47KZN6jy6nGMc4be-uAgTA","AREb_dE_qXoIEOItubck2AmcO7Up56AQgEze3IW5R9KUyR2r0Clu6baSTYHjcZWIjjaXYL2qT_Et61KmntTglR-5i2i9He3ggL0iXMpRjzpxajA87ueCMg","AREFb-g3IY_fJgjYgoHobPzyM_w2XQJC8jACXI2dMlNrX9IQgsgFOQtu7I7r5PJxJn3--68VHsxxvg3-k4YW-N-5LcD8Zz1W53otraXFUi4","ARHvK6Zfj3ohS-g4PtbMQKGjpQcDwM1Ymx5cvGMfx8jI8qaBgWHj4cwlMRadTgz5hUobG3mDPyvpmn3WzJE_wcq-fmTAhAzTje2dj7BqZO4","AREIZc_OkE3gluYQF9LuZL5-aScUpBhCg7E7nB8t5LhEpWXtZXkYGrkZ9kS2Fc2jrbcHjsz1NaMYwj1FvfKp-GGGAjWIpEnsyrOaCxYxog","ARE5mHWZs03uwM4Mga3uwPb-iQVjy-JEGnoOMGfTxYotHKYvjxFeXB03Sj2Lnoy6OxLCD1koWjxmXJmiMzTnEFKce1QekB9dieotFDVoMWHV6Te69Fjc0A","ARGiUfROJpyp6iN8Uh6p55oloSoxthdBWP_gGvbLL74aOT56TaDQKpNos1YbrwmQws3mL74OQdJxbk39HKaTqFbWXnwJ8oISh9mPtdc","ARETdUeYsN5kHLpov3CuAjkbFi87cwI9g6Y88U4Z-v0-epWYn7Qgp67JaMk5ZC0E2M3-njo50Khl8UqrSV2sIilKoherQ-y6_99bMbIaQw","ARFsD5_LDbNPz6wcdTbiUQNCixKamt_5Mo0ZYZwQM7b6Bqjb-vwRzBvF0IEqT8IKRpgchlVGp3xkrGLbHXNPl2_OL3uUW7jlQZcrdYY","ARGr4XNsfnzNW0bLGn6H0-vLpNC1WKklCcsEOL8YDD4dmb_zXQkYRmzbQjI_hRIpm9i4HO0iSpzueoSLEvBATnZ2pbZAIMiuEwNBG0L_","ARFknJMSXiCdvp7w4gKffNQhqI9tTil1dyDOCTSrJvhQBfWPYkHzka1EedyouO64qcpv9l2kpjioz7A7cgXlq-7DoK24uw4sHCxJ08tS1cU","ARERwosXZ9AdKPmSIyLY5TTHaE4MKdLFVpyB7cEtmdlO_gX5wUrL38rBfICAvpFfvxbXG9Tp9xgzi-9ocMm-_tsc31a-DJeShZwhTA6ATsMJ","ARGswKDenDDZlP7-7yJCE9ifYdk0wthDFqaCVsCTeaaN8LZzBpzb2c2LcK9kKTJLHO9cWmWq8z1h_SMBjGlm_CpjgQpVusF7uzu41y0P_raj","ARHg_sOEUD8GHlscQuK1xZyT0pHjlOqlCA6-PCepFrs7hyStMMXU4Xi8ur1bw2wHHlOWgR7gOhXEmscklgfexWPYpKPkuibhbbUU-aeu_X_J0DYVoXJ7og","ARE0sndtduyvBK5nIz6_CctLK_FVcB5gJVvu7u1x1rlI9Y_5h0DH9yhXn3cFOSnvRFezRcGcmUjFyE_d0FpHOxxpMx6_6FdezUYP4w","AREOvrt-Z3vtuP7qI-2Bc1dqBqKnzlXRl1BlWqg1x4c1ucJCDcka2DrZbFkN8lLLFFIsndIFHmM_d6KIrwfdDlViRkh2aC0zVCSRzPKSMUo","AREoGygMXU2WkUabxbYz5alZ_TUc3pizcFrh2gLavLabh5cHgN9i7SiYO8-XfHZ-IAqK2styW8gx9P3jocjDmz7jGHJlezjW9B8SlDzkMqN5okowqMRolA","ARF-Qzn4MhuMdZz2Sodhkrqhc3A-uxmJQsPfQ-tl2raWy2Jryor1PlxDnCtqnobP5gO1qFBcMCvD5B3PHCZdG-osOjsDgWK4exmV9d9B-aH8","ARGwlB-Qh4c62vYIWGZaRAx8rHPyPLMtscAS-Jj8WzJAzdSVK7cPRvL7sRGDFdekfsJpgUcBy56OW8SCWYjHSOKb6Edo_jdlrlCdrSpNFuPZ","ARGsWvxb1YLPmYK2w06eXTFQ9pAYVjKbKuf4n3864gK3ulCoJ4GD-yrqOqFJUcMMDNDBx_S5rmYwBgs2eHXKZQoro42f-BhkcMiqxIoBAA","ARE6dbW9WTZb7OXKvTSQvqI5tsiFFff-fDRfNAoICb9PIoax8m7fwhB41M8O0DcynHl5qWlolAf6_YWlngLi8E0Ktdw8D8e2VAxy2OrQ1rRG","ARGLVbH0AgDqAqIAs44XYveZD01kv5xfNxdPYTqJcDjNoH2lV9rswiBaPML6hQRZTif6538pY1WIWsqAd7h6W9tj75FlT8A4wrx6GWNExJa6","ARGVaTJ4HiT5o_E5yDT2omMQf7x3Ie7fF39TPnztuJDG-o3q689UUL5CUQNPdi7iZ9Gw1KiCiM7_rBIeeYiIfag___g72rJMfFi73kw","AREQNuled_1vMJ0O2rQ6kV8THple2rgRzHRFhira06inTl2LSaeS9GfOEDb02gVhrP9FZiTUUO2oeszdmd2I1v9KRcLaX7Z87uWlIlMm1A","ARFnhwj5ALLIaSJapD983n_jdal9xdni_t1iwW-BZLZe_olJCzayO5zVDd5tvpqksMuzbRW5FW0K-c-nYMFIxFUroovv3cT2En8cTdmwz6NT","AREd4rpJCioUjX7JRgTkor-P1SVwCbQflMtaL-wxdFAZNsraaeNF0HTP7cFyc4Bmd1cjbcAPgd-Y0lNN2bOaSEohoCdOEG7249GR2pSbr9ED","ARG4AeR_5UI4SfCqDB2LVWNnR4L3w-J7yAWw2U5OqoZgLgUznXIyMOft163cEmnj88mdOiqN6Sdv5f5BGuoCKY2b1rdYIgTeZvs8CecRChBI","ARGtwC2rszpe8sYWjcHrMmxTSBEI_8wDc5TKL17YhbqGPoJsXdh1PMf86emMICmvDc33wBhRHeqqvELig2XVF25M2FIjRBoZ_eaxzP08","AREGrSym-Q3QC5tyj75SVrNfuMUqyU4RY_scivNWRHrXhYIYxLpL8VOSwc2dx1CT3kQ4N92JQUuuMTHnaDFbSrS7L-r7J1wBVsNWvlZivOY","ARF1do7-jFliYi9KGRT59l8mlQIjRRJFFNA2TIkFSISHDs2m1wOMwYhSSK8GJyuOoHw6Oj0ROkh1Z-nTl5CJiygREwLqB1xkO9X4lMwUEt-hONphLXwvhw","ARH0jVos3C3SQAWC7QR7_PC-3sQRN6RVjTmHYmzTSrikR6iXGkiJ6jdAWbVH-tO23jfbdYZv_Hd2pMQZJF1qH2dbbCaNK8sOsKRDZ7ACwA","AREgQOY718203YJXDa2eox0f9HoIXDvj4QzqYK4W9Lrgt0RSEL3u_I73nxfgNTGCXjaNWYL0NMs-3bvLpc627uF2JqLMikUkxkJ3NmRIvqA","ARFECZYBLD5x1WNXZ6pi0SFmLCcqMOEFs9jW8WPbfKfCoVIurRQYWRQNSvm1OQcAwTpIWFXlQJgvuBr4dj3f3VT0hByvBxF8MjLqqHolqE4","AREhMy3e-Y-KiDBBtzcYGlVEsp0rCZH7-WnUAN1xGdmtAc34-MidokPUDK9GMdN1QQS4xSo5N6nhVJOHmRN88GDQc92p8c4wxKDF3IZmOU8","ARGhbRUnh9H8AHybxHN33F3tjh3XPs2A7rTX8iJUBRJcvFE8STqBMOmn3JrUopxbRyU9NaiDmshdH_1Pf4xAUXjo7TdD_-CNHK8ExrvMtA","ARGjwWEkjPLhnX40d0il9pWjtn_O9k_QPsXYY1sIGPIYEgDZ9KZTAaS0kmFyblw_9aXQO1zavRL_Do6I7ewMiy30fnI-8mNSZKROUVyTAA","ARF45iQQHMhO3kVMzvLUg23-RujueVDQywifOFoYpaYS4j65UU0DX4qYmaos5e1czFkTNfameQMCX_05UQAAppN-L6z1duE9SGo6p2Ht3Q","ARGMrjhCXeq8NHK2DIQGpSVzxJ59HMegLf1uWBhJ6NZCLT3DraDkFPAtHORDRiWln4vZmcwzbms29xLAW6P3OOtYGkVvF42pDtbhCPtOSo4m","ARF7DBDUXzNH9Zd3OVCt3NYkFK1l8qZVcV8pWaOY1owPrTGACfZn_EsocOrP4AIEYPyAgK9lLKXHyAMuxHgKL7LMy83so3NheCrQuPxF0ZPG","ARHZdne7J5IGTzPyY7mZ1y5KemtoEYOHg2Wztuqu8UIxDdzzv4XE6qaK6LRWrl0vWdwk6sIy3tU-05oozYYpiIVbsIROoVb3-QWAjgep83cLJwx35IbLTw","ARFAV7HtTjBLb8W3furtBGsEDkag1OOCywpcDWBTP2klpqz0LzdWcxgSKAM3vgHWyoWtIhd115TlY7QBj7V_svSlGFncA3DeOklavEP7dYA2kki2nHl0eQ","ARGqjELrndiGFQ6OAXZG8QF3jjPg6_42aq51Olq5kEHrL2V4E1mK0I1boLaSZNnzy5-natJnTgATIWXo37Y0CU6vAs9Tt05YxbNnM-3gzQ","ARHiHE6VW76faRlnqg65ycqe7ZUVFtX4sAaPmnQmO214BBBENGypJJTicnaVWUDVQ_wA1nKqAZSs5GCiGyu0YmqDUT9SlfhXigYEnHI6fw","ARGe2cky3UElHKN1kZ3_DNgFzc6ZPj6X04s4yhRsZRrHxEU7hdScRHuq4PzfywPJb4G77z4N8Mc2R2RhhAAM5MTzKXwNIgWzAlFEbCKamv6z","AREI2xh8gDHZ_Itmc35-ZTQ3yTDhXaeuImKvNLx1O31ABpYPa1OsjGmwk1akmm7ISdmUEY0YEX-kej0PLh9kbedWmAsYjNkehbAEQtv-GPk","ARGhlGPysRn8yi8vwmvzhcNlWpmfdO-h-6HvoDzdmL2NLWO3Vsywr7V1q8KTiOuYh5GOmR8_nO1KvwR0v8jJWuXLWuuApDhbiqo5qHlcpxE","AREsZiJaol7dfrboCVQHK_58stfqJtLZfLR05yDXvzLmTuyJ-qaWQatBhQukN4xHdKZxDX32Hmnen2M6EQc84kgKQFHR9c5yvIMKn30qclvB",
//	ahBzfmx1ZWUtd2FsbHktZGV2chELEgRhZmZzGICAoLWs2tkKDA|"ARFyfIxt_XUn_T4dUAZusl-SBt78ple2eOd8oNeBV8SJUVZ5QHZnJpJTp3kHcnBoJPW83STiy-gjrhiqAbHLtKRSfCZoSW1s_9bbDlfmrgUW_BU","ARGdol_RSrulmy17uVR2aKE3gg0xSD2t6bS0vWrxX-aK3uYAPQgpR6q6jgS4gno5bxrDIPrbVR-siP7SG8xK9m4mK7OObngo0LjV6JA_hqGkOaYQMcoP","ARGWAub1u62-Z9gMSbDwzN7pCWj9LhOPr-Y0pxRRXxSGB7Bd4_6mdKqSpfz4uBZmIO8_Cm2-_uV97kL3poqu0KPGsxiXnQpEgcWxNRvLApgvEvZvzQ","ARGbbVvd8pHa027ld3Fl0OcisQ2tgkc9WsK3rk8fg8eIgO-IHwwbCVaY3tOKLbnpNatu28Lpl-aKvyKPJppLcx3Mr0bYWE1IlUJqwI-ge7c7q-d4","AREkFGS8BrJg63yCtA2K3oERcsPwDR7dTwBpG1lYt1ICp4BhOWvw40ADnz4TOAWlI7pLxWnh8_ugnuneQVpNSA17Q9X35qpI9V5ytb0ZRhg8BWbezw","ARHBXr4O1Mh6V-VFcqdSgi4SJTD6b0CHUhApi5YZg2fjczJpf34edIWCrivYHbroU6HlQMB5i7fS4PoFkQmWDos0CBFymkS7zu3OWR9oceilzhGmzMix","AREJKyf1Gy-3dhgcL-irdMGD2qePAEGbnAN37MvahbkkZRiEJPy570Q_Rzhey99Q6KtRPnMUy3Ocj2F2rsFUJE7qRq-TNYHmlHfe3-Jace9Rss7ZhJ0cjA","ARGg1A4U3d0DIhohPzV2uRseTN6oKPyDM5tY6K_lJH9Tfk61FVfRj5fkT71uFvLja--Nm-mdPpqbgnPzWH7I--_DXhzCNv4WCNRHs0j33KZwTZ4P","ARGtwBbdfcdRdxKo-Y1lTUZWI1DoUo6qS2xucMo_QbsQMt1aX06iioJTampU8DLNOrQAtGsxyEnK7pVrt7cy9X54f1O_42dAY5F3RsOUZZUzgo6e","AREuaVHWpotIQXw33Zkz61Ze2zRklZiI-0K2kFWVj-y7_M5KpxjiZRsRTLdbrJjjtscBsWQBLS_9KXa56ztcjqf_GIjWXTM_oYY2zmqcvvTueov4Ioo","ARFFT4KqHaXeuQkyTc1YD2iX8lB2Sqd8W1og1ZRq7cnBqGOL6xVHqy-j3MsLFwHfxfQrw21GHANpA6I-OZqPoLXAiaGd5eiCe3DwdmuB5owcorqQ_pU","AREOkCo6SWCy4B9j42b8HPhDA4QEez1y4g9plUpKwJ2Mfrx0gp1R_XPEihSIYOYk6TnScKzTIuJJqGm_aL0Qy43YRqZCXmiIzhpxliiFvVaNDzHteZkI","ARF8_9nIa8HKNfeMo8o0sazPso8wP5Y-GWXmUh05KLl_OQIgNuo6w_-nTOrHiTi-oTn2Q3wa5yNM7O7fwdQRUPfEWn7GAfNv5zF62G1ev5lXFTfhIKjPsw","AREGRtPFsJxoHIT7pvQ4GmyK65po_X4HtZ2G3BpV5wrKSeWWKbnX358ARBJgzhEWfaj7OjIY1eepLXHQVqLC9VZNxcMQA8l8aPJpqZvwWAh-l3hvLA","ARFB-BhybPHEFpGAsCDg0PSWn6oYC-4b-QAQBxM5AUzV_3Jc3owf_3r-VMBTtcaCI9OomYAENzwJFaDkIkm4II4g0j-76Cy2SEQlQoM","ARG8kBNjHJ-Z_gv62ExI4QkpapDLnnacw4ome6CWD_ObvLhd0i-jRZ4Rf2dihhbQsnSLJOc0kItYz3_CESMx3Z0-DMIVJRnQMqRjig4SNjex5jIPEJKAxQ","AREDbzu-_UyfHerjGpJPmKf8Feb2XG_6g77OohMT46R0jTFL1Jm4M_UWqlSfpx6LKudmaak9vfcKK8BoY-Os9cs3rPIlIWzRXbnMQGY",
//	ahBzfmx1ZWUtd2FsbHktZGV2chELEgRhZmZzGICAkIKF68MJDA|"ARHXXNqP_y0OtOeChHG6Y-jviwxej2G0Ji__lvi-432FxZF5LnKY51yMFgoLSLbNdLdktdFnQ8mH16No-cce3vwlynMZ1x21wOT3cWCCn7WF",
//	ahBzfmx1ZWUtd2FsbHktZGV2chELEgRhZmZzGICAkOSBnboIDA|"ARF1Z7YqaoArmvLNiup-LcojtE6ysi2SFxLtxC-cHJmxbPFp6IKtbfb8PvdiUkTOTWJeDSiTOIntlRPfqNTB1JNjWN4SvClhe45Eito","ARGKT_PgLPBsmzkiL_IHRuVH8cu-LC5-Smlsab6OxmIG4aHQ3WtdOVRi1jYiav4wzrxbEA5Z0B9D_ap_gfy4E2DuZ0xItNsw8dMiNqYNQOu0","AREoEeAKtveEsi9gHRnpDnLAl84ipgkNiW8CvvBwcxrsWQMGeu08damrXKchTEEQQeZWDe07ofyJj-oc_73nH1w69ztnknmOaY-bOQWSuqbGXZcauqMIbQ","ARHo49BEFWC6O5J1CIHD-BwDY2AVYJm_As1iSgeINzQUI59E-7DkmE3gN2kifeHAqyOzXCPYpomNfrTdT668_Y9-p6lDmzXb71wPjawplcwL","ARFkAsDtchnlnQyz7Npp53g_x-2QL-109ocUFr88L6TISrcdgjCpqgb6Ek-Tl5NGL9GyRE0o01MQvIRhwtJFrqNKosoy9PaFZzXcjZM","ARH74IxXR9S_10yZQwHzameLBODG6GdExNyw-aBxtrAYY5J5F2JrCJ0vxCZt-ddodPZpAyiA3AGHUc5hAVw5u6vhWvWyVVpDt74WKN3WyA","AREnb2zirpYtnZKB3lCVIuBHtnNW5LhIItrlTt5xqoYiIWMk3YUiMo-5w4Ewk6NNRRKv4PwWCUTc8oyXLxg_MwJkDIk3O3F7POMz-_JhvEc","AREUyQn1uVcb-uuSGgtDD0eqC4UZtuhh52wyEuXDEEawdrdNVMEjsWg07-GOY5eHwzOOHQ-h6gxHeUrEh8K6ayOMvyj8ulKGgg7YZnKeWog","ARFEyCNPGkHXktwEkJlHNAob9roF5wr6RJDsb2PHKx7I_W3oKkm9NoH4DKNV8f730VrcxZpRg8Gk_azBs3nIxN90bcn3B9yND3CpRN8CLm3d","ARHg0VKBOT_8BNdziaw002oY_IX3YKxbeOWiu8q9RTLkuDQYmy5_OLcD1aAh0SFGL6u8aAe5HAARFPaRWltDug-pFOchEOfgU7YrxCGguNlRbOnz3yoxTg","ARG5ZPp1tF28ZXDgh4wQjfnNtMtqAxbOdFBm6rkqigpobLJ9ERxi2Uvs7nEBVUKaBjeJxKnkl6uPOp4_0G7fMlQD4-jkBpNpY0TR2WLVsFGT","ARHc5vaTX_sp7nnGOpaxnDaplS32N83T8YuEg9u-RMIw-WOXdvX18T5367UaC8if1B5yvTtanz5JDoWAWEKwMJOCnDeJpcP9D6cHLq-HHXnU","ARFN3phvqBkxS7giuTHSuAjhrCtZ33_oJ-VKP0UpTkO8dzrwKp6RvgPoM7VyI5Y3LscEgzXw-Tkp2qP7xR45zAMWDT7c9pxssghCaQ","ARGIJz2BzPYjIhshi2zeAgVUdV24oJoT6EsDn99ZT6-6V4Yw4vJQ5zGnbBYCn0faNX1EgNa4z_ovGkLWoYANRj2VBjRzVaucs3jyI1JlOq3d56KCcrFzYg","ARHar61SLKIweSozVn3loQVPA757zcppVIRt3GCZKSBGRvbRdwAXBbHy1a_rIxTCe11Bp-urvBKpc-BUfLtZGHWQufTHNoeuEdlJHP5dALo","ARHqE--DS-8gTA5-BZ5GTupSYPE9tFLNQdvFa3zUFt_WtI7YVgB2reDRd09X8bh7ZTJBa2erPC8kvvl7ZHqI0bUzzl60Ow18DebUNnmrfOl5","ARFa8uZB-_4PHL1SqcazDJ1uBlPiS6pD2TYNuv3dPYWhxcwbJ7sggNrAoTQRDemSV0RdBIdzyvIIR7gMRHvuUKhr557hWPj55NfmJcSVDzCK","ARGqCoEIrbPo1JWYMuTy74Kf0HNZBg5zt7VYUhRd4hL10ceUUF6yCrJapPAre-mGOQw-y7izBWwhZMW9DlK4SBmBThDf4SNfPvZ6yJ6VNhsM9KdiPrPJtg","ARFUPEpcStsHdJ447eOrmHZsByQraJnOjVddK4TgCtXKWiOFIZ9WCjkvhASbxiHcNflI0uFVUjwefvwhzACFIZN8YvydPxVxK4RLR-rn5g7D","ARGn5KrDTnDRUFZj5AoeEQQY8rtupQa1Tc5quXz-0yZOy-fYTMf1E8Uaj4mBiJsZK6AtklYj6AIvqZhFimEXLC3y6fnQ3pQTClsj_Ory--E","AREpLV3mdUP8YHkGMoFs8IEg8Cv6UAUuuD45AJ8FcTr7SbxnpqWu5TEiPjeUFU126L2PA-AdbmqWbP0inEuq8BhnKOYgPXsUPSEZ48jjSY-1ZXDoQg","ARHhEfiOWM_munL-vRSI8SDm3bnf5QNhyMFmZ9pM-3_-O7XEBcQlhuLLRABwbvlwwGD6aCD4gHL-UhWAbGxZhsFb5B22MQJHzLdrwx-iat8O","ARFFZ31TBk571K7dP6ArZho3ICR5r-2q3Rt7d21USOP-Zqinvn9suTEDLUzOtdQz6qbIyG4vVNHwPokp1p2RYex672ELiKWPf39ELAXL5W8","ARFriXJUw7EWfRMS2V1FSdasIXkGSRR-NYMocd27VxPYVswCJN_W1iiWvZucwQBOF1qc3nBzZ5zDijVtFSl4GLWlWp-GGDIA7DxAdVdC3YgN","AREKjaeBGhj16wTBwoEUJy3xQa2wFQhcBTXGqeugtUGocmTVU-UjLwBncal1YWVAB_gsUtYVHcRhIc09P_1H_j4Cv3JgEKVdv0wT2COfpLkbWW3GMzk2zQ","ARGd-wsQUu5kDlgt3ZfvHs-9tqwPEmP8dPVTk1G6CF9Lu4Go0uoZyVEgDQCdQn0-3bcNJ_4S8Art4lpHAERvio5SOSRQhNNNR75KM7eob_E","ARGGz45VMm9sWImqihOF7lb8okNjDgEbs3DJzaLlsRTFK10n1jb3fwLjk6Hfd1xNOv2Pnv7QRvLvu1aSAs5ZhCIYwUXerg6q3NhTxZVeAMY","ARHD5Pr7TqrF-THPbhFBtJlDjh6bNyiKV4Xsx5KkMf04jTvJ0fUlDSEV8BybsaK-umjr3bVpsh1uf90lEOxE90fHb-_aCxyxxeSpl7Ojs55J","ARGSWdIzHcisaDn8UPsRZhjKsOdTWVtRguNIvHZAgWKZRv5aV1S90a5KBYSpieoWxecRHle8QGyxVUSjo2XGVv155R_x6oHrnVqgXo7lAIHXYogoHrEO-w","AREQajAO_Z-y8Vwi3ndLuSXsXTCjJPnTck0FFE5OGhMWNw-wBZIbmafHdV3WwNfJrTVQL7QyWi5ffX_8tMJAfk_HDEhVVn4sRXqtSPNfvFg",
//	ahBzfmx1ZWUtd2FsbHktZGV2chELEgRhZmZzGICAkPzFr8kKDA|"ARFA-GcOeyfBnB69gnG_X679LCCGsBVfFrermW0g6Ubn87GQAj_Zt-2GqoBUlq3y7bIk45yBcu-SxWCuITzuRwQT-J6XhL-f7pUY_Gg","ARH4B0szm9xLPIhnBeBUkkyCOGGUMz9vEUZZUctB8533bHWI4NEHlU5awzuND25UajZ4ZFb1kkBzzxwxLeQ0rgsrV15F-2FzAEPMyVhmVmw_KSPpDMTKxw",
//	ahBzfmx1ZWUtd2FsbHktZGV2chELEgRhZmZzGICAkNz_7sUJDA|"ARFuu3QT-Pwg358OilT5V9hK0HMIa5JxPWX0km_0AYy__y4uUfNXHyOzLPNz3jQ2Bt9dBDRMRtxRaMPZ2kc5PJX5i7RfQHHWU6qjuzmywQM",
//	ahBzfmx1ZWUtd2FsbHktZGV2chELEgRhZmZzGICAkIKevO8KDA|"ARGCEHrAnVlTw6iS-rIINwyyB6WI8fkglFf7ZcxqBJJdphV8PXmUtysuQMuyE4wRG1sVg8u_32Nn_YiTAuYLbGEa2Otm1sQHX1tdLY8","ARFAbBAV3NBtcXw_XjhcHf4FjHB8OuD5KH4S-9KMbkh0mhCgt-I5hbPZBT-k0v7MKiTDk0Z7Ph5xab2-J9Ftv6RMMAxghYHAr8k_pjyrOtKD","AREaaao5dV4AftWAziecytvKzPLvXBQSzoMDrXMiZ2G6ytqfZTygrsBiwQ86thqOpQbPQZYfc4NLyUhcf0F7tRf5yB9St78mHQMq9dmfU5FL","ARGYZUqjQQT7dw_4u2HgXUc4KizycE05aCd2hX0yur1XT7iCYHMfKX2UcdIdrHsybz0HUY5yKH0Hqz5GjidKlMFFcou-PRQ5T6F1LvFU3Wi5","ARHAGJh4dBGvjN7-95rgcygR0irKt5I7-NAhPCpcGXNm7QAKs6c-o8ILtxirm1df6LL2ikxGseF-Sd_ropfSGDGIJX4isCbIL5EbuOef-n5g","ARHFKQK5RV728GavRsI3bn9eH91VLOQCVSS39-2vZHcNnP7MLL-lXxA7_MNJj2-HGocSZCUAF-IMstBi-QesCQA4RVLe9aQ-NAqoUa_1rg","AREsRULwh9EjJxRfBLfmCNe9w0CWsOEmDIHjUPkOJSs6h9c4OEQhon5CNegdg-5xUfKFC5q2ova7jdgyQmO3uBdUrGqE7POJOPYDgYfl8Kn7","ARGfbh6FZSZhxK3DqZXzrI-WNC2J6cEzAZncZ8zcifUJOn0SH9SIxO9QZUIcbyKzrF2ngfDK10UEhdG8DbGUq_dLyEfTZUINBrUeQWfGGRv_arj8-A","ARG3gb3gH_AzMW5c3Eqp2iCOpvb36gAGT3-pzI4oUEs2-IvDafuHaDDS3nvneDPeM_LIJEik94Q_wnrgI0_0p6gwTZ1wZpKgUaXNENUpRP4","ARGgUIG2NMpHLRpep6UzhbTHwSVpg2o4_kJc6_Mz_1BQuapzMxNdZm1W3mi_IpEF76x_I7vr7CrmnA0V72W3MOX2wK65YRV5HG6Tr_VA7E2IfCdhTnqAqg","ARHwp4QsWIizkgXMDSNW3ZauoRIOMeXQ8l8hmlNLRqTUScW7iLPTNbD09hni3FRgpHEhodaPW6WPYiq9tFef8iLOIJPyaBQcDgFFPA_ruJ-X1-XxagWFow","ARHIJTelskzD_s5D10tl-j48aFGQTLd3Hqzvm1ebEhG4c8p0dhFjCLBQNFyp6YmzxC8ds8n0vpmlR6YH5a83SU8jhUShMamiA4hZrpWrWt5A","AREltBEIsmRsRoxYfq94g4AgS60lZC-xElz05sAlexxrgjpcRU3bkMr-9hTBgfsWOpY79ocyeYRtIgeH2Vf-sKaibin6HXLKJW2KGVB0oQUB","ARER7sfeTa0ZY1bjMGdoeRy9CJSLUr880K0JAzNjGB3EqmpfePIFhi6lfraqaUEmy5ak0cUQZPLD9KMHMpzeMnEAMP_U-AkbGnU4eDTYKt-g","ARGmgK5KxHVpTwdJ6ufviyAEOT1K7IVfVaZNQXAo0-tkLSYb9IYjC3_HooZCuKR0vbZeJWmX0_u7P24QYw_M2dnhiDF0IUXFHWQ3-KjIkm29","ARGhlg8-A0z-Zdq_6-oGkNTW12m2fOcQd9NkPmOIpHsvnyaMiaAbPGSUXHw5wXhodG_Wna1nh9qYMXNjthTJqiwrMhaaziRtqkHMxl4eio5D","ARH4Yq7SeWaqrBl_K0DQKryhEOTT9XJmRYrsju3Y86l8nHTWuVFv6RholaXyRboh7LN1tccYWEChw-N-mwela1_GLy3VSzY9GBIhntGCjA","AREMoZUrq_y4KEYeQo_HZ4i3i2UUozaZgWvGLHBeBGE4rN5Hx-CP0_y-rVzbE-o_7m5_S4Yig9ARV-4zn1hUqu5rMyox4zc45gWvpMbigRoKrbzkEJB-Dw","ARHO9K3cisI_Q5s0Cm36SSKiugO5Bb-EZUHaeJoZ7o4cJIeG-zgotAIb9mn1iXunZZFm-iJrHIBs65L-CnL4LppOdmDj_H61cmItZiEi1xQ1",
//	ahBzfmx1ZWUtd2FsbHktZGV2chELEgRhZmZzGICA4L-2xYoLDA|"ARE79cm7_WxafiPW0Ilgectk5Q3PNe_j3i4-vLwD8Y4VVTJq-yDArJ9FjuFrSCUWas-eNxFZUQA8GxUTeVGgmXtiYxE_kVRshF9A0lfI-lw",
//	ahBzfmx1ZWUtd2FsbHktZGV2chELEgRhZmZzGICAkPTfr6kLDA|"AREJDdcWgi40re-Bu1Q0MHJ3d_RDe4OIOga835Zop8hCQZS16pTj7uIUL6ZKp4omX_zbmZRnYsBViWWQ9ph8YxW6Fh6aov9kLWQ80ODEuegV","ARGD1HmO1BTiWXO_PL9HId1Rxdj_L6JcsmJHAcwVXfKHWLqKOL3M2ZW1EnP2AEeamHovZKCU1bLa5-hTLqyxsjGIqEZrHmpcFraVOXrLDblF","ARGseS0WfCT4Nd56ls6vsSFPiUa1IfALLsaSQ2-jYmqRLHeWBfQMBRtxLbTKePgOoedStU7dsmF3wUvhcQtFJVEf5IDK6Cjm8lbZ1ycAMw","ARFvriX27fes_yg_itZf8fr_AkJ0tvIWDmauEvz7mUCbM3x3se1QUCgDV-BHFq36ke8ojJB3TkfIJmG6-xsgEmMOaVhzyql_hGnGhPWSIufL","AREjx4xHNeriaWCtCOxKGLXahcIxvvJNa5nnvD5oDB3TVOSpV-QZRq3Epe-2LuGl0DsO_L7ts-aH84Tc0hNViS5svfM-UE9V4nWMEFFCoawZ","ARHU__A-83BnRprY0XVuat6bxJMPtXauJv2gqbU4M-kxuiVzFiuzC4D8QvtzlaxOXdf3jOadpaQ5BytBHpoN6EcvCb9jajU2NMdzkjcRIqCJ",
//	ahBzfmx1ZWUtd2FsbHktZGV2chELEgRhZmZzGICAkMTRpvgKDA|"ARGfeRXeLDbb-iYmfOvXCHE3w8POviMMZIW8v8F-JxPnedd2pzHG4jI0qVbo3dOVhUrP_6DG3lIyCmDi64oIiVJ3ho5TfxIKtmuyY4nJUWI5WIqYd-ODBg","AREX8cCodaCimsR1J9ZCH-vjeDvDTw7LTLNzmZX-f9d-qyaKJwYMrJZa8EzL5B8BaLPEcy5lrns-82UHF0bYkUs4aox1m5wdP_DydV0qo23c","ARHzfKyCWC4kwrl1yWPqQOCqSmcBN9PfRjJYvXCqHVEWvWFDdNbJj9I0ttzMgLeaT_DpOKKo9Oeg_p4u2iR3F9kXksWX5bc944IEftJcAQ",
//	ahBzfmx1ZWUtd2FsbHktZGV2chELEgRhZmZzGICAkPyUoZILDA|"ARFTcuHVb0TnJY5ki34_8J-9kxOc_9lJ7XALKJLs0cupo6-c_fwS9ai64aOoSn24jQS_-IkvNqHF7Cysitbm34zAqBbsNVsMmSNDrQY","ARE-NPflS6svsU_3TNLjKODKWOiKbqTvvfZlQeunl_0-Q3up4Avl-dklE-nYuXecNjbTxj_9SoEnDKY0UwNGlyTGGNYepqOiUclmthk",
//	ahBzfmx1ZWUtd2FsbHktZGV2chELEgRhZmZzGICAkJjeuogJDA|"AREe7rk-kgCoc9aO4mt6e_4bfdvL8JIbVeB2ooMp6y6IA5gh2R3Jkh_s_mxMGn1A4hHhhnRTQdKV8EJp28mCfXHs2rnXMsSdfxvGLcJvEIRz","ARGjdvsX9t7i6JNMGXY4Xcv-y-mChNJPnCm4wMY6HfyMoII8wVrTwg-zTB3LdkqhfPVUHdniBuqTO3MDSHoLloylFUyXGVxf6wwzh5FUi85qJHtgvX-Yrg",
//	ahBzfmx1ZWUtd2FsbHktZGV2chELEgRhZmZzGICAwLuZo_8LDA|"ARENuq8yKEvlD9sarhEs80HaKbow1FJTR3fylkApId-miQ6dExSPV8WvQGrqYw3HtPn18L3Iybc430wT6eTSenLSwjSxxca5-nxQw6tRXrty9sUeDDVg1Q","ARGQQ7sOP-aNqMuJbyOcX6188z801Uv8qoq7_9RhVpSlGBo7BKpvoTHAVqDN83Eu6pIWDAlbF6T7pxiET-KFfJIQSQqppoq70088PEnl4AA",
//	ahBzfmx1ZWUtd2FsbHktZGV2chELEgRhZmZzGICAkKzRh4IKDA|"ARG3VNwTzq5cR7yLkrGo58o2OEroWxfVvikTsHy2TFDlvmWiWbIB6F2NuLIXu-MsLZZVBnWqdIFhCGQm5adcE7arVAYhVDn907wydSQTPSZ1",
//	ahBzfmx1ZWUtd2FsbHktZGV2chELEgRhZmZzGICAkLixnaMJDA|"AREuqN3g2XquOHtpeL4lniEj2V28txVTIk6IX-1189aa7lI2tnWC2cUTURPdekfrjZyI7irlJizJlDEnH7diI-OOqB_0KMBhcIWDfx3BnJXJooj70BFZlQ","ARHctp1-nlsswZ8Nzjzt-BVCHwzVVfBZGb06W3GIL0eFAwsDNFvgmK38acAWpkegEGeVF_rNgMu-d38pFU3S3bc22RMRk3pzI1wx2NnFWuBC","AREA6hktgg_6SzlNz4zdw_4fOaIcy5hA3zD5uvlRed8c3ykSd9vWMHD_JHCFVIAoWVwMiG7nZTT5bX62naMqsTaNte0UbnX7CrD5YvOQRyij",
//	ahBzfmx1ZWUtd2FsbHktZGV2chELEgRhZmZzGICAkKzribIKDA|"ARFiP0g_zhFlpjTPArVpDblZ9pVjRr8PYsnI99VeX4lJiBvP61SRZKIF_SvBX-xYpBZtWgVH2tu8YhlxpEBdPy4sUwszZBsMAenIR_9ZTQ8A","ARFuYoR6_71PHV0WwQbo1yNvYArKqrLWMinmqSeXUR4pgX3zw08CQtx2z5xmf_zlmp2PixdzjzSE788pHuXmT2bhiPUebcqrgWCgT-aNJi0","ARFcJdm3oWAEH5jdNa34viWPkP9MvOc_i03qkD0Tw9WLKpum1MKEG8SBxpsPmPjYnZsER8mjsjHBXs2jsRM1LSLNLt8x_K6k5w30OH5egDA_",
//	ahBzfmx1ZWUtd2FsbHktZGV2chELEgRhZmZzGICAwLGU67ILDA|"AREJCd8z2YV55UVyIPsl--e7CgELy6kgQBsXLs9iBjh2zvg1mtbijfbGBZ0w5a4jT9dpRguuqOtblItPnVk4ycfCFlUTKKnFeAFKv-hCjds","AREp4dqxY4fcj6wigIJdyoNNPK3IGW37kjmA2Dw8_Pkl7sfDH35jhErFmXYZfFqS93qsI6xgAi4BPtK_l6MnzAPTYjOOwtsVKCW8SOgsmjw","ARGT1n-VsCIKg7ylz6UHJNRRtrJfirYyk420Skroqm-Kz0lhKX9Kldh2QU8fYIlFq9HFG46wdf5sinJmzr4bK1jHyfDk6nB23Kk7UKw14XQ",
//	ahBzfmx1ZWUtd2FsbHktZGV2chELEgRhZmZzGICAkIyA4rILDA|"ARGaY4jAwGYjvSGie5rCVPFY9msDwI620eqJfHIaCX5CJlivL5y0U7t6uALZq96U8e0suhyL27Ow9kr3rfytLA63WURn1HRxLXkfdc6eZAy4ZmSkzY8XCA","AREDetO0nFojXDbEPUvO13kuQxksGwgQAeYd41sgQguUCQ7In1kfH1t3GCM4pYDbGL56r-ZDlB3ZFE08zA5eyNDBC9IrsGChhYQB5nv9Hw","ARHw2PCeF2qZ36ADmC6VCbVQfK9XKKz7b55xnqeAuNJq4H44Bgf9S5GJ5-jVMSSAuBTg4kAOJFNhYPKb7WrdnfywOsjSW6r0zf5evSaGt-Q","ARFtXApgNNjvRg8wnXcqiqi2gy8-L8iJuwVHhd-CTZt7OS_dnn_YTY7ABnHHESbEGcRk-68KMqA1xJBS7MCbf0z8-oC2NfBT3Mj2ngygsg",
//	ahBzfmx1ZWUtd2FsbHktZGV2chELEgRhZmZzGICA4JuvsvEKDA|"ARHfVG6CZooA9UJDukC0gCH-LWuw8WQ5WcnBsS5UA7pnBq9He8BzSnFhhu1lXnZrJeZudKunCtdej2XR-lMJduk50DXJfJtWxd1jE3yxgA","AREpLjcsCpb2mpzCi7aOk_03ZZA48Rzh8qt9TPNTlQqPejSQrHTy5A2uXumJaa5-vxTtG7wzt4ApArKDAtgqHsVRv1Gm7nH3TpTONj_wRPM",
//	ahBzfmx1ZWUtd2FsbHktZGV2chELEgRhZmZzGICAkLjXm_QJDA|"ARF0E0iXw4MlPEoMXtrbz0n_xDYyYB9KjHEgomtxbMZ9fKBpOyC-Ha9IKvoxSsK4vLjJgErE2ALWgo7DSJ4qNSo0bYW39hj0WBsdIGu0ZDE","ARHaGEBOXn-kVUKF4_jj2A0XE7axKI-sVhsFmGCydA9HBnepHYPwI0dS0JwlDJ2znM2PhBjB_XX9mkNidUlFbsCSqicLncnnV8ir1f2jo4Y","ARET1QGi5eW3vaM0xluWmwAdLEgj2S728aE2ebZ9Iv66ElRS4dXYOEw_iweIHG81IW6nK54KnCF0cIaJvBKJuWnZlsciCAW67u2omYpCww","AREORroCcojddw2_0bqhUj1sEXd-VzHtQPPZZ5dJrELFNm0kGv-hhA98I_a79qT_euj34GzC6D6LVHD1ZhnWmnsbEbPm8d2JqUoaUsvdc8o","ARED6fOxQtIOSbyMpFWZrDcVIPACHbU5L9Nb7G24t6Cs1Fy82UxrYgQ7FUFHjf3a7qhzmQuAYdLoUux0jrcCnKUAsGTPxHkpZ_o-2GP_g2s","ARHqDrVj3Pl2UUhzaZrJxZJ5_7tLy0ECnhFowqFF1P-TaBS7UoRb4zPjh0OyTeb-rb0KU8ZJc8JEwkxzbh9HE-rbVX1ObFN2GCrqRs2P0g","ARFN4oSH4h3wAMnLrEECOJQOBlHGOTSsJiuYJ20MDjWCXTnZpkTyqCzA9HksVX7W3KRSwhJpWZYpGJ9fMC1-QeRsDOmYVbmoNavqpAn2uig","ARGQYY9_STffJJuzsJYl0CozjlLfG9qbX2b5Js1MYK-QK8tUhnQkVkbaf4EZeFI8tuWFhVHsYZsTnolH4EhPeVMZZfIKSuGixmex3RlnlQ","ARFScHjcffHRogHda-Vu1t1HSj65V3k1uC1XzBCQ-cE9jd25Pg7OPk3eklYLi7floB8QOBQZWJknEnwpqhhpPkX5GcGLtavkW8Qmnkblu4A","ARGqp12chOalaIiXVBRdIT9EIsSRq3YVs0kEs0l0UOpPqRWdrfdoBiVkJmKQX4cKqBZGHQFu2KcCEpStb2jMsdM4kBTykv6bzkMqdcrG028","ARGTkb6eGgifnkFZ0XtVTgv6R6ygg-6MqA3Gmm8QBi7QcVIDQmvmAs2pjsM4ztXSUnRSUtxshIWZbpA2Fd7Q900-Zbb3KNgwXTIMQnJCTyDztCeFDTvM3g","ARGeZbZIp3o34DopSYOj6RI7fbdq51Y-rcCHCtB3WPj8kbXDmefM9xpkXXiH5slBKAe4dADdrbJbOMf3m6EiD4318vb4vklBTWwEe9ePwCI","ARHmKDCrJemwTz9nkVmxUOwbQHkVlVASEb0nCrD2UV0rfQ4dK9QMNFBUWSKBt7HcHU6yzAb8y2xMx-cn-DVqFPKTRGVGZAyomM3PciNMZA","ARFY9JXjw8vRw3YcZPZg12Hzw_RYxhy7GlxtG8hsBqlTjdDHtenUiPzbAjvMNb-cjQJQDIYQVSsTJUSRSKYVtDLU_eLGY4zMx9yN5am-XclYwWS6am1EgQ","AREbnnnDbV8L6LIDHuQN4EUAti0xnVSlTsNfEHhT-AneisQo7pND47Vj0zbckTgonDV8oZOOmh87g8FL4ByfYm8w0Z6ORRFXdZ2K2IPPDmI","ARH58P-V5y75OlI-aeQvGD5yyvCK5nsLdNeQaVc9u-XILKWjICMqf7vLdbL2VA9FC5WoxFekArm0CazK-zRp_DSLA3sEmbHCiuS6xwQp1Q","AREPKXNq34pGXKVAHsMCs3Hyvuu7mspfDwwCB2yorcWnd7USmXSvS2j94l93Qlch-O7NyuvebTbqNKU9xmFzojpHvKvZIsw7fadwopd0RA","ARGfZYKasTjvxKgKqAQF0RugZC3-mnaRE3xtd-s7Vm0M6_NjT-27eVpRwIhOlYaMIrq6iMxwIwpXK4L8Yepl5lgJJqiciUtw3crdrUdnOg","ARH9gFut8mooj9mhuFJmOXdg_KNCI4_7l1zThGsqNROBo95hdzs95Crv5lW1hdEJTKXY8MXSVDjfSAO-pzeaW3FUKARALr4jdD_TA6f577Y","ARGbPlcxir2UeGpxoWJg2QWrnMbgt2sKkOaKBHKBW2vWUnMKj_Ykivf_8i5yXLZhX8-5sBsw3Zd9QusC3WFQji3DA7vDf59Y_dHL65AjHWo","ARHKSdkl4RsKlx1CKnfwhsoSREfpHgT3hY7eWxEc2qIFrhoBFyblhLODDKirz9yND-MgdlVWD75uFzOZmf976nC7UUV9hH5nud5V1GuI_4I","ARGm22XyPv-7aFZymfdo8EGI3_QU-m37DkqiHdsrnVCzGtAG7AJd-pjMSEPz7LTZKV8b5BRxfiJqBZDEw9h5rGByIvbK_8cBydbrUBBaxf0","ARFYcfMI0GHC4RtXHMeIweIb4VEWRl9F3fj4S6r0aAEvyGl-mTCv5QXcvPJf5qCQ-SH2VqNzWcrWiuRxrvJDubU1LelG1nDdOyLomW-p","ARE5ftQ9N2p-HYxbAHi5XvNZmB-sb5ATOFzoG9kr-js6A1wPeAQGMZwsqB2VlM7RWYU78lSSIojaAOsVQhdYfq8L1McEnd93wbsffuhvIX0",
//	ahBzfmx1ZWUtd2FsbHktZGV2chELEgRhZmZzGICA4Jzuma8JDA|"AREWUBcW7H3Y_gJk7SjgCaVwh9jGi54b576Mk81sL1PWX_0rI1glE4IunXUAyMMPsgbJ4S9yeI2rWT3tWZcAgxbPZpqDAM2AVj_arwktJf29_dcc1LYggg",
//	ahBzfmx1ZWUtd2FsbHktZGV2chELEgRhZmZzGICAkLzb99sKDA|"ARHXwMgrw5RoRUua2HVpsFpiSrZ3UkpUOtvdo9YpSdASvDjH_ngN6xFFKFlFztD44JaH_eO0XOs3_-XMN1ZLsJR8aps1_S9rQjnSHjJndLQ","ARGK68RSQdqjIGDfu5g8Hm3IA_uE7CuI1ix8sWqw9zyRai5q_PpFNQ1M5ojIA5ZWBy-3TDizB0tHk7ygDxi_fLS2nm2t85N4Aawpdz0","AREfm98KPIpDNFr6KMJP2RQ0NbDjNDbUdYm7zSlJ9H8OSZisPRoLIn7iosJlF4Fp7NhOLGlyvcFTVqsyAWL6_Xr6WhPsH9rBgs4PQ9wGXA",
//	ahBzfmx1ZWUtd2FsbHktZGV2chELEgRhZmZzGICA4O6j0K4LDA|"ARHz7sNzxIU_wkDSqKCNhrQJJz_h4Wb0wbKfuf8DZm705eV5umnI1bDl98828JQROIrCEBkgDooQyrmtfJeJExhV1GXzMoIeZiQdIbFA4GZ5LOSAP8gumg","ARG6i4ah4tnQW_zsQJ67GrTMJVd1R6hwr74IA_CjNY7ZK0XhtBhJvLYeTJjGmovz-ZFWLIGtqZYb233Hpl9Xx01kkr2jpzDB4Hp2JVvH0oo9Merti7VY","ARFt9H94nXvemDv2qz2XgvNP60hx7lPlOAXtN8TvAERfHoqBgD0KXXRsRuVJyVmR1gJ_DJFS227m2nkNZA6A0bQ81QUy7PuvTT_37LlVGUHfEYgT1osYKg","ARHXFhzb1Zhwnzm7smkfUphnTR6PB-kfYa9olLdNAU-cKL8IixM94D04LGYkH59P6_NKqTJVOYlOMlkP97GRAOaDopKBtBNJOwS6CDkmH0re","AREHBnENeU7-_3zPHxnOa7i1JrkkyeZcEEsel0oYc-jkJxtHdeqMca0M-10SkUCC5PIDd5E_kCNKTkt70724cjZCy5Et7fQ2FofSKohEKA","ARHeMSR0XzP_xu_4XFgz4BTqjFFdGrvKDjgSJWNsEloHU9LwDDNds3Hh1DH4ymjudfIXDzC_RVIrGfCJ8TQ0xpH6qSwo9eHfdyHlTDVCoCl88p8YZnbz","ARFVsVw7J73rDSVM7lqrVu0yqZ_LAnL3bo2kHwN7V6WbJzQioFRJJfzd3G_YxfEQKe46jC3w9sx8y42bkGslndgZPaW8aS8w81bDONw9ulw","ARFi1EgyoYPKTjjbo-zaLFCtfDXOcNr35nM2Z_2gg4h2QoXgKdcG9rKLNz1uicT0ZvWadMFL9JfBNXzkkit5PKGZ1m0zr4wWe1zmfukLj-ScO_gSrp7Wfw","ARHFbgB-ED3u7iT8u3frFpjbO4YugZ5G84Xme-sHZwu321598wbIb4bA6PBrihXbQb6b86TqraOjJeKBDPhb74H00EH5tR64svzcrBK-2SLZk_NGHiJT","AREvNs-X6L21WoTLnQde6xmotCm-S1yZqn-A_aET_t5V7Zn88h1rTnpazFPue7WVpUwF4BOKFnrjyTZX54SWCfBiOCF_jsUdbD5NDc0Ojg","ARGiA_veWJhOm1MdK8udmIWDtz6GYRDs4zJtzuxT1IX-lrdbiExFrvEncnXbm9gEZwI6Y6HFsfeOpSkmSw1nX3mfWqqqxnO5njsTmVYduFwESOBvOtaCdg","ARHk03OJ7stWHGD-apGNUGfFgDnTaKa6QrQOMWQg8BlV-2ae26k7QqRnYBjJLTc1dN4r_P4F3e0BgSSq9_NJmi4OBp2YEZJ2_krC7aAFENqEtW_snXPC3A","AREm-4A-5ofW1ATcDFgC36nBp0xVZlR3zbbfAIOMpeKuPW2gI2EzN6KmqassHelqvjY-TEJ8rPNc9fQmqJPYaqRqG9ejXWfrxM56_hhfohQ","ARELdtZKvDvndagIqR-2aKzCpe5F1vZf5rLUlssrHKhWZtgIt5S84RG8OpRexTljPvseDD0w-yZipa7JdVZxjY68F7MRkPZdazNIwSDDONhYN5Yz14MN","ARGdRYQSe9sIOQ2qOp3iyW3-O8sur3hi0ewJfsrvrGjrNjjc9YtkAjAOZydPn0RphaGWi_uTQnqlj8JHWjvThSNyGob215q7_0T_I2kGqS3MK6dvIeiJaQ","ARH8B7Kvox6WA3PS-4R7JwAwSawaucNhk9WEWbquQESRZr3q2KxDeOfMtCPmz6TeyOq7LwpEhwLvfK1z5Fm01t_LwTn4xGHC972uCdLerBeFFAdIdpULCg","AREpFM_mB1TkhIiYUmPoPXS8uAJYtmc8wjb0L4dFipu3UYcfDcQ2EqrAtElzEobV9tRVePEsihhbDdx3ohJeV2Luli4IAtbMIH-UIoHxTTvb","ARHr63rMgi5A2dtQnmLjpqGtWYqjWvynShc1YpnsWIaujbBnT4TYn1erBnoNg2XRHKAhWRHKDCIRteDv7IjMWnbKizFT5PQHjwp3vcmILLjWrPPmiWthRw","ARHUSWsraQUUsXGfMIkAnj69VCLVNRrKg43FRCp6VktvIz_hyKQdUvQez6JcudDBRlgT2C258Ro-Dob0_ECVwAuycrj4xemaHsTPv-YfupTBE1FlIL_i","ARF_GnIih5KrYtY3-pHElc8Mf2__1Z6MquQ3VX_8X3lCb8dkXNL52r6zO24YXD_8SQE-gT7Ws97rTrWtrFYA9Hbd3IKvc18K-kwgIEkA5CoTo_Y3fGsRJQ","ARGKnfp8DcAdw7Est9h_aMCUzRx4eDUTEq8dLsh-5ABf7ObBx0QWrdIdGsZu4qyUM2SwOeMmAKa7AKRoVlcWZc_1dXN6b5TF1FZbFXBkVg","ARFTrq_pisv8uXN0izdQRz42wcbk4tF7HW-YSajQh7FBwX5jopP5liFnBGlSQ2L3Ixncv0PlcJL3kSv9mYrg0DgQXUqzBY9EROfDMlz0uhIcZ5rqUzoD","ARG-USCMyj3ty-QmXHGsEPuFYZugLDS1eun4zIaCSoNXuXe_7BPSTDPOrS19vmmIQ3arXTeNVHA3WbsUfHKrMHJyiCmolohjnf0hUi4tQsp3-fINkhLLUg","ARGsmXQvSGDq4Cm6ZDQc_PW5nJGqq0zkxwBGP5mhIzmDDqlerrUTmAgdP09KWYdmiYZwYZsmAafpcAQfT3EKl-wl1dJzhGyqGlZRWMhzO34M","ARGVl-9rLK2NEuwkN0nY9-5dX9qVPN-KHwrCJs6NkktyRmBc55ACP-VW0YUbF_aM8MFuRVCpq7WvJTCxNH2h0kPt-Ebebw0G2YZJkXTTfsNWp4d41aEojg","ARG9qlKQY8WgjaIE8Bhls4wF3JjTn8hCO9OYbqAlk75p27yizLgU75ZTEJj4_ShEznSSOnJ9nSnyw0Q6zcP5fYuylUjeP7x8cgeKbVY7iw","ARFKOogQ4m3Sc3bEdAFpnw_CLUrsqL-bxXe7Wmy1r8EW0u75qEkP2hUkSwzdmuqI_vXALAtT8T97-asBaoQA4ihaVQZlg1Z9Ap0AWn547WimS_Xs6AgStA","ARED9y4qvzKEBxoUS9LT0qRnLsltFM-qHcHS6Nw4emIzN9ntypRED0Pme-EMe_vAGLWENxwFI_qGzfrNbXtXPMSO1iD8p3oj63rqDhy3HN9bfovRImwpfA","ARECj0e3qr6wluhpehuIbeqEOcNzcLvk2bz9Gx_9a4zHQrretNWbfULD91jFzREVJAiKWpUhdc_2ga4n_N7uqVeCa_CrLbH8Xhsflv9aCtxk","ARF_Rs_xLe0mjO0d3wraXn7sWQaO8mdlItR5rlwehb9fxJGL41RzfcbG_x5P0oOfAu_X57GcRD9-QVNkxJ4K01W_pRquDUd8XQGCJ543HwCg","AREBguDmoVqJpMvfjtFph_FskLXd9q1572nj16v4pBuZNUa-WTYc3XYkLwthOz-PN7dJ5_dc3OqZHLHuky4Za1RV3WPRHOtU0rvBkz6kf0rEGsIAu-A2","ARFpRkmpt_tCx8E7WxB5XeEvmITPAZRpk42NPM0SxFZrysBO90ILmBoLJumBs1Gdk_mmZM__PThkHBkRnatP4pEnDP4YitVo3QGrHbUF1Tem","AREcWx5FUVfe3isdiKViu8Urkklxu5l-tlaLH4uuGgyTlHRC0JVP3rYWsbyhsEFB8-NfauH6WwA_wjDc4gByVc7c6bxN2bZ9RsGrIqi2zRAtLUlkbVp7","ARFm4INQEyemXgthostHt_ZjmcV7SlHIwJHEILN5jbVuFGzFKBRFXq_NBpIg2UVO9vZUibfGOBuDMUPk6iBuQtX9ptZiyl54sZVcYpq7fY9Wqf_5SotvAw",

	
		
	@Test
	public void fbAffsRevenueAPICallTest() throws Exception {
		
		List<String> acpms=new ArrayList<>();
		acpms.addAll(Arrays.asList(s1));
		acpms.addAll(Arrays.asList(s2));
		acpms.addAll(Arrays.asList(s3));
		acpms.addAll(Arrays.asList(s4));
		acpms.addAll(Arrays.asList(s5));
		acpms.addAll(Arrays.asList(s6));
		acpms.addAll(Arrays.asList(s7));
		acpms.addAll(Arrays.asList(s8));
		acpms.addAll(Arrays.asList(s9));
		acpms.addAll(Arrays.asList(s10));
		acpms.addAll(Arrays.asList(s11));
		acpms.addAll(Arrays.asList(s12));
		acpms.addAll(Arrays.asList(s13));
		acpms.addAll(Arrays.asList(s14));
		acpms.addAll(Arrays.asList(s15));
		acpms.addAll(Arrays.asList(s16));
		acpms.addAll(Arrays.asList(s17));
		acpms.addAll(Arrays.asList(s18));
//		s.addAll(Arrays.asList(s19));
//		s.addAll(Arrays.asList(s20));
//		s.addAll(Arrays.asList(s21));
//		s.addAll(Arrays.asList(s22));
//		s.addAll(Arrays.asList(s23));
//		s.addAll(Arrays.asList(s24));
//		s.addAll(Arrays.asList(s25));
		
		FBAffsSearchService service=new FBAffsSearchService();			    

		
//		String result=service.fetchResultFromFBAirByQueryId("9e5634d5-8f94-43dc-a5e9-68e4115d7585");
//		System.out.println(result);
//		String url=String.format("https://graph.facebook.com/%s/aggregate_revenue",Constants.FB_AIR_APP_ID);
//		System.out.println(url);
//		
//		Map<String,Object> input=new HashMap<>();
//		input.put("request_id", "1");
//		input.put("ecpms", s);
//		input.put("access_token",Constants.FB_AIR_ACCESS_TOKEN);
//		
//		//System.out.println(input);
//		String content=JSONUtils.writeObject(input);		
//		//System.out.println(content);
//		
//		System.out.println(ConnectionMgr.INSTANCE.postJSON(url,content));	
		
		
	}
	@Test
	public void fbAffsRevenueAPIResult_both_ecpms_and_queries_passed_in_request_Test() throws Exception {				
		Collection<Key> keys=this.createAffsEntities();
		
		FBAffsSearchService service=new FBAffsSearchService();	
		Map<String,Map<String,List<String>>> ecpms= service.getECPMs(keys);
	    
		ecpms.values().iterator().next().entrySet().forEach(e->{
			 
			System.out.println(e.getKey()+"::"+e.getValue().size());	
		});
		
	    
	    
	}
	
	
}
