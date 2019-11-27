package com.luee.wally.admin.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;
import com.luee.wally.command.PaidUserForm;
import com.luee.wally.constants.Constants;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.entity.SearchFilterTemplate;

public class ApplicationSettingsRepository extends AbstractRepository{
	  private final Logger logger = Logger.getLogger(ApplicationSettingsRepository.class.getName());
	  
	  public static final String ACCOUNT_EMAIL_SETTING="ACCOUNT_EMAIL_SETTING";
	  public static final String SHOW_TANGO_GIFT_CARD="SHOW_TANGO_GIFT_CARD";
	  public static final String SHOW_PAYPAL_PAY="SHOW_PAYPAL_PAY";
	  
	  
//	  //cache
//	  Map<String,String> applicationSettings;
//	  
//	  public Map<String,String> getApplicationSettingsFromCache(){
//		  if(applicationSettings!=null){
//			  applicationSettings=getApplicationSettings();
//		  }
//		  
//		  return applicationSettings;
//	  }
	  
	   public Map<String,String> getApplicationSettings(){
			Map<String,String> map = new HashMap<>();

			DatastoreService ds = createDatastoreService(Consistency.EVENTUAL);
			Query query = new Query("application_settings");
			

			PreparedQuery pq = ds.prepare(query);
			QueryResultList<Entity> entities = pq.asQueryResultList(FetchOptions.Builder.withDefaults());

			for (Entity entity : entities) {
				map.put((String)entity.getProperty("name"),(String)entity.getProperty("value"));
			}
			
			return map;
	   }
	
}
