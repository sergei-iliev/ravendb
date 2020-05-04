package com.luee.wally.admin.repository;

import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;

public class EmailTemplateRepository extends AbstractRepository {
	private final Logger logger = Logger.getLogger(EmailTemplateRepository.class.getName());

	public static final String SEND_ELIGIBLE_USER_EMAIL_TEMPLATE="SEND_ELIGIBLE_USER_EMAIL_TEMPLATE";
	public static final String CONFIRM_EMAIL_REMINDER_PAYPAL="CONFIRM_EMAIL_REMINDER_PAYPAL";
	
	public Collection<Entity> getEmailTemplates(String name,String type){
		DatastoreService ds = createDatastoreService(Consistency.EVENTUAL);
		Query query = new Query("email_templates");
		
		if(name!=null){
	       query.setFilter(new FilterPredicate("name", FilterOperator.EQUAL, name));
		}
		if(type!=null){
		    query.setFilter(new FilterPredicate("type", FilterOperator.EQUAL, type));
		}
		
		PreparedQuery pq = ds.prepare(query);
		return pq.asList(FetchOptions.Builder.withDefaults());

	}
	
	public void createRedeemingRequestsEmailJob(Key redeemingRequestKey){
		DatastoreService ds = createDatastoreService(Consistency.EVENTUAL);
		Entity entity = new Entity("redeeming_requests_email_job");	
		entity.setProperty("redeeming_request_key",KeyFactory.keyToString(redeemingRequestKey));
		entity.setIndexedProperty("created_date",new Date());				
		ds.put(entity);		
	}
	
	public Collection<Entity> getRedeemingRequestsEmailJobs(Date date){
		DatastoreService ds = createDatastoreService(Consistency.EVENTUAL);
		Query query = new Query("redeeming_requests_email_job");				
		
		query.setFilter(new FilterPredicate("created_date", FilterOperator.LESS_THAN,date));
		
		PreparedQuery pq = ds.prepare(query);
		return pq.asList(FetchOptions.Builder.withDefaults());
	}
	
}
