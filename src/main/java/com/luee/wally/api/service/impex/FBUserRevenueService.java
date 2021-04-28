package com.luee.wally.api.service.impex;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.luee.wally.admin.repository.AffsRepository;
import com.luee.wally.admin.repository.FBUserRevenueRepository;
import com.luee.wally.api.ConnectionMgr;
import com.luee.wally.api.service.AbstractService;
import com.luee.wally.command.PackageURLGroup;
import com.luee.wally.constants.FBPackageNameConstants;
import com.luee.wally.csv.FBUserLevelRevenue;
import com.luee.wally.json.JSONUtils;
import com.luee.wally.json.RevenueLinkVO;

public class FBUserRevenueService extends AbstractService{
	private final Logger logger = Logger.getLogger(FBUserRevenueService.class.getName());


	private FBImportService importService;

	
	private FBUserRevenueRepository fbUserRevenueRepository;
	
	private AffsRepository affsRepository;
	
	public FBUserRevenueService() {
		importService = new FBImportService();
		fbUserRevenueRepository=new FBUserRevenueRepository();
		affsRepository=new AffsRepository();
	}
	/*
	 * Process User Revenue for a particular date in format "yyyy-MM-dd"
	 * #3 - prosess links sequencially to avoid time out
	 */
	public Collection<String> processFBUserRevenueAggregated(String date)throws Exception{
		Objects.requireNonNull(date, "Date to process daily revenue is null");
		Collection<String> unfinishedPackages=new ArrayList<>();
		// for each package iterate
		//int unfinishedPackagesCount=0;	
		for (PackageURLGroup packageURLGroup : FBPackageNameConstants.packageURLGroups) {
			
			RevenueLinkVO revenueLink=this.getUserLevelRevenueLink(packageURLGroup, date);
			if(revenueLink==null){
			   //unfinishedPackagesCount++;	
				unfinishedPackages.add(packageURLGroup.getPackageName());
				continue;	
			}
			//skip if package is  processed for this date
			if(isUserRevPackageProcessed(revenueLink.getPackageName(), date)){
				logger.warning("fb package name was already processed for:"+revenueLink.getPackageName()+". therefore skipping.");
				continue;
			}
			Collection<FBUserLevelRevenue> userLevelRevenues = getUserLevelRevenues(revenueLink);
			// group package entries per user 
			Map<String, List<String>> userLevelRevenuesByUserMap = userLevelRevenues.stream()
				    .collect(Collectors.groupingBy( FBUserLevelRevenue::getIDFA,Collectors.mapping(FBUserLevelRevenue::getEncryptedCPM, Collectors.toList())));
//			userLevelRevenuesByUserMap.entrySet().forEach(					
//					e->{
//						e.getValue().forEach(l->{
//						  System.out.println(e.getKey()+"::"+l);
//					    });
//						}
//					);
			for (Map.Entry<String,List<String>> userLevelRevenue : userLevelRevenuesByUserMap.entrySet()) {	
				//read user revenue aff table
				Entity affs=affsRepository.getLastAffEntryByGaid(userLevelRevenue.getKey());
				if(affs==null){  //no user present
					continue;
				}
				Entity fbUserDailyRevenue = fbUserRevenueRepository.getUserDailyRevenueByGaid(userLevelRevenue.getKey(),date);
				if(fbUserDailyRevenue==null){
					fbUserDailyRevenue = new Entity("user_daily_revenue_fb");
					fbUserDailyRevenue.setProperty("gaid", userLevelRevenue.getKey());
					fbUserDailyRevenue.setProperty("aff_key",affs.getKey());										 
					fbUserDailyRevenue.setProperty("rev_check_date",date);
					fbUserDailyRevenue.setProperty("app_cpm",new EmbeddedEntity());
				}
				//encrypted
				EmbeddedEntity  appCpm=(EmbeddedEntity) fbUserDailyRevenue.getProperty("app_cpm");
				if(appCpm==null||appCpm.getProperty(revenueLink.getPackageName())==null){  //1. no entry at all
					//create encripted CPM entry for first time
					appCpm.setProperty(revenueLink.getPackageName(), userLevelRevenue.getValue());															
					fbUserDailyRevenue.setProperty("app_cpm",appCpm);	
				}else if(appCpm.getProperty(revenueLink.getPackageName())!=null){  //2.there is a package -> check app cpm
					List<String> values = (List<String>)appCpm.getProperty(revenueLink.getPackageName());
					if(values==null){
						values = new ArrayList<>();									
					}
					Set<String> aset = new HashSet<String>(values);
					//merge with new ones - must be the same
					aset.addAll( userLevelRevenue.getValue());
					appCpm.setProperty(revenueLink.getPackageName(), aset);															
					fbUserDailyRevenue.setProperty("app_cpm",appCpm);	
				}else{
					List<String> list = new ArrayList<>();					
					list.add(date);							
					appCpm.setProperty(revenueLink.getPackageName(),userLevelRevenue.getValue());															
					fbUserDailyRevenue.setProperty("app_cpm", appCpm);					
				}
				
				fbUserRevenueRepository.save(fbUserDailyRevenue);
			}

			
			//mark package as complete for this date
			fbUserRevenueRepository.saveUserRevPackage(revenueLink.getPackageName(), date);				
		}
		
		//return unfinishedPackagesCount==0;
		return unfinishedPackages;
	}
	
	private boolean isUserRevPackageProcessed(String packageName,String date){		
		Entity userRevPackage= fbUserRevenueRepository.getUserRevPackage(packageName);
		if(userRevPackage==null){
			return false;
		}

		if(!date.equals(userRevPackage.getProperty("last_used_date"))){
			return false;
		}		
		return true;
	  	
	}
	
	/*
	 * Read user revenue for each package 
	 */
	private Collection<FBUserLevelRevenue> getUserLevelRevenues(RevenueLinkVO revenue)
			throws MalformedURLException, IOException {


		try {
			String response = ConnectionMgr.INSTANCE.getCSV(revenue.getUrl());
			return importService.importFBCSVText(response);

		} catch (Exception e) {
		//	e.printStackTrace();
			logger.log(Level.WARNING, "csv file read error", e);
		}

		return Collections.EMPTY_LIST;
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
}
