package usecase;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.luee.wally.admin.repository.PaidUsersRepository;
import com.luee.wally.api.ConnectionMgr;
import com.luee.wally.api.service.PaidUsersService;
import com.luee.wally.api.service.impex.ExportService;
import com.luee.wally.command.PaidUserGroupByForm;
import com.luee.wally.command.PaidUserGroupByForm.GroupByType;
import com.luee.wally.command.PaidUserGroupByResult;
import com.luee.wally.command.viewobject.PaidUserGroupByVO;
import com.luee.wally.entity.PaidUser;
import com.luee.wally.entity.PaidUserExternal;
import com.luee.wally.json.JSONUtils;
import com.luee.wally.json.RevenueLinkVO;
import com.luee.wally.utils.TestDatabase;

public class PayedUserTest {

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
	public void searchPayedUserGroupByLocaleTest(){				
		TestDatabase.INSTANCE.generateDB();
		ZonedDateTime now=ZonedDateTime.now();
		ZonedDateTime yesterday=now.minusDays(10);
		   
		PaidUserGroupByForm form=new PaidUserGroupByForm();

		form.getCountryCodes().clear();
		form.getPackageNames().clear();
		form.setGroupByType(GroupByType.LOCALE);
		form.setGroupByLocale("country");
		
		form.setStartDate(null);
		form.setEndDate(Date.from(yesterday.toInstant()));
		
		//form.setAmountFrom("0.9");
		//form.setAmountTo("1");
		//form.getTypes().add("PayPal");
		
		//form.getPackageNames().add("com.moregames.makemoney1");
		//form.getPackageNames().add("com.moregames.makemoney2");
		
		//form.getCountryCodes().add("DE");
		//form.getCountryCodes().add("US");
		
        PaidUsersService paidUsersService=new PaidUsersService();
        
        Collection<PaidUserGroupByVO> l=paidUsersService.searchGroupBy(form);
        Collection<PaidUserGroupByResult> r=paidUsersService.groupBy(l, form.getGroupByType(),form.getGroupByTime(), form.getGroupByLocale());
        
        r.forEach(e->System.out.println(e.getGroupByLocaleValue()+"::"+e.getAmount()));
	}
	
	@Test
	public void searchPayedUserGroupByDayTest(){				
		TestDatabase.INSTANCE.generateDB();
		ZonedDateTime now=ZonedDateTime.now();
		ZonedDateTime yesterday=now.minusDays(10);
		   
		PaidUserGroupByForm form=new PaidUserGroupByForm();

		form.getCountryCodes().clear();
		form.getPackageNames().clear();
		form.setType(null);
		form.setGroupByType(GroupByType.TIME);
		form.setGroupByTime("day");
		
		form.setStartDate(null);
		form.setEndDate(TestDatabase.createDate(1,1, 2070));
		
        PaidUsersService paidUsersService=new PaidUsersService();
        
        Collection<PaidUserGroupByVO> l=paidUsersService.searchGroupBy(form);
        List<PaidUserGroupByResult> r=paidUsersService.groupBy(l, form.getGroupByType(),form.getGroupByTime(), form.getGroupByLocale());
        paidUsersService.sortBy(r);
        
        r.forEach(e->System.out.println(e.getGroupByTimeValue()+"::"+e.getAmount()+"::"+e.getDayTime()));
	}
	@Test
	public void searchPayedUserGroupByMonthTest(){				
		TestDatabase.INSTANCE.generateDB();
		ZonedDateTime now=ZonedDateTime.now();
		ZonedDateTime yesterday=now.minusDays(10);
		   
		PaidUserGroupByForm form=new PaidUserGroupByForm();

		form.getCountryCodes().clear();
		form.getPackageNames().clear();
		form.setGroupByType(GroupByType.TIME);
		form.setGroupByTime("month");
		form.setType(null);
		
		form.setStartDate(null);
		form.setEndDate(TestDatabase.createDate(1,1, 2070));
		

		
        PaidUsersService paidUsersService=new PaidUsersService();
        
        Collection<PaidUserGroupByVO> l=paidUsersService.searchGroupBy(form);
        List<PaidUserGroupByResult> r=paidUsersService.groupBy(l, form.getGroupByType(),form.getGroupByTime(), form.getGroupByLocale());
        paidUsersService.sortBy(r);
        
        r.forEach(e->System.out.println(e.getGroupByTimeValue()+"::"+e.getAmount()+"::"+e.getDayTime()));
	}	
	@Test
	public void searchPayedUserGroupByYearTest(){				
		TestDatabase.INSTANCE.generateDB();
		ZonedDateTime now=ZonedDateTime.now();
		ZonedDateTime yesterday=now.minusDays(10);
		   
		PaidUserGroupByForm form=new PaidUserGroupByForm();

		form.getCountryCodes().clear();
		form.getPackageNames().clear();
		form.setGroupByType(GroupByType.TIME);
		form.setGroupByTime("year");
		form.setType(null);
		form.setStartDate(null);
		form.setEndDate(TestDatabase.createDate(1,1, 2070));
		
		
        PaidUsersService paidUsersService=new PaidUsersService();
        
        Collection<PaidUserGroupByVO> l=paidUsersService.searchGroupBy(form);
        List<PaidUserGroupByResult> r=paidUsersService.groupBy(l, form.getGroupByType(),form.getGroupByTime(), form.getGroupByLocale());
        
        paidUsersService.sortBy(r);
        
        r.forEach(e->System.out.println(e.getGroupByTimeValue()+"::"+e.getAmount()+"::"+e.getDayTime()));
	}	
	@Test
	public void searchPayedUserGroupByDayCountryTest(){				
		TestDatabase.INSTANCE.generateDB();
		ZonedDateTime now=ZonedDateTime.now();
		ZonedDateTime yesterday=now.minusDays(10);
		   
		PaidUserGroupByForm form=new PaidUserGroupByForm();
	
		form.getCountryCodes().clear();
		form.getPackageNames().clear();
		form.setGroupByType(GroupByType.ALL);
		form.setGroupByTime("day");
		form.setGroupByLocale("currency");
		form.setStartDate(null);
		form.setEndDate(Date.from(yesterday.toInstant()));
		
		
        PaidUsersService paidUsersService=new PaidUsersService();
        
        Collection<PaidUserGroupByVO> l=paidUsersService.searchGroupBy(form);
        List<PaidUserGroupByResult> r=paidUsersService.groupBy(l, form.getGroupByType(),form.getGroupByTime(), form.getGroupByLocale());
        
        paidUsersService.sortBy(r);
        
        r.forEach(e->System.out.println(e.getGroupByTimeValue()+"::"+e.getAmount()+"::"+e.getDayTime()));
	}
	@Test
	public void searchPayedUserGroupByMonthCountryTest(){				
		TestDatabase.INSTANCE.generateDB();
		ZonedDateTime now=ZonedDateTime.now();
		ZonedDateTime yesterday=now.minusDays(10);
		   
		PaidUserGroupByForm form=new PaidUserGroupByForm();
		form.getCountryCodes().clear();
		form.getPackageNames().clear();
		form.setGroupByType(GroupByType.LOCALE);
		//form.setGroupByTime("month");
		form.setGroupByLocale("currency");
		form.setStartDate(null);
		form.setEndDate(Date.from(yesterday.toInstant()));
		
		
        PaidUsersService paidUsersService=new PaidUsersService();
        
        Collection<PaidUserGroupByVO> l=paidUsersService.searchGroupBy(form);
        Collection<PaidUserGroupByResult> r=paidUsersService.groupBy(l, form.getGroupByType(),form.getGroupByTime(), form.getGroupByLocale());
        
        r.forEach(e->System.out.println(e.getGroupByTimeValue()+"::"+e.getGroupByLocaleValue()+"::"+e.getAmount()));
	}	
	@Test
	public void searchPayedUserGroupByYearCountryTest(){				
		TestDatabase.INSTANCE.generateDB();
		ZonedDateTime now=ZonedDateTime.now();
		ZonedDateTime yesterday=now.minusDays(10);
		   
		PaidUserGroupByForm form=new PaidUserGroupByForm();
	
		form.getCountryCodes().clear();
		form.getPackageNames().clear();
		form.setType(null);
		form.setGroupByType(GroupByType.ALL);
		form.setGroupByTime("day");
		form.setGroupByLocale("country");
		form.setStartDate(null);
		form.setEndDate(Date.from(yesterday.toInstant()));
		
		
        PaidUsersService paidUsersService=new PaidUsersService();
        
        Collection<PaidUserGroupByVO> l=paidUsersService.searchGroupBy(form);
        List<PaidUserGroupByResult> r=paidUsersService.groupBy(l, form.getGroupByType(),form.getGroupByTime(), form.getGroupByLocale());
        paidUsersService.sortBy(r);
        
        r.forEach(e->System.out.println(e.getGroupByTimeValue()+"::"+e.getGroupByLocaleValue()+"::"+e.getAmount()));
	}	
	
	@Test
	public void exportPaiedUserTest(){	
		TestDatabase.INSTANCE.generateDB();
		ZonedDateTime now=ZonedDateTime.now();
		ZonedDateTime yesterday=now.minusMonths(4);
		
		ExportService exportService=new ExportService();
		Collection<PaidUser> users=exportService.findPaidUsersByDate(Date.from(yesterday.toInstant()), Date.from(now.toInstant()));		
		
	}
	@Test
	public void exportPaiedUserExternalTest() throws Exception{
		
//		String response = ConnectionMgr.INSTANCE.getJSON("https://r.applovin.com/max/userAdRevenueReport?api_key=QuN5chUnh2cONoLJRB9oI8tu2bqrOhVqatvBZOzFQaepM-7pAHaSPSLR29GQsmFQd9cBXZRz94mV2uIC9tfTJ_&date=2021-01-05&platform=android&application=com.relaxingbraintraining.cookiejellymatch");
//		RevenueLinkVO revenue = JSONUtils.readObject(response, RevenueLinkVO.class);
//		System.out.println(revenue.getUrl());
//		System.out.println(revenue.getAd_revenue_report_url());
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		
		for(int i=0;i<10000;i++){
			Entity entity = new Entity("paid_users_external");
			entity.setProperty("date", new Date());
			entity.setProperty("eur_currency", 10.0);
			entity.setProperty("email_address","email@"+i);
			entity.setProperty("payment_reference_id", "234244");
			entity.setProperty("invoice_number", "5434444");				
			ds.put(entity);
		}
	}
}
