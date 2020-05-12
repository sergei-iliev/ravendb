package usecase;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.luee.wally.api.service.PaidUsersService;
import com.luee.wally.command.PaidUserGroupByForm;
import com.luee.wally.command.PaidUserGroupByForm.GroupByType;
import com.luee.wally.command.PaidUserGroupByResult;
import com.luee.wally.command.viewobject.PaidUserGroupByVO;
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
		form.getTypes().clear();
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
		form.getTypes().clear();
		form.getCountryCodes().clear();
		form.getPackageNames().clear();
		form.setGroupByType(GroupByType.TIME);
		form.setGroupByTime("day");
		
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
        
        r.forEach(e->System.out.println(e.getGroupByTimeValue()+"::"+e.getAmount()));
	}
	@Test
	public void searchPayedUserGroupByMonthTest(){				
		TestDatabase.INSTANCE.generateDB();
		ZonedDateTime now=ZonedDateTime.now();
		ZonedDateTime yesterday=now.minusDays(10);
		   
		PaidUserGroupByForm form=new PaidUserGroupByForm();
		form.getTypes().clear();
		form.getCountryCodes().clear();
		form.getPackageNames().clear();
		form.setGroupByType(GroupByType.TIME);
		form.setGroupByTime("month");
		
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
        
        r.forEach(e->System.out.println(e.getGroupByTimeValue()+"::"+e.getAmount()));
	}	
	@Test
	public void searchPayedUserGroupByYearTest(){				
		TestDatabase.INSTANCE.generateDB();
		ZonedDateTime now=ZonedDateTime.now();
		ZonedDateTime yesterday=now.minusDays(10);
		   
		PaidUserGroupByForm form=new PaidUserGroupByForm();
		form.getTypes().clear();
		form.getCountryCodes().clear();
		form.getPackageNames().clear();
		form.setGroupByType(GroupByType.TIME);
		form.setGroupByTime("year");
		
		form.setStartDate(null);
		form.setEndDate(Date.from(yesterday.toInstant()));
		
		
        PaidUsersService paidUsersService=new PaidUsersService();
        
        Collection<PaidUserGroupByVO> l=paidUsersService.searchGroupBy(form);
        Collection<PaidUserGroupByResult> r=paidUsersService.groupBy(l, form.getGroupByType(),form.getGroupByTime(), form.getGroupByLocale());
        
        r.forEach(e->System.out.println(e.getGroupByTimeValue()+"::"+e.getAmount()));
	}	
	@Test
	public void searchPayedUserGroupByDayCountryTest(){				
		TestDatabase.INSTANCE.generateDB();
		ZonedDateTime now=ZonedDateTime.now();
		ZonedDateTime yesterday=now.minusDays(10);
		   
		PaidUserGroupByForm form=new PaidUserGroupByForm();
		form.getTypes().clear();
		form.getCountryCodes().clear();
		form.getPackageNames().clear();
		form.setGroupByType(GroupByType.ALL);
		form.setGroupByTime("day");
		form.setGroupByLocale("currency");
		form.setStartDate(null);
		form.setEndDate(Date.from(yesterday.toInstant()));
		
		
        PaidUsersService paidUsersService=new PaidUsersService();
        
        Collection<PaidUserGroupByVO> l=paidUsersService.searchGroupBy(form);
        Collection<PaidUserGroupByResult> r=paidUsersService.groupBy(l, form.getGroupByType(),form.getGroupByTime(), form.getGroupByLocale());
        
        r.forEach(e->System.out.println(e.getGroupByTimeValue()+"::"+e.getGroupByLocaleValue()+"::"+e.getAmount()));
	}
	@Test
	public void searchPayedUserGroupByMonthCountryTest(){				
		TestDatabase.INSTANCE.generateDB();
		ZonedDateTime now=ZonedDateTime.now();
		ZonedDateTime yesterday=now.minusDays(10);
		   
		PaidUserGroupByForm form=new PaidUserGroupByForm();
		form.getTypes().clear();
		form.getCountryCodes().clear();
		form.getPackageNames().clear();
		form.setGroupByType(GroupByType.ALL);
		form.setGroupByTime("month");
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
		form.getTypes().clear();
		form.getCountryCodes().clear();
		form.getPackageNames().clear();
		form.setGroupByType(GroupByType.ALL);
		form.setGroupByTime("year");
		form.setGroupByLocale("country");
		form.setStartDate(null);
		form.setEndDate(Date.from(yesterday.toInstant()));
		
		
        PaidUsersService paidUsersService=new PaidUsersService();
        
        Collection<PaidUserGroupByVO> l=paidUsersService.searchGroupBy(form);
        Collection<PaidUserGroupByResult> r=paidUsersService.groupBy(l, form.getGroupByType(),form.getGroupByTime(), form.getGroupByLocale());
        
        r.forEach(e->System.out.println(e.getGroupByTimeValue()+"::"+e.getGroupByLocaleValue()+"::"+e.getAmount()));
	}		
}
