package com.paypal.integrate.admin.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceConfig;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.ReadPolicy;
import com.paypal.integrate.admin.command.AffsSearchForm;

public class AffsSearchService {
	private final Logger logger = Logger.getLogger(AffsSearchService.class.getName());
	public void processAffsSearch(AffsSearchForm affsSearchForm){

		 DatastoreService ds=createDatastoreService();
		 
		 
		 
		 String experiment=affsSearchForm.getExperiments().isEmpty()?null:affsSearchForm.getExperiments().iterator().next();
		 String packageName=affsSearchForm.getPackageNames().isEmpty()?null:affsSearchForm.getPackageNames().iterator().next();;
		 
		 Query query=createQuery(affsSearchForm.getStartDate(), affsSearchForm.getEndDate(), affsSearchForm.getCountryCode(),experiment, packageName);
		 PreparedQuery preparedQuery = ds.prepare(query);
		 
		 QueryResultList<Entity> results;
		 
		 Cursor cursor=null;
	     BigDecimal sum=BigDecimal.ZERO;
		 do{
	    	 FetchOptions fetchOptions;	 
	    	 if(cursor!=null){	 
	    		 fetchOptions = FetchOptions.Builder.withLimit(100).startCursor(cursor);
	    	 }else{
	    		 fetchOptions = FetchOptions.Builder.withLimit(100);	 
	    	 }
   	     
	    	 
	    	 results = preparedQuery.asQueryResultList(fetchOptions);
	     
	    	 for(Entity e:results){
	    		BigDecimal amount=BigDecimal.valueOf(e.getProperty("total_ad_rev")==null?0:(double)e.getProperty("total_ad_rev"));
	    		sum=sum.add(amount); 
	    	 }	     	     

		     cursor=results.getCursor();
	     }while(results.size()>0);
		 
		logger.log(Level.WARNING,"Sum="+sum);
		
		int count=(getAffsSearchCount(affsSearchForm.getStartDate(), affsSearchForm.getEndDate(), affsSearchForm.getCountryCode(),experiment, packageName));
		logger.log(Level.WARNING,"Records #="+count);
		if(count!=0){
		  BigDecimal avr=sum.divide(new BigDecimal(count),4, BigDecimal.ROUND_HALF_UP);
		  logger.log(Level.WARNING,"Avr="+avr);
		}
		
		
	}
	public int getAffsSearchCount(Date startDate,Date endDate,String country,String experiment,String packageName){
		 DatastoreService ds = DatastoreServiceFactory.getDatastoreService(); 
		 Query query=createQuery(startDate, endDate, country, experiment, packageName);
		 PreparedQuery preparedQuery = ds.prepare(query);
	     
	     return preparedQuery.countEntities(FetchOptions.Builder.withDefaults());
	}
	/*
	 * Eventual consistency
	 */
	
	private DatastoreService createDatastoreService(){
		 double deadline = 15.0; //seconds
		 // Construct a read policy for eventual consistency
		 ReadPolicy policy = new ReadPolicy(ReadPolicy.Consistency.EVENTUAL);

		// Set both the read policy and the call deadline
		 DatastoreServiceConfig datastoreConfig =
		    DatastoreServiceConfig.Builder.withReadPolicy(policy).deadline(deadline);

		// Get Datastore service with the given configuration
		 return DatastoreServiceFactory.getDatastoreService(datastoreConfig);
	}
	private Query createQuery(Date startDate,Date endDate,String country,String experiment,String packageName){
		logger.log(Level.WARNING,"Query on - "+startDate+":"+endDate+":"+country+":"+experiment+":"+packageName);
		
		Query query = new Query("affs");
		Collection<Filter> predicates=new ArrayList<>();
		
		if(startDate!=null){
			predicates.add(new FilterPredicate("date", FilterOperator.GREATER_THAN, startDate));
		}
		if(endDate!=null){
			predicates.add(new FilterPredicate("date", FilterOperator.LESS_THAN , endDate));
		}
		if(country!=null){
			predicates.add(new FilterPredicate("country_code", FilterOperator.EQUAL , country));
		}
		if(experiment!=null){
			predicates.add(new FilterPredicate("experiment", FilterOperator.EQUAL , experiment));
		}
		if(packageName!=null){
			predicates.add(new FilterPredicate("package_name", FilterOperator.EQUAL , packageName));
		}
		if(predicates.size()>1){
			CompositeFilter filter=new CompositeFilter(CompositeFilterOperator.AND, predicates);
			query.setFilter(filter);			
		}else{
			query.setFilter(predicates.iterator().next());
		}
		
		return query;
	}
}
