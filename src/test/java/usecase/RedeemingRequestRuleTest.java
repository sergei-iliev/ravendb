package usecase;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.luee.wally.admin.repository.PaidUsersRepository;
import com.luee.wally.api.rule.redeemingrequest.RedeemingRequestEngine;
import com.luee.wally.api.rule.redeemingrequest.RuleResultType;
import com.luee.wally.api.service.PaidUsersService;
import com.luee.wally.api.service.PaymentReportsService;
import com.luee.wally.api.service.PaymentService;
import com.luee.wally.command.PaidUserSearchForm;
import com.luee.wally.command.PaymentEligibleUserForm;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.utils.TestDatabase;
import com.luee.wally.utils.Utilities;

public class RedeemingRequestRuleTest {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	@Before
	public void initialize() {
		Utilities.domain="test";
		helper.setUp();

	}

	@After
	public void release() throws Exception {

		helper.tearDown();
	}

	@Test
	public void timeToCashLess24OrganicRuleTest(){
		TestDatabase.INSTANCE.generateDB();
		PaymentService paymentService = new PaymentService();
		
		PaymentEligibleUserForm form = new PaymentEligibleUserForm();
		form.setStartDate(null);
		form.setEndDate(null);
		Collection<RedeemingRequests> entities = paymentService.searchEligibleUsers(form);
        
		RedeemingRequestEngine engine=new RedeemingRequestEngine();
		for(RedeemingRequests redeemingRequests:entities){			
			Date date=(redeemingRequests.getDate());
			ZonedDateTime now=ZonedDateTime.now();
			ZonedDateTime yesterday=now.minusHours(22);
			redeemingRequests.setCreationDate(Date.from(yesterday.toInstant()));
			
			
			//List<RuleResultType> list=engine.execute(redeemingRequests,true);
		    //System.out.println(list);
		}
	}
	

	@Test
	public void nameOrAddressOrIpRuleTest(){
		TestDatabase.INSTANCE.generateDB();
		PaymentService paymentService = new PaymentService();
		
		PaymentEligibleUserForm form = new PaymentEligibleUserForm();
		form.setStartDate(null);
		form.setEndDate(null);
		Collection<RedeemingRequests> entities = paymentService.searchEligibleUsers(form);
        
		RedeemingRequestEngine engine=new RedeemingRequestEngine();
		for(RedeemingRequests redeemingRequests:entities){								
		    System.out.println(engine.execute(redeemingRequests,false));		    
		}
		
	}	

}
