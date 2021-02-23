package com.luee.wally.admin.repository;

import java.util.Collection;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;

public class UserRepository extends AbstractRepository {
	private final Logger logger = Logger.getLogger(UserRepository.class.getName());

	
	public Collection<Entity> findAffsByUserGuids(Collection<String> userGuids){
		DatastoreService ds = createDatastoreService(Consistency.EVENTUAL);
        Query query=this.filterByOr("affs", "user_guid",userGuids);
		PreparedQuery pq = ds.prepare(query);
		return pq.asList(FetchOptions.Builder.withDefaults());	
	}
	
}
