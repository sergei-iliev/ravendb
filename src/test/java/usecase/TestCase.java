package usecase;

import java.io.Closeable;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.cloud.datastore.DatastoreOptions;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import net.paypal.integrate.AppEngineMemcacheClientService;
import net.paypal.integrate.PaypalApplication;
import net.paypal.integrate.command.csv.UserLevelRevenue;
import net.paypal.integrate.command.json.JSONUtils;
import net.paypal.integrate.command.json.RevenueLinkVO;
import net.paypal.integrate.entity.Affs;
import net.paypal.integrate.entity.PayPalUser;
import net.paypal.integrate.entity.RedeemingRequests;
import net.paypal.integrate.entity.UserDailyRevenue;
import net.paypal.integrate.repository.ObjectifyRepository;
import net.paypal.integrate.service.ConnectionMgr;
import net.paypal.integrate.service.ImportService;
import net.paypal.integrate.service.InvoiceService;
import net.paypal.integrate.service.UserRevenueService;



@RunWith(SpringRunner.class)
@SpringBootTest(
		  classes = PaypalApplication.class)
public class TestCase{
	private Closeable session;
	
	private final LocalServiceTestHelper helper =
	            new LocalServiceTestHelper(
	                    new LocalMemcacheServiceTestConfig()); //memcache

	 
//	@TestConfiguration
//    static class RepositoryImplTestContextConfiguration {
//  
//        @Bean
//        public InvoiceService getInvoiceService() {
//            return new InvoiceService();
//        }
//        @Bean
//        public ImportService getImportService() {
//            return new ImportService();
//        }
//
//
//    }
	
	@Before
	public void initialize(){
		helper.setUp();
        ObjectifyService.init(new ObjectifyFactory(
                DatastoreOptions.newBuilder().setHost("http://localhost:8884")
                    .setProjectId("sapient-office-232912")
                    .build().getService(),
                    new AppEngineMemcacheClientService()
            ));
                

		 ObjectifyService.register(RedeemingRequests.class);
		 ObjectifyService.register(PayPalUser.class);
		 ObjectifyService.register(UserDailyRevenue.class);
		 ObjectifyService.register(Affs.class);
		 
	      session=ObjectifyService.begin();
	}
	@After
	public void release()throws Exception{
		session.close();
		helper.tearDown();
	}
	
	@Autowired
	private ObjectifyRepository objectifyRepository;

	@Autowired
	private ImportService importService;
	
	@Autowired
	private UserRevenueService userRevenueService;
	
	@Test
	public void createDemoTest() throws Exception{
	  userRevenueService.createUserRevenueDEMO("2019-05-28");	
	}
	@Test
	public void processUserRevenueTest() throws Exception{
	    userRevenueService.processUserRevenue("2019-06-01");	
	}
	@Test
	public void connectionMgrTest() throws Exception{
//		String result=ConnectionMgr.INSTANCE.getJSON(u);
//		
//		RevenueVO revenue=JSONUtils.readObject(result, RevenueVO.class);
//		
//	    System.out.println(revenue.getUrl());
//	    
//	    result=ConnectionMgr.INSTANCE.getCSV(revenue.getUrl());
//	    
//	    Collection<UserLevelRevenue> csv= importService.importCSVText(result);
//	    csv.forEach(c->System.out.println(c.getRevenue()));
	    


	    
	}
		
}