package com.luee.wally.admin.repository;

import java.util.Collection;
import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceConfig;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.ReadPolicy;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;

public class AbstractRepository {

	public Map<Key,Entity> findEntitiesByKey(Collection<Key> keys){
		  DatastoreService ds = createDatastoreService(Consistency.EVENTUAL);
		  return ds.get(keys);
	}
	public Entity findEntityByKey(String key) {		
		  DatastoreService ds = createDatastoreService(Consistency.EVENTUAL);
		  try{
		     return ds.get(KeyFactory.stringToKey(key));
		  }catch(EntityNotFoundException e){
			 return null; 
		  }
	}
	public void deleteEntity(Key key){
		DatastoreService ds = createDatastoreService(Consistency.STRONG);
		ds.delete(key);
	}
	public void createOrUpdateEntity(Entity entity){
		DatastoreService ds = createDatastoreService(Consistency.STRONG);
		ds.put(entity);
	}
	public void deleteEntity(String key){
		DatastoreService ds = createDatastoreService(Consistency.STRONG);
		ds.delete(KeyFactory.stringToKey(key));
	}
	
	protected DatastoreService createDatastoreService(ReadPolicy.Consistency consistency) {
	  	
		double deadline = 15.0; // seconds
		DatastoreServiceConfig datastoreConfig;
		if(consistency==Consistency.STRONG){
		// Set both the read policy and the call deadline
		   datastoreConfig = DatastoreServiceConfig.Builder.withReadPolicy(new ReadPolicy(Consistency.STRONG))
				.deadline(deadline);
		}else{
		   datastoreConfig = DatastoreServiceConfig.Builder.withReadPolicy(new ReadPolicy(Consistency.EVENTUAL));				
		}
		// Get Datastore service with the given configuration
		return DatastoreServiceFactory.getDatastoreService(datastoreConfig);
	}
}
