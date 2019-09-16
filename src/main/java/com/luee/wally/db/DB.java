package com.luee.wally.db;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import com.google.appengine.api.datastore.Entity;

public class DB {

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
