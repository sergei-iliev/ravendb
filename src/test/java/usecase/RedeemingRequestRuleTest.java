package usecase;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.luee.wally.admin.repository.PaidUsersRepository;
import com.luee.wally.admin.repository.PaymentRepository;
import com.luee.wally.api.rule.redeemingrequest.RedeemingRequestEngine;
import com.luee.wally.api.rule.redeemingrequest.RuleResultType;
import com.luee.wally.api.service.PaidUsersService;
import com.luee.wally.api.service.PaymentReportsService;
import com.luee.wally.api.service.PaymentService;
import com.luee.wally.command.PaidUserSearchForm;
import com.luee.wally.command.PaymentEligibleUserForm;
import com.luee.wally.constants.Constants;
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
	public void adRevRuleTest(){
		TestDatabase.INSTANCE.generateDB();

		
		PaymentService paymentService = new PaymentService();
		
		PaymentEligibleUserForm form = new PaymentEligibleUserForm();
		form.setStartDate(null);
		form.setEndDate(null);
		form.getPackageNames().clear();
		form.getCountryCodes().clear();
		
		
		
		Collection<RedeemingRequests> entities = paymentService.searchEligibleUsers(form);
		
		RedeemingRequestEngine engine=new RedeemingRequestEngine();
		for(RedeemingRequests redeemingRequests:entities){			
			Collection<RuleResultType> list=engine.execute(redeemingRequests,false);	
		}
		
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
	public void nameOrAddressOrIpRuleTest()throws Exception{
		//PaidUsersService paymentUsersService=new PaidUsersService();
		//https://ps-forwarding-server.appspot.com/c/s?ip=2001:8a0:b96b:5300:18a1:77d9:fc88:7d3&country=PT
		//paymentUsersService.checkVPNUsageAsync(null,"128.90.22.179", "GB");
		
		TestDatabase.INSTANCE.generateDB();

		PaymentService paymentService = new PaymentService();
		PaymentEligibleUserForm form = new PaymentEligibleUserForm();
		form.getPackageNames().clear();
		form.getCountryCodes().clear();
		form.getCountryCodes().add("FR");
		form.setStartDate(null);
		form.setEndDate(null);
		Collection<RedeemingRequests> entities = paymentService.searchEligibleUsers(form);
        
		RedeemingRequestEngine engine=new RedeemingRequestEngine();
		for(RedeemingRequests redeemingRequests:entities){								
		    System.out.println(engine.execute(redeemingRequests,false));		    
		}
		
	}	
	@Test
	public void userCountriesConnectedTest()throws Exception{
		List<String> countriesConnectedFrom=Arrays.asList("BG","FR");						
		boolean forbidden = countriesConnectedFrom.stream().anyMatch(element -> !Constants.ALLOWED_USER_COUNTIES_CONNECTION_FROM.contains(element));
        Assert.assertTrue(forbidden);
		
	}
}
