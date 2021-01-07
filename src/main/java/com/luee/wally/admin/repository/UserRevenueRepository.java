package com.luee.wally.admin.repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PreparedQuery.TooManyResultsException;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;

public class UserRevenueRepository extends AbstractRepository {
	private final Logger logger = Logger.getLogger(UserRevenueRepository.class.getName());

	public Entity getUserRevPackage(String packageName,String date) {
		DatastoreService ds = createDatastoreService(Consistency.STRONG);
		Filter filter1 = new FilterPredicate("package_name", FilterOperator.EQUAL, packageName);
		Filter filter2 = new FilterPredicate("last_used_date", FilterOperator.EQUAL, date);
		Query q = new Query("user_rev_package");		
		q.setFilter(Query.CompositeFilterOperator.and(filter1,filter2));				
		PreparedQuery pq = ds.prepare(q);
		
		return pq.asSingleEntity();
	}
	
	public  void createUserRevPackage(String packageName, String date) {
		DatastoreService ds = createDatastoreService(Consistency.STRONG);
		Entity entity = getUserRevPackage(packageName,date);
		//create if it does not exists
		if(entity==null) {
			entity = new Entity("user_rev_package");
			entity.setProperty("package_name", packageName);
			entity.setProperty("last_used_date", date);
			ds.put(entity);
		}
	}

//	public Entity getLastAffEntryByGaid(String gaid) {
//		DatastoreService ds = createDatastoreService(Consistency.STRONG);
//
//		Filter userGuidFilter = new FilterPredicate("gaid", FilterOperator.EQUAL, gaid);
//
//		Query q = new Query("affs");
//		q.setFilter(userGuidFilter);
//		PreparedQuery pq = ds.prepare(q);
//		List<Entity> entities = pq.asList(FetchOptions.Builder.withDefaults());
//
//		Comparator<Entity> comparator = createDateComparator("date");
//		return entities.stream().max(comparator).orElse(null);
//	}

	public Entity getUserDailyRevenueByGaid(String gaid) {
		DatastoreService ds = createDatastoreService(Consistency.STRONG);

		Filter userGuidFilter = new FilterPredicate("gaid", FilterOperator.EQUAL, gaid);

		Query q = new Query("user_daily_revenue");
		q.setFilter(userGuidFilter);
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

	public void saveAffsTotalAdRev(Entity affs, Entity userDailyRev) {
		DatastoreService ds = createDatastoreService(Consistency.STRONG);

		TransactionOptions options = TransactionOptions.Builder.withXG(true);
		Transaction txn = ds.beginTransaction(options);
		for (int i = 1; i <= 10; i++) {
			try {

				ds.put(txn, affs);
				userDailyRev.setProperty("aff_key", affs.getKey());
				ds.put(txn, userDailyRev);
				txn.commit();
				return; // in case of success , loop out
			} catch (ConcurrentModificationException | DatastoreTimeoutException | DatastoreFailureException
					| IllegalStateException e) {
				logger.warning(" Got concurrent update exception. Trying again. " + i);
				logger.log(Level.SEVERE, "caught exception", e);
				try {
					Thread.sleep(500); // wait some time
				} catch (InterruptedException ie) {
				}
				continue;
			} finally {
				if (txn.isActive()) {
					txn.rollback();
				}
			}
		}
		logger.log(Level.SEVERE,
				"failed to log data for aff key:" + affs.getKey() + ", gaid:" + affs.getProperty("gaid"));
	}
	
}
