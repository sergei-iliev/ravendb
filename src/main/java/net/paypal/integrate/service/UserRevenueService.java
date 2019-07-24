package net.paypal.integrate.service;

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
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.googlecode.objectify.ObjectifyService;

import net.paypal.integrate.command.PackageURLGroup;
import net.paypal.integrate.command.csv.UserLevelRevenue;
import net.paypal.integrate.command.json.JSONUtils;
import net.paypal.integrate.command.json.RevenueLinkVO;
import net.paypal.integrate.entity.Affs;
import net.paypal.integrate.entity.UserDailyRevenue;
import net.paypal.integrate.entity.UserRevPackage;
import net.paypal.integrate.repository.RevenueRepository;

//BASED ON REST API
//https://docs.google.com/document/d/1CoGRN_NzmMCVNUCQsdtbU0jm5VgEmtlkV8YTCsYNajE/edit
	
@Service
public class UserRevenueService {
	private final Logger logger = Logger.getLogger(UserRevenueService.class.getName());

	private static final String API_KEY_A = "QuN5chUnh2cONoLJRB9oI8tu2bqrOhVqatvBZOzFQaepM-7pAHaSPSLR29GQsmFQd9cBXZRz94mV2uIC9tfTJ_";
	private static final String API_KEY_B = "wZ2EZPalZxgPKVHk7CmcPn4ekCDTz1itbl699gviba6w4OeCaPp_Atev4sR2u9vTu_d2gXlG6sBBFrNrEAqO2S";
	private static final String API_KEY_C = "u1RvBTe6CkOz4Cr9V0JdVktg1cJrWqTOrvI1Sx7eeWciiRsDRAsyvicxJxmrlEDa7VbaRkD4ErnyGd9ip0fhiH";
	
	@Autowired
	private ImportService importService;

	@Autowired
	private RevenueRepository revenueRepository;

	private static String[] packageNamesA = { "com.relaxingbraintraining.knives", "com.relaxingbraintraining.cookiejellymatch",
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
			"com.relaxingbraintraining.poolpro", "com.relaxingbraintraining.unblockbar",
			"com.relaxingbraintraining.snakeclash", "com.relaxingbraintraining.onelineadvanced",
			"com.relaxingbraintraining.cookiejellycrush", "com.relaxingbraintraining.pixelcolor",
			"com.relaxingbraintraining.masterofsudoku", "com.relaxingbraintraining.snakez",
			"com.relaxingbraintraining.planes" };

	private static String[] packageNamesB = { "com.adp.gamebox" };

	 

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
	/*
	 * Test DEMO create method
	 *
	public void createUserRevenueDEMO(String date) throws Exception {
		System.out.println("affs count="+ObjectifyService.ofy().load().type(Affs.class).count());
		Collection<UserDailyRevenue> revs= ObjectifyService.ofy().load().type(UserDailyRevenue.class).limit(1000).list();
		revs.forEach(a->{System.out.println(a.getRevenueCheckDates()+"::"+a.getGaid());});
		// for each link read csv
		Collection<RevenueLinkVO> revenueLinks = this.getRevenueLinks(date);
		for (RevenueLinkVO revenueLink : revenueLinks) {
			Collection<UserLevelRevenue> userLevelRevenues = getUserLevelRevenues(revenueLink);
			if (userLevelRevenues.size() > 1000) {
				continue;
			}
			int i = 0;
			for (UserLevelRevenue userLevelRevenue : userLevelRevenues) {
				
				UserDailyRevenue userDailyRevenue = revenueRepository
						.getUserDailyRevenueByGaid(userLevelRevenue.getIDFA());

				if (userDailyRevenue == null) {
					userDailyRevenue = new UserDailyRevenue();
					userDailyRevenue.setGaid(userLevelRevenue.getIDFA());
					Set<String> set = new HashSet<>(1);
					set.add(date);
					System.out.println(set);
					userDailyRevenue.getRevenueCheckDates().put(revenueLink.getPackageName(), set);
				} else {										
					Set<String> dates = userDailyRevenue.getRevenueCheckDates().get(revenueLink.getPackageName());
					if (dates == null) {
						dates = new HashSet<>(1);
						userDailyRevenue.getRevenueCheckDates().put(revenueLink.getPackageName(), dates);
					}else{
						System.out.println("Found:"+dates);	
					}
					dates.add(date);

					System.out.println("Save:"+userDailyRevenue.getRevenueCheckDates());
				}
				if ((++i % 200) == 0) {
					System.out.println(i);
				}
				Affs affs = revenueRepository.getAffsByGaid(userLevelRevenue.getIDFA());
				if (affs == null) {
					affs = new Affs();
					affs.setGaid(userLevelRevenue.getIDFA());
				}

				revenueRepository.save(affs, userDailyRevenue);
			}

		}
		
		//TEST
		UserDailyRevenue userDailyRevenue = revenueRepository
				.getUserDailyRevenueByGaid("7093026c-29e3-4492-9040-17061aa12ce8");
		Set<String> dates = new HashSet<>(1);
		dates.add("2020-03-03");
		userDailyRevenue.getRevenueCheckDates().put("com.relaxingbraintraining.mergecandy",dates);
		revenueRepository.save(userDailyRevenue);
		
		System.out.println("affs count="+ObjectifyService.ofy().load().type(Affs.class).count());
	}
	*/
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
		
		// for each package itterate
			
		for (PackageURLGroup packageURLGroup : packageURLGroups) {
			RevenueLinkVO revenueLink=this.getUserLevelRevenueLink(packageURLGroup, date);
			if(revenueLink==null){
			   continue;	
			}
			Collection<UserLevelRevenue> userLevelRevenues = getUserLevelRevenues(revenueLink);

			//user map per package name
			Map<String, BigDecimal> aggregatedUserRevenueMap = userLevelRevenues.stream()
				    .collect(
				        groupingBy(
				            UserLevelRevenue::getIDFA,
				            reducing(BigDecimal.ZERO, UserLevelRevenue::getRevenue, BigDecimal::add)));

			//DEBUG
//			if (userLevelRevenues.size() > 2000) {
//				continue;
//			}
			for (Map.Entry<String,BigDecimal> userLevelRevenue : aggregatedUserRevenueMap.entrySet()) {
				//System.out.println(userLevelRevenues.size());
				
				
				//read user revenue history table
				Affs affs=revenueRepository.getAffsByGaid(userLevelRevenue.getKey());
				if(affs==null){  //no user present
					continue;
				}
				
				UserDailyRevenue userDailyRevenue = revenueRepository
						.getUserDailyRevenueByGaid(userLevelRevenue.getKey());
				if(userDailyRevenue==null){
			    	userDailyRevenue = new UserDailyRevenue();
					userDailyRevenue.setGaid(userLevelRevenue.getKey());
					userDailyRevenue.setAffs(affs.getKey());									  	
				}
				
				Map<String, Set<String>>  historyMap=userDailyRevenue.getRevenueCheckDates();
				if(historyMap==null||historyMap.isEmpty()){  //1. no entry at all
					//create history entry for first time
					Set<String> set = new HashSet<>(1);
					set.add(date);		
					userDailyRevenue.getRevenueCheckDates().put(revenueLink.getPackageName(), set);										
					
					//accumulate revenue
				    this.updateUserRevenue(affs, userDailyRevenue, userLevelRevenue.getValue());					
				}else if(historyMap.containsKey(revenueLink.getPackageName())){  //2.there is a package -> check date history
					Set<String> set = historyMap.get(revenueLink.getPackageName());
					if(set==null){
						set = new HashSet<>(1);									
					}
					
					if(set.contains(date)){   //date is registered already. Second run? 
						continue;
					}
					
					set.add(date);		
					userDailyRevenue.getRevenueCheckDates().put(revenueLink.getPackageName(), set);										
					//accumulate revenue
				    this.updateUserRevenue(affs, userDailyRevenue, userLevelRevenue.getValue());
				    
				}else{  //no package , add it
					//create history entry for first time
					Set<String> set = new HashSet<>(1);
					
					set.add(date);		
					userDailyRevenue.getRevenueCheckDates().put(revenueLink.getPackageName(), set);										
					//accumulate revenue
				    this.updateUserRevenue(affs, userDailyRevenue, userLevelRevenue.getValue());
				}
				
			}
			aggregatedUserRevenueMap.clear();
			aggregatedUserRevenueMap=null;
			
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
			//skip if package is  processed for this date
			if(isUserRevPackageProcessed(revenueLink.getPackageName(), date)){
				continue;
			}
			
			Collection<UserLevelRevenue> userLevelRevenues = getUserLevelRevenues(revenueLink);

			//user map per package name
			Map<String, BigDecimal> aggregatedUserRevenueMap = userLevelRevenues.stream()
				    .collect(
				        groupingBy(
				            UserLevelRevenue::getIDFA,
				            reducing(BigDecimal.ZERO, UserLevelRevenue::getRevenue, BigDecimal::add)));

			//DEBUG
//			if (userLevelRevenues.size() > 2000) {
//				continue;
//			}
			for (Map.Entry<String,BigDecimal> userLevelRevenue : aggregatedUserRevenueMap.entrySet()) {
				//System.out.println(userLevelRevenues.size());
				
				
				//read user revenue history table
				Affs affs=revenueRepository.getAffsByGaid(userLevelRevenue.getKey());
				if(affs==null){  //no user present
					continue;
				}
				
				UserDailyRevenue userDailyRevenue = revenueRepository
						.getUserDailyRevenueByGaid(userLevelRevenue.getKey());
				if(userDailyRevenue==null){
			    	userDailyRevenue = new UserDailyRevenue();
					userDailyRevenue.setGaid(userLevelRevenue.getKey());
					userDailyRevenue.setAffs(affs.getKey());									  	
				}
				
				Map<String, Set<String>>  historyMap=userDailyRevenue.getRevenueCheckDates();
				if(historyMap==null||historyMap.isEmpty()){  //1. no entry at all
					//create history entry for first time
					Set<String> set = new HashSet<>(1);
					set.add(date);		
					userDailyRevenue.getRevenueCheckDates().put(revenueLink.getPackageName(), set);										
					
					//accumulate revenue
				    this.updateUserRevenue(affs, userDailyRevenue, userLevelRevenue.getValue());					
				}else if(historyMap.containsKey(revenueLink.getPackageName())){  //2.there is a package -> check date history
					Set<String> set = historyMap.get(revenueLink.getPackageName());
					if(set==null){
						set = new HashSet<>(1);									
					}
					
					if(set.contains(date)){   //date is registered already. Second run? 
						continue;
					}
					
					set.add(date);		
					userDailyRevenue.getRevenueCheckDates().put(revenueLink.getPackageName(), set);										
					//accumulate revenue
				    this.updateUserRevenue(affs, userDailyRevenue, userLevelRevenue.getValue());
				    
				}else{  //no package , add it
					//create history entry for first time
					Set<String> set = new HashSet<>(1);
					
					set.add(date);		
					userDailyRevenue.getRevenueCheckDates().put(revenueLink.getPackageName(), set);										
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
	*/
	/*
	 * Process User Revenue for a particular date in format "yyyy-MM-dd"
	 * #1
	 *
	public void processUserRevenue(String date)throws Exception{
		Objects.requireNonNull(date, "Date to process daily revenue is null");
		
		// for each link read csv
		Collection<RevenueLinkVO> revenueLinks = this.getRevenueLinks(date);		
		for (RevenueLinkVO revenueLink : revenueLinks) {
			Collection<UserLevelRevenue> userLevelRevenues = getUserLevelRevenues(revenueLink);
			
			System.out.println(userLevelRevenues.size()+"==="+revenueLink.getPackageName());
			//DEBUG
			if (userLevelRevenues.size() > 2000) {
				continue;
			}
			for (UserLevelRevenue userLevelRevenue : userLevelRevenues) {
				//read user revenue history table
				Affs affs=revenueRepository.getAffsByGaid(userLevelRevenue.getIDFA());
				if(affs==null){  //no user present
					continue;
				}
				
				UserDailyRevenue userDailyRevenue = revenueRepository
						.getUserDailyRevenueByGaid(userLevelRevenue.getIDFA());
				if(userDailyRevenue==null){
			    	userDailyRevenue = new UserDailyRevenue();
					userDailyRevenue.setGaid(userLevelRevenue.getIDFA());
					userDailyRevenue.setAffs(affs.getKey());									  	
				}
				
				Map<String, Set<String>>  historyMap=userDailyRevenue.getRevenueCheckDates();
				if(historyMap==null||historyMap.isEmpty()){  //1. no entry at all
					//create history entry for first time
					Set<String> set = new HashSet<>(1);
					set.add(date);		
					userDailyRevenue.getRevenueCheckDates().put(revenueLink.getPackageName(), set);										
					
					//accumulate revenue
				    this.updateUserRevenue(affs, userDailyRevenue, userLevelRevenue.getRevenue());					
				}else if(historyMap.containsKey(revenueLink.getPackageName())){  //2.there is a package -> check date history
					Set<String> set = historyMap.get(revenueLink.getPackageName());
					if(set==null){
						set = new HashSet<>(1);									
					}
					
					if(set.contains(date)){   //date is registered already. Second run? 
						continue;
					}
					
					set.add(date);		
					userDailyRevenue.getRevenueCheckDates().put(revenueLink.getPackageName(), set);										
					//accumulate revenue
				    this.updateUserRevenue(affs, userDailyRevenue, userLevelRevenue.getRevenue());
				    
				}else{  //no package , add it
					//create history entry for first time
					Set<String> set = new HashSet<>(1);
					
					set.add(date);		
					userDailyRevenue.getRevenueCheckDates().put(revenueLink.getPackageName(), set);										
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
	private void updateUserRevenue(Affs affs,UserDailyRevenue userDailyRevenue,BigDecimal revenue){
			BigDecimal amount=BigDecimal.valueOf(affs.getTotalRevenue());
			BigDecimal total=amount.add(revenue);					
			affs.setTotalRevenue(total.doubleValue());
			revenueRepository.save(affs, userDailyRevenue);			  
	}
	/*
	 * Read Revenue links for each package  
	 */
	public Collection<RevenueLinkVO> getRevenueLinks(String date) throws MalformedURLException, IOException {
		Collection<RevenueLinkVO> revenues = new LinkedList<>();

		for (String packageName : packageNamesA) {
			String url = "https://r.applovin.com/max/userAdRevenueReport?api_key=" + API_KEY_A + "&date=" + date
					+ "&platform=android&application=" + packageName;

			try {
				String response = ConnectionMgr.INSTANCE.getJSON(url);
				RevenueLinkVO revenue = JSONUtils.readObject(response, RevenueLinkVO.class);
				revenue.setPackageName(packageName);
				revenues.add(revenue);
			} catch (Exception e) {
				logger.log(Level.WARNING,"json url read result:"+e.getMessage());
			}
		}

		for (String packageName : packageNamesB) {
			String url = "https://r.applovin.com/max/userAdRevenueReport?api_key=" + API_KEY_B + "&date=" + date
					+ "&platform=android&application=" + packageName;

			try {
				String response = ConnectionMgr.INSTANCE.getJSON(url);
				RevenueLinkVO revenue = JSONUtils.readObject(response, RevenueLinkVO.class);
				revenue.setPackageName(packageName);
				revenues.add(revenue);
			} catch (Exception e) {
				logger.log(Level.WARNING ,"json url read result:"+e.getMessage());

			}
		}
		for (String packageName : packageNamesC) {
			String url = "https://r.applovin.com/max/userAdRevenueReport?api_key=" + API_KEY_C + "&date=" + date
					+ "&platform=android&application=" + packageName;

			try {
				String response = ConnectionMgr.INSTANCE.getJSON(url);
				RevenueLinkVO revenue = JSONUtils.readObject(response, RevenueLinkVO.class);
				revenue.setPackageName(packageName);
				revenues.add(revenue);
			} catch (Exception e) {
				logger.log(Level.WARNING ,"json url read result:"+e.getMessage());

			}
		}		
		return revenues;

	}
	
	
	private RevenueLinkVO getUserLevelRevenueLink(PackageURLGroup packageURLGroup,String date){
		try {
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

		try {
			// read csv
			String response = ConnectionMgr.INSTANCE.getCSV(revenue.getUrl());			
			return importService.importCSVText(response);

		} catch (Exception e) {
			logger.log(Level.SEVERE, "csv file read error", e);
		}

		return Collections.EMPTY_LIST;
	}

	private boolean isUserRevPackageProcessed(String packageName,String date){
		UserRevPackage userRevPackage= revenueRepository.getUserRevPackage(packageName);
		if(userRevPackage==null){
			return false;
		}
		if(!date.equals(userRevPackage.getLastUsedDate())){
			return false;
		}		
		return true;
	  	
	}
	
	private void saveUserRevPackage(String packageName,String date){
		revenueRepository.save(new UserRevPackage(packageName, date));	
	}

}
