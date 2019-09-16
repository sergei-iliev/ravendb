package com.luee.wally.api.service.impex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.luee.wally.command.Attachment;
import com.luee.wally.csv.PaidUsers2018;
import com.luee.wally.csv.UserLevelRevenue;
import com.luee.wally.paypal.InvoiceService;
import com.luee.wally.paypal.PdfAttachment;

public class ImportService {
	
	private static final String AD_UNIT_ID="Ad Unit ID";
	private static final String PLACEMENT="Placement";
	private static final String IDFA="IDFA";
	private static final String IDFV="IDFV";
	private static final String USER_ID="User ID";
	private static final String REVENUE="Revenue";
	private static final String IMPRESSIONS="Impressions";
	
	private final Logger logger = Logger.getLogger(ExportService.class.getName());
	
	public static final String IMPORT_CSV_FILE = "csv/paid_users_2018.csv";
	public static final String IMPORT_CSV_FILE_2019_eur_amount = "csv/paid_users_2019_eur_amount.csv";
	public static final String IMPORT_CSV_FILE_2019_currency_amount = "csv/paid_users_2019_currency_amount.csv";
	
	public static final String BUCKET_NAME="luee-wally-v2-cpc.appspot.com";
	
	
	
	public Collection<PaidUsers2018> importCSVFile()throws Exception{
		
		List<List<String>> list=readFile(IMPORT_CSV_FILE);		
		return convertToObject(list);
	}
	/*
	 * 2 variations are possible
	 * 1.currency in EUR
	 * 2.currency in anything else
	 */
	public Collection<PaidUsers2018> importCSVFile2019(String filePath,boolean isEURCurrency)throws Exception{
		
		List<List<String>> list=readFile(filePath);		
		if(isEURCurrency){
		   return convertToObject2019EUR(list);
		}else{
		   return convertToObject2019Currency(list);	
		}
	}
	
	public List<List<String>>  readFile(String fileName) throws Exception{
		  InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
		  //InputStream inputStream = new FileInputStream(new File(fileName));

		  try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
			 return GenerateCSV.INSTANCE.readLines(br);
		  } 
		  
	}
	
	public List<List<String>>  readCSVText(String text) throws Exception{		  		  
		  try (BufferedReader br = new BufferedReader(new StringReader(text))) {
			 return GenerateCSV.INSTANCE.readLines(br);
		  } 		  
	} 
	
	public Collection<UserLevelRevenue> importCSVText(String text)throws Exception{
		
		List<List<String>> list=readCSVText(text);		
		return convertToUserLevelRevenue(list);
	}
	
	/*
	 * First line carries the info about Column names
	 */
	public Collection<UserLevelRevenue> convertToUserLevelRevenue(List<List<String>> lines){
		Collection<UserLevelRevenue> result=new ArrayList<>();
		List<String> headers=new ArrayList<>();
		
		boolean isFirstLine=false;
		for(List<String> line:lines){
			if(!isFirstLine){
				for(String item:line){
				  headers.add(item);	
				}
				
				isFirstLine=true;
				continue;
			}
			UserLevelRevenue user=new UserLevelRevenue();
			user.setAdUnitID(line.get(headers.indexOf(AD_UNIT_ID)));
			user.setIDFA(line.get(headers.indexOf(IDFA)).trim());
			//user.setIDFV(line.get(headers.indexOf(IDFV)).trim());
			//user.setUserID(line.get(3));
			user.setRevenue(new BigDecimal(line.get(headers.indexOf(REVENUE)).trim()));
			//user.setImpressions(Integer.parseInt(line.get(5)));
			result.add(user);
		}
		
		return result;
	}	
	
	
	
	public void saveFile(Attachment attachment){
		Storage storage = StorageOptions.getDefaultInstance().getService();

		// Upload a blob to the newly created bucket
		BlobId blobId = BlobId.of(BUCKET_NAME,attachment.getFileName());
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(attachment.getContentType()).build();
		Blob blob = storage.create(blobInfo,attachment.getBuffer());		
	}
	
	public void createPDFInCloudStore(Entity redeemingRequest,PaidUsers2018 paidUsers2018,String invoiceNumber)throws Exception{
        PdfAttachment attachment=new PdfAttachment();
        attachment.setFileName("user_credit_notes_2018_with_id/PaidUsers2018_"+invoiceNumber+".pdf");
        attachment.setContentType("application/pdf");       
        InvoiceService invoiceService = new InvoiceService();
        attachment.readFromStream(invoiceService.createInvoice(redeemingRequest,paidUsers2018,invoiceNumber)); 
	
        this.saveFile(attachment);
        
	}
	
	private Collection<PaidUsers2018> convertToObject(List<List<String>> lines){
		Collection<PaidUsers2018> result=new ArrayList<>();
		for(List<String> line:lines){
			PaidUsers2018 user=new PaidUsers2018();
			user.setDate(line.get(0));
			user.setUserGuid(line.get(1));
			user.setCountryCode(line.get(2));
			user.setPayedAmount(line.get(3));
			user.setPaymentMethod(line.get(4));
			result.add(user);
		}
		return result;
	}
	private Collection<PaidUsers2018> convertToObject2019EUR(List<List<String>> lines){
		Collection<PaidUsers2018> result=new ArrayList<>();
		for(List<String> line:lines){			
			PaidUsers2018 user=new PaidUsers2018();
			user.setDate(line.get(0));
			user.setUserGuid(line.get(1));
			user.setCountryCode(line.get(2));
			user.setCurrencyCode("EUR");
			user.setPayedAmount(line.get(3));
			user.setPaymentMethod(line.get(6));
			result.add(user);			
		}
		return result;
	}
	private Collection<PaidUsers2018> convertToObject2019Currency(List<List<String>> lines){
		Collection<PaidUsers2018> result=new ArrayList<>();
		for(List<String> line:lines){
			PaidUsers2018 user=new PaidUsers2018();
			user.setDate(line.get(0));
			user.setUserGuid(line.get(1));
			user.setCurrencyCode(line.get(2));
			user.setPayedAmount(line.get(3));
			user.setPaymentMethod(line.get(4));
			result.add(user);
		}
		return result;
	}	
	
}
