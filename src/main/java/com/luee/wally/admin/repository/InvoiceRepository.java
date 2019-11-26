package com.luee.wally.admin.repository;

import java.time.LocalDate;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;

public class InvoiceRepository extends AbstractRepository{
	  private final Logger logger = Logger.getLogger(InvoiceRepository.class.getName());
	  
	  
	  public long createInvoiceNumber() {
			
			DatastoreService ds = createDatastoreService(Consistency.STRONG);
			Filter userGuidFilter = new FilterPredicate("type",FilterOperator.EQUAL,"last_num");

			Query q = new Query("credit_note_id");
			q.setFilter(userGuidFilter);
			PreparedQuery pq = ds.prepare(q);

			Entity e = pq.asSingleEntity();
			
			LocalDate date = LocalDate.now();
			int year=date.getYear();

			
			if(e==null) {
				e= new Entity("credit_note_id") ;
				e.setProperty("type", "last_num");
				e.setProperty("last_id", Long.parseLong(Integer.toString(year)+"111111"));
				
			}
			
			long lastNum = (Long) e.getProperty("last_id");
			
			e.setProperty("last_id", lastNum+1);
			ds.put(e);
			
			return lastNum;
			
		}
	
}
