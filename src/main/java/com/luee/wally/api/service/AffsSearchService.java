package com.luee.wally.api.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
import com.luee.wally.admin.repository.AbstractRepository;
import com.luee.wally.admin.repository.AffsRepository;
import com.luee.wally.admin.repository.CloudStorageRepository;
import com.luee.wally.admin.repository.PaidUsersRepository;
import com.luee.wally.api.service.impex.GenerateCSV;
import com.luee.wally.command.AffsSearchForm;
import com.luee.wally.command.AffsSearchResult;
import com.luee.wally.command.Attachment;
import com.luee.wally.entity.Affs;
import com.luee.wally.entity.PaidUser;

public class AffsSearchService extends AbstractService{
	private final Logger logger = Logger.getLogger(AffsSearchService.class.getName());


	private Collection<String> searchHeader = Arrays.asList("experiment", "count", "sum_total_ad_rev", "avr_total_ad_rev",
			"sum_offerwall_rev", "avr_offerwall_rev","paid_users_total","avr_paid_users");

	private Collection<String> exportHeader = Arrays.asList("user_guid","date","experiment","offerwall_rev","total_ad_rev");
	
	private AffsRepository affsRepository=new AffsRepository();
	
	public void createExportFile(Writer writer, AffsSearchForm form, Collection<Affs> content)
			throws IOException {

		// set header
		writer.append(form.toString() + "\n");
		// field names
		convertHeaderToCSV(writer, exportHeader);
		// set content
		convertContentExportToCSV(writer, content);

	}
	

	private void convertHeaderToCSV(Writer writer, Collection<String> header) throws IOException {
		GenerateCSV.INSTANCE.writeLine(writer, header);
	}

	private void convertContentExportToCSV(Writer writer, Collection<Affs> list) throws IOException {
		Collection<String> line = new ArrayList<String>();

		for (Affs item : list) {
			// item
			line.add(item.getUserGuid());
			line.add(item.getDate().toString());
			line.add(item.getExperiment());
			line.add(String.valueOf(item.getOfferwallRev()));
			line.add(String.valueOf(item.getTotalAdRev()));
			GenerateCSV.INSTANCE.writeLine(writer, line);
			line.clear();
		}
	}
	public void createFile(Writer writer, AffsSearchForm form, Collection<AffsSearchResult> content)
			throws IOException {

		// set header
		writer.append(form.toString() + "\n");
		// field names
		convertHeaderToCSV(writer, searchHeader);
		// set content
		convertContentToCSV(writer, content);

	}
	private void convertContentToCSV(Writer writer, Collection<AffsSearchResult> list) throws IOException {
		Collection<String> line = new ArrayList<String>();

		for (AffsSearchResult item : list) {
			// item
			line.add(item.getExperiment());
			line.add(String.valueOf(item.getCount()));
			line.add(item.getTotalAdRev().setScale(4, BigDecimal.ROUND_HALF_EVEN).toString());
			line.add(item.getAvrTotalAdRev().setScale(4, BigDecimal.ROUND_HALF_EVEN).toString());

			line.add(item.getOfferwallRev().setScale(4, BigDecimal.ROUND_HALF_EVEN).toString());
			line.add(item.getAvrOfferwallRev().setScale(4, BigDecimal.ROUND_HALF_EVEN).toString());
			
			line.add(item.getTotalPaidUsersUSD().setScale(4, BigDecimal.ROUND_HALF_EVEN).toString());
			line.add(item.getAvrTotalPaidUsersUSD().setScale(4, BigDecimal.ROUND_HALF_EVEN).toString());
			
			GenerateCSV.INSTANCE.writeLine(writer, line);
			line.clear();
		}
	}
    /*
     * Process and export gaid search in background
     */
	public void exportGaid(AffsSearchForm affsSearchForm,String subfolder)throws IOException{
		Cursor cursor = null;
		int counter=0;
		int suffix=0;
		CloudStorageRepository cloudStorageRepository=new CloudStorageRepository();
		byte[] end="\r\n".getBytes();

		//create header to each file
		StringBuilder header=new StringBuilder();		
		header.append("startDate:"+(affsSearchForm.getStartDate()==null?"":affsSearchForm.getStartDate())+"\r\n");
		header.append("endDate:"+(affsSearchForm.getEndDate()==null?"":affsSearchForm.getEndDate())+"\r\n");
		header.append("country:"+(affsSearchForm.getCountryCode()==null?"":affsSearchForm.getCountryCode())+"\r\n");
		header.append("packageName:"+(affsSearchForm.getPackageName()==null?"":affsSearchForm.getPackageName())+"\r\n");
		
		ByteArrayOutputStream os=new ByteArrayOutputStream();
		os.write(header.toString().getBytes());	//first file has the filter
		
		Attachment attachment=new Attachment();
		attachment.setContentType("text/plain");
		attachment.setFileName("ExportGAID/"+subfolder+"/ExportGaidList_"+(++suffix)+".txt");
		attachment.setBuffer(os.toByteArray());
		
		cloudStorageRepository.saveFile(attachment);
		os.reset();
		

		
		DatastoreService ds = createDatastoreService();
		Query query = affsRepository.createQuery(affsSearchForm.getStartDate(), affsSearchForm.getEndDate(),affsSearchForm.getCountryCode(),null, affsSearchForm.getPackageName());
		PreparedQuery preparedQuery = ds.prepare(query);

		QueryResultList<Entity> results;

		do {
			FetchOptions fetchOptions;
			if (cursor != null) {
				fetchOptions = FetchOptions.Builder.withLimit(AbstractRepository.CURSOR_SIZE).startCursor(cursor);
			} else {
				fetchOptions = FetchOptions.Builder.withLimit(AbstractRepository.CURSOR_SIZE);
			}

			results = preparedQuery.asQueryResultList(fetchOptions);

			for (Entity e : results) {				
		    	 if(e.getProperty("gaid")!=null) {
			    	   os.write(((String)e.getProperty("gaid")).getBytes());	 
			           os.write(end);
			           
			           counter++;
		    	 }
		    	 
			}			
			
		    if(counter>100000){	
			    	//save in  				
					attachment=new Attachment();
					attachment.setContentType("text/plain");
					attachment.setFileName("ExportGAID/"+subfolder+"/ExportGaidList_"+(++suffix)+".txt");
					attachment.setBuffer(os.toByteArray());
					cloudStorageRepository.saveFile(attachment);
					
					os.reset();
					counter=0;
			}
		     
			cursor = results.getCursor();
		} while (results.size() > 0);

		//remaining list		 
		 attachment=new Attachment();
		 attachment.setContentType("text/plain");
		 attachment.setFileName("ExportGAID/"+subfolder+"/ExportGaidList_"+(++suffix)+".txt");
		 attachment.setBuffer(os.toByteArray());
		 

		 cloudStorageRepository.saveFile(attachment);
		
	}
	
    /*
     * Filter out Affs export
     */
	public Collection<Affs> processAffsExport(AffsSearchForm affsSearchForm) {
		Collection<Affs> affsExportResults = new ArrayList<>();

		if (affsSearchForm.getExperiments().size() > 0) {

			for (String experiment : affsSearchForm.getExperiments()) {
				Collection<Affs> result = processAffsExport(affsSearchForm.getStartDate(), affsSearchForm.getEndDate(),
						affsSearchForm.getCountryCode(), experiment, affsSearchForm.getPackageName());
				affsExportResults.addAll(result);
			}

		} else {
			String experiment = affsSearchForm.getExperiments().isEmpty() ? null
					: affsSearchForm.getExperiments().iterator().next();

			Collection<Affs> result = processAffsExport(affsSearchForm.getStartDate(), affsSearchForm.getEndDate(),
					affsSearchForm.getCountryCode(), experiment, affsSearchForm.getPackageName());
			affsExportResults.addAll(result);

		}
		return affsExportResults;
	}
	/*
	 * Affs export
	 */
	private Collection<Affs> processAffsExport(Date startDate, Date endDate, String country, String experiment,
			String packageName) {
		Collection<Affs> result = new ArrayList<>();
		
		DatastoreService ds = createDatastoreService();

		Query query = affsRepository.createQuery(startDate, endDate, country, experiment, packageName);

		PreparedQuery preparedQuery = ds.prepare(query);

		QueryResultList<Entity> results;

		Cursor cursor = null;

		do {
			FetchOptions fetchOptions;
			if (cursor != null) {
				fetchOptions = FetchOptions.Builder.withLimit(AbstractRepository.CURSOR_SIZE).startCursor(cursor);
			} else {
				fetchOptions = FetchOptions.Builder.withLimit(AbstractRepository.CURSOR_SIZE);
			}

			results = preparedQuery.asQueryResultList(fetchOptions);

			for (Entity e : results) {
				Affs affs=new Affs();
				affs.setDate((Date) e.getProperty("date"));
				affs.setTotalAdRev(e.getProperty("total_ad_rev") == null ? 0 : (double) e.getProperty("total_ad_rev"));
				affs.setExperiment((String) e.getProperty("experiment"));
				affs.setOfferwallRev(e.getProperty("offerwall_rev") == null ? 0 : (double) e.getProperty("offerwall_rev"));
				affs.setUserGuid((String)e.getProperty("user_guid"));
			    result.add(affs);				
			}			
			
			cursor = results.getCursor();
		} while (results.size() > 0);

		return result;

	}	
	
	public Collection<AffsSearchResult> processAffsSearch(AffsSearchForm affsSearchForm) {

		Collection<AffsSearchResult> affsSearchResults = new ArrayList<>();

		if (affsSearchForm.getExperiments().size() > 0) {

			for (String experiment : affsSearchForm.getExperiments()) {
				AffsSearchResult result = processAffsSearch(affsSearchForm.getStartDate(), affsSearchForm.getEndDate(),
						affsSearchForm.getCountryCode(), experiment, affsSearchForm.getPackageName());
				affsSearchResults.add(result);
			}

		} else {
			String experiment = affsSearchForm.getExperiments().isEmpty() ? null
					: affsSearchForm.getExperiments().iterator().next();

			AffsSearchResult result = processAffsSearch(affsSearchForm.getStartDate(), affsSearchForm.getEndDate(),
					affsSearchForm.getCountryCode(), experiment, affsSearchForm.getPackageName());
			affsSearchResults.add(result);

		}
		return affsSearchResults;
	}

	public AffsSearchResult processAffsSearch(Date startDate, Date endDate, String country, String experiment,
			String packageName) {
		PaidUsersRepository paidUsersRepository=new PaidUsersRepository();
		
		DatastoreService ds = createDatastoreService();

		Query query = affsRepository.createQuery(startDate, endDate, country, experiment, packageName);

		PreparedQuery preparedQuery = ds.prepare(query);

		QueryResultList<Entity> results;

		Cursor cursor = null;
		BigDecimal totalAdRev = BigDecimal.ZERO;
		BigDecimal offerwallRev = BigDecimal.ZERO;
		BigDecimal totalPaidUsers = BigDecimal.ZERO;
		
		Collection<String> userGuids=new HashSet<>();
		int count = 0;
		do {
			FetchOptions fetchOptions;
			if (cursor != null) {
				fetchOptions = FetchOptions.Builder.withLimit(AbstractRepository.CURSOR_SIZE).startCursor(cursor);
			} else {
				fetchOptions = FetchOptions.Builder.withLimit(AbstractRepository.CURSOR_SIZE);
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
				
				userGuids.add((String)e.getProperty("user_guid"));
			}
			
			//read paid_users record by record
			for(String userGuid:userGuids){
				Collection<PaidUser> paidUsers=paidUsersRepository.findPaidUsersByGuid(userGuid);
				BigDecimal sum=paidUsers.stream().map(u->u.getEurCurrency()).reduce(BigDecimal.ZERO, BigDecimal::add);
				totalPaidUsers = totalPaidUsers.add(sum);
			}
			
			count += results.size();
			cursor = results.getCursor();
		} while (results.size() > 0);

		return new AffsSearchResult(experiment, totalAdRev, offerwallRev,totalPaidUsers,count);

	}


	/*
	 * Eventual consistency
	 */

		public AffsSearchResult processAffsSearch(Double minRevThreshold,Collection<String> gaids,Date startDate,Date endDate) {
			PaidUsersRepository paidUsersRepository=new PaidUsersRepository();
			DatastoreService ds = createDatastoreService();
	
			Collection<String> userGuids=new HashSet<>();
			BigDecimal totalAdRev = BigDecimal.ZERO;
			BigDecimal offerwallRev = BigDecimal.ZERO;
			BigDecimal totalPaidUsers = BigDecimal.ZERO;
			BigDecimal minRevThres=BigDecimal.valueOf(minRevThreshold==null?0.0:minRevThreshold);
			int count = 0;
			int minRevCount=0;
			// loop for each guid
			for (String gaid : gaids) {			
				Query query=affsRepository.createQuery(startDate, endDate, gaid);
	
				PreparedQuery preparedQuery = ds.prepare(query);
				QueryResultList<Entity> results = preparedQuery.asQueryResultList(FetchOptions.Builder.withDefaults());
				
				userGuids.clear();
				for (Entity e : results) {
					BigDecimal _totalAdRev = BigDecimal
							.valueOf(e.getProperty("total_ad_rev") == null ? 0 : (double) e.getProperty("total_ad_rev"));
					totalAdRev = totalAdRev.add(_totalAdRev);
	 
					BigDecimal _offerwallRev = BigDecimal
							.valueOf(e.getProperty("offerwall_rev") == null ? 0 : (double) e.getProperty("offerwall_rev"));
					offerwallRev = offerwallRev.add(_offerwallRev);	
					
					userGuids.add((String)e.getProperty("user_guid"));
					
					if(minRevThreshold!=null&&(minRevThreshold!=0)){
						  BigDecimal sum=_totalAdRev.add(_offerwallRev);
						  if(sum.compareTo(minRevThres)==0||sum.compareTo(minRevThres)==1){
						   	minRevCount++;
						  }
					}					
				}
				
				//read paid_users record by record
				for(String userGuid:userGuids){
					Collection<PaidUser> paidUsers=paidUsersRepository.findPaidUsersByGuid(userGuid);
					BigDecimal sum=paidUsers.stream().map(u->u.getEurCurrency()).reduce(BigDecimal.ZERO, BigDecimal::add);
					totalPaidUsers = totalPaidUsers.add(sum);
				}
				
				count+=results.size();
			}
		return new AffsSearchResult(null, totalAdRev, offerwallRev,totalPaidUsers,count,minRevCount);
	}
	
	public void removeExperiment(String userGuid,String experiment){
		Collection<Entity> affs=affsRepository.findAffsByUserGuids(Arrays.asList(userGuid));
		if(affs.size()!=1){
			throw new IllegalStateException( "Found "+affs.size()+" record number(s).");
		}
		
		Entity entity=affs.iterator().next();		
		String oldValue=(String) entity.getProperty("experiment");		
		List<String> experiments=new ArrayList<>(Arrays.asList(oldValue.split("\\|")));
		
		if(!experiments.remove(experiment)){
			throw new IllegalStateException( "Experiment '"+experiment+"' is not found in record value: '"+oldValue+"'");	
		}
		
		String newValue=experiments.stream().collect(Collectors.joining("|"));
		entity.setProperty("experiment",newValue);
		affsRepository.save(entity);
	}
	
	public void setExperiment(String userGuid,String experiment){
		Collection<Entity> affs=affsRepository.findAffsByUserGuids(Arrays.asList(userGuid));
		if(affs.size()!=1){
			throw new IllegalStateException( "Found "+affs.size()+" record number(s).");
		}
		
		Entity entity=affs.iterator().next();		
		String oldValue=(String) entity.getProperty("experiment");		
		List<String> experiments=new ArrayList<>(Arrays.asList(oldValue.split("\\|")));
		
		if(experiments.contains(experiment)){
			throw new IllegalStateException( "Experiment '"+experiment+"' is already present in record value");	
		}
		experiments.add(experiment);
		String newValue=experiments.stream().collect(Collectors.joining("|"));
		entity.setProperty("experiment",newValue);
		affsRepository.save(entity);
	}
	
}
