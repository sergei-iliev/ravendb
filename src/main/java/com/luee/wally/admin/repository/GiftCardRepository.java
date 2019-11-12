package com.luee.wally.admin.repository;

import java.util.Date;

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
	  
	  public void saveGiftCardOrder(String redeemingRequestId,String referenceOrderId){
		     DatastoreService ds = createDatastoreService(Consistency.STRONG);
		  	         
			 Entity entity=new Entity("tango_cards_purchased");				 
			 entity.setIndexedProperty("redeeming_request_id",redeemingRequestId);
			 entity.setProperty("date", new Date());
			 entity.setIndexedProperty("reference_order_id",referenceOrderId);
			 ds.put(entity);
	  }
	  
	  public Entity getGiftCardOrder(String redeemingRequestId){
		     DatastoreService ds = createDatastoreService(Consistency.STRONG);
			 
		     Query query = new Query("tango_cards_purchased");
		     query.setFilter(new FilterPredicate("redeeming_request_id", FilterOperator.EQUAL, redeemingRequestId));
			 PreparedQuery pq = ds.prepare(query);
			 return pq.asSingleEntity();
	  }
	  
	  
}
