package usecase;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.luee.wally.admin.repository.PaidUsersRepository;
import com.luee.wally.api.service.PaidUsersService;
import com.luee.wally.api.service.PaymentReportsService;
import com.luee.wally.command.PaidUserSearchForm;
import com.luee.wally.utils.TestDatabase;

public class PaymentReports {

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
        PaymentReportsService paymentReportsService=new PaymentReportsService();
        System.out.println(paymentReportsService.getPaymentReportForYesterday().getTotalAmountByCountryCodeMap());
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
}
