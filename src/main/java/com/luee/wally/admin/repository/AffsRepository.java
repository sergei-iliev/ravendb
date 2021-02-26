package com.luee.wally.admin.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;

public class AffsRepository extends AbstractRepository {
	private final Logger logger = Logger.getLogger(AffsRepository.class.getName());

	public Collection<Entity> findAffsByUserGuids(Collection<String> userGuids){
		DatastoreService ds = createDatastoreService(Consistency.EVENTUAL);
        Query query=this.filterByOr("affs", "user_guid",userGuids);
		PreparedQuery pq = ds.prepare(query);
		return pq.asList(FetchOptions.Builder.withDefaults());	
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
	
	public Query createQuery(Date startDate, Date endDate, String gaid) {
		Query query = new Query("affs");
		Collection<Filter> predicates = new ArrayList<>();

		if (startDate != null) {
			predicates.add(new FilterPredicate("date", FilterOperator.GREATER_THAN_OR_EQUAL, startDate));
		}
		if (endDate != null) {
			predicates.add(new FilterPredicate("date", FilterOperator.LESS_THAN, endDate));
		}
		if (gaid != null) {
			predicates.add(new FilterPredicate("gaid", FilterOperator.EQUAL, gaid));
		}

		if (predicates.size() > 1) {
			query.setFilter(Query.CompositeFilterOperator.and(predicates));
		} else {
			query.setFilter(predicates.iterator().next());
		}

		return query;
	}

	public Query createQuery(Date startDate, Date endDate, String country, String experiment, String packageName) {

		Query query = new Query("affs");
		Collection<Filter> predicates = new ArrayList<>();

		if (startDate != null) {
			predicates.add(new FilterPredicate("date", FilterOperator.GREATER_THAN_OR_EQUAL, startDate));
		}
		if (endDate != null) {
			predicates.add(new FilterPredicate("date", FilterOperator.LESS_THAN, endDate));
		}
		if (country != null) {
			predicates.add(new FilterPredicate("country_code", FilterOperator.EQUAL, country));
		}
		if (experiment != null) {
			predicates.add(new FilterPredicate("experiment", FilterOperator.EQUAL, experiment));
		}
		if (packageName != null) {
			predicates.add(new FilterPredicate("package_name", FilterOperator.EQUAL, packageName));
		}

		if (predicates.size() > 1) {
			query.setFilter(Query.CompositeFilterOperator.and(predicates));
		} else {
			query.setFilter(predicates.iterator().next());
		}

		return query;
	}

}
