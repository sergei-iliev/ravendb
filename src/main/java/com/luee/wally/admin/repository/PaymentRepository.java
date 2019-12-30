package com.luee.wally.admin.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.luee.wally.api.service.impex.ImportService;
import com.luee.wally.command.PaidUserForm;
import com.luee.wally.constants.Constants;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.json.ExchangeRateVO;
import com.luee.wally.utils.Utilities;

public class PaymentRepository extends AbstractRepository{
	  private final Logger logger = Logger.getLogger(PaymentRepository.class.getName());
	  
	  public BigDecimal convert(double amount,String currencyCode) throws Exception{
			BigDecimal rateValue=BigDecimal.ONE;
			
			if(!currencyCode.equals("EUR")){
			     MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
			     Double cachedValue = (Double) memcache.get(currencyCode);
			     if(cachedValue==null){
			    	 String formatedDate=Utilities.formatedDate(new Date(),"yyyy-MM-dd");
			    	 ImportService importService=new ImportService();
			    	 ExchangeRateVO rate=importService.getExchangeRates(formatedDate,"EUR",currencyCode);
			    	 rateValue = BigDecimal.valueOf(rate.getRates().get(currencyCode));
			    	 memcache.put(currencyCode,rate.getRates().get(currencyCode), Expiration.byDeltaSeconds(3600));
			     }else{				 
			    	 
				     rateValue = BigDecimal.valueOf(cachedValue);
			     }
			}
			
			BigDecimal currentValue = new BigDecimal(amount);
			BigDecimal eurAmount = currentValue.divide(rateValue,2, BigDecimal.ROUND_HALF_EVEN);
			
			return eurAmount;
	  }
	  public String getPayPalCurrencyCode(String countryCode){		     
		     DatastoreService ds = createDatastoreService(Consistency.STRONG);
		     Query query = new Query("paypal_country_code_mapping");
		     query.setFilter(new FilterPredicate("country_code", FilterOperator.EQUAL, countryCode));
			 PreparedQuery pq = ds.prepare(query);
			 
			 return (String)pq.asSingleEntity().getProperty("currency");			 
	  }
	  
	  public  Entity getRedeemingRequestsByUserGuid(String userGuid) {
		  DatastoreService ds = createDatastoreService(Consistency.EVENTUAL);	


			Filter userGuidFilter = new FilterPredicate("user_guid",
	                FilterOperator.EQUAL,
	                userGuid);

			Query q = new Query("redeeming_requests_new");
			q.setFilter(userGuidFilter);
			PreparedQuery pq = ds.prepare(q);
			Entity e = pq.asSingleEntity();
			return e;
	  }
	  
	  public Entity getRedeemingRequestsByKey(String _key){
		     Key key=KeyFactory.stringToKey(_key);
		     DatastoreService ds = createDatastoreService(Consistency.STRONG);		     
		     try{
		         return ds.get(key);			  
		     }catch(EntityNotFoundException e){
		    	 return null;
		     }
	  }
	  
	  public Entity getPaidUserByGuid(String userGuid){
		     DatastoreService ds = createDatastoreService(Consistency.STRONG);		     
		     Query query = new Query("paid_users");
		     query.setFilter(new FilterPredicate("user_guid", FilterOperator.EQUAL, userGuid));
			 PreparedQuery pq = ds.prepare(query);
			 return pq.asSingleEntity();			 
	  }
	  public Entity getPaidUserByRedeemingRequestId(String redeemingRequestId){
		     DatastoreService ds = createDatastoreService(Consistency.STRONG);			 
		     Query query = new Query("paid_users");
		     query.setFilter(new FilterPredicate("redeeming_request_id", FilterOperator.EQUAL, redeemingRequestId));
			 PreparedQuery pq = ds.prepare(query);
			 return pq.asSingleEntity();
	  }
	  
	  public void savePayPalPayment(RedeemingRequests redeemingRequests,String currencyCode,BigDecimal eurAmount,String invoiceNumber,String payoutBatchId){	
	        
		   DatastoreService ds = createDatastoreService(Consistency.STRONG);
		   Entity entity=new Entity("paid_users");	
		   entity.setProperty("date", new Date());
		   entity.setProperty("user_guid",redeemingRequests.getUserGuid());
		   entity.setProperty("paid_currency",currencyCode);
		   entity.setProperty("amount", redeemingRequests.getAmount());
		   entity.setProperty("type", redeemingRequests.getType());
		   entity.setProperty("eur_currency", eurAmount.doubleValue());
		   entity.setProperty("email_address",redeemingRequests.getEmail());
		   entity.setProperty("paypal_account",redeemingRequests.getPaypalAccount());
		   entity.setProperty("paid_user_success", true);
		   entity.setProperty("email_sent_success",true);
		   entity.setProperty("redeeming_request_key",redeemingRequests.getKey());
		   entity.setProperty("redeeming_request_id",redeemingRequests.getRedeemingRequestId());
		   entity.setProperty("payment_reference_id",payoutBatchId);
		   entity.setProperty("invoice_number",invoiceNumber);
		   ds.put(entity);		  
	  }
	  public void saveGiftCardPayment(RedeemingRequests redeemingRequests,String currencyCode,BigDecimal eurAmount,String paymentReferenceId){	
		   
		   DatastoreService ds = createDatastoreService(Consistency.STRONG);
		   Entity entity=new Entity("paid_users");	
		   entity.setProperty("date", new Date());
		   entity.setProperty("user_guid",redeemingRequests.getUserGuid());
		   entity.setProperty("paid_currency",currencyCode);
		   entity.setProperty("amount", redeemingRequests.getAmount());
		   entity.setProperty("type", redeemingRequests.getType());
		   entity.setProperty("eur_currency", eurAmount.doubleValue());
		   entity.setProperty("email_address",redeemingRequests.getEmail());
		   entity.setProperty("paypal_account",redeemingRequests.getPaypalAccount());
		   entity.setProperty("paid_user_success", true);
		   entity.setProperty("email_sent_success",true);
		   entity.setProperty("redeeming_request_key",redeemingRequests.getKey());
		   entity.setProperty("redeeming_request_id",redeemingRequests.getRedeemingRequestId());
		   entity.setProperty("payment_reference_id",paymentReferenceId);
		   ds.put(entity);
	  }
	  public void saveUserPayment(PaidUserForm form,Entity redeemingRequests,BigDecimal eurAmount){	
		   
		   DatastoreService ds = createDatastoreService(Consistency.STRONG);
		   Entity entity=new Entity("paid_users");	
		   entity.setProperty("date", new Date());
		   entity.setProperty("user_guid",redeemingRequests.getProperty("user_guid"));
		   entity.setProperty("paid_currency", form.getCurrencyCode());
		   entity.setProperty("amount", Double.toString(form.getAmount()));
		   entity.setProperty("type", form.getPaymentType());
		   entity.setProperty("eur_currency", eurAmount.doubleValue());
		   entity.setProperty("email_address",redeemingRequests.getProperty("email"));
		   entity.setProperty("paypal_account",redeemingRequests.getProperty("paypal_account"));
		   entity.setProperty("paid_user_success", form.isPaidUserSuccess());
		   entity.setProperty("email_sent_success",form.isEmailSentSuccess());
		   entity.setProperty("redeeming_request_key",KeyFactory.keyToString(redeemingRequests.getKey()));
		   entity.setProperty("redeeming_request_id",redeemingRequests.getProperty("redeeming_request_id"));
		   ds.put(entity);
	  }
	  public void saveUserPaymentRemovalReason(String _key,String reason)throws EntityNotFoundException{
	     Key key=KeyFactory.stringToKey(_key);
	     DatastoreService ds = createDatastoreService(Consistency.STRONG);
	     
	     Entity entity=ds.get(key);	
	     entity.setProperty("type","Removed");
	     entity.setProperty("removal_reason", reason);
	     ds.put(entity);	     
	   }
	  
	   public Collection<String> getUserPaymentsRemovalReasons(){
			Collection<String> list = new ArrayList<>();

			DatastoreService ds = createDatastoreService(Consistency.STRONG);
			Query query = new Query("user_payments_removal_reasons");
			

			PreparedQuery pq = ds.prepare(query);
			QueryResultList<Entity> entities = pq.asQueryResultList(FetchOptions.Builder.withDefaults());

			for (Entity entity : entities) {

				list.add((String)entity.getProperty("removal_reason"));
			}
			
			return list;
	   }
	
		public Collection<RedeemingRequests> findEligibleUsers(String type,Date startDate,Date endDate,String packageName,String countryCode,Boolean confirmedEmail){
			DatastoreService  ds= createDatastoreService(Consistency.EVENTUAL);
			
			PreparedQuery pq = ds.prepare(createEligibleUsersQuery(type,startDate,endDate,packageName, countryCode,confirmedEmail));
			
			Collection<Entity> entities=new LinkedList<>();
			QueryResultList<Entity> results;
			 
			Cursor cursor=null;
		     
				
			 do{
		    	 FetchOptions fetchOptions;	 
		    	 if(cursor!=null){	 
		    		 fetchOptions = FetchOptions.Builder.withLimit(Constants.CURSOR_SIZE).startCursor(cursor);
		    	 }else{
		    		 fetchOptions = FetchOptions.Builder.withLimit(Constants.CURSOR_SIZE);	 
		    	 }
	  	     	    	 
		    	 results = pq.asQueryResultList(fetchOptions);
		    	 entities.addAll(results);		    	
		    	 
			     cursor=results.getCursor();		    		    	
		     }while(results.size()>0);

			
			return entities.stream().map(RedeemingRequests::valueOf).collect(Collectors.toList());			
		}
		
		private Query createEligibleUsersQuery(String type,Date startDate,Date endDate,String packageName,String countryCode,Boolean confirmedEmail){
			Query query = new Query("redeeming_requests_new");
			Collection<Filter> predicates=new ArrayList<>();
			
			predicates.add(new FilterPredicate("is_paid", FilterOperator.EQUAL, false));	
			
			if(confirmedEmail!=null){
				predicates.add(new FilterPredicate("confirmed_email", FilterOperator.EQUAL, confirmedEmail));	
			}
			
			if(startDate!=null){
				predicates.add(new FilterPredicate("date", FilterOperator.GREATER_THAN_OR_EQUAL, startDate));
			}
			if(endDate!=null){
				predicates.add(new FilterPredicate("date", FilterOperator.LESS_THAN , endDate));
			}
			
			
			if(countryCode!=null){
				predicates.add(new FilterPredicate("country_code", FilterOperator.EQUAL , countryCode));
			}
			if(packageName!=null&&packageName.trim().length()>0){			
				predicates.add(new FilterPredicate("package_name", FilterOperator.EQUAL , packageName));			
			}
			if(type!=null&&type.trim().length()>0){			
				predicates.add(new FilterPredicate("type", FilterOperator.EQUAL , type));			
			}
			
			if(predicates.size()>1){
				query.setFilter(Query.CompositeFilterOperator.and(predicates));			
			}else{
				query.setFilter(predicates.iterator().next());
			}
			return query.addSort("date",SortDirection.ASCENDING);	
		}
}
