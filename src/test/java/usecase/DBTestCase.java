package usecase;

import java.io.Closeable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


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
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
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
	
	public void processUserRevenue(String date)throws Exception{
		Objects.requireNonNull(date, "Date to process daily revenue is null");
		
		// for each link read csv
		Collection<RevenueLinkVO> revenueLinks = this.getRevenueLinks(date);
		for (RevenueLinkVO revenueLink : revenueLinks) {
			Collection<UserLevelRevenue> userLevelRevenues = getUserLevelRevenues(revenueLink);
			for (UserLevelRevenue userLevelRevenue : userLevelRevenues) {				
				
				//read user revenue history table
				Entity affs=DB.getAffForGaid(userLevelRevenue.getIDFA());
				if(affs==null){  //no user present
					continue;
				}
				else {
					logger.warning("found user:"+affs.getProperty("user_guid"));
				}
				
				Entity userDailyRevenue = DB
						.getUserDailyRevenueByGaid(userLevelRevenue.getIDFA());
				if(userDailyRevenue==null){
			    	userDailyRevenue = new Entity("user_daily_revenue");
					userDailyRevenue.setProperty("gaid", userLevelRevenue.getIDFA());
					userDailyRevenue.setProperty("aff_key",affs.getKey());										 
					userDailyRevenue.setProperty("rev_check_dates",new EmbeddedEntity());	
				}
				
				EmbeddedEntity  historyMap=(EmbeddedEntity) userDailyRevenue.getProperty("rev_check_dates");
				if(historyMap==null||historyMap.getProperty(revenueLink.getPackageName())==null){  //1. no entry at all
					//create history entry for first time
					logger.warning("no entry at all");

					List<String> list = new ArrayList<>(1);
					list.add(date);		
					historyMap.setProperty(revenueLink.getPackageName(), list);										
					
					userDailyRevenue.setProperty("rev_check_dates", historyMap);	

					//accumulate revenue
				    this.updateUserRevenue(affs, userDailyRevenue, userLevelRevenue.getRevenue());					
				}else if(historyMap.getProperty(revenueLink.getPackageName())!=null){  //2.there is a package -> check date history
					logger.warning("there is a package -> check date history");

					List<String> list = (List<String>)historyMap.getProperty(revenueLink.getPackageName());
					if(list==null){
						list = new ArrayList<>(1);									
					}
					Set<String> aset = new HashSet<String>(list);
					if(aset.contains(date)){   //date is registered already. Second run? 
						continue;
					}
					
					list.add(date);
					historyMap.setProperty(revenueLink.getPackageName(), list);										
					
					userDailyRevenue.setProperty("rev_check_dates", historyMap);	

					//accumulate revenue
				    this.updateUserRevenue(affs, userDailyRevenue, userLevelRevenue.getRevenue());
				    
				}else{  //no package , add it
					//create history entry for first time
					logger.warning("create history entry for first time");

					List<String> list = new ArrayList<>(1);
					
					list.add(date);		
					
					historyMap.setProperty(revenueLink.getPackageName(), list);										
					
					userDailyRevenue.setProperty("rev_check_dates", historyMap);	

					//accumulate revenue
				    this.updateUserRevenue(affs, userDailyRevenue, userLevelRevenue.getRevenue());
				}
				
			}
			
			
		}
		
	}

		
}