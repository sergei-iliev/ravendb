package com.luee.wally;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.*;


public class DB {

	public static List<Entity> getRedeemingRequestFromGuid(String gaid){
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			Filter userGuidFilter = new FilterPredicate("user_guid",
                FilterOperator.EQUAL,
                gaid);

			Query q = new Query("redeeming_requests_new");
			q.setFilter(userGuidFilter);
			PreparedQuery pq = datastore.prepare(q);
			return pq.asList(FetchOptions.Builder.withDefaults());
	}
	
	public static Collection<Entity> getAmazonUsers(boolean paid,String countryCode,String packageName,boolean confirmedEmail,String type,String amount,Date date){
	       return Collections.EMPTY_LIST;	
	}
	
	public static Entity getAffForGaid(String gaid){
		return null;
	}

	public static Entity getUserDailyRevenueByGaid(String gaid){
		return null;
	}
	
	public static void saveAffsTotalAdRev(Entity affs,Entity userDailyRevenue){
		
	}
	
	public static Entity getUserRevPackage(String packageName){
		return null;
	}
	public static void saveUserRevPackage(String packageName,String date){
		
	}
}
