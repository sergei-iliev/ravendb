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
import com.paypal.integrate.admin.command.AffsSearchResult;

public class AffsSearchService {
	private final Logger logger = Logger.getLogger(AffsSearchService.class.getName());
	
	public void processAffsSearch(AffsSearchForm affsSearchForm){
		 logger.log(Level.WARNING,affsSearchForm.toString());
		 if(affsSearchForm.getExperiments().size()>0){
			 Collection<AffsSearchResult> results=new ArrayList<>();
			 for(String experiment:affsSearchForm.getExperiments()){
				 String packageName=affsSearchForm.getPackageNames().isEmpty()?null:affsSearchForm.getPackageNames().iterator().next();; 			 
			     
				 AffsSearchResult result=processAffsSearch(affsSearchForm.getStartDate(), affsSearchForm.getEndDate(), affsSearchForm.getCountryCode(),experiment, packageName); 
	 			 results.add(result);
	 			 				 
			 }
			 for(AffsSearchResult result:results){
			    logger.log(Level.WARNING,"Sum="+result.getSum());
				
			    logger.log(Level.WARNING,"Records #="+result.getCount());
			    if(result.getCount()!=0){
			      BigDecimal avr=result.getSum().divide(new BigDecimal(result.getCount()),4, BigDecimal.ROUND_HALF_UP);
			      logger.log(Level.WARNING,"Avr="+avr);
			    }			 			 
			 }
		 }else{
			 String experiment=affsSearchForm.getExperiments().isEmpty()?null:affsSearchForm.getExperiments().iterator().next();
			 String packageName=affsSearchForm.getPackageNames().isEmpty()?null:affsSearchForm.getPackageNames().iterator().next();; 			 
		     
			 AffsSearchResult result=processAffsSearch(affsSearchForm.getStartDate(), affsSearchForm.getEndDate(), affsSearchForm.getCountryCode(),experiment, packageName); 
 			 logger.log(Level.WARNING,"Sum="+result.getSum());
				
			 logger.log(Level.WARNING,"Records #="+result.getCount());
			 if(result.getCount()!=0){
			  BigDecimal avr=result.getSum().divide(new BigDecimal(result.getCount()),4, BigDecimal.ROUND_HALF_UP);
			  logger.log(Level.WARNING,"Avr="+avr);
			 }

		 }
		 
	}
	public AffsSearchResult processAffsSearch(Date startDate,Date endDate,String country,String experiment,String packageName){
	
		 DatastoreService ds=createDatastoreService();
		 
		 
		
		 
		 Query query=createQuery(startDate, endDate,country, experiment, packageName);
		 
		 PreparedQuery preparedQuery = ds.prepare(query);
		 
		 QueryResultList<Entity> results;
		 
		 Cursor cursor=null;
	     BigDecimal sum=BigDecimal.ZERO;
	     int count=0;
		 do{
	    	 FetchOptions fetchOptions;	 
	    	 if(cursor!=null){	 
	    		 fetchOptions = FetchOptions.Builder.withLimit(3).startCursor(cursor);
	    	 }else{
	    		 fetchOptions = FetchOptions.Builder.withLimit(3);	 
	    	 }
   	     	    	 
	    	 results = preparedQuery.asQueryResultList(fetchOptions);
	     
	    	 for(Entity e:results){
	    		BigDecimal amount=BigDecimal.valueOf(e.getProperty("total_ad_rev")==null?0:(double)e.getProperty("total_ad_rev"));
	    		sum=sum.add(amount); 
	    	 }
	    	 count+=results.size();
		     cursor=results.getCursor();		    		    	
	     }while(results.size()>0);
		 		
		return new AffsSearchResult(sum, count);
		
	}
//	public int getAffsSearchCount(Date startDate,Date endDate,String country,String experiment,String packageName){
//		 DatastoreService ds = DatastoreServiceFactory.getDatastoreService(); 
//		 Query query=createQuery(startDate, endDate, country, experiment, packageName);
//		 PreparedQuery preparedQuery = ds.prepare(query);
//	     
//	     return preparedQuery.countEntities(FetchOptions.Builder.withDefaults());
//	}
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
			query.setFilter(Query.CompositeFilterOperator.and(predicates));			
		}else{
			query.setFilter(predicates.iterator().next());
		}
		
		return query;
	}
	
}
