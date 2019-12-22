package usecase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.luee.wally.admin.repository.PaidUsersRepository;
import com.luee.wally.api.service.PaymentReportsService;
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
        paymentReportsService.getPaymentReportForYesterday();
	}

	@Test
	public void getPayedUsersByEmailOrPayPalTest(){
		TestDatabase.INSTANCE.generateDB();
        PaidUsersRepository paidUsersRepository=new PaidUsersRepository();
        System.out.println(paidUsersRepository.findPaidUsersByEmail("mikelo@yahoo.com","mikelo@yahoo.com"));
	}	
}
