package com.paypal.integrate.admin.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceConfig;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.ReadPolicy;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.paypal.integrate.admin.command.AffsSearchResult;
import com.paypal.integrate.admin.command.CampaignSearchForm;

public class CampaignSearchService {
	private final Logger logger = Logger.getLogger(CampaignSearchService.class.getName());
	
	private static final int CURSOR_SIZE=1000;
	
	public Collection<AffsSearchResult> processCampaignSearch(CampaignSearchForm campaignSearchForm){
		logger.log(Level.WARNING,campaignSearchForm.toString());
		
		 Collection<AffsSearchResult> campaignSearchResults=new ArrayList<>();
		 if(campaignSearchForm.getAdNetworks().size()>1){  //multi search
			 for(String adNetwork:campaignSearchForm.getAdNetworks()){
				 Collection<AffsSearchResult> affsSearchResult= this.processCampaignSearch(campaignSearchForm.getStartDate(), campaignSearchForm.getEndDate(),campaignSearchForm.getCountryCode(), campaignSearchForm.getPackageName(),adNetwork,campaignSearchForm.getCampaignsFirst(),campaignSearchForm.getSourcesFirst());  
				 campaignSearchResults.addAll(affsSearchResult);				 
			 }			 
		 }else if(campaignSearchForm.getSources().size()>1){
			 for(String source:campaignSearchForm.getSources()){
				 Collection<AffsSearchResult> affsSearchResult= this.processCampaignSearch(campaignSearchForm.getStartDate(), campaignSearchForm.getEndDate(),campaignSearchForm.getCountryCode(), campaignSearchForm.getPackageName(),campaignSearchForm.getAdNetworksFirst(),campaignSearchForm.getCampaignsFirst(),source); 
				 campaignSearchResults.addAll(affsSearchResult);				 
			 }			 
		 }else if(campaignSearchForm.getCampaigns().size()>1){
			 for(String campaign:campaignSearchForm.getCampaigns()){
				 Collection<AffsSearchResult> affsSearchResult= this.processCampaignSearch(campaignSearchForm.getStartDate(), campaignSearchForm.getEndDate(),campaignSearchForm.getCountryCode(), campaignSearchForm.getPackageName(),campaignSearchForm.getAdNetworksFirst(),campaign,campaignSearchForm.getSourcesFirst()); 
				 campaignSearchResults.addAll(affsSearchResult);				 
			 }
		 }else{   //single pass
			 Collection<AffsSearchResult> affsSearchResult= this.processCampaignSearch(campaignSearchForm.getStartDate(), campaignSearchForm.getEndDate(),campaignSearchForm.getCountryCode(), campaignSearchForm.getPackageName(),campaignSearchForm.getAdNetworksFirst(),campaignSearchForm.getCampaignsFirst(),campaignSearchForm.getSourcesFirst()); 
			 campaignSearchResults.addAll(affsSearchResult);
		 }
		 
	 	 return campaignSearchResults;
	}
	public Collection<AffsSearchResult>  processCampaignSearch(Date startDate,Date endDate,String country,String packageName,String addNetwork,String campaignId,String sourceId){
		Collection<AffsSearchResult> campaignSearchResults=new ArrayList<>(); 
		DatastoreService ds=createDatastoreService();
	 	 
		 Query query=createCampaignQuery(startDate, endDate,country,  packageName,addNetwork,campaignId,sourceId);
		 
		 PreparedQuery preparedQuery = ds.prepare(query);
		 
		 QueryResultList<Entity> results;
		 
		 Cursor cursor=null;
	     
	     Collection<String> affsIds=new ArrayList<>();
	     AffsSearchService affsSearchService=new AffsSearchService();
	     
		 do{
	    	 FetchOptions fetchOptions;	 
	    	 if(cursor!=null){	 
	    		 fetchOptions = FetchOptions.Builder.withLimit(CURSOR_SIZE).startCursor(cursor);
	    	 }else{
	    		 fetchOptions = FetchOptions.Builder.withLimit(CURSOR_SIZE);	 
	    	 }
  	     	    	 
	    	 results = preparedQuery.asQueryResultList(fetchOptions);
	         affsIds.clear();
	    	 for(Entity e:results){
	    		affsIds.add(e.getKey().getName());   //accumulate affs ids
	    	 }
	    	 /*
	    	  * calculate affs data by id
	    	  */
	    	 if(affsIds.size()>0){
	    		 AffsSearchResult affsSearchResult= affsSearchService.processAffsSearch(affsIds);
	    		 campaignSearchResults.add(affsSearchResult);
	    	 }
		     cursor=results.getCursor();		    		    	
	     }while(results.size()>0);

		 return campaignSearchResults;
	}
	
	private DatastoreService createDatastoreService(){
		 double deadline = 15.0; //seconds
		 // Construct a read policy for eventual consistency
		 ReadPolicy policy = new ReadPolicy(ReadPolicy.Consistency.EVENTUAL);

		// Set both the read policy and the call deadline
		 DatastoreServiceConfig datastoreConfig =
		    DatastoreServiceConfig.Builder.withReadPolicy(policy).deadline(deadline);

		// Get Datastore service with the given configuration
		 return DatastoreServiceFactory.getDatastoreService(datastoreConfig);
	}
	
	private Query createCampaignQuery(Date startDate,Date endDate,String country,String packageName,String addNetwork,String campaignId,String sourceId){
		Query query = new Query("user_campaign_data");
		Collection<Filter> predicates=new ArrayList<>();
		
		if(startDate!=null){
			predicates.add(new FilterPredicate("date", FilterOperator.GREATER_THAN, startDate));
		}
		if(endDate!=null){
			predicates.add(new FilterPredicate("date", FilterOperator.LESS_THAN , endDate));
		}
		if(country!=null){
			predicates.add(new FilterPredicate("country_code", FilterOperator.EQUAL , country));
		}
		if(packageName!=null){			
			predicates.add(new FilterPredicate("package_name", FilterOperator.EQUAL , packageName));			
		}
		if(addNetwork!=null){
			predicates.add(new FilterPredicate("ad_network", FilterOperator.EQUAL , addNetwork));
		}

		if(sourceId!=null){
			predicates.add(new FilterPredicate("source_id", FilterOperator.EQUAL , sourceId));
		}
		if(campaignId!=null){
			predicates.add(new FilterPredicate("tenjin_camp_id", FilterOperator.EQUAL , campaignId));
		}
		
		if(predicates.size()>1){
			query.setFilter(Query.CompositeFilterOperator.and(predicates));			
		}else{
			query.setFilter(predicates.iterator().next());
		}
		
		return query;		
	}
//	private AffsSearchResult processUserCampaignDataSearch(Date startDate,Date endDate,String country,String experiment,String packageName){
//		
//	}
//	private AffsSearchResult processAffsSearch(Date startDate,Date endDate,String country,String experiment,String packageName){
//		
//	}
}
