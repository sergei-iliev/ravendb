package com.luee.wally.api.service;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.luee.wally.admin.repository.AffsRepository;
import com.luee.wally.admin.repository.FBUserRevenueRepository;
import com.luee.wally.admin.repository.PaidUsersRepository;
import com.luee.wally.api.ConnectionMgr;
import com.luee.wally.api.service.impex.GenerateCSV;
import com.luee.wally.command.AffsSearchForm;
import com.luee.wally.command.AffsSearchResult;
import com.luee.wally.command.FBAffsSearchResult;
import com.luee.wally.constants.Constants;
import com.luee.wally.constants.FBAirConstants;
import com.luee.wally.entity.Affs;
import com.luee.wally.entity.PaidUser;
import com.luee.wally.json.JSONUtils;

public class FBAffsSearchService extends AbstractService {
	private final Logger logger = Logger.getLogger(FBAffsSearchService.class.getName());

	private Collection<String> searchHeader = Arrays.asList("experiment", "count", "sum_total_ad_rev", "avr_total_ad_rev",
			"sum_offerwall_rev", "avr_offerwall_rev","paid_users_total","avr_paid_users", "total_fb_rev" , "avg_fb_rev");
	
	private static final int FB_AIR_LOWER_LIMIT = 500;
	private static final int FB_AIR_UPPER_LIMIT = 2000;

	private static final int FB_AIR_BATCH_LIMIT = 1000;
	
	private static final int FB_AIR_WAIT_LOOP = 20;

	private static final int CURSOR_SIZE = 1000;

	private static AtomicInteger counter = new AtomicInteger(1);

	private PaidUsersRepository paidUsersRepository = new PaidUsersRepository();

	private AffsRepository affsRepository = new AffsRepository();

	private FBUserRevenueRepository fbUserRevenueRepository = new FBUserRevenueRepository();

	
	public void createFile(Writer writer, AffsSearchForm form, Collection<FBAffsSearchResult> content)
			throws IOException {

		// set header
		writer.append(form.toString() + "\n");
		// field names
		convertHeaderToCSV(writer, searchHeader);
		// set content
		convertContentToCSV(writer, content);

	}
	private void convertHeaderToCSV(Writer writer, Collection<String> header) throws IOException {
		GenerateCSV.INSTANCE.writeLine(writer, header);
	}
	
	private void convertContentToCSV(Writer writer, Collection<FBAffsSearchResult> list) throws IOException {
		Collection<String> line = new ArrayList<String>();

		for (FBAffsSearchResult item : list) {
			// item
			line.add(item.getExperiment());
			line.add(String.valueOf(item.getCount()));
			line.add(item.getTotalAdRev().setScale(4, BigDecimal.ROUND_HALF_EVEN).toString());
			line.add(item.getAvrTotalAdRev().setScale(4, BigDecimal.ROUND_HALF_EVEN).toString());

			line.add(item.getOfferwallRev().setScale(4, BigDecimal.ROUND_HALF_EVEN).toString());
			line.add(item.getAvrOfferwallRev().setScale(4, BigDecimal.ROUND_HALF_EVEN).toString());
			
			line.add(item.getTotalPaidUsersUSD().setScale(4, BigDecimal.ROUND_HALF_EVEN).toString());
			line.add(item.getAvrTotalPaidUsersUSD().setScale(4, BigDecimal.ROUND_HALF_EVEN).toString());
			//Facebook
			line.add(item.getTotalFBRev().setScale(4, BigDecimal.ROUND_HALF_EVEN).toString());
			line.add(item.getAvrTotalFBRev().setScale(4, BigDecimal.ROUND_HALF_EVEN).toString());

			GenerateCSV.INSTANCE.writeLine(writer, line);
			line.clear();
		}
	}	
	
	public Collection<Pair<Collection<Key>, FBAffsSearchResult>> processAffsSearch(AffsSearchForm affsSearchForm) {

		Collection<Pair<Collection<Key>, FBAffsSearchResult>> affsSearchResults = new ArrayList<>();

		if (affsSearchForm.getExperiments().size() > 0) {

			for (String experiment : affsSearchForm.getExperiments()) {
				Pair<Collection<Key>, FBAffsSearchResult> result = processAffsSearch(affsSearchForm.getStartDate(),
						affsSearchForm.getEndDate(), affsSearchForm.getCountryCode(), experiment,
						affsSearchForm.getPackageName());
				affsSearchResults.add(result);
			}

		} else {
			String experiment = affsSearchForm.getExperiments().isEmpty() ? null
					: affsSearchForm.getExperiments().iterator().next();

			Pair<Collection<Key>, FBAffsSearchResult> result = processAffsSearch(affsSearchForm.getStartDate(),
					affsSearchForm.getEndDate(), affsSearchForm.getCountryCode(), experiment,
					affsSearchForm.getPackageName());
			affsSearchResults.add(result);

		}
		return affsSearchResults;
	}

	private Pair<Collection<Key>, FBAffsSearchResult> processAffsSearch(Date startDate, Date endDate, String country,
			String experiment, String packageName) {

		DatastoreService ds = createDatastoreService();

		Query query = affsRepository.createQuery(startDate, endDate, country, experiment, packageName);

		PreparedQuery preparedQuery = ds.prepare(query);

		QueryResultList<Entity> results;

		Cursor cursor = null;
		BigDecimal totalAdRev = BigDecimal.ZERO;
		BigDecimal offerwallRev = BigDecimal.ZERO;
		BigDecimal totalPaidUsers = BigDecimal.ZERO;

		Collection<String> userGuids = new HashSet<>();
		Collection<Key> affsKeys = new HashSet<>();
		int count = 0;
		do {
			FetchOptions fetchOptions;
			if (cursor != null) {
				fetchOptions = FetchOptions.Builder.withLimit(CURSOR_SIZE).startCursor(cursor);
			} else {
				fetchOptions = FetchOptions.Builder.withLimit(CURSOR_SIZE);
			}

			results = preparedQuery.asQueryResultList(fetchOptions);
			userGuids.clear();
			for (Entity e : results) {
				BigDecimal _totalAdRev = BigDecimal
						.valueOf(e.getProperty("total_ad_rev") == null ? 0 : (double) e.getProperty("total_ad_rev"));
				totalAdRev = totalAdRev.add(_totalAdRev);

				BigDecimal _offerwallRev = BigDecimal
						.valueOf(e.getProperty("offerwall_rev") == null ? 0 : (double) e.getProperty("offerwall_rev"));
				offerwallRev = offerwallRev.add(_offerwallRev);

				userGuids.add((String) e.getProperty("user_guid"));
				// collect keys for later use
				affsKeys.add(e.getKey());
			}

			// read paid_users record by record
			for (String userGuid : userGuids) {
				Collection<PaidUser> paidUsers = paidUsersRepository.findPaidUsersByGuid(userGuid);
				BigDecimal sum = paidUsers.stream().map(u -> u.getEurCurrency()).reduce(BigDecimal.ZERO,
						BigDecimal::add);
				totalPaidUsers = totalPaidUsers.add(sum);
			}

			count += results.size();
			cursor = results.getCursor();
		} while (results.size() > 0);

		return new ImmutablePair<>(affsKeys,
				new FBAffsSearchResult(experiment, totalAdRev, offerwallRev, totalPaidUsers, count));

	}

	/*
	 * affs keys have corresponding user_daily_revenue_fb records with encoded
	 * ecm embeded entities 
	 * returns : date grouping for list of ecpms per package (FB Air requires such grouping)
	 */
	public Map<String, Map<String, List<String>>> getECPMs(Collection<Key> affsKeys) {
		Map<String, Map<String, List<String>>> result = new HashMap<>();

		for (Key key : affsKeys) {
			Collection<Entity> entities = fbUserRevenueRepository.getUserDailyRevenueByAffsKey(key);
			if (entities.isEmpty()) {
				continue;
			}
			for (Entity entity : entities) {
				String date = (String) entity.getProperty("rev_check_date");
				Map<String, Object> map = (Map<String, Object>) ((EmbeddedEntity) entity.getProperty("app_cpm"))
						.getProperties();
				// for this date find package to list mappings
				Map<String, List<String>> packageToEcpms = result.get(date);
				if (packageToEcpms == null) {
					packageToEcpms = new HashMap<>();
					result.put(date, packageToEcpms);
				}
				// add db package to list mapping to the result map
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					String packageId = entry.getKey();
					List<String> ecpm = packageToEcpms.get(packageId); // is key
																		// already
																		// in
																		// memory?
					if (ecpm == null) {
						ecpm = new ArrayList<>();
						packageToEcpms.put(packageId, ecpm);
					}
					ecpm.addAll((List<String>) entry.getValue()); // accumulate
																	// ecpms to
																	// the same
																	// packageId
					packageId = null;
				}
			}
		}
		return result;
	}

	/*
	 * Prepare query data for FB Air pass by date and group by package id
	 */
	public BigDecimal calculateFBUserRevenue(Map<String, Map<String, List<String>>> ecpms) throws IOException {
		// for each package (and its list of ecpms), start FB Air each package
		// name has a correspondent app_id and token in facebook db
		Map<String, Entity> packageNameMap = fbUserRevenueRepository.getFacebookPackageNameTokenMap();
        BigDecimal amount=BigDecimal.ZERO; 
		for (Map.Entry<String, Map<String, List<String>>> dateToPackageEcpms : ecpms.entrySet()) { // by
																						// date
																						// group
			for (Map.Entry<String, List<String>> packageToEcpms : dateToPackageEcpms.getValue().entrySet()) { // by
																									// packageName
																									// group				
				Entity packageToken = packageNameMap.get(packageToEcpms.getKey());
				if (packageToken == null) {
					//throw new IOException("Unable to find facebook token for package: " + packageToEcpms.getKey());
					logger.warning("Unable to find facebook token for package: " + packageToEcpms.getKey());
					continue;
				}
				String appId = (String) packageToken.getProperty("facebook_app_id");
				String token = (String) packageToken.getProperty("token");
				
				logger.warning(String.format("Current input: date: %s ,package: %s ,appId: %s , size: %d",dateToPackageEcpms.getKey(),packageToEcpms.getKey(),appId,packageToEcpms.getValue().size()));
                //for ech separate date and package ids in it
				BigDecimal subamount=this.calculateFBUserRevenue(packageToEcpms.getKey(),dateToPackageEcpms.getKey(),appId, token, packageToEcpms.getValue()); 
				amount=amount.add(subamount);
			}

		}
		
		return amount;
	}

	/*
	 * run FB AIR keeping a rule icpms count<500 and count <2000
	 */
	public BigDecimal calculateFBUserRevenue(String packageName,String date,String appId, String token, List<String> ecpms) throws IOException {

		int size = ecpms.size();
		if(size<=FB_AIR_LOWER_LIMIT){
			logger.severe("Lower limit is "+FB_AIR_LOWER_LIMIT+" , provided size is "+size+ " for packageName: "+packageName+" and date: "+date);
			return BigDecimal.ZERO;
		}
        BigDecimal amount=BigDecimal.ZERO; 
		if (size <= FB_AIR_UPPER_LIMIT) { // 1 butch of icpms -> one shot
		   try{	
			  return processFBUserRevenue(appId, token, ecpms);
		   }catch(IOException e){
			   logger.log(Level.SEVERE,"FB AIR error for packageName: "+packageName+" and date: "+date,e);
			   return BigDecimal.ZERO;
		   }
		} else { // split into chunks since more then 2000 ecpms available
			int listIndex = 0;
			Collection<String> batchOfEcpms = new ArrayList<>();
			while (listIndex < size) {
				batchOfEcpms.clear();
				if ((size - listIndex) > FB_AIR_UPPER_LIMIT) { // next chunk
					for (int i = 0; i < FB_AIR_BATCH_LIMIT; i++) {
						batchOfEcpms.add(ecpms.get(listIndex));
						listIndex++;
					}
					// fire FB processing result for the chunk
					try{
						BigDecimal subamount=this.processFBUserRevenue(appId, token, batchOfEcpms);
						amount=amount.add(subamount);   
					}catch(IOException e){
					   logger.log(Level.SEVERE,"FB AIR error for packageName: "+packageName+" and date: "+date,e);					
					}					
				} else { // last chunk
					while (listIndex < size) {
						batchOfEcpms.add(ecpms.get(listIndex));
						listIndex++;
					}
					// fire FB processing result for last chunk
					try{
						BigDecimal subamount=this.processFBUserRevenue(appId, token, batchOfEcpms);
						amount=amount.add(subamount);
					}catch(IOException e){
						logger.log(Level.SEVERE,"FB AIR error for packageName: "+packageName+" and date: "+date,e);	
					}
				}

			}

		}
		return amount;

	}

	/*
	 * Process the batch of ecpms in regrad to Facebook response
	 * {both_ecpms_and_queries_passed_in_request,
	 * neither_ecpms_nor_queries_passed_in_request, forbidden,
	 * no_valid_query_ids_passed_in_request, too_many_query_ids, submitted,
	 * processing, internal_error, too_few_ecpms }
	 */
	private BigDecimal processFBUserRevenue(String appId, String token, Collection<String> batchOfEcpms)
			throws IOException {
		String queryId=fetchQueryIdFromFBAir(appId,token,batchOfEcpms);
		String result=null;
		for (int i = 0; i < FB_AIR_WAIT_LOOP; i++) {			
			result=fetchResultFromFBAirByQueryId(appId,token,queryId);
			
			switch (result) {
			case FBAirConstants.both_ecpms_and_queries_passed_in_request:
				throw new IOException("Response from server: " + FBAirConstants.both_ecpms_and_queries_passed_in_request
						+ " appId: " + appId + " queryId: " + queryId);
			case FBAirConstants.neither_ecpms_nor_queries_passed_in_request:
				throw new IOException(
						"Response from server: " + FBAirConstants.neither_ecpms_nor_queries_passed_in_request
								+ " appId: " + appId + " queryId: " + queryId);
			case FBAirConstants.forbidden:
				throw new IOException(
						"Response from server: " + FBAirConstants.forbidden + " appId: " + appId + " token: " + token);
			case FBAirConstants.no_valid_query_ids_passed_in_request:
				throw new IOException("Response from server: " + FBAirConstants.no_valid_query_ids_passed_in_request
						+ " appId: " + appId + " queryId: " + queryId);
			case FBAirConstants.too_many_query_ids:
				throw new IOException("Response from server: " + FBAirConstants.too_many_query_ids + " appId: " + appId
						+ " batch size: " + batchOfEcpms.size());
			case FBAirConstants.submitted:
				//WAIT
                try{Thread.currentThread().sleep(500);}catch(InterruptedException e){}
				break;
			case FBAirConstants.processing:
				//WAIT
				try{Thread.currentThread().sleep(500);}catch(InterruptedException e){}
				break;
			case FBAirConstants.internal_error:
				throw new IOException("Response from server: " + FBAirConstants.internal_error + " appId: " + appId
						+ " queryId : " + queryId);
			case FBAirConstants.too_few_ecpms:
				throw new IOException("Response from server: " + FBAirConstants.too_few_ecpms + " appId: " + appId
						+ " batch size: " + batchOfEcpms.size());

			default: // must be the money
			    
				return new BigDecimal(result);
			}
		}
		//timeout!
		throw new IOException("Timeout from FB server unexpected result: "+result);
		
	}

	/*
	 * Pass 1 to get query id
	 */
	private String fetchQueryIdFromFBAir(String appId, String token, Collection<String> icpms) throws IOException {
		String url = String.format("https://graph.facebook.com/%s/aggregate_revenue", appId);

		Map<String, Object> input = new HashMap<>();
		input.put("request_id", counter.incrementAndGet());
		input.put("ecpms", icpms);
		input.put("access_token", token);

		String content = JSONUtils.writeObject(input);

		String result = ConnectionMgr.INSTANCE.postJSON(url, content);
		Map<String, Object> map = JSONUtils.readObject(result, Map.class);
		String queryId = (String) map.get("query_id");
		if (queryId == null) {
			logger.severe("FB Air response is not recognized: " + result);
			throw new IOException("FB Air response " + result);
		}
		return queryId;

	}
	/*
	 * Pass 2 to get result by query Id
	 */

	public String fetchResultFromFBAirByQueryId(String appId, String token,String queryId) throws IOException {
		String url = String.format("https://graph.facebook.com/%s/aggregate_revenue", appId);

		Map<String, Object> input = new HashMap<>();
		input.put("query_ids", Arrays.asList(queryId));
		input.put("access_token",token);

		String content = JSONUtils.writeObject(input);

		String result = ConnectionMgr.INSTANCE.postJSON(url, content);
		Map<String, Object> map = JSONUtils.readObject(result, Map.class);
		Map<String, String> resultMap = (Map) map.get("query_ids");
		return resultMap.get(queryId);
	}

}
