package com.luee.wally.admin.repository;

import java.util.Collection;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;

public class UserRepository extends AbstractRepository {
	private final Logger logger = Logger.getLogger(UserRepository.class.getName());

	public Collection<Entity> getRecordsByEmails(Collection<String> emails,String tableName,String fieldName){
		DatastoreService ds = createDatastoreService(Consistency.STRONG);
		Query query=this.filterByOr(tableName, fieldName, emails);
		PreparedQuery pq = ds.prepare(query);
		QueryResultList<Entity> entities = pq.asQueryResultList(FetchOptions.Builder.withDefaults());		
		return entities;
	}
	
//	private Query findRecordsByEmails(Collection<String> emails,String tableName,String fieldName){
//		Query query = new Query(tableName);
//		Collection<Filter> predicates=new ArrayList<>();
//		
//		emails.forEach(e->{
//			predicates.add(new FilterPredicate(fieldName, FilterOperator.EQUAL, e));
//		});
//		
//
//		if(predicates.size()>1){
//			query.setFilter(Query.CompositeFilterOperator.or(predicates));			
//		}else{
//			query.setFilter(predicates.iterator().next());
//		}
//		return query;
//	}
	
}
