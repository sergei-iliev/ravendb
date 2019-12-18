package com.luee.wally.admin.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;
import com.luee.wally.constants.Constants;
import com.luee.wally.entity.PaymentAmount;

public class PaymentReportsRepository extends AbstractRepository{
	  private final Logger logger = Logger.getLogger(PaymentReportsRepository.class.getName());
	
	    
		public PaymentAmount getPaymentReports(Date startDate,Date endDate){
			DatastoreService ds = createDatastoreService(Consistency.EVENTUAL);

			
			Query query = createQuery(startDate, endDate);

			PreparedQuery preparedQuery = ds.prepare(query);

			QueryResultList<Entity> results;
			Cursor cursor = null;
			PaymentAmount paymentAmount=new PaymentAmount();
			
			do {
				FetchOptions fetchOptions;
				if (cursor != null) {
					fetchOptions = FetchOptions.Builder.withLimit(Constants.CURSOR_SIZE).startCursor(cursor);
				} else {
					fetchOptions = FetchOptions.Builder.withLimit(Constants.CURSOR_SIZE);
				}

				results = preparedQuery.asQueryResultList(fetchOptions);

				for (Entity e : results) {
					paymentAmount.addTotalAmountEur((double) e.getProperty("eur_currency"));
					paymentAmount.addTotalAmountByCurrencyMap((String) e.getProperty("paid_currency"),(String) e.getProperty("amount"));
					paymentAmount.addTotalAmountByTypeMap((String) e.getProperty("type"),(double) e.getProperty("eur_currency"));										
				}
			
				cursor = results.getCursor();
			} while (results.size() > 0);
			return paymentAmount;
		}
		
		private Query createQuery(Date startDate, Date endDate) {

			Query query = new Query("paid_users");			
			query.setFilter(Query.CompositeFilterOperator.and(new FilterPredicate("date", FilterOperator.GREATER_THAN, startDate),new FilterPredicate("date", FilterOperator.LESS_THAN, endDate)));
			return query;
		}
}
