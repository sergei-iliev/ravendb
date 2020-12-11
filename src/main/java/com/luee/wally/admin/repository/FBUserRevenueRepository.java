package com.luee.wally.admin.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PreparedQuery.TooManyResultsException;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;
import com.luee.wally.entity.Affs;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;

public class FBUserRevenueRepository extends AbstractRepository {
	private final Logger logger = Logger.getLogger(FBUserRevenueRepository.class.getName());

	public Entity getUserRevPackage(String packageName) {
		DatastoreService ds = createDatastoreService(Consistency.STRONG);
		Filter filter = new FilterPredicate("package_name", FilterOperator.EQUAL, packageName);
		Query q = new Query("user_rev_package_fb");
		q.setFilter(filter);
		PreparedQuery pq = ds.prepare(q);
		return pq.asSingleEntity();
	}
	
	public Map<String,Entity> getFacebookPackageNameTokenMap() {
		Collection<Entity> entities=this.findEntities("facebook_package_token",null,null);
		return entities.stream().collect(Collectors.toMap(e->(String)e.getProperty("package_name"), Function.identity(),(a,b)->a));	
	}
	
	public  void saveUserRevPackage(String packageName, String date) {
		DatastoreService ds = createDatastoreService(Consistency.STRONG);

		Entity entity = getUserRevPackage(packageName);
		
		if(entity==null) {
			entity = new Entity("user_rev_package_fb");
			entity.setProperty("package_name", packageName);
		}
		entity.setProperty("last_used_date", date);		
		ds.put(entity);		
	}

	public Entity getUserDailyRevenueByGaid(String gaid,String date) {
		DatastoreService ds = createDatastoreService(Consistency.STRONG);

		Filter gaidFilter = new FilterPredicate("gaid",FilterOperator.EQUAL,gaid);

		Filter revDateFilter = new FilterPredicate("rev_check_date",FilterOperator.EQUAL,date);


		CompositeFilter filter = CompositeFilterOperator.and(gaidFilter,revDateFilter);

		Query q = new Query("user_daily_revenue_fb");
		q.setFilter(filter);
		PreparedQuery pq = ds.prepare(q);
		try {
			Entity entity = pq.asSingleEntity();
			return entity;

		} catch (TooManyResultsException ex) {
			List<Entity> entities = pq.asList(FetchOptions.Builder.withDefaults());

			Entity entity = entities.get(0);

			for (Entity e : entities) {
				logger.warning("entity key: " + e.getKey());
				if (!e.getKey().equals(entity.getKey())) {
					ds.delete(e.getKey());
					logger.warning("removing entity key: " + e.getKey());
				}
			}
			return entity;

		}

	}

	public Collection<Entity> getUserDailyRevenueByAffsKey(Key key) {
		DatastoreService ds = createDatastoreService(Consistency.EVENTUAL);

		Filter filter = new FilterPredicate("aff_key",FilterOperator.EQUAL,key);				
		Query query = new Query("user_daily_revenue_fb");
		query.setFilter(filter);
		PreparedQuery preparedQuery = ds.prepare(query);
		
		Cursor cursor = null;
		QueryResultList<Entity> results;
		Collection<Entity> entities=new ArrayList<>();
		do {
			FetchOptions fetchOptions;
			if (cursor != null) {
				fetchOptions = FetchOptions.Builder.withLimit(CURSOR_SIZE).startCursor(cursor);
			} else {
				fetchOptions = FetchOptions.Builder.withLimit(CURSOR_SIZE);
			}

			results = preparedQuery.asQueryResultList(fetchOptions);
            entities.addAll(results);		
			
			cursor = results.getCursor();
		} while (results.size() > 0);
				
		return entities;		

	}
	

}
