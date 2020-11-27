package com.luee.wally.admin.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceConfig;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.ReadPolicy;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;
import com.luee.wally.api.service.UserService;

public class AbstractRepository {
	private final Logger logger = Logger.getLogger(AbstractRepository.class.getName());
	public void save(Entity entity) {
		DatastoreService ds = createDatastoreService(Consistency.STRONG);
		ds.put(entity);
	}

	public Map<Key, Entity> findEntitiesByKey(Collection<Key> keys) {
		DatastoreService ds = createDatastoreService(Consistency.EVENTUAL);
		return ds.get(keys);
	}

	public Entity findEntityByKey(String key) {
		DatastoreService ds = createDatastoreService(Consistency.EVENTUAL);
		try {
			return ds.get(KeyFactory.stringToKey(key));
		} catch (EntityNotFoundException e) {
			return null;
		}
	}

	public void deleteEntity(Key key) {
		DatastoreService ds = createDatastoreService(Consistency.STRONG);
		ds.delete(key);
		logger.warning("DELETE: "+key);
	}

	public void createOrUpdateEntity(Entity entity) {
		DatastoreService ds = createDatastoreService(Consistency.STRONG);
		ds.put(entity);
		logger.warning("UPDATE: "+entity.getKey());
	}

	public void deleteEntity(String key) {
		DatastoreService ds = createDatastoreService(Consistency.STRONG);
		ds.delete(KeyFactory.stringToKey(key));
		logger.warning("DELETE: "+KeyFactory.stringToKey(key));
	}

	public Comparator<Entity> createDateComparator(String fieldName) {
		Comparator<Entity> comparator = new Comparator<Entity>() {
			@Override
			public int compare(Entity e1, Entity e2) {
				Date d1 = (Date) e1.getProperty(fieldName);
				Date d2 = (Date) e2.getProperty(fieldName);
				return d1.compareTo(d2);
			}
		};
		return comparator;
	}
	public Query filterByOr(String tableName,String fieldName,Collection<String> values){
		Query query = new Query(tableName);
		Collection<Filter> predicates=new ArrayList<>();
		
		values.forEach(e->{
			predicates.add(new FilterPredicate(fieldName, FilterOperator.EQUAL, e));
		});
		

		if(predicates.size()>1){
			query.setFilter(Query.CompositeFilterOperator.or(predicates));			
		}else{
			query.setFilter(predicates.iterator().next());
		}
		return query;		
	}
	
	public Collection<Entity> findEntities(String tableName,String fieldName, String value){
		DatastoreService ds = createDatastoreService(Consistency.EVENTUAL);
		Query query = new Query(tableName);
		if(fieldName!=null){
			query.setFilter(new FilterPredicate(fieldName, FilterOperator.EQUAL, value));
		}
		PreparedQuery pq = ds.prepare(query);
		return pq.asQueryResultList(FetchOptions.Builder.withDefaults());			
	}
	
	public DatastoreService createDatastoreService(ReadPolicy.Consistency consistency) {

		double deadline = 15.0; // seconds
		DatastoreServiceConfig datastoreConfig;
		if (consistency == Consistency.STRONG) {
			// Set both the read policy and the call deadline
			datastoreConfig = DatastoreServiceConfig.Builder.withReadPolicy(new ReadPolicy(Consistency.STRONG))
					.deadline(deadline);
		} else {
			datastoreConfig = DatastoreServiceConfig.Builder.withReadPolicy(new ReadPolicy(Consistency.EVENTUAL));
		}
		// Get Datastore service with the given configuration
		return DatastoreServiceFactory.getDatastoreService(datastoreConfig);
	}
	
	public Entity getLastAffEntryByGaid(String gaid) {
		DatastoreService ds = createDatastoreService(Consistency.STRONG);

		Filter userGuidFilter = new FilterPredicate("gaid", FilterOperator.EQUAL, gaid);

		Query q = new Query("affs");
		q.setFilter(userGuidFilter);
		PreparedQuery pq = ds.prepare(q);
		List<Entity> entities = pq.asList(FetchOptions.Builder.withDefaults());

		Comparator<Entity> comparator = createDateComparator("date");
		return entities.stream().max(comparator).orElse(null);
	}
}
