package com.luee.wally.admin.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entities;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;
import com.luee.wally.constants.Constants;
import com.luee.wally.entity.PaidUser;

public class PaidUsersRepository extends AbstractRepository{
	  private final Logger logger = Logger.getLogger(PaidUsersRepository.class.getName());
	
	  /*
	   * In memory filtering
	   */
	  public  Collection<Key> findRedeemingRequests(Collection<Key> keys,Collection<String> countryCodes,Collection<String> packageNames) {		  
          Map<Key,Entity> map=this.findEntitiesByKey(keys);
		  Stream<Entity> stream= map.values().stream();
		  if(countryCodes.size()>0){
			  stream=stream.filter(e->countryCodes.contains((String)e.getProperty("country_code")));
		  }
		  if(packageNames.size()>0){
			  stream=stream.filter(e->packageNames.contains((String)e.getProperty("package_name")));		                       
		  }
		  return stream.map(e->e.getKey()).collect(Collectors.toSet());
	  }
	  public  Map<Key,Entity> findRedeemingRequestEntities(Collection<Key> keys,Collection<String> countryCodes,Collection<String> packageNames) {		  
          Map<Key,Entity> map=this.findEntitiesByKey(keys);
		  Stream<Entity> stream= map.values().stream();
		  if(countryCodes.size()>0){
			  stream=stream.filter(e->countryCodes.contains((String)e.getProperty("country_code")));
		  }
		  if(packageNames.size()>0){
			  stream=stream.filter(e->packageNames.contains((String)e.getProperty("package_name")));		                       
		  }
		  return stream.collect(Collectors.toMap(e->e.getKey(),Function.identity()));
	  }	  
	  public String findPaidUsers(String start,Collection<Entity> result,String type,Date startDate,Date endDate){
			     DatastoreService  ds= createDatastoreService(Consistency.EVENTUAL);
			
			     PreparedQuery pq = ds.prepare(createPaidUsersQuery(type,startDate,endDate));
			
		
			     QueryResultList<Entity> results;
			     
			     Cursor cursor=start==null?null:Cursor.fromWebSafeString(start);
		     
		    	 FetchOptions fetchOptions;	 
		    	 if(cursor!=null){	 
		    		 fetchOptions = FetchOptions.Builder.withLimit(Constants.CURSOR_SIZE).startCursor(cursor);
		    	 }else{
		    		 fetchOptions = FetchOptions.Builder.withLimit(Constants.CURSOR_SIZE);	 
		    	 }
	  	     	    	 
		    	 results = pq.asQueryResultList(fetchOptions);
		    	 result.addAll(results);		    	
		    	 
			     cursor=results.getCursor();	
			     if(results.size()>0){
			    	return cursor.toWebSafeString(); 
			     }else{
			    	return null; 
			     }
	
		}

	    public Collection<PaidUser> findPaidUsersByGuid(String userGuid){
		     DatastoreService ds = createDatastoreService(Consistency.EVENTUAL);			 
			 PreparedQuery pq = ds.prepare(createPaidUsersQuery(userGuid));
			 QueryResultList<Entity> entities = pq.asQueryResultList(FetchOptions.Builder.withDefaults());
			 return entities.stream().map(PaidUser::valueOf).collect(Collectors.toList());
	    }
		public Collection<PaidUser> findPaidUsersByEmail(String email,String paypalAccount){
		     DatastoreService ds = createDatastoreService(Consistency.EVENTUAL);			 

			 PreparedQuery pq = ds.prepare(createPaidUsersQuery(email,paypalAccount));
			 QueryResultList<Entity> entities = pq.asQueryResultList(FetchOptions.Builder.withDefaults());
			 return entities.stream().map(PaidUser::valueOf).collect(Collectors.toList());			 
		}
		
		private Query createPaidUsersQuery(String userGuid){
			Query query = new Query("paid_users");
			query.setFilter(new FilterPredicate("user_guid", FilterOperator.EQUAL , userGuid));			
			return query;
		}	
		
		private Query createPaidUsersQuery(String email,String paypalAccount){
			Query query = new Query("paid_users");
			Collection<Filter> predicates=new ArrayList<>();
			
			
			if(email!=null){
				predicates.add(new FilterPredicate("email_address", FilterOperator.EQUAL, email));
			}
			if(paypalAccount!=null){
				predicates.add(new FilterPredicate("paypal_account", FilterOperator.EQUAL , paypalAccount));
			}

			if(predicates.size()>1){
				query.setFilter(Query.CompositeFilterOperator.or(predicates));			
			}else{
				query.setFilter(predicates.iterator().next());
			}
			return query;
		}
		
		
		private Query createPaidUsersQuery(String type,Date startDate,Date endDate){
			Query query = new Query("paid_users");
			Collection<Filter> predicates=new ArrayList<>();
			
			
			if(startDate!=null){
				predicates.add(new FilterPredicate("date", FilterOperator.GREATER_THAN_OR_EQUAL, startDate));
			}
			if(endDate!=null){
				predicates.add(new FilterPredicate("date", FilterOperator.LESS_THAN , endDate));
			}
            
			
			if(type!=null&&type.trim().length()>0){						   
			   predicates.add(new FilterPredicate("type", FilterOperator.EQUAL  ,type));			
			}
			
			if(predicates.size()>1){
				query.setFilter(Query.CompositeFilterOperator.and(predicates));			
			}else{
				query.setFilter(predicates.iterator().next());
			}
			return query.addSort("date",SortDirection.ASCENDING);	
		}

}
