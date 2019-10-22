package com.luee.wally.admin.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
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
import com.luee.wally.constants.Constants;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.entity.SearchFilterTemplate;

public class PaymentRepository extends AbstractRepository{
	private final Logger logger = Logger.getLogger(PaymentRepository.class.getName());
	
	  public void saveUserPaymentRemovalReason(String _key,String reason)throws EntityNotFoundException{
	     Key key=KeyFactory.stringToKey(_key);
	     DatastoreService ds = createDatastoreService(Consistency.STRONG);
	     
	     Entity entity=ds.get(key);	
	     entity.setProperty("type","Removed");
	     entity.setProperty("removal_reason", reason);
	     ds.put(entity);	     
	   }
	  
	   public Collection<String> getUserPaymentsRemovalReasons(){
			Collection<String> list = new ArrayList<>();

			DatastoreService ds = createDatastoreService(Consistency.STRONG);
			Query query = new Query("user_payments_removal_reasons");
			

			PreparedQuery pq = ds.prepare(query);
			QueryResultList<Entity> entities = pq.asQueryResultList(FetchOptions.Builder.withDefaults());

			for (Entity entity : entities) {

				list.add((String)entity.getProperty("removal_reason"));
			}
			
			return list;
	   }
	
		public Collection<RedeemingRequests> findEligibleUsers(String type,Date startDate,Date endDate,String packageName,String countryCode,Boolean confirmedEmail){
			DatastoreService  ds= createDatastoreService(Consistency.EVENTUAL);
			
			PreparedQuery pq = ds.prepare(createEligibleUsersQuery(type,startDate,endDate,packageName, countryCode,confirmedEmail));
			
			Collection<Entity> entities=new LinkedList<>();
			QueryResultList<Entity> results;
			 
			Cursor cursor=null;
		     
				
			 do{
		    	 FetchOptions fetchOptions;	 
		    	 if(cursor!=null){	 
		    		 fetchOptions = FetchOptions.Builder.withLimit(Constants.CURSOR_SIZE).startCursor(cursor);
		    	 }else{
		    		 fetchOptions = FetchOptions.Builder.withLimit(Constants.CURSOR_SIZE);	 
		    	 }
	  	     	    	 
		    	 results = pq.asQueryResultList(fetchOptions);
		    	 entities.addAll(results);		    	
		    	 
			     cursor=results.getCursor();		    		    	
		     }while(results.size()>0);

			
			return entities.stream().map(RedeemingRequests::valueOf).collect(Collectors.toList());			
		}
		
		private Query createEligibleUsersQuery(String type,Date startDate,Date endDate,String packageName,String countryCode,Boolean confirmedEmail){
			Query query = new Query("redeeming_requests_new");
			Collection<Filter> predicates=new ArrayList<>();
			
			predicates.add(new FilterPredicate("is_paid", FilterOperator.EQUAL, false));	
			
			if(confirmedEmail!=null){
				predicates.add(new FilterPredicate("confirmed_email", FilterOperator.EQUAL, confirmedEmail));	
			}
			
			if(startDate!=null){
				predicates.add(new FilterPredicate("date", FilterOperator.GREATER_THAN_OR_EQUAL, startDate));
			}
			if(endDate!=null){
				predicates.add(new FilterPredicate("date", FilterOperator.LESS_THAN , endDate));
			}
			
			
			if(countryCode!=null){
				predicates.add(new FilterPredicate("country_code", FilterOperator.EQUAL , countryCode));
			}
			if(packageName!=null&&packageName.trim().length()>0){			
				predicates.add(new FilterPredicate("package_name", FilterOperator.EQUAL , packageName));			
			}
			if(type!=null&&type.trim().length()>0){			
				predicates.add(new FilterPredicate("type", FilterOperator.EQUAL , type));			
			}
			
			if(predicates.size()>1){
				query.setFilter(Query.CompositeFilterOperator.and(predicates));			
			}else{
				query.setFilter(predicates.iterator().next());
			}
			
			return query.addSort("date",SortDirection.ASCENDING);	
		}
}
