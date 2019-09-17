package com.luee.wally.api.service.impex;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.luee.wally.DB;
import com.luee.wally.api.ConnectionMgr;
import com.luee.wally.command.PackageURLGroup;
import com.luee.wally.csv.UserLevelRevenue;
import com.luee.wally.json.*;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

//BASED ON REST API
//https://docs.google.com/document/d/1CoGRN_NzmMCVNUCQsdtbU0jm5VgEmtlkV8YTCsYNajE/edit
	
public class UserRevenueService {
	private final Logger logger = Logger.getLogger(UserRevenueService.class.getName());

	private static final String API_KEY_B = "QuN5chUnh2cONoLJRB9oI8tu2bqrOhVqatvBZOzFQaepM-7pAHaSPSLR29GQsmFQd9cBXZRz94mV2uIC9tfTJ_";
	private static final String API_KEY_A = "wZ2EZPalZxgPKVHk7CmcPn4ekCDTz1itbl699gviba6w4OeCaPp_Atev4sR2u9vTu_d2gXlG6sBBFrNrEAqO2S";
	private static final String API_KEY_C = "u1RvBTe6CkOz4Cr9V0JdVktg1cJrWqTOrvI1Sx7eeWciiRsDRAsyvicxJxmrlEDa7VbaRkD4ErnyGd9ip0fhiH";
	
	private ImportService importService;


	private static String[] packageNamesA = { "com.adp.gamebox" };

	private static String[] packageNamesB = { "com.relaxingbraintraining.knives", "com.relaxingbraintraining.cookiejellymatch",
			"com.relaxingbraintraining.raccoonbubbles", "com.relaxingbraintraining.popslice",
			"com.relaxingbraintraining.blocks", "com.relaxingbraintraining.wordcup",
			"com.relaxingbraintraining.hexapuzzle", "com.relaxingbraintraining.dunk",
			"com.relaxingbraintraining.rollthatball", "com.relaxingbraintraining.idleemojis",
			"com.relaxingbraintraining.solitairekingdom", "com.relaxingbraintraining.zenpuzzle",
			"com.relaxingbraintraining.pixelpaint", "com.relaxingbraintraining.ballrush",
			"com.relaxingbraintraining.oneline", "com.relaxingbraintraining.sudokumaster",
			"com.relaxingbraintraining.mergecandy", "com.relaxingbraintraining.grindmygears",
			"com.relaxingbraintraining.mousekeeper", "com.relaxingbraintraining.logicblocks",
			"com.relaxingbraintraining.zombiechallenge", "com.relaxingbraintraining.six",
			"com.relaxingbraintraining.emojibounce", "com.relaxingbraintraining.brickmania",
			"com.relaxingbraintraining.colorpuzzle", "com.relaxingbraintraining.colorjump",
			"com.relaxingbraintraining.numbermerge", "com.relaxingbraintraining.pipeout",
			 "com.relaxingbraintraining.unblockbar","com.relaxingbraintraining.pixelcolor",
			"com.relaxingbraintraining.masterofsudoku",
			"com.relaxingbraintraining.planes" };

	
	 
		

	
	/*
	//debug:
	private String[] packageNamesA = { "xxx"};
		private String[] packageNamesB = { "yyy" };

	//debug
	 * 
	 */
	 

	private static String[] packageNamesC = { "com.moregames.makemoney", "com.coinmachine.app",
			"com.matchmine.app" };
	
	private static Collection<PackageURLGroup> packageURLGroups=new ArrayList<>();
	
	static{
		
		for (String packageName : packageNamesA) {
	        PackageURLGroup group=new PackageURLGroup("https://r.applovin.com/max/userAdRevenueReport?api_key=", API_KEY_A, packageName);	
	        packageURLGroups.add(group);
		}
		for (String packageName : packageNamesB) {
			PackageURLGroup group=new PackageURLGroup("https://r.applovin.com/max/userAdRevenueReport?api_key=", API_KEY_B, packageName);
			packageURLGroups.add(group);	
		}
		for (String packageName : packageNamesC) {
			PackageURLGroup group=new PackageURLGroup("https://r.applovin.com/max/userAdRevenueReport?api_key=", API_KEY_C, packageName);	
			packageURLGroups.add(group);		
		}		
	}
		
	public UserRevenueService() {
		importService = new ImportService();
	}
	
	public String getYesterdayDate(){		 
		Date yesterday=new Date(System.currentTimeMillis()-24*60*60*1000);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(yesterday);
	}
	
	/*
	 * Process User Revenue for a particular date in format "yyyy-MM-dd"
	 * #3 - prosess links sequencially to avoid time out
	 */
	public void processUserRevenueAggregated(String date)throws Exception{
		Objects.requireNonNull(date, "Date to process daily revenue is null");
		
		// for each package iterate
			
		for (PackageURLGroup packageURLGroup : packageURLGroups) {

			RevenueLinkVO revenueLink=this.getUserLevelRevenueLink(packageURLGroup, date);
			if(revenueLink==null){
			   continue;	
			}

			//skip if package is  processed for this date
			if(isUserRevPackageProcessed(revenueLink.getPackageName(), date)){
				logger.warning("package name was already processed for:"+revenueLink.getPackageName()+". therefore skipping.");
				continue;
			}
			
			Collection<UserLevelRevenue> userLevelRevenues = getUserLevelRevenues(revenueLink);

			//user map per package name
			Map<String, BigDecimal> aggregatedUserRevenueMap = userLevelRevenues.stream()
				    .collect(
				        groupingBy(
				            UserLevelRevenue::getIDFA,
				            reducing(BigDecimal.ZERO, UserLevelRevenue::getRevenue, BigDecimal::add)));


			for (Map.Entry<String,BigDecimal> userLevelRevenue : aggregatedUserRevenueMap.entrySet()) {				
				
				//read user revenue history table
				Entity affs=DB.getAffForGaid(userLevelRevenue.getKey());
				if(affs==null){  //no user present
					continue;
				}
				else {
				//	logger.warning("found user:"+affs.getProperty("user_guid"));
				}
				
				Entity userDailyRevenue = DB
						.getUserDailyRevenueByGaid(userLevelRevenue.getKey());
				if(userDailyRevenue==null){
			    	userDailyRevenue = new Entity("user_daily_revenue");
					userDailyRevenue.setProperty("gaid", userLevelRevenue.getKey());
					userDailyRevenue.setProperty("aff_key",affs.getKey());										 
					userDailyRevenue.setProperty("rev_check_dates",new EmbeddedEntity());	
				}
				
				EmbeddedEntity  historyMap=(EmbeddedEntity) userDailyRevenue.getProperty("rev_check_dates");
				if(historyMap==null||historyMap.getProperty(revenueLink.getPackageName())==null){  //1. no entry at all
					//create history entry for first time

					List<String> list = new ArrayList<>(1);
					list.add(date);		
					historyMap.setProperty(revenueLink.getPackageName(), list);										
					
					userDailyRevenue.setProperty("rev_check_dates", historyMap);	

					//accumulate revenue
				    this.updateUserRevenue(affs, userDailyRevenue, userLevelRevenue.getValue());					
				}else if(historyMap.getProperty(revenueLink.getPackageName())!=null){  //2.there is a package -> check date history

					List<String> list = (List<String>)historyMap.getProperty(revenueLink.getPackageName());
					if(list==null){
						list = new ArrayList<>(1);									
					}
					Set<String> aset = new HashSet<String>(list);
					if(aset.contains(date)){   //date is registered already. Second run? 
					//	logger.warning("date is registered already for package");

						continue;
					}
					
					list.add(date);
					historyMap.setProperty(revenueLink.getPackageName(), list);										
					
					userDailyRevenue.setProperty("rev_check_dates", historyMap);	

					//accumulate revenue
				    this.updateUserRevenue(affs, userDailyRevenue, userLevelRevenue.getValue());
				    
				}else{  //no package , add it
					//create history entry for first time
					logger.warning("create history entry for first time");

					List<String> list = new ArrayList<>(1);
					
					list.add(date);		
					
					historyMap.setProperty(revenueLink.getPackageName(), list);										
					
					userDailyRevenue.setProperty("rev_check_dates", historyMap);	

					//accumulate revenue
				    this.updateUserRevenue(affs, userDailyRevenue, userLevelRevenue.getValue());
				}
				
			}
			
			aggregatedUserRevenueMap.clear();
			aggregatedUserRevenueMap=null;
			
			//mark package as complete for this date
			saveUserRevPackage(revenueLink.getPackageName(), date);
			
		}
	}	
	/*
	 * Process User Revenue for a particular date in format "yyyy-MM-dd"
	 * #2 - reduce/agregate datastore hits
	 *
	public void processUserRevenueAggregated(String date)throws Exception{
		Objects.requireNonNull(date, "Date to process daily revenue is null");
		
		// for each link read csv
		Collection<RevenueLinkVO> revenueLinks = this.getRevenueLinks(date);		
		for (RevenueLinkVO revenueLink : revenueLinks) {
			Collection<UserLevelRevenue> userLevelRevenues = getUserLevelRevenues(revenueLink);

			//user map per package name
			Map<String, BigDecimal> aggregatedUserRevenueMap = userLevelRevenues.stream()
				    .collect(
				        groupingBy(
				            UserLevelRevenue::getIDFA,
				            reducing(BigDecimal.ZERO, UserLevelRevenue::getRevenue, BigDecimal::add)));


			for (Map.Entry<String,BigDecimal> userLevelRevenue : aggregatedUserRevenueMap.entrySet()) {				
				
				//read user revenue history table
				Entity affs=DB.getAffForGaid(userLevelRevenue.getKey());
				if(affs==null){  //no user present
					continue;
				}
				else {
				//	logger.warning("found user:"+affs.getProperty("user_guid"));
				}
				
				Entity userDailyRevenue = DB
						.getUserDailyRevenueByGaid(userLevelRevenue.getKey());
				if(userDailyRevenue==null){
			    	userDailyRevenue = new Entity("user_daily_revenue");
					userDailyRevenue.setProperty("gaid", userLevelRevenue.getKey());
					userDailyRevenue.setProperty("aff_key",affs.getKey());										 
					userDailyRevenue.setProperty("rev_check_dates",new EmbeddedEntity());	
				}
				
				EmbeddedEntity  historyMap=(EmbeddedEntity) userDailyRevenue.getProperty("rev_check_dates");
				if(historyMap==null||historyMap.getProperty(revenueLink.getPackageName())==null){  //1. no entry at all
					//create history entry for first time

					List<String> list = new ArrayList<>(1);
					list.add(date);		
					historyMap.setProperty(revenueLink.getPackageName(), list);										
					
					userDailyRevenue.setProperty("rev_check_dates", historyMap);	

					//accumulate revenue
				    this.updateUserRevenue(affs, userDailyRevenue, userLevelRevenue.getValue());					
				}else if(historyMap.getProperty(revenueLink.getPackageName())!=null){  //2.there is a package -> check date history

					List<String> list = (List<String>)historyMap.getProperty(revenueLink.getPackageName());
					if(list==null){
						list = new ArrayList<>(1);									
					}
					Set<String> aset = new HashSet<String>(list);
					if(aset.contains(date)){   //date is registered already. Second run? 
					//	logger.warning("date is registered already for package");

						continue;
					}
					
					list.add(date);
					historyMap.setProperty(revenueLink.getPackageName(), list);										
					
					userDailyRevenue.setProperty("rev_check_dates", historyMap);	

					//accumulate revenue
				    this.updateUserRevenue(affs, userDailyRevenue, userLevelRevenue.getValue());
				    
				}else{  //no package , add it
					//create history entry for first time
					logger.warning("create history entry for first time");

					List<String> list = new ArrayList<>(1);
					
					list.add(date);		
					
					historyMap.setProperty(revenueLink.getPackageName(), list);										
					
					userDailyRevenue.setProperty("rev_check_dates", historyMap);	

					//accumulate revenue
				    this.updateUserRevenue(affs, userDailyRevenue, userLevelRevenue.getValue());
				}
				
			}
			
			aggregatedUserRevenueMap.clear();
			aggregatedUserRevenueMap=null;
			
			
		}
		
	}	
	*/
	/*
	 * Process User Revenue for a particular date in format "yyyy-MM-dd"
	 *
	public void processUserRevenue(String date)throws Exception{
		Objects.requireNonNull(date, "Date to process daily revenue is null");
		
		// for each link read csv
		Collection<RevenueLinkVO> revenueLinks = this.getRevenueLinks(date);
		for (RevenueLinkVO revenueLink : revenueLinks) {
			Collection<UserLevelRevenue> userLevelRevenues = getUserLevelRevenues(revenueLink);
			for (UserLevelRevenue userLevelRevenue : userLevelRevenues) {				
				
				//read user revenue history table
				Entity affs=DB.getAffForGaid(userLevelRevenue.getIDFA());
				if(affs==null){  //no user present
					continue;
				}
				else {
					logger.warning("found user:"+affs.getProperty("user_guid"));
				}
				
				Entity userDailyRevenue = DB
						.getUserDailyRevenueByGaid(userLevelRevenue.getIDFA());
				if(userDailyRevenue==null){
			    	userDailyRevenue = new Entity("user_daily_revenue",affs.getKey());
					userDailyRevenue.setProperty("gaid", userLevelRevenue.getIDFA());
					userDailyRevenue.setProperty("aff_key",affs.getKey());										 
					userDailyRevenue.setProperty("rev_check_dates",new EmbeddedEntity());	
				}
				
				EmbeddedEntity  historyMap=(EmbeddedEntity) userDailyRevenue.getProperty("rev_check_dates");
				if(historyMap==null||historyMap.getProperty(revenueLink.getPackageName())==null){  //1. no entry at all
					//create history entry for first time
				//	logger.warning("no entry at all");

					List<String> list = new ArrayList<>(1);
					list.add(date);		
					historyMap.setProperty(revenueLink.getPackageName(), list);										
					
					userDailyRevenue.setProperty("rev_check_dates", historyMap);	

					//accumulate revenue
				    this.updateUserRevenue(affs, userDailyRevenue, userLevelRevenue.getRevenue());					
				}else if(historyMap.getProperty(revenueLink.getPackageName())!=null){  //2.there is a package -> check date history
					logger.warning("there is a package -> check date history");

					List<String> list = (List<String>)historyMap.getProperty(revenueLink.getPackageName());
					if(list==null){
						list = new ArrayList<>(1);									
					}
					Set<String> aset = new HashSet<String>(list);
					if(aset.contains(date)){   //date is registered already. Second run? 
						continue;
					}
					
					list.add(date);
					historyMap.setProperty(revenueLink.getPackageName(), list);										
					
					userDailyRevenue.setProperty("rev_check_dates", historyMap);	

					//accumulate revenue
				    this.updateUserRevenue(affs, userDailyRevenue, userLevelRevenue.getRevenue());
				    
				}else{  //no package , add it
					//create history entry for first time
					logger.warning("create history entry for first time");

					List<String> list = new ArrayList<>(1);
					
					list.add(date);		
					
					historyMap.setProperty(revenueLink.getPackageName(), list);										
					
					userDailyRevenue.setProperty("rev_check_dates", historyMap);	

					//accumulate revenue
				    this.updateUserRevenue(affs, userDailyRevenue, userLevelRevenue.getRevenue());
				}
				
			}
			
			
		}
		
	}
	*/
	
	/*
	 * Update both user revenue and affs records in transaction
	 */
	private void updateUserRevenue(Entity affs,Entity userDailyRevenue,BigDecimal revenue){
			BigDecimal amount=BigDecimal.valueOf(affs.getProperty("total_ad_rev")==null?0:(double)affs.getProperty("total_ad_rev"));
			BigDecimal total=amount.add(revenue);					
			affs.setProperty("total_ad_rev",total.doubleValue());
			
			//logger.warning("saveAffsTotalAdRev");
			
			DB.saveAffsTotalAdRev(affs, userDailyRevenue);			  
	}

	private RevenueLinkVO getUserLevelRevenueLink(PackageURLGroup packageURLGroup,String date){
		try {
			logger.warning("calling url:"+packageURLGroup.createLink(date));

			String response = ConnectionMgr.INSTANCE.getJSON(packageURLGroup.createLink(date));
			RevenueLinkVO revenue = JSONUtils.readObject(response, RevenueLinkVO.class);
			revenue.setPackageName(packageURLGroup.getPackageName());
			return revenue;
		} catch (Exception e) {
			logger.log(Level.WARNING ,"json url read result:"+e.getMessage());

		}
		return null;
	}
	
	/*
	 * Read user revenue for each package 
	 */
	public Collection<UserLevelRevenue> getUserLevelRevenues(RevenueLinkVO revenue)
			throws MalformedURLException, IOException {

		// read csv
		//logger.log(Level.WARNING ,response);
		//logger.log(Level.WARNING ,revenue.getUrl());


		try {
			String response = ConnectionMgr.INSTANCE.getCSV(revenue.getUrl());
			return importService.importCSVText(response);

		} catch (Exception e) {
		//	e.printStackTrace();
			logger.log(Level.WARNING, "csv file read error", e);
		}

		return Collections.EMPTY_LIST;
	}


	private boolean isUserRevPackageProcessed(String packageName,String date){
		Entity userRevPackage= DB.getUserRevPackage(packageName);
		if(userRevPackage==null){
			return false;
		}
	//	logger.warning(userRevPackage.toString());
	//	logger.warning((String) userRevPackage.getProperty("package_name"));

		if(!date.equals(userRevPackage.getProperty("last_used_date"))){
			return false;
		}		
		return true;
	  	
	}	


	private void saveUserRevPackage(String packageName,String date){
		DB.saveUserRevPackage(packageName, date);	
	}
}
