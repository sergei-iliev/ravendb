package net.paypal.integrate.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.cloud.Timestamp;

import net.paypal.integrate.api.GenerateCSV;
import net.paypal.integrate.command.PdfAttachment;
import net.paypal.integrate.command.csv.PaidUsers2018;
import net.paypal.integrate.command.csv.UserLevelRevenue;
import net.paypal.integrate.command.invoice.Money;
import net.paypal.integrate.command.invoice.PayoutResult;
import net.paypal.integrate.entity.PayPalUser;
import net.paypal.integrate.entity.RedeemingRequests;
import net.paypal.integrate.repository.CloudStorageRepository;
import net.paypal.integrate.repository.ImpexRepository;

@Service
public class ImportService {
	private final Logger logger = Logger.getLogger(ImportService.class.getName());

	public static final String IMPORT_CSV_FILE = "/csv/paid_users_2018.csv";
	
	@Autowired
	private InvoiceService invoiceService;
	
	@Autowired
	private CloudStorageRepository cloudStorageRepository;
	
	@Autowired
	private ImpexRepository impexRepository;
	
	public Collection<RedeemingRequests> find(Collection<String> userGuids){
	   return impexRepository.find(userGuids);	
	}
	
	public void generateData(Collection<PaidUsers2018> csvData) throws ParseException{
		int i=0;
		for(PaidUsers2018 paidUsers2018:csvData){
			i++;
			RedeemingRequests r=new RedeemingRequests();
			r.setUserGuid(paidUsers2018.getUserGuid());
			r.setCreationDate(Timestamp.now());					
			//dummy future
			Date _date=new SimpleDateFormat("MM/dd/yyyy").parse("6/12/2019"); 			
			r.setDate( Timestamp.of(_date));	
			
			
			r.setPaid((i%2)==0);
			if(r.isPaid())
				r.setCountryCode("US");
			else
				r.setCountryCode("BG");	
			r.setEmail("br@home.bg");
			r.setFullAddress("Levski town, Baba Tonka 7");
			r.setFullName("Sergei Rachev Iliev");
			if(i%2==0){
				r.setAmount("20.23");				
			}else if(i%3==0){
				r.setAmount("25.23");
				
			}else{
				r.setAmount("36.23");							    
			}
			
						
			r.setType("Amazon");
			r.setConfirmedEmail(true);
			impexRepository.save(r);			
		}
	}
	public void createPDFInCloudStore(RedeemingRequests redeemingRequests,PaidUsers2018 paidUsers2018,String invoiceNumber)throws Exception{
        PdfAttachment attachment=new PdfAttachment();
        attachment.setFileName("PaidUsers2018_"+invoiceNumber+".pdf");
        attachment.setContentType("application/pdf");        
        attachment.readFromStream(invoiceService.createInvoice(redeemingRequests,paidUsers2018,invoiceNumber)); 
	
        cloudStorageRepository.saveFile(attachment);
        
	}
	
	public void createDemoPDFInCloudStore()throws Exception{
        PdfAttachment attachment=new PdfAttachment();
        attachment.setFileName("Demo_PDF.pdf");
        attachment.setContentType("application/pdf");
        
        PayoutResult payoutResult=new PayoutResult();
        payoutResult.setAmount(new Money("2","USD"));
        payoutResult.setFee(new Money("3", "USD"));
        payoutResult.setPayoutBatchId("DEMO11111");
        
        PayPalUser payPalUser=new PayPalUser();
        payPalUser.setAmount("2");
        payPalUser.setCountryCode("US");
        payPalUser.setCurrency("USD");
        payPalUser.setFullAddress("Brigo");
        payPalUser.setFullName("Megan Rodriges");
        
        
        attachment.readFromStream(invoiceService.createInvoice(payoutResult,payPalUser,"2017111111")); 
	
        cloudStorageRepository.saveFile(attachment);
        
	}
	
	public Collection<PaidUsers2018> importCSVFile()throws Exception{
		
		List<List<String>> list=readCSVFile(IMPORT_CSV_FILE);		
		return convertToPaidUsers2018(list);
	}
	
	public List<List<String>>  readCSVFile(String fileName) throws Exception{
		  URL url = getClass().getResource(fileName);
		  File file= new File(url.getFile());
		  try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			 return GenerateCSV.INSTANCE.readLines(br);
		  } 
		  
	} 
	public Collection<PaidUsers2018> importCSVText()throws Exception{
		
		List<List<String>> list=readCSVFile(IMPORT_CSV_FILE);		
		return convertToPaidUsers2018(list);
	}
	
	public Collection<UserLevelRevenue> importCSVText(String text)throws Exception{
		
		List<List<String>> list=readCSVText(text);		
		return convertToUserLevelRevenue(list);
	}
	
	public List<List<String>>  readCSVText(String text) throws Exception{		  		  
		  try (BufferedReader br = new BufferedReader(new StringReader(text))) {
			 return GenerateCSV.INSTANCE.readLines(br);
		  } 		  
	} 
	/*
	 * Skip first line
	 */
	public Collection<UserLevelRevenue> convertToUserLevelRevenue(List<List<String>> lines){
		Collection<UserLevelRevenue> result=new ArrayList<>();
		boolean isFirstLine=false;
		for(List<String> line:lines){
			if(!isFirstLine){
				isFirstLine=true;
				continue;
			}
			UserLevelRevenue user=new UserLevelRevenue();
			user.setAdUnitID(line.get(0));
			user.setIDFA(line.get(1));
			user.setIDFV(line.get(2));
			user.setUserID(line.get(3));
			user.setRevenue(new BigDecimal(line.get(4)));
			user.setImpressions(Integer.parseInt(line.get(5)));
			result.add(user);
		}
		
		return result;
	}
	
	public Collection<PaidUsers2018> convertToPaidUsers2018(List<List<String>> lines){
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
}
