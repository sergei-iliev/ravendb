package com.luee.wally.api.service.impex;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
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
import com.luee.wally.entity.Payable;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.json.JustPlayAmountVO;
import com.luee.wally.utils.Utilities;



public class ExportService extends AbstractService{
	private final Logger logger = Logger.getLogger(ExportService.class.getName());
	
	private Collection<String> HEADER = Arrays.asList("date","CET date","internal user id","payment reference id","email","email encoded","country code","full name","paid currency","paid amount","amount in eur","payment method","credit note id");
	
	
	//private final static String message = "Congratulations! This is your reward from PlaySpot :-)";
	//private final static String  from = "PlaySpot"; 
	
	public void createCSVFile(Writer writer, Collection<Pair<PaidUser,RedeemingRequests>> entities)
			throws IOException {
		GenerateCSV.INSTANCE.writeLine(writer, HEADER);
		Collection<String> line = new ArrayList<String>();
		for (Pair<PaidUser, RedeemingRequests> entity : entities) {
			// item
			line.add(entity.getLeft().getDate().toString());
			line.add(Utilities.toCETZoneDateTime(entity.getLeft().getDate()).toString());
			line.add(entity.getLeft().getUserGuid());
			line.add(entity.getLeft().getPaymentReferenceId()!=null?entity.getLeft().getPaymentReferenceId():"");
			if(entity.getLeft().getType().equalsIgnoreCase("PayPal")){
			  	line.add(entity.getLeft().getPaypalAccount());
			  	line.add(Utilities.encodeEmail(entity.getLeft().getPaypalAccount()));
			}else{
				line.add(entity.getLeft().getEmail());
				line.add(Utilities.encodeEmail(entity.getLeft().getEmail()));
			}
			line.add(entity.getRight()!=null?entity.getRight().getCountryCode():"");
			line.add(entity.getRight()!=null?entity.getRight().getFullName():"");
			
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
			line.add(Utilities.toCETZoneDateTime(entity.getDate()).toString());
			line.add(entity.getRedeemingRequestId());
			line.add(entity.getPaymentReferenceId()!=null?entity.getPaymentReferenceId():"");
			if(entity.getType().equalsIgnoreCase("PayPal")){
			  	line.add(entity.getPaypalAccount());
			  	line.add(Utilities.encodeEmail(entity.getPaypalAccount()));
			}else{
				line.add(entity.getEmail());
				line.add(Utilities.encodeEmail(entity.getEmail()));
			}						
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
	 * Export summary currency total
	 */
	public void createPDFExportSummary(String folder,Date startDate,Date endDate,String creditNoteNumber,String subject,
			String minCreditNoteId,String maxCreditNoteId,Collection<? extends Payable> list)throws Exception{
	   ZonedDateTime start= Utilities.toCETZoneDateTime(startDate);
	   ZonedDateTime e= Utilities.toCETZoneDateTime(endDate);
	   ZonedDateTime end=e.minusDays(1);
	   DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	   
	   String reportDateRange=formatter.format(start)+" - "+formatter.format(end);
	   String creditNoteIdRange=minCreditNoteId+" - "+maxCreditNoteId;
	   
	   InvoiceService invoiceService=new InvoiceService();
	   //extract paypal
	   Collection<? extends Payable> paypalList=list.stream().filter(i->i.getType().equalsIgnoreCase("paypal")).collect(Collectors.toList());
	   Map<String,Pair<Integer,BigDecimal>> map=this.groupBy(paypalList);	   	   
	   InputStream in=invoiceService.createExportSummary(new Date(), creditNoteNumber, reportDateRange,"PayPal",subject,creditNoteIdRange,map);
	   
	   //create file name
	   String fileName=start.getYear()+"_"+start.getMonthValue() +"_"+start.getDayOfMonth()+"_"+end.getYear()+"_"+end.getMonthValue()+"_"+end.getDayOfMonth()+"_paypal";
	   this.savePDFInCloudStore(folder,null,fileName,in);
	   
	   //File targetFile = new File("D:\\demo.pdf");		
	   //FileUtils.copyInputStreamToFile(in, targetFile);
	   
	   //extract amazon
	   Collection<? extends Payable> amazonList=list.stream().filter(i->i.getType().equalsIgnoreCase("amazon")).collect(Collectors.toList());
	   map=this.groupBy(amazonList);	   	   
	   in=invoiceService.createExportSummary(new Date(), creditNoteNumber, reportDateRange,"TangoCard",subject,creditNoteIdRange,map);

	   //create file name
	   fileName=start.getYear()+"_"+start.getMonthValue()+"_"+start.getDayOfMonth()+"_"+end.getYear()+"_"+end.getMonthValue()+"_"+end.getDayOfMonth()+"_amazon";	   
	   this.savePDFInCloudStore(folder,null,fileName,in);
	   
	   //targetFile = new File("D:\\demo1.pdf");		
	   //FileUtils.copyInputStreamToFile(in, targetFile);

	}
	
	/*
	 * group by currency code
	 */	
	public Map<String,Pair<Integer,BigDecimal>> groupBy(Collection<? extends Payable> list)throws Exception{
		Map<String,Pair<Integer,BigDecimal>> result=new HashMap<String, Pair<Integer,BigDecimal>>();
		BigDecimal sum;
		int counter=0;
		for(Payable item:list){
			Pair<Integer,BigDecimal> value=result.get(item.getPaidCurrency());
			
			if(value==null){
				sum=new BigDecimal(0);
				counter=0;				
			}else{
				sum=value.getValue();
				counter=value.getKey();
			}
			
			BigDecimal accumulator=sum.add(item.getCalculatedAmount());
			counter++;
			result.put(item.getPaidCurrency(),Pair.of(counter,accumulator));
		}	
		
		return result;
	}
	
	private void savePDFInCloudStore(String folder,String subfolder,String fileName,InputStream in)throws Exception{
		PdfAttachment attachment=new PdfAttachment();               
        
		attachment.setFileName(subfolder!=null?folder+"/"+subfolder+"/"+fileName+".pdf":folder+"/"+fileName+".pdf");
        attachment.setContentType("application/pdf");               
        attachment.readFromStream(in); 
        
        
	    CloudStorageRepository cloudStorageRepository=new CloudStorageRepository();
	    cloudStorageRepository.saveFile(attachment);               
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
	

}
