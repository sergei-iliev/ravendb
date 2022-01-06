package usecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.luee.wally.admin.repository.PaymentRepository;
import com.luee.wally.admin.repository.UserRepository;
import com.luee.wally.api.service.AffsSearchService;
import com.luee.wally.api.service.PaidUsersService;
import com.luee.wally.api.service.impex.ExportService;
import com.luee.wally.api.service.impex.ImportService;
import com.luee.wally.command.PaidUserGroupByForm;
import com.luee.wally.command.PaidUserGroupByForm.GroupByType;
import com.luee.wally.command.PaidUserGroupByResult;
import com.luee.wally.command.PaidUserSearchForm;
import com.luee.wally.command.viewobject.PaidUserGroupByVO;
import com.luee.wally.entity.PaidUser;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.json.ExchangeRateVO;
import com.luee.wally.utils.TestDatabase;
import com.luee.wally.utils.Utilities;

public class PaiedUserTest {

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
	public void notifyFirebaseTest() throws Exception {
		TestDatabase.INSTANCE.generateDB();
		AffsSearchService affsSearchService = new AffsSearchService();
		affsSearchService.notifyPaidUserFirebase("dddd2675-a072-4b6b-ab66-cb599a29147d", "Hi man",
				"you got it all fine",
				"https://lh3.googleusercontent.com/NZMR39qnpQf4uqwJQ0CYUGenuyrDqtLiaKhKXpLp2-Hp9OiPIbJyDGfiqRez2vymCwtE=w75",
				"https://luee-wally-dev.appspot.com/payout_notice.html");

	}

	@Test
	public void searchPayedUserGroupByLocaleTest() {
		TestDatabase.INSTANCE.generateDB();
		ZonedDateTime now = ZonedDateTime.now();
		ZonedDateTime yesterday = now.minusDays(10);

		PaidUserGroupByForm form = new PaidUserGroupByForm();

		form.getCountryCodes().clear();
		form.getPackageNames().clear();
		form.setGroupByType(GroupByType.LOCALE);
		form.setGroupByLocale("country");

		form.setStartDate(null);
		form.setEndDate(Date.from(yesterday.toInstant()));

		// form.setAmountFrom("0.9");
		// form.setAmountTo("1");
		// form.getTypes().add("PayPal");

		// form.getPackageNames().add("com.moregames.makemoney1");
		// form.getPackageNames().add("com.moregames.makemoney2");

		// form.getCountryCodes().add("DE");
		// form.getCountryCodes().add("US");

		PaidUsersService paidUsersService = new PaidUsersService();

		Collection<PaidUserGroupByVO> l = paidUsersService.searchGroupBy(form);
		Collection<PaidUserGroupByResult> r = paidUsersService.groupBy(l, form.getGroupByType(), form.getGroupByTime(),
				form.getGroupByLocale());

		r.forEach(e -> System.out.println(e.getGroupByLocaleValue() + "::" + e.getAmount()));
	}

	@Test
	public void searchPayedUserGroupByDayTest() {
		TestDatabase.INSTANCE.generateDB();
		ZonedDateTime now = ZonedDateTime.now();
		ZonedDateTime yesterday = now.minusDays(10);

		PaidUserGroupByForm form = new PaidUserGroupByForm();

		form.getCountryCodes().clear();
		form.getPackageNames().clear();
		form.setType(null);
		form.setGroupByType(GroupByType.TIME);
		form.setGroupByTime("day");

		form.setStartDate(null);
		form.setEndDate(TestDatabase.createDate(1, 1, 2070));

		PaidUsersService paidUsersService = new PaidUsersService();

		Collection<PaidUserGroupByVO> l = paidUsersService.searchGroupBy(form);
		List<PaidUserGroupByResult> r = paidUsersService.groupBy(l, form.getGroupByType(), form.getGroupByTime(),
				form.getGroupByLocale());
		paidUsersService.sortBy(r);

		r.forEach(e -> System.out.println(e.getGroupByTimeValue() + "::" + e.getAmount() + "::" + e.getDayTime()));
	}

	@Test
	public void searchPayedUserGroupByMonthTest() {
		TestDatabase.INSTANCE.generateDB();
		ZonedDateTime now = ZonedDateTime.now();
		ZonedDateTime yesterday = now.minusDays(10);

		PaidUserGroupByForm form = new PaidUserGroupByForm();

		form.getCountryCodes().clear();
		form.getPackageNames().clear();
		form.setGroupByType(GroupByType.TIME);
		form.setGroupByTime("month");
		form.setType(null);

		form.setStartDate(null);
		form.setEndDate(TestDatabase.createDate(1, 1, 2070));

		PaidUsersService paidUsersService = new PaidUsersService();

		Collection<PaidUserGroupByVO> l = paidUsersService.searchGroupBy(form);
		List<PaidUserGroupByResult> r = paidUsersService.groupBy(l, form.getGroupByType(), form.getGroupByTime(),
				form.getGroupByLocale());
		paidUsersService.sortBy(r);

		r.forEach(e -> System.out.println(e.getGroupByTimeValue() + "::" + e.getAmount() + "::" + e.getDayTime()));
	}

	@Test
	public void searchPayedUserGroupByYearTest() {
		TestDatabase.INSTANCE.generateDB();
		ZonedDateTime now = ZonedDateTime.now();
		ZonedDateTime yesterday = now.minusDays(10);

		PaidUserGroupByForm form = new PaidUserGroupByForm();

		form.getCountryCodes().clear();
		form.getPackageNames().clear();
		form.setGroupByType(GroupByType.TIME);
		form.setGroupByTime("year");
		form.setType(null);
		form.setStartDate(null);
		form.setEndDate(TestDatabase.createDate(1, 1, 2070));

		PaidUsersService paidUsersService = new PaidUsersService();

		Collection<PaidUserGroupByVO> l = paidUsersService.searchGroupBy(form);
		List<PaidUserGroupByResult> r = paidUsersService.groupBy(l, form.getGroupByType(), form.getGroupByTime(),
				form.getGroupByLocale());

		paidUsersService.sortBy(r);

		r.forEach(e -> System.out.println(e.getGroupByTimeValue() + "::" + e.getAmount() + "::" + e.getDayTime()));
	}

	@Test
	public void searchPayedUserGroupByDayCountryTest() {
		TestDatabase.INSTANCE.generateDB();
		ZonedDateTime now = ZonedDateTime.now();
		ZonedDateTime yesterday = now.minusDays(10);

		PaidUserGroupByForm form = new PaidUserGroupByForm();

		form.getCountryCodes().clear();
		form.getPackageNames().clear();
		form.setGroupByType(GroupByType.ALL);
		form.setGroupByTime("day");
		form.setGroupByLocale("currency");
		form.setStartDate(null);
		form.setEndDate(Date.from(yesterday.toInstant()));

		PaidUsersService paidUsersService = new PaidUsersService();

		Collection<PaidUserGroupByVO> l = paidUsersService.searchGroupBy(form);
		List<PaidUserGroupByResult> r = paidUsersService.groupBy(l, form.getGroupByType(), form.getGroupByTime(),
				form.getGroupByLocale());

		paidUsersService.sortBy(r);

		r.forEach(e -> System.out.println(e.getGroupByTimeValue() + "::" + e.getAmount() + "::" + e.getDayTime()));
	}

	@Test
	public void searchPayedUserGroupByMonthCountryTest() {
		TestDatabase.INSTANCE.generateDB();
		ZonedDateTime now = ZonedDateTime.now();
		ZonedDateTime yesterday = now.minusDays(10);

		PaidUserGroupByForm form = new PaidUserGroupByForm();
		form.getCountryCodes().clear();
		form.getPackageNames().clear();
		form.setGroupByType(GroupByType.LOCALE);
		// form.setGroupByTime("month");
		form.setGroupByLocale("currency");
		form.setStartDate(null);
		form.setEndDate(Date.from(yesterday.toInstant()));

		PaidUsersService paidUsersService = new PaidUsersService();

		Collection<PaidUserGroupByVO> l = paidUsersService.searchGroupBy(form);
		Collection<PaidUserGroupByResult> r = paidUsersService.groupBy(l, form.getGroupByType(), form.getGroupByTime(),
				form.getGroupByLocale());

		r.forEach(e -> System.out
				.println(e.getGroupByTimeValue() + "::" + e.getGroupByLocaleValue() + "::" + e.getAmount()));
	}

	@Test
	public void searchPayedUserGroupByYearCountryTest() {
		TestDatabase.INSTANCE.generateDB();
		ZonedDateTime now = ZonedDateTime.now();
		ZonedDateTime yesterday = now.minusDays(10);

		PaidUserGroupByForm form = new PaidUserGroupByForm();

		form.getCountryCodes().clear();
		form.getPackageNames().clear();
		form.setType(null);
		form.setGroupByType(GroupByType.ALL);
		form.setGroupByTime("day");
		form.setGroupByLocale("country");
		form.setStartDate(null);
		form.setEndDate(Date.from(yesterday.toInstant()));

		PaidUsersService paidUsersService = new PaidUsersService();

		Collection<PaidUserGroupByVO> l = paidUsersService.searchGroupBy(form);
		List<PaidUserGroupByResult> r = paidUsersService.groupBy(l, form.getGroupByType(), form.getGroupByTime(),
				form.getGroupByLocale());
		paidUsersService.sortBy(r);

		r.forEach(e -> System.out
				.println(e.getGroupByTimeValue() + "::" + e.getGroupByLocaleValue() + "::" + e.getAmount()));
	}

	@Test
	public void exportPaiedUserTest() {
		TestDatabase.INSTANCE.generateDB();
		ZonedDateTime now = ZonedDateTime.now();
		ZonedDateTime yesterday = now.minusMonths(4);

		ExportService exportService = new ExportService();
		Collection<PaidUser> users = exportService.findPaidUsersByDate(Date.from(yesterday.toInstant()),
				Date.from(now.toInstant()));

	}

	@Test
	public void fixEURCurrencyTest() throws Exception {
		TestDatabase.INSTANCE.generateDB();
		
		PaymentRepository paymentRepository = new PaymentRepository();
		Collection<Entity> es=paymentRepository.findEntities("paid_users",null, null);
		es.forEach(e->{
			System.out.println(e.getProperty("eur_currency"));
		});
		
		PaidUserSearchForm form = new PaidUserSearchForm();
		form.setStartDate(TestDatabase.INSTANCE.createDate(1,1, 2011));
		form.setEndDate(TestDatabase.INSTANCE.createDate(1,1, 2027));
		form.getTypes().clear();
		form.setAmountFrom(null);
		form.setAmountTo(null);

		PaidUsersService paidUsersService = new PaidUsersService();
		Collection<PaidUser> paidUsers = paidUsersService.search(form);

		// map by date
		Map<String, List<PaidUser>> dateUsersMap = paidUsers.stream()
				.collect(Collectors.groupingBy(pu -> Utilities.formatedDate(pu.getDate(), "yyyy-MM-dd")));

		
		ImportService importService = new ImportService();

		for (Map.Entry<String, List<PaidUser>> entry : dateUsersMap.entrySet()) {

			// read exchange rates by date
			ExchangeRateVO rate = importService.getExchangeRates(entry.getKey(), "EUR");

			for (PaidUser paidUser : entry.getValue()) {
				//fix only currencies 
				if(!paidUser.getPaidCurrency().equalsIgnoreCase("EUR")){
					BigDecimal amount = new BigDecimal(paidUser.getAmount());
					BigDecimal rateValue = BigDecimal.valueOf(rate.getRates().get(paidUser.getPaidCurrency()));
					BigDecimal eurAmount = amount.divide(rateValue, 2, BigDecimal.ROUND_HALF_EVEN);

					Entity e = paymentRepository.findEntityByKey(paidUser.getKey());
					e.setProperty("eur_currency", eurAmount.doubleValue());
					paymentRepository.save(e);
				}
			}

		}
		es=paymentRepository.findEntities("paid_users",null, null);
		es.forEach(e->{			
			System.out.println(e.getProperty("amount")+"-"+e.getProperty("paid_currency")+"-"+e.getProperty("eur_currency"));
		});
	}

	@Test
	public void exportPaiedUserExternalTest() throws Exception {

		// String response =
		// ConnectionMgr.INSTANCE.getJSON("https://r.applovin.com/max/userAdRevenueReport?api_key=QuN5chUnh2cONoLJRB9oI8tu2bqrOhVqatvBZOzFQaepM-7pAHaSPSLR29GQsmFQd9cBXZRz94mV2uIC9tfTJ_&date=2021-01-05&platform=android&application=com.relaxingbraintraining.cookiejellymatch");
		// RevenueLinkVO revenue = JSONUtils.readObject(response,
		// RevenueLinkVO.class);
		// System.out.println(revenue.getUrl());
		// System.out.println(revenue.getAd_revenue_report_url());

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		for (int i = 0; i < 10000; i++) {
			Entity entity = new Entity("paid_users_external");
			entity.setProperty("date", new Date());
			entity.setProperty("eur_currency", 10.0);
			entity.setProperty("email_address", "email@" + i);
			entity.setProperty("payment_reference_id", "234244");
			entity.setProperty("invoice_number", "5434444");
			ds.put(entity);
		}
	}

	@Test
	public void unremovePaiedUserTest() throws Exception {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		UserRepository userRepository = new UserRepository();
		PaidUsersService paidUsersService = new PaidUsersService();

		Entity redeeming = new Entity("redeeming_requests_new");
		redeeming.setIndexedProperty("removal_reason", "Suspected fraud");
		redeeming.setIndexedProperty("user_guid", "12345");
		redeeming.setIndexedProperty("date", new Date());
		redeeming.setIndexedProperty("creation_date", new Date());
		redeeming.setIndexedProperty("type", "Removed");
		redeeming.setIndexedProperty("is_paid", false);
		redeeming.setIndexedProperty("max_rev", 1.2);
		redeeming.setIndexedProperty("confirmed_email", false);

		ds.put(redeeming);

		Assert.assertTrue(userRepository.findEntities("redeeming_requests_new", null, null).size() == 1);

		Collection<RedeemingRequests> list = paidUsersService.getRedeemingRequestsRemoved("12345", "Suspected fraud");
		Assert.assertTrue(list.size() == 1);

		userRepository.deleteEntity(list.iterator().next().getKey());

		Assert.assertTrue(userRepository.findEntities("redeeming_requests_new", null, null).size() == 0);

	}

	@Test
	public void getPaidUsersByDateAndTypeTest() throws Exception {
		TestDatabase.INSTANCE.generateDB();
		Date startDate=TestDatabase.INSTANCE.createDate(1,1, 2000);
		Date endDate=TestDatabase.INSTANCE.createDate(1,1, 2027);
		PaidUsersService paidUsersService=new PaidUsersService();
		Collection<PaidUser> list=paidUsersService.getPaidUsersByDateAndType("PayPal",startDate, endDate);
		list.forEach(e->System.out.println(e.getPaidCurrency()+"::"+e.getAmountNet()));
			
		Map<String,BigDecimal> localPlaySpotMap=list.stream().collect(Collectors.groupingBy(PaidUser::getPaidCurrency, Collectors.reducing(BigDecimal.ZERO, PaidUser::getAmountNet, BigDecimal::add)));
		System.out.println(localPlaySpotMap);
		Map<String,Double> result=list.stream().collect(Collectors.groupingBy(PaidUser::getPaidCurrency,Collectors.summingDouble(p->p.getAmountNet().doubleValue())));
		System.out.println(result);
	}
	
	@Test
	public void encodeEmailTest() throws Exception {
	  String email="sergei.iliev@gmail.com";	
	  String result=Utilities.encodeEmail(email);
	  
	  Assert.assertTrue(result.equals("sergei!iliev9gmail!com"));
	  
	  email="sergei.s.iliev@gmail.mega.com";	
	  result=Utilities.encodeEmail(email);
	  
	  Assert.assertTrue(result.equals("sergei!s!iliev9gmail!mega!com"));
	  
	}
	
	@Test
	public void dateETCTest() throws Exception {
		Date d=new Date();
		System.out.println(d);
		ZonedDateTime cet=Utilities.toCETZoneDateTime(d);
		System.out.println(cet);
		
		//ZonedDateTime date = ZonedDateTime.now();
        //System.out.println(date);
		
		
        //ZonedDateTime cetdate = utcToCet(date);
        //System.out.println(cetdate);
	}
	
	public static ZonedDateTime cetToUtc(ZonedDateTime timeInCet) {
		//ZonedDateTime cetTimeZoned = ZonedDateTime.of .of(timeInCet, ZoneId.of("CET"));
        return timeInCet.withZoneSameInstant(ZoneOffset.UTC);
    }

    public static ZonedDateTime utcToCet(ZonedDateTime timeInUtc) {
         //ZonedDateTime utcTimeZoned = ZonedDateTime.of(timeInUtc,ZoneId.of("UTC"));
         return timeInUtc.withZoneSameInstant(ZoneId.of("CET"));
     }
	
}
