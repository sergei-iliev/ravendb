package usecase;

import java.io.Closeable;
import java.io.StringWriter;
import java.io.Writer;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;

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
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.luee.wally.admin.repository.CloudStorageRepository;
import com.luee.wally.api.service.AffsSearchService;
import com.luee.wally.api.service.CampaignSearchService;
import com.luee.wally.api.service.PaymentService;
import com.luee.wally.command.AffsSearchForm;
import com.luee.wally.command.AffsSearchResult;
import com.luee.wally.command.CampaignSearchForm;
import com.luee.wally.command.PaymentEligibleUserForm;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.json.JSONUtils;

public class DBTestCase {
	private Closeable session;

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
	public void createAffsCountEntityTest() throws Exception {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		for (int i = 0; i < 10001; i++) {

			Entity affs = new Entity("affs");
			affs.setProperty("total_ad_rev", 1.4d);
			affs.setProperty("date", new Date());
			affs.setProperty("country_code", "US");
			affs.setProperty("experiment", "preview_images");
			affs.setProperty("package_name", "com.moregames.makemoney");
			if (i == 1000) {
				affs.setProperty("country_code", "BG");
			}
			ds.put(affs);
		}

		Query query = new Query("affs");
		query.setFilter(new FilterPredicate("country_code", FilterOperator.EQUAL, "BG"));
		PreparedQuery preparedQuery = ds.prepare(query);

		int count = preparedQuery.countEntities(FetchOptions.Builder.withDefaults());
		Assert.assertTrue(count == 1);

	}

	@Test
	public void searchAffsEntityTest() throws Exception {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		AffsSearchForm form = new AffsSearchForm();
		// form.setCountryCode("US");
		// form.getPackageNames().add("com.moregames.stuff");
		// form.getPackageNames().add("com.boo.stuff");
		form.setPackageName("com.moregames.makemoney");
		form.getExperiments().add("preview_images");
		form.getExperiments().add("redesign");
		form.setStartDate(new Date());

		for (int i = 0; i < 10; i++) {

			Entity affs = new Entity("affs");
			affs.setProperty("id", i);
			affs.setProperty("total_ad_rev", 1100.4d);
			affs.setIndexedProperty("date", new Date());
			affs.setIndexedProperty("country_code", "US");
			affs.setIndexedProperty("experiment", "preview_images");
			affs.setIndexedProperty("package_name", "com.moregames.makemoney");

			if (i == 2) {
				affs.setIndexedProperty("country_code", "BG");
				affs.setIndexedProperty("experiment", "redesign");
				affs.setIndexedProperty("package_name", "com.moregames.stuff");
			}

			if (i == 3) {
				affs.setIndexedProperty("country_code", "US");
				affs.setIndexedProperty("package_name", "com.boo.stuff");
			}

			ds.put(affs);
		}

		// Query query = new Query("affs");
		// Filter filter1 = new FilterPredicate("package_name",
		// FilterOperator.EQUAL ,"com.boo.stuff");
		// Filter filter2 = new FilterPredicate("package_name",
		// FilterOperator.EQUAL ,"com.moregames.stuff");
		//
		// Filter ff=new
		// FilterPredicate("country_code",FilterOperator.IN,Arrays.asList("BG","US"));
		//
		// Filter q=Query.CompositeFilterOperator.or(filter1, filter2);
		//
		//
		// query.setFilter(Query.CompositeFilterOperator.and(ff, q));
		//
		// System.out.println( ds.prepare(query).countEntities(
		// FetchOptions.Builder.withDefaults()));

		AffsSearchService affsSearchService = new AffsSearchService();
		Collection<AffsSearchResult> affsSearchResults = affsSearchService.processAffsSearch(form);

		try (Writer writer = new StringWriter()) {
			affsSearchService.createFile(writer, form, affsSearchResults);

			CloudStorageRepository cloudStorageRepository = new CloudStorageRepository();
			cloudStorageRepository.save(writer, "affs_ad_rev_search " + new Date());
		}
	}

	@Test
	public void dateFormatTest() throws Exception {
		DateTimeFormatter TICKET_DATE_FORMATTER = DateTimeFormatter.ofPattern("MM_dd_yyyy_hh_mm_ss");
		ZonedDateTime sd = (new Date()).toInstant().atZone(ZoneId.of("GMT+8"));

		System.out.println(TICKET_DATE_FORMATTER.format(sd));
	}

	@Test
	public void createCampaignEntityTest() throws Exception {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		CampaignSearchForm form=new CampaignSearchForm();
		form.setCountryCode("US");
		form.getSources().add("86e528bfc83c299614f18f25cbd02f6b");
		
		
		// campaign
		Entity campaign = new Entity("user_campaign_data","dc6641da-007f-4853-9b88-099c13bd9f99");		
		campaign.setIndexedProperty("ad_network", "organic");
		campaign.setIndexedProperty("package_name", "com.moregames.makemoney");
		campaign.setIndexedProperty("date", new Date());
		campaign.setIndexedProperty("source_id", "86e528bfc83c299614f18f25cbd02f6b");
		campaign.setIndexedProperty("tenjin_camp_id", "9ff474c9-60e1-48fa-93a7-ab4eb4eb7a29");
		campaign.setIndexedProperty("country_code", "US");

		ds.put(campaign);

		// campaign
		
		campaign = new Entity("user_campaign_data", "caad1520-1a0f-4285-be0c-b74f1a18fe68");
		campaign.setIndexedProperty("ad_network", "organic");
		campaign.setIndexedProperty("package_name", "com.moregames.makemoney");
		campaign.setIndexedProperty("date", new Date());
		campaign.setIndexedProperty("source_id", "86e528bfc83c299614f18f25cbd02f6b");
		campaign.setIndexedProperty("tenjin_camp_id", "9ff474c9-60e1-48fa-93a7-ab4eb4eb7a29");
		campaign.setIndexedProperty("country_code", "US");

		ds.put(campaign);
		
		// affs
		Entity affs = new Entity("affs");
		affs.setProperty("id", 1);
		affs.setProperty("total_ad_rev", 110.4d);
		affs.setIndexedProperty("gaid", "caad1520-1a0f-4285-be0c-b74f1a18fe68");
		affs.setIndexedProperty("date", new Date());
		affs.setIndexedProperty("country_code", "US");
		affs.setIndexedProperty("experiment", "preview_images");
		affs.setIndexedProperty("package_name", "com.moregames.makemoney");

		ds.put(affs);
		
		affs = new Entity("affs");
		affs.setProperty("id", 2);
		affs.setProperty("total_ad_rev", 110.4d);
		affs.setIndexedProperty("gaid", "dc6641da-007f-4853-9b88-099c13bd9f99");
		affs.setIndexedProperty("date", new Date());
		affs.setIndexedProperty("country_code", "US");
		affs.setIndexedProperty("experiment", "preview_images");
		affs.setIndexedProperty("package_name", "com.moregames.makemoney");

		ds.put(affs);
		
		CampaignSearchService service=new CampaignSearchService();
		service.processCampaignSearch(form);
		
//		Query q = new Query("user_campaign_data");
//
//		PreparedQuery pq = ds.prepare(q);
//		List<Entity> list = pq.asList(FetchOptions.Builder.withLimit(5));
//
//		for (Entity ee : list) {
//			System.out.println(ee.getKey().getParent().getName());
//
//			Query qq = new Query("affs");
//			qq.setFilter(new FilterPredicate("gaid", FilterOperator.EQUAL,ee.getKey().getParent().getName()));
//			PreparedQuery pqq = ds.prepare(q);
//			List<Entity> listq = pq.asList(FetchOptions.Builder.withLimit(5));
//            
//		    System.out.println(listq.size());
//		}
		// }
	}
	private void createRedeemingRequestEntity(String userGuid,String amount,Date date,String packageName,String type,String paypalAccount,String countryCode){
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
	
		Entity redeeming = new Entity("redeeming_requests_new");		
		redeeming.setIndexedProperty("amount", amount);
		redeeming.setIndexedProperty("user_guid",userGuid);
		redeeming.setIndexedProperty("package_name", packageName);
		redeeming.setIndexedProperty("date",date);		
		redeeming.setIndexedProperty("type", type);
		redeeming.setIndexedProperty("paypal_account", paypalAccount);
		redeeming.setIndexedProperty("country_code", countryCode);
		redeeming.setIndexedProperty("is_paid", false);

		ds.put(redeeming);

		
	}
	@Test
	public void paymentRedeemingRequestTest() throws Exception {

		
		createRedeemingRequestEntity("48bb2675-a072-4b6b-ab66-cb599a29147d", "12", new Date(), "com.moregames.makemoney", "PayPal", "gil.mincberg@gmail.com", "US");
		Thread.currentThread().sleep(2000);
		createRedeemingRequestEntity("ffff2675-a072-4b6b-ab66-cb599a29147d", "14", new Date(), "com.moregames.makemoney", "PayPal", "gil1.mincberg@gmail.com", "US");
		Thread.currentThread().sleep(3000);
		createRedeemingRequestEntity("aaaa2675-a072-4b6b-ab66-cb599a29147d", "24", new Date(), "com.moregames.makemoney", "Amazon", "gil2.mincberg@gmail.com", "US");
		Thread.currentThread().sleep(4000);
		createRedeemingRequestEntity("bbbb2675-a072-4b6b-ab66-cb599a29147d", "27", new Date(), "com.moregames.makemoney", "Amazon", "gil2.mincberg@gmail.com", "BG");
		createRedeemingRequestEntity("bbbb2675-a072-4b6b-ab66-cb599a29147d", "3", new Date(), "com.moregames.makemoney", "Creon", "gil2.mincberg@gmail.com", "BG");


	   PaymentEligibleUserForm form=new PaymentEligibleUserForm();
	   //form.getTypes().add("PayPal");
	   //form.getTypes().add("Creon");
	   PaymentService paymentService=new PaymentService();
	   Collection<RedeemingRequests> r=paymentService.searchEligibleUsers(form);
	   r.forEach(a->{ System.out.println(a.getDate());});
	  
	   
	}
	
	@Test
	public void searchFilterTemplateTest() throws Exception {
		PaymentEligibleUserForm form=new PaymentEligibleUserForm();
		form.setEndDate(new Date());
		form.getTypes().add("Sega");
		
		String v=JSONUtils.writeObject(form, PaymentEligibleUserForm.class);
		
		
	}

}