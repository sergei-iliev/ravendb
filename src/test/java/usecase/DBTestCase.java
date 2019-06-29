package usecase;

import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
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
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.cloud.datastore.DatastoreOptions;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import net.paypal.integrate.AppEngineMemcacheClientService;
import net.paypal.integrate.PaypalApplication;
import net.paypal.integrate.api.GenerateCSV;
import net.paypal.integrate.command.Attachment;
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




public class DBTestCase{
	private Closeable session;
	
	  private final LocalServiceTestHelper helper =
		      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	 

	
	@Before
	public void initialize(){
		helper.setUp();

	}
	@After
	public void release()throws Exception{

		helper.tearDown();
	}
	


	@Test
	public void createDBTest() throws Exception{
		 DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		 Entity affs=new Entity("affs");
		 affs.setProperty("total",1.0d);		 
		 ds.put(affs);
		
		
		 Assert.assertEquals(1, ds.prepare(new Query("affs")).countEntities(FetchOptions.Builder.withLimit(10)));
		  
		 EmbeddedEntity embeddedEntity=new EmbeddedEntity();
		 Set<String> s=new HashSet<>();
		 s.add("2007-12-12");
		 s.add("2013-11-11");
		 embeddedEntity.setProperty("package.1.x",s);
		 
		 s=new HashSet<>();
		 int m=1900;
		 for(int i=0;i<400;i++){
		     m++; 
			 s.add(m+"-03-17");
		 }	 
		 embeddedEntity.setProperty("package.2.x",s);
		 
		 Entity user_history=new Entity("user_history");
		 user_history.setProperty("affsKey", affs.getKey());
		 user_history.setProperty("history",embeddedEntity);

		 user_history.setProperty("guid","123456789");
		 
		 ds.put(user_history);
		 
		List<Entity> e= ds.prepare(new Query("user_history").addFilter("guid", FilterOperator.EQUAL, "123456789")).asList(FetchOptions.Builder.withDefaults());
		EmbeddedEntity em= (EmbeddedEntity)e.get(0).getProperty("history");
		List<String> dates=(List<String>)em.getProperty("package.2.x");
		System.out.println(dates.contains("2281-03-17"));;
		 
	}
	/*
	 * Export from last 9 months all guid's from table affs and save in Cloud Store 
	 */
	@Test
	public void createAffsGuidDBTest() throws Exception{
		 DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		 
		 for(int i=0;i<5002;i++){
		  String uniqueID = UUID.randomUUID().toString();			 
		  Entity affs=new Entity("affs");
		  affs.setIndexedProperty("guid",uniqueID);
		  Thread.currentThread().sleep(1);
		  affs.setProperty("created",new Date());
		  System.out.println(i);
		  ds.put(affs);		 
		 }

		 //read huge result set in batches
		 
		 Cursor cursor=null;
		 QueryResultList<Entity> results;
		 StringWriter sw=new StringWriter();
	     do{
	     FetchOptions fetchOptions;	 
	     if(cursor!=null){	 
	        fetchOptions = FetchOptions.Builder.withLimit(100).startCursor(cursor);
	     }else{
	        fetchOptions = FetchOptions.Builder.withLimit(100);	 
	     }
    	 Query query = new Query("affs");
	     PreparedQuery preparedQuery = ds.prepare(query);
	     results = preparedQuery.asQueryResultList(fetchOptions);
	     
	     for(Entity e:results){
	    	 if(e.getProperty("guid")!=null) {
	           GenerateCSV.INSTANCE.writeLine(sw, (String)e.getProperty("guid"));
	    	 }
	     }

		 cursor=results.getCursor();
	     }while(results.size()>0);
			  
	     
		 //save in byte array
		 Attachment attachment=new Attachment();
		 attachment.setContentType("text/plain");
		 attachment.setFileName("Last9.txt");
		 attachment.readFromStringWriter(sw);
		 
		 try (FileOutputStream fos = new FileOutputStream("d:\\dddd.txt")) {
			   fos.write(attachment.getBuffer());			   
	     }
		 
	}
	
	
		
}