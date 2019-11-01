package usecase;

import java.io.Closeable;
import java.io.FileOutputStream;
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
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.DatastoreOptions;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import net.paypal.integrate.AppEngineMemcacheClientService;
import net.paypal.integrate.PaypalApplication;
import net.paypal.integrate.command.PdfAttachment;
import net.paypal.integrate.command.csv.PaidUsers2018;
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
	
//	@Test
//	public void createDemoTest() throws Exception{
//	  userRevenueService.createUserRevenueDEMO("2019-05-28");	
//	}
	
	//1.Ad Unit ID,IDFA,IDFV,User ID,Revenue,Impressions
	//2.Ad Unit ID,Placement,IDFA,IDFV,User ID,Revenue,Impressions
	
	@Test
	public void processUserRevenueAggregatedTest() throws Exception{
	    userRevenueService.processUserRevenueAggregated("2019-06-13");	
	}	
	@Test
	public void createPdfTest() throws Exception{
        RedeemingRequests redeemingRequests=new RedeemingRequests();
        redeemingRequests.setDate(Timestamp.of(new Date()));
        
        Thread.sleep(1000);
        redeemingRequests.setAmount("12");
        redeemingRequests.setCountryCode("USD");
        redeemingRequests.setEmail("pegi@yahoo.lcom");
        redeemingRequests.setFrom("Belarus");
        redeemingRequests.setFullAddress("Baba Tonka 7");
        redeemingRequests.setFullName("Benjamin Franclin");        
        redeemingRequests.setCreationDate(Timestamp.of(new Date()));
		redeemingRequests.setUserGuid("c59ef40b-b4d8-4d62-a3d4-bee3646d4932");
        
        PaidUsers2018 paidUsers2018=new PaidUsers2018();
        paidUsers2018.setDate("19-19-2012");
        paidUsers2018.setPayedAmount("23");
        
        PdfAttachment attachment=new PdfAttachment();
		
		InvoiceService invoiceService=new InvoiceService();
		
		String invoiceNumber="0000012334";
		
        attachment.setFileName("PaidUsers2018_"+invoiceNumber+".pdf");
        attachment.setContentType("application/pdf");  
        
        
        attachment.readFromStream(invoiceService.createInvoice(redeemingRequests,paidUsers2018,invoiceNumber)); 
	
        try (FileOutputStream fos = new FileOutputStream("D:\\"+attachment.getFileName())) {
        	   fos.write(attachment.getBuffer());        	   
        }
     
       
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