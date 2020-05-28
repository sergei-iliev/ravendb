package com.luee.wally.admin.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
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

	  public Entity getGiftCardCountryCodeExternalMapping(String countryCode,String currency){		     
		     DatastoreService ds = createDatastoreService(Consistency.STRONG);
		     Query query = new Query("tango_card_country_code_mapping_external");
			 
		     List<Filter> filters = new ArrayList<Query.Filter>();
			 filters.add(new FilterPredicate("country_code", FilterOperator.EQUAL, countryCode));
			 filters.add(new FilterPredicate("currency", FilterOperator.EQUAL, currency));
		     
			 CompositeFilter compFilter = new CompositeFilter(CompositeFilterOperator.AND, filters );
		     query.setFilter(compFilter);
			 
		     PreparedQuery pq = ds.prepare(query);
			 return pq.asSingleEntity();			 
	  }
	  public Entity getPackageNameTitleMapping(String packageName){		     
		     DatastoreService ds = createDatastoreService(Consistency.STRONG);
		     Query query = new Query("package_name_title_mapping");
		     query.setFilter(new FilterPredicate("package_name", FilterOperator.EQUAL, packageName));
			 PreparedQuery pq = ds.prepare(query);
			 return pq.asSingleEntity();			 
	  }
	  /*
	  public void saveGiftCardOrder(String redeemingRequestId,String referenceOrderId){
		     DatastoreService ds = createDatastoreService(Consistency.STRONG);
		  	         
			 Entity entity=new Entity("tango_cards_purchased");				 
			 entity.setIndexedProperty("redeeming_request_id",redeemingRequestId);
			 entity.setProperty("date", new Date());
			 entity.setIndexedProperty("reference_order_id",referenceOrderId);
			 ds.put(entity);
	  }
	  */
	  /*
	  public Entity getGiftCardOrder(String redeemingRequestId){
		     DatastoreService ds = createDatastoreService(Consistency.STRONG);
			 
		     Query query = new Query("tango_cards_purchased");
		     query.setFilter(new FilterPredicate("redeeming_request_id", FilterOperator.EQUAL, redeemingRequestId));
			 PreparedQuery pq = ds.prepare(query);
			 return pq.asSingleEntity();
	  }
	  */
	  
}
