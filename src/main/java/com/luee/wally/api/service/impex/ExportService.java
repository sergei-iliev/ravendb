package com.luee.wally.api.service.impex;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.cloud.Timestamp;
import com.luee.wally.DB;
import com.luee.wally.admin.repository.CloudStorageRepository;
import com.luee.wally.admin.repository.PaidUsersRepository;
import com.luee.wally.api.service.AbstractService;
import com.luee.wally.api.service.InvoiceService;
import com.luee.wally.command.PdfAttachment;
import com.luee.wally.csv.PaidUsers2018;
import com.luee.wally.entity.PaidUser;
import com.luee.wally.entity.PaidUserExternal;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.utils.Utilities;



public class ExportService extends AbstractService{
	private final Logger logger = Logger.getLogger(ExportService.class.getName());
	
	private Collection<String> HEADER = Arrays.asList("date","internal user id","country code","full name","paid currency","paid amount","amount in eur","payment method","credit note id");
	
	
	//private final static String message = "Congratulations! This is your reward from PlaySpot :-)";
	//private final static String  from = "PlaySpot"; 
	
	public void createCSVFile(Writer writer, Collection<Pair<PaidUser,RedeemingRequests>> entities)
			throws IOException {
		GenerateCSV.INSTANCE.writeLine(writer, HEADER);
		Collection<String> line = new ArrayList<String>();
		for (Pair<PaidUser, RedeemingRequests> entity : entities) {
			// item
			line.add(entity.getLeft().getDate().toString());
			line.add(entity.getLeft().getUserGuid());
			line.add((String)entity.getRight().getCountryCode());
			line.add((String)entity.getRight().getFullName());
			if(entity.getLeft().getPaidCurrency().equals("EUR")){
				line.add("EUR");
				line.add(Utilities.formatPrice(entity.getLeft().getEurCurrency()));				
			}else{				
				line.add(entity.getLeft().getPaidCurrency());
				line.add(entity.getLeft().getAmount());
			}			
			line.add(Utilities.formatPrice(entity.getLeft().getEurCurrency()));	
			line.add((String)entity.getLeft().getType());
			line.add((String)entity.getLeft().getInvoiceNumber());
			
			GenerateCSV.INSTANCE.writeLine(writer, line);
			line.clear();
		}
	}
	/*
	 * External payment
	 */
	public void _createCSVFile(Writer writer, Collection<PaidUserExternal> entities)
			throws IOException {
		GenerateCSV.INSTANCE.writeLine(writer, HEADER);
		Collection<String> line = new ArrayList<String>();
		for (PaidUserExternal entity : entities) {
			// item
			line.add(entity.getDate().toString());
			line.add(entity.getRedeemingRequestId());
			line.add(entity.getCountryCode());
			line.add(entity.getFullName());
			if(entity.getPaidCurrency().equals("EUR")){
				line.add("EUR");
				line.add(Utilities.formatPrice(entity.getEurCurrency()));				
			}else{				
				line.add(entity.getPaidCurrency());
				line.add(entity.getAmount());
			}			
			line.add(Utilities.formatPrice(entity.getEurCurrency()));	
			line.add(entity.getType());
			line.add(entity.getInvoiceNumber());
			
			GenerateCSV.INSTANCE.writeLine(writer, line);
			line.clear();
		}
	}
	
	/*
	 * User payment
	 */
	public void createPDFInCloudStore(RedeemingRequests redeemingRequest,PaidUser paidUser,String folder,String subfolder,String fileName,String invoiceNumber)throws Exception{
		PdfAttachment attachment=new PdfAttachment();               
        attachment.setFileName(folder+"/"+subfolder+"/"+fileName+".pdf");
        attachment.setContentType("application/pdf");       
        InvoiceService invoiceService = new InvoiceService();
        attachment.readFromStream(invoiceService.createInvoice(redeemingRequest,paidUser,invoiceNumber)); 
        
	    CloudStorageRepository cloudStorageRepository=new CloudStorageRepository();
	    cloudStorageRepository.saveFile(attachment);               
	}
	/*
	 * External user payment
	 */
	public void createPDFInCloudStore(PaidUserExternal paidUser,String folder,String subfolder,String fileName,String invoiceNumber)throws Exception{
		PdfAttachment attachment=new PdfAttachment();                
        attachment.setFileName(folder+"/"+subfolder+"/"+fileName+".pdf");
        attachment.setContentType("application/pdf");       
        InvoiceService invoiceService = new InvoiceService();
        attachment.readFromStream(invoiceService.createInvoice(paidUser,invoiceNumber)); 
        
	    CloudStorageRepository cloudStorageRepository=new CloudStorageRepository();
	    cloudStorageRepository.saveFile(attachment);               
	}
	public Collection<PaidUserExternal> findPaidUsersExternalByDate(Date startDate,Date endDate){
		PaidUsersRepository paidUsersRepository=new PaidUsersRepository();		
		Collection<Entity> result=new ArrayList<>();
		
		String cursor=null;
	    do{		       
		       //read batch from paid users
		       cursor= paidUsersRepository.findPaidUsersExternal(cursor,result, startDate, endDate);
		       if(cursor==null){
		         break;
		       }		       		       	       
		    }while(cursor!=null);
	    Collection<PaidUserExternal> paidUsers=result.stream().map(PaidUserExternal::valueOf).collect(Collectors.toList());
		return paidUsers;
	}
	
	public Collection<PaidUser> findPaidUsersByDate(Date startDate,Date endDate){
		PaidUsersRepository paidUsersRepository=new PaidUsersRepository();		
		Collection<Entity> result=new ArrayList<>();
		
		String cursor=null;
	    do{		       
		       //read batch from paid users
		       cursor= paidUsersRepository.findPaidUsers(cursor,result,null, startDate, endDate);
		       if(cursor==null){
		         break;
		       }		       		       	       
		    }while(cursor!=null);
	    Collection<PaidUser> paidUsers=result.stream().map(PaidUser::valueOf).collect(Collectors.toList());
		return paidUsers;
	}
	
//	public Collection<Entity> find(boolean paid,String countryCode,String packageName,boolean confirmedEmail,String type,String amount,Date date){
//		return DB.getAmazonUsers( paid, countryCode, packageName, confirmedEmail, type, amount, date);
//	}
//	
//	
//	public void convertToCSV(Writer write,Collection<Entity> list)throws IOException{
//		Collection<String> line=new ArrayList<String>();
//		
//		line.add("Amount ($) *");
//		line.add("Name of Recipient  (50 character limit)");
//		line.add("Email Address *");
//		line.add("Message (1500 character limit)");
//		line.add("From *  (50 character limit)");
//		line.add("user_guid");
//		line.add("country_code");
//		GenerateCSV.INSTANCE.writeLine(write, line);
//		line.clear();
//
//
//
//		for(Entity item:list){		    
//			//item
//			line.add((String) item.getProperty("amount"));
//			
//			String s = ((String) item.getProperty("full_name")).replaceAll(",", " ");
//			
//			String[] values = s.split(",");
//
//			line.add(values[0]);
//			line.add((String) item.getProperty("email"));
//			line.add(message);
//			line.add(from);
//			line.add((String) item.getProperty("user_guid"));
//			line.add((String) item.getProperty("country_code"));
//			GenerateCSV.INSTANCE.writeLine(write, line);
//			line.clear();
//		}
//	}
//
//	public void convertToCSVGlobal(Writer write,Collection<Entity> list)throws IOException{
//		Collection<String> line=new ArrayList<String>();
//		
//		line.add("Amount");
//		line.add("Name of Recipient  (50 character limit)");
//		line.add("Email Address *");
//		line.add("user_guid");
//		line.add("country_code");
//		GenerateCSV.INSTANCE.writeLine(write, line);
//		line.clear();
//
//
//
//		for(Entity item:list){		    
//			//item
//			line.add((String) item.getProperty("amount"));
//			
//			String s = ((String) item.getProperty("full_name")).replaceAll(",", " ");
//			
//			String[] values = s.split(",");
//
//			line.add(values[0]);
//			line.add((String) item.getProperty("email"));
//			line.add((String) item.getProperty("user_guid"));
//			line.add((String) item.getProperty("country_code"));
//			GenerateCSV.INSTANCE.writeLine(write, line);
//			line.clear();
//		}
//	}

}
