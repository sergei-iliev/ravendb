package com.luee.wally.api.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceConfig;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.ReadPolicy;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;

public class AbstractService {

	public String convert(Throwable throwable) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		throwable.printStackTrace(pw);
		return sw.toString();
	}
	
	public String getYesterdayDate(){		 
		Date yesterday=new Date(System.currentTimeMillis()-24*60*60*1000);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(yesterday);
	}
	
	protected DatastoreService createDatastoreService() {
		return this.createDatastoreService(Consistency.EVENTUAL);
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
