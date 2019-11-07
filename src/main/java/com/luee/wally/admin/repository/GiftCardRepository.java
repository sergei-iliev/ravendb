package com.luee.wally.admin.repository;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;

public class GiftCardRepository extends AbstractRepository {

	  public Entity getGiftCardCountryCodeMapping(String countryCode){		     
		     DatastoreService ds = createDatastoreService(Consistency.STRONG);
		     Query query = new Query("tango_card_country_code_mapping");
		     query.setFilter(new FilterPredicate("country_code", FilterOperator.EQUAL, countryCode));
			 PreparedQuery pq = ds.prepare(query);
			 return pq.asSingleEntity();			 
	  }
	  
	  public Entity getPackageNameTitleMapping(String packagName){		     
		     DatastoreService ds = createDatastoreService(Consistency.STRONG);
		     Query query = new Query("package_name_title_mapping");
		     query.setFilter(new FilterPredicate("package_name", FilterOperator.EQUAL, packagName));
			 PreparedQuery pq = ds.prepare(query);
			 return pq.asSingleEntity();			 
	  }
	  
}
