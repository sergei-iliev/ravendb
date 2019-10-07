package com.luee.wally.admin.repository;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceConfig;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.ReadPolicy;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;

public class AbstractRepository {

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
