package usecase;

import java.io.Closeable;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.paypal.integrate.admin.service.AffsSearchService;




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
	public void createAffsCountEntityTest() throws Exception{
		 DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		 for(int i=0;i<10001;i++){
			 
		 Entity affs=new Entity("affs");
		  affs.setProperty("total_ad_rev",1.4d);
		  affs.setProperty("date",new Date());
		  affs.setProperty("country_code","US");
		  affs.setProperty("experiment","preview_images");
		  affs.setProperty("package_name","com.moregames.makemoney");
		  if(i==1000){
			  affs.setProperty("country_code","BG");
		  }
		  ds.put(affs);
		 }
		
    	 Query query = new Query("affs");
	     query.setFilter(new FilterPredicate("country_code", FilterOperator.EQUAL, "BG"));
    	 PreparedQuery preparedQuery = ds.prepare(query);
	     
	     int count = preparedQuery.countEntities(FetchOptions.Builder.withDefaults());
		 Assert.assertTrue(count==1);
	     
	}
	
	@Test
	public void searchAffsEntityTest() throws Exception{
		 DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		 for(int i=0;i<2004;i++){
			 
		 Entity affs=new Entity("affs");
		  affs.setProperty("id", i);
		  affs.setProperty("total_ad_rev",1100.4d);
		  affs.setIndexedProperty("date",new Date());
		  affs.setIndexedProperty("country_code","US");
		  affs.setIndexedProperty("experiment","preview_images");
		  affs.setIndexedProperty("package_name","com.moregames.makemoney");
		  if(i==1000){
			  affs.setIndexedProperty("country_code","BG");
			  affs.setIndexedProperty("package_name","boo");
		  }
		  ds.put(affs);
		 }
		
         AffsSearchService affsSearchService=new AffsSearchService();
         affsSearchService.processAffsSearch(null, new Date(),null, null, "boo");
	     
	}

	

		
}