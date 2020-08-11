package com.luee.wally.api.service.impex;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.luee.wally.admin.repository.UserRevenueRepository;
import com.luee.wally.api.ConnectionMgr;
import com.luee.wally.command.PackageURLGroup;
import com.luee.wally.constants.PackageNameConstants;
import com.luee.wally.csv.UserLevelRevenue;
import com.luee.wally.json.JSONUtils;
import com.luee.wally.json.RevenueLinkVO;

//BASED ON REST API
//https://docs.google.com/document/d/1CoGRN_NzmMCVNUCQsdtbU0jm5VgEmtlkV8YTCsYNajE/edit
	
public class UserRevenueService {
	private final Logger logger = Logger.getLogger(UserRevenueService.class.getName());

	private ImportService importService;

	
	private UserRevenueRepository userRevenueRepository=new UserRevenueRepository();
	
	public UserRevenueService() {
		importService = new ImportService();
		userRevenueRepository=new UserRevenueRepository();
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
	public boolean processUserRevenueAggregated(String date)throws Exception{
		Objects.requireNonNull(date, "Date to process daily revenue is null");
		
		// for each package iterate
		int unfinishedPackagesCount=0;	
		for (PackageURLGroup packageURLGroup : PackageNameConstants.packageURLGroups) {

			RevenueLinkVO revenueLink=this.getUserLevelRevenueLink(packageURLGroup, date);
			if(revenueLink==null){
			   unfinishedPackagesCount++;	
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
				Entity affs=userRevenueRepository.getLastAffEntryByGaid(userLevelRevenue.getKey());
				if(affs==null){  //no user present
					continue;
				}
				
				Entity userDailyRevenue = userRevenueRepository.getUserDailyRevenueByGaid(userLevelRevenue.getKey());
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
			userRevenueRepository.saveUserRevPackage(revenueLink.getPackageName(), date);	
		}
		return unfinishedPackagesCount==0; 		
	}	
	
	/*
	 * Update both user revenue and affs records in transaction
	 */
	private void updateUserRevenue(Entity affs,Entity userDailyRevenue,BigDecimal revenue){
			BigDecimal amount=BigDecimal.valueOf(affs.getProperty("total_ad_rev")==null?0:(double)affs.getProperty("total_ad_rev"));
			BigDecimal total=amount.add(revenue);					
			affs.setProperty("total_ad_rev",total.doubleValue());
			
			userRevenueRepository.saveAffsTotalAdRev(affs, userDailyRevenue);			  
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
		Entity userRevPackage= userRevenueRepository.getUserRevPackage(packageName);
		if(userRevPackage==null){
			return false;
		}

		if(!date.equals(userRevPackage.getProperty("last_used_date"))){
			return false;
		}		
		return true;
	  	
	}	
	/*
	 * mark job as INCOMPLETE
	 * job entry must exist
	 */
	public boolean resetJobFailure(String jobName,String status,String date){
		
		Entity entity =userRevenueRepository.getJob(jobName, date); 
		Objects.requireNonNull(entity,"Job entity does not exists for date: "+date);
		
		Integer attempts=(Integer)entity.getProperty("attempts");
		int counter=0;
		if(attempts!=null){
			counter=attempts.intValue();
		}
		counter++;
		
		entity.setProperty("attempts",counter);
		entity.setProperty("status", status);
		entity.setProperty("last_update_time", new Date());
		
		userRevenueRepository.save(entity);
		
		return counter<50;
	}
}
