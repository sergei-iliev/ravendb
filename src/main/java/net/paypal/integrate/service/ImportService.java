package net.paypal.integrate.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.cloud.Timestamp;

import net.paypal.integrate.api.GenerateCSV;
import net.paypal.integrate.command.PdfAttachment;
import net.paypal.integrate.command.csv.PaidUsers2018;
import net.paypal.integrate.command.invoice.Money;
import net.paypal.integrate.command.invoice.PayoutResult;
import net.paypal.integrate.entity.PayPalUser;
import net.paypal.integrate.entity.RedeemingRequests;
import net.paypal.integrate.repository.CloudStorageRepository;
import net.paypal.integrate.repository.ExportRepository;

@Service
public class ImportService {
	private final Logger logger = Logger.getLogger(ImportService.class.getName());

	public static final String IMPORT_CSV_FILE = "/csv/paid_users_2018.csv";
	
	@Autowired
	private InvoiceService invoiceService;
	
	@Autowired
	private CloudStorageRepository cloudStorageRepository;
	
	public void createDemoPDF()throws Exception{
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
	
	public Collection<PaidUsers2018> importFile(){
		
		List<List<String>> list=readFile(IMPORT_CSV_FILE);		
		return convertToObject(list);
	}
	
	public List<List<String>>  readFile(String fileName) {
		  URL url = getClass().getResource(fileName);
		  File file= new File(url.getFile());
		  String line;
		  try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			 return GenerateCSV.INSTANCE.readLines(br);
		  } 
		  catch (IOException e) {
	          logger.log(Level.SEVERE,"csv", e);			  
	      }
		  
		  return Collections.emptyList();
	} 
	
	public Collection<PaidUsers2018> convertToObject(List<List<String>> lines){
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
