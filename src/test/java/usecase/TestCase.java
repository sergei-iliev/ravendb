package usecase;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.cloud.datastore.DatastoreOptions;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import net.paypal.integrate.AppEngineMemcacheClientService;
import net.paypal.integrate.api.GenerateCSV;
import net.paypal.integrate.command.csv.PaidUsers2018;
import net.paypal.integrate.entity.RedeemingRequests;
import net.paypal.integrate.service.ImportService;



@RunWith(SpringRunner.class)
public class TestCase{
	private Closeable session;
	
	private final LocalServiceTestHelper helper =
	            new LocalServiceTestHelper(
	                    new LocalMemcacheServiceTestConfig()); //memcache

	 
	@TestConfiguration
    static class RepositoryImplTestContextConfiguration {
  
        @Bean
        public ImportService getImportService() {
            return new ImportService();
        }
//        
//        @Bean
//        public BoardRepository getBoardRepository() {
//            return new BoardRepository();
//        }
//        
//        @Bean
//        public UserRepository getUserRepository() {
//            return new UserRepository();
//        }
    }
	
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

	      session=ObjectifyService.begin();
	}
	@After
	public void release()throws Exception{
		session.close();
		helper.tearDown();
	}
	@Autowired
	private  ImportService importService;
	
	private static final String IMPORT_CSV_FILE = "/csv/paid_users_2018.csv";
	
	
	@Test
	public void testCSVRead() throws Exception{
	   
		Collection<PaidUsers2018> items=importService.importFile();
		
		
		
//		URL fileUrl = getClass().getResource(IMPORT_CSV_FILE);
//		  File file= new File(fileUrl.getFile());
//		  try (BufferedReader br = new BufferedReader(new FileReader(file))) {
//			 List<Collection<String>> items= GenerateCSV.INSTANCE.readLines(br);
			 items.forEach(e->{
			 try{
			 System.out.println(e.getDateToTimestamp());
			 }catch(ParseException ee){ee.printStackTrace();}
			 });
//
//		  } 
		

	}
}