package com.luee.wally.api.service;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceConfig;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.ReadPolicy;
import com.luee.wally.api.service.impex.GenerateCSV;
import com.luee.wally.command.AffsSearchResult;
import com.luee.wally.command.CampaignSearchForm;
import com.luee.wally.command.CampaignSearchResult;
import com.luee.wally.constants.Constants;

public class CampaignSearchService extends AbstractService{
	private final Logger logger = Logger.getLogger(CampaignSearchService.class.getName());
	
	
	private Collection<String> SEARCH_HEADER = Arrays.asList("*","campaign_count", "affs_count", "sum_total_ad_rev", "avr_total_ad_rev",
			"sum_offerwall_rev", "avr_offerwall_rev","sum_applike_rev_total","avr_applike_rev","paid_users_total","avr_paid_users","min_rev_count");

	public void createFile(Writer writer, CampaignSearchForm form, Collection<CampaignSearchResult> content)
			throws IOException {

		// set header
		writer.append(form.toString() + "\n");
		// field names
		convertHeaderToCSV(writer, SEARCH_HEADER);
		// set content
		convertContentToCSV(writer, content);

	}
	private void convertContentToCSV(Writer writer, Collection<CampaignSearchResult> list) throws IOException {
		Collection<String> line = new ArrayList<String>();

		for (CampaignSearchResult item : list) {
			// item
			line.add(item.getGroupValue());
			line.add(String.valueOf(item.getCampaignCount()));
			line.add(String.valueOf(item.getAffsCount()));
			line.add(item.getTotalAdRev().setScale(4, BigDecimal.ROUND_HALF_EVEN).toString());
			line.add(item.getAvrTotalAdRev().setScale(4, BigDecimal.ROUND_HALF_EVEN).toString());

			line.add(item.getOfferwallRev().setScale(4, BigDecimal.ROUND_HALF_EVEN).toString());
			line.add(item.getAvrOfferwallRev().setScale(4, BigDecimal.ROUND_HALF_EVEN).toString());

			line.add(item.getAppLikeRev().setScale(4, BigDecimal.ROUND_HALF_EVEN).toString());
			line.add(item.getAvrAppLikeRev().setScale(4, BigDecimal.ROUND_HALF_EVEN).toString());
			
			line.add(item.getTotalPaidUsersUSD().setScale(4, BigDecimal.ROUND_HALF_EVEN).toString());
			line.add(item.getAvrTotalPaidUsersUSD().setScale(4, BigDecimal.ROUND_HALF_EVEN).toString());
			
			line.add(String.valueOf(item.getMinRevCount()));
			
			GenerateCSV.INSTANCE.writeLine(writer, line);
			line.clear();
		}
	}	
	private void convertHeaderToCSV(Writer writer, Collection<String> header) throws IOException {
		GenerateCSV.INSTANCE.writeLine(writer, header);
	}
	
	public Collection<CampaignSearchResult> processCampaignSearch(CampaignSearchForm campaignSearchForm){		
		 Collection<CampaignSearchResult> campaignSearchResults=new ArrayList<>();
		 if(campaignSearchForm.getAdNetworks().size()>1){  //multi search
			 for(String adNetwork:campaignSearchForm.getAdNetworks()){
				 CampaignSearchResult affsSearchResult= this.processCampaignSearch(campaignSearchForm.getMinRevThreshold(),campaignSearchForm.getStartDate(), campaignSearchForm.getEndDate(),campaignSearchForm.getCountryCode(), campaignSearchForm.getPackageName(),adNetwork,campaignSearchForm.getCampaignsFirst(),campaignSearchForm.getSourcesFirst());  
				 affsSearchResult.setGroupName("adNetwork");
				 affsSearchResult.setGroupValue(adNetwork);
				 campaignSearchResults.add(affsSearchResult);				 
			 }			 
		 }else if(campaignSearchForm.getSources().size()>1){
			 for(String source:campaignSearchForm.getSources()){
				 CampaignSearchResult affsSearchResult= this.processCampaignSearch(campaignSearchForm.getMinRevThreshold(),campaignSearchForm.getStartDate(), campaignSearchForm.getEndDate(),campaignSearchForm.getCountryCode(), campaignSearchForm.getPackageName(),campaignSearchForm.getAdNetworksFirst(),campaignSearchForm.getCampaignsFirst(),source); 
				 affsSearchResult.setGroupName("Sources");
				 affsSearchResult.setGroupValue(source);
				 campaignSearchResults.add(affsSearchResult);				 
			 }			 
		 }else if(campaignSearchForm.getCampaigns().size()>1){
			 for(String campaign:campaignSearchForm.getCampaigns()){
				 CampaignSearchResult affsSearchResult= this.processCampaignSearch(campaignSearchForm.getMinRevThreshold(),campaignSearchForm.getStartDate(), campaignSearchForm.getEndDate(),campaignSearchForm.getCountryCode(), campaignSearchForm.getPackageName(),campaignSearchForm.getAdNetworksFirst(),campaign,campaignSearchForm.getSourcesFirst()); 
				 affsSearchResult.setGroupName("Campaign");
				 affsSearchResult.setGroupValue(campaign);
				 campaignSearchResults.add(affsSearchResult);				 
			 }
		 }else{   //single pass
			 CampaignSearchResult affsSearchResult= this.processCampaignSearch(campaignSearchForm.getMinRevThreshold(), campaignSearchForm.getStartDate(), campaignSearchForm.getEndDate(),campaignSearchForm.getCountryCode(), campaignSearchForm.getPackageName(),campaignSearchForm.getAdNetworksFirst(),campaignSearchForm.getCampaignsFirst(),campaignSearchForm.getSourcesFirst()); 
			 campaignSearchResults.add(affsSearchResult);
		 }
		 
	 	 return campaignSearchResults;
	}
	private CampaignSearchResult  processCampaignSearch(Double minRevThreshold, Date startDate,Date endDate,String country,String packageName,String addNetwork,String campaignId,String sourceId){
		
		DatastoreService ds=createDatastoreService();
	 	 
		 Query query=createCampaignQuery(startDate, endDate,country,  packageName,addNetwork,campaignId,sourceId);
		 
		 PreparedQuery preparedQuery = ds.prepare(query);
		 
		 QueryResultList<Entity> results;
		 
		 Cursor cursor=null;
	     
	     Collection<String> affsIds=new ArrayList<>();
	     AffsSearchService affsSearchService=new AffsSearchService();
	     
		 BigDecimal totalAdRev = BigDecimal.ZERO;
		 BigDecimal offerwallRev = BigDecimal.ZERO;
		 BigDecimal totalPaidUsers = BigDecimal.ZERO;		 
		 BigDecimal appLikeRev = BigDecimal.ZERO;
		 
 		 int affsCount=0,campaignCount  = 0;
		 int minRevCount=0;
			
		 do{
	    	 FetchOptions fetchOptions;	 
	    	 if(cursor!=null){	 
	    		 fetchOptions = FetchOptions.Builder.withLimit(Constants.CURSOR_SIZE).startCursor(cursor);
	    	 }else{
	    		 fetchOptions = FetchOptions.Builder.withLimit(Constants.CURSOR_SIZE);	 
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
	    		    AffsSearchResult affsSearchResult= affsSearchService.processAffsSearch(minRevThreshold, affsIds,startDate,endDate);	 								
					totalAdRev = totalAdRev.add(affsSearchResult.getTotalAdRev());					
					offerwallRev = offerwallRev.add(affsSearchResult.getOfferwallRev());
					appLikeRev=appLikeRev.add(affsSearchResult.getAppLikeRev());
					totalPaidUsers =totalPaidUsers.add(affsSearchResult.getTotalPaidUsers()); 
					affsCount+=affsSearchResult.getCount();		
					minRevCount+=affsSearchResult.getMinRevCount();
	    	 }
	    	 campaignCount+=results.size();
	    	 
		     cursor=results.getCursor();		    		    	
	     }while(results.size()>0);
		 
		 return new CampaignSearchResult(totalAdRev, offerwallRev,appLikeRev,totalPaidUsers,affsCount,campaignCount,minRevCount);
	}
	
	private Query createCampaignQuery(Date startDate,Date endDate,String country,String packageName,String addNetwork,String campaignId,String sourceId){
		Query query = new Query("user_campaign_data");
		Collection<Filter> predicates=new ArrayList<>();
		
		if(startDate!=null){
			predicates.add(new FilterPredicate("date", FilterOperator.GREATER_THAN_OR_EQUAL, startDate));
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
