package com.luee.wally.admin.repository;

import java.util.Date;
import java.util.Objects;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;

public class JobsRepository extends AbstractRepository{
    public static String FB_USER_REVENUE_JOB="fb.user.revenue.job";
    public static String USER_REVENUE_JOB="user.revenue.job";
    
    
	public enum JobStatus{STARTED,ABORTED,FINISHED,INCOMPLETE};
	
	public Entity getJob(String jobName,String processingDate){
		DatastoreService ds = createDatastoreService(Consistency.STRONG);

		Filter nameFilter = new FilterPredicate("job_name",
                FilterOperator.EQUAL,jobName);

		Filter processingDateFilter = new FilterPredicate("processing_date",
                FilterOperator.EQUAL,
                processingDate);


		Query q = new Query("jobs");
		CompositeFilter filter = CompositeFilterOperator.and(nameFilter,processingDateFilter);
		q.setFilter(filter);
		PreparedQuery pq = ds.prepare(q);		
		return pq.asSingleEntity();						
	}
	
	public void saveJobEntry(String jobName, JobStatus status, String processingDate ) {

		DatastoreService ds = createDatastoreService(Consistency.STRONG);

		Filter taskNameFilter = new FilterPredicate("job_name",
                FilterOperator.EQUAL,
                jobName);

		Filter processingDateFilter = new FilterPredicate("processing_date",
                FilterOperator.EQUAL,
                processingDate);


		Query q = new Query("jobs");
		
				
		CompositeFilter filter = CompositeFilterOperator.and(taskNameFilter,processingDateFilter);
		q.setFilter(filter);
		
		PreparedQuery pq = ds.prepare(q);
		Entity entity = pq.asSingleEntity();
		
		
		if(entity==null) {
			entity = new Entity("jobs");
			entity.setProperty("job_name", jobName);
			entity.setProperty("processing_date", processingDate);
			entity.setProperty("status", status.name());			
			entity.setProperty("last_started_date", new Date());
			entity.setProperty("attempts",1);
		}
		else {
			//long attempts = (Long) entity.getProperty("attempts");
			entity.setProperty("last_update_time", new Date());
			entity.setProperty("status", status.name());
			//entity.setProperty("attempts", (attempts+1));
		}
		
		
		ds.put(entity);


	}
	/*
	 * mark job as INCOMPLETE
	 * job entry must exist
	 */
	public boolean resetJobFailure(String jobName,String status,String date){
		
		Entity entity =this.getJob(jobName, date); 
		Objects.requireNonNull(entity,"Job entity does not exists for date: "+date);
		
		long attempts=((Long)entity.getProperty("attempts")==null?0:(Long)entity.getProperty("attempts"));				
		attempts++;
		
		entity.setProperty("attempts",attempts);
		entity.setProperty("status", status);
		entity.setProperty("last_update_time", new Date());
		
		this.save(entity);
		
		return attempts<200;
	}	
}
