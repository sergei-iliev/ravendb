package com.luee.wally.api.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.google.appengine.api.datastore.Entity;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.luee.wally.command.invoice.PayoutResult;
import com.luee.wally.csv.PaidUsers2018;
import com.luee.wally.entity.PaidUser;
import com.luee.wally.entity.PaidUserExternal;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.utils.Utilities;


public class InvoiceService extends AbstractService{
	
	public InputStream createInvoice(PaidUserExternal paidUserExternal,String invoiceNumber) throws Exception{
	    ByteArrayOutputStream output=new ByteArrayOutputStream();
		Document document = new Document();
		PdfWriter.getInstance(document,output);		
	    
		Paragraph title=setHeader();
		
		//long diffInSec = Math.abs(paidUserExternal.getDate().getTime() - (paidUserExternal.getDate()).getTime());
		//long days = TimeUnit.DAYS.convert(diffInSec, TimeUnit.MILLISECONDS);
		//long workInDays=3*days;
		
		PdfPTable topTable = new PdfPTable(2);
		topTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
		topTable.setWidthPercentage(160 / 4f);
		topTable.addCell(getIRDCell("Credit Note No"));
		topTable.addCell(getIRDCell("Date"));
		topTable.addCell(getIRDCell(invoiceNumber)); // pass invoice number
		topTable.addCell(getIRDCell(paidUserExternal.getDate().toString())); // pass invoice date	
				
		PdfPTable addressTable = new PdfPTable(3);
		addressTable.setSpacingBefore(12);
		
		addressTable.setWidthPercentage(100);
		addressTable.setWidths(new int[]{200,50,100});
		
		addressTable.addCell(getCell("To",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("From",12, PdfPCell.ALIGN_LEFT, Font.BOLD));	
		
		addressTable.addCell(getCell((String) paidUserExternal.getFullName(),10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("Soft Baked Apps GmbH (haftungsbeschränkt)",10, PdfPCell.ALIGN_LEFT));
		
		addressTable.addCell(getCell(Objects.toString("Address: "+(String) paidUserExternal.getAddress(), ""),10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("Schinkestrasse 14",10, PdfPCell.ALIGN_LEFT));
		
		
		addressTable.addCell(getCell("Country: "+Objects.toString((String) paidUserExternal.getCountryCode(), ""),10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("12047, Berlin, Germany",10, PdfPCell.ALIGN_LEFT));
		
		
		addressTable.addCell(getCell("Email: "+Objects.toString((String) paidUserExternal.getEmail(), "") ,10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("VAT ID: DE300857037",10, PdfPCell.ALIGN_LEFT));
		
		addressTable.addCell(getCell("Internal user ID:"+Objects.toString(paidUserExternal.getRedeemingRequestId(), "") ,10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell(" ",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell(" ",10, PdfPCell.ALIGN_LEFT));
		
		PdfPTable billTable = new PdfPTable(4); //one page contains 15 records 
		billTable.setWidthPercentage(100);
		billTable.setWidths(new float[] {5,1,2,2 });
		billTable.setSpacingBefore(30.0f);
		billTable.addCell(getBillHeaderCell("Description"));
		billTable.addCell(getBillHeaderCell("Unit Price"));
		billTable.addCell(getBillHeaderCell("Qty"));
		billTable.addCell(getBillHeaderCell("Amount"));
		
		//billTable.addCell(getBillRowCell("Interaction and usage of mobile app - "+String.valueOf(workInDays) +" hours"));				
		if(paidUserExternal.getPaidCurrency().equals("EUR")){
			billTable.addCell(getBillRowCell(Utilities.formatPrice(paidUserExternal.getEurCurrency())));
			billTable.addCell(getBillRowCell("1"));
			billTable.addCell(getBillRowCell(Utilities.formatPrice(paidUserExternal.getEurCurrency())+" EUR"));
		}else{
			billTable.addCell(getBillRowCell(paidUserExternal.getAmount()));
			billTable.addCell(getBillRowCell("1"));
			billTable.addCell(getBillRowCell(paidUserExternal.getAmount()+" "+paidUserExternal.getPaidCurrency()));			
		}
		
		PdfPTable validity = new PdfPTable(1);
		validity.setWidthPercentage(100);	
		
		if(paidUserExternal.getType()!=null){
		  if(((String) paidUserExternal.getType()).equalsIgnoreCase("paypal")){
			validity.addCell(getValidityCell(" * Paid through "+(String) paidUserExternal.getType()));
		  }else{
			validity.addCell(getValidityCell(" * Paid through "+(String) paidUserExternal.getType()+" voucher"));  
		  }
		}
		PdfPCell summaryL = new PdfPCell (validity);
		summaryL.setColspan (2);
		summaryL.setPadding (1.0f);	                   
		billTable.addCell(summaryL);

		PdfPTable accounts = new PdfPTable(1);
		accounts.setWidthPercentage(100);

		String total;
		if(paidUserExternal.getPaidCurrency().equals("EUR")){
		  total=("Total: "+Utilities.formatPrice(paidUserExternal.getEurCurrency())+" EUR");
		}else{
		  total=("Total: "+(paidUserExternal.getAmount()+" "+paidUserExternal.getPaidCurrency()+" ("+ Utilities.formatPrice(paidUserExternal.getEurCurrency())+" EUR)"));	
		}
		
		accounts.addCell(getAccountsCellR(total));
		PdfPCell summaryR = new PdfPCell (accounts);
		summaryR.setColspan (2);         
		billTable.addCell(summaryR);  
		
		document.open();
		document.add(title);
		document.add(topTable);
		document.add(addressTable);
		document.add(billTable);
		
		document.close();

		return  new ByteArrayInputStream(output.toByteArray());
	}
	
	public InputStream createInvoice(RedeemingRequests redeemingRequest,PaidUser paidUser,String invoiceNumber) throws Exception{
	    ByteArrayOutputStream output=new ByteArrayOutputStream();
		Document document = new Document();
		PdfWriter.getInstance(document,output);

		
	    
		Paragraph title=setHeader();
		
		long diffInSec = Math.abs(redeemingRequest.getDate().getTime() - (redeemingRequest.getCreationDate()).getTime());
		long days = TimeUnit.DAYS.convert(diffInSec, TimeUnit.MILLISECONDS);
		long workInDays=3*days;
		
		PdfPTable topTable = new PdfPTable(2);
		topTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
		topTable.setWidthPercentage(160 / 4f);
		topTable.addCell(getIRDCell("Credit Note No"));
		topTable.addCell(getIRDCell("Date"));
		topTable.addCell(getIRDCell(invoiceNumber)); // pass invoice number
		topTable.addCell(getIRDCell(paidUser.getDate().toString())); // pass invoice date	
				
		PdfPTable addressTable = new PdfPTable(3);
		addressTable.setSpacingBefore(12);
		
		addressTable.setWidthPercentage(100);
		addressTable.setWidths(new int[]{200,50,100});
		
		addressTable.addCell(getCell("To",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("From",12, PdfPCell.ALIGN_LEFT, Font.BOLD));	
		
		addressTable.addCell(getCell((String) redeemingRequest.getFullName(),10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("Soft Baked Apps GmbH (haftungsbeschränkt)",10, PdfPCell.ALIGN_LEFT));
		
		addressTable.addCell(getCell(Objects.toString("Address: "+(String) redeemingRequest.getFullAddress(), ""),10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("Schinkestrasse 14",10, PdfPCell.ALIGN_LEFT));
		
		
		addressTable.addCell(getCell("Country: "+Objects.toString((String) redeemingRequest.getCountryCode(), ""),10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("12047, Berlin, Germany",10, PdfPCell.ALIGN_LEFT));
		
		
		addressTable.addCell(getCell("Email: "+Objects.toString((String) redeemingRequest.getEmail(), "") ,10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("VAT ID: DE300857037",10, PdfPCell.ALIGN_LEFT));
		
		addressTable.addCell(getCell("Internal user ID:"+Objects.toString(redeemingRequest.getUserGuid(), "") ,10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell(" ",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell(" ",10, PdfPCell.ALIGN_LEFT));
		
		PdfPTable billTable = new PdfPTable(4); //one page contains 15 records 
		billTable.setWidthPercentage(100);
		billTable.setWidths(new float[] {5,1,2,2 });
		billTable.setSpacingBefore(30.0f);
		billTable.addCell(getBillHeaderCell("Description"));
		billTable.addCell(getBillHeaderCell("Unit Price"));
		billTable.addCell(getBillHeaderCell("Qty"));
		billTable.addCell(getBillHeaderCell("Amount"));
		
		billTable.addCell(getBillRowCell("Interaction and usage of mobile app - "+String.valueOf(workInDays) +" hours"));				
		if(paidUser.getPaidCurrency().equals("EUR")){
			billTable.addCell(getBillRowCell(Utilities.formatPrice(paidUser.getEurCurrency())));
			billTable.addCell(getBillRowCell("1"));
			billTable.addCell(getBillRowCell(Utilities.formatPrice(paidUser.getEurCurrency())+" EUR"));
		}else{
			billTable.addCell(getBillRowCell(paidUser.getAmount()));
			billTable.addCell(getBillRowCell("1"));
			billTable.addCell(getBillRowCell(paidUser.getAmount()+" "+paidUser.getPaidCurrency()));			
		}
		
		PdfPTable validity = new PdfPTable(1);
		validity.setWidthPercentage(100);	
		
		if(redeemingRequest.getType()!=null){
		  if(((String) redeemingRequest.getType()).equalsIgnoreCase("paypal")){
			validity.addCell(getValidityCell(" * Paid through "+(String) redeemingRequest.getType()));
		  }else{
			validity.addCell(getValidityCell(" * Paid through "+(String) redeemingRequest.getType()+" voucher"));  
		  }
		}
		PdfPCell summaryL = new PdfPCell (validity);
		summaryL.setColspan (2);
		summaryL.setPadding (1.0f);	                   
		billTable.addCell(summaryL);

		PdfPTable accounts = new PdfPTable(1);
		accounts.setWidthPercentage(100);

		String total;
		if(paidUser.getPaidCurrency().equals("EUR")){
		  total=("Total: "+Utilities.formatPrice(paidUser.getEurCurrency())+" EUR");
		}else{
		  total=("Total: "+(paidUser.getAmount()+" "+paidUser.getPaidCurrency()+" ("+ Utilities.formatPrice(paidUser.getEurCurrency())+" EUR)"));	
		}
		
		accounts.addCell(getAccountsCellR(total));
		PdfPCell summaryR = new PdfPCell (accounts);
		summaryR.setColspan (2);         
		billTable.addCell(summaryR);  
		
		document.open();
		document.add(title);
		document.add(topTable);
		document.add(addressTable);
		document.add(billTable);
		
		document.close();

		return  new ByteArrayInputStream(output.toByteArray());
	}

	/*
	 * CSV file import to pdf
	 */
	public InputStream createInvoice(Entity redeemingRequest,PaidUsers2018 paidUsers2018,String invoiceNumber) throws Exception{
	    ByteArrayOutputStream output=new ByteArrayOutputStream();
		Document document = new Document();
		PdfWriter.getInstance(document,output);

		
	    
		Paragraph title=setHeader();
		
		long diffInSec = Math.abs(((Date) redeemingRequest.getProperty("date")).getTime() - ((Date) redeemingRequest.getProperty("creation_date")).getTime());
		long days = TimeUnit.DAYS.convert(diffInSec, TimeUnit.MILLISECONDS);
		long workInDays=3*days;
		
		PdfPTable topTable = new PdfPTable(2);
		topTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
		topTable.setWidthPercentage(160 / 4f);
		topTable.addCell(getIRDCell("Credit Note No"));
		topTable.addCell(getIRDCell("Date"));
		topTable.addCell(getIRDCell(invoiceNumber)); // pass invoice number
		topTable.addCell(getIRDCell(paidUsers2018.getDate())); // pass invoice date	
				
		PdfPTable addressTable = new PdfPTable(3);
		addressTable.setSpacingBefore(12);
		
		addressTable.setWidthPercentage(100);
		addressTable.setWidths(new int[]{200,50,100});
		
		addressTable.addCell(getCell("To",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("From",12, PdfPCell.ALIGN_LEFT, Font.BOLD));	
		
		addressTable.addCell(getCell((String) redeemingRequest.getProperty("full_name"),10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("Soft Baked Apps GmbH (haftungsbeschränkt)",10, PdfPCell.ALIGN_LEFT));
		
		addressTable.addCell(getCell(Objects.toString("Address: "+(String) redeemingRequest.getProperty("full_address"), ""),10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("Schinkestrasse 14",10, PdfPCell.ALIGN_LEFT));
		
		
		addressTable.addCell(getCell("Country: "+Objects.toString((String) redeemingRequest.getProperty("country_code"), ""),10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("12047, Berlin, Germany",10, PdfPCell.ALIGN_LEFT));
		
		
		addressTable.addCell(getCell("Email: "+Objects.toString((String) redeemingRequest.getProperty("email"), "") ,10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("VAT ID: DE300857037",10, PdfPCell.ALIGN_LEFT));
		
		addressTable.addCell(getCell("Internal user ID:"+Objects.toString(redeemingRequest.getProperty("user_guid"), "") ,10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell(" ",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell(" ",10, PdfPCell.ALIGN_LEFT));
		
		PdfPTable billTable = new PdfPTable(4); //one page contains 15 records 
		billTable.setWidthPercentage(100);
		billTable.setWidths(new float[] {5,1,2,2 });
		billTable.setSpacingBefore(30.0f);
		billTable.addCell(getBillHeaderCell("Description"));
		billTable.addCell(getBillHeaderCell("Unit Price"));
		billTable.addCell(getBillHeaderCell("Qty"));
		billTable.addCell(getBillHeaderCell("Amount"));
		
		billTable.addCell(getBillRowCell("Interaction and usage of mobile app - "+String.valueOf(workInDays) +" hours"));				
		if(paidUsers2018.getUserCurrencyCode().equals("EUR")){
			billTable.addCell(getBillRowCell(paidUsers2018.getPayedAmount()));
			billTable.addCell(getBillRowCell("1"));
			billTable.addCell(getBillRowCell(paidUsers2018.getPayedAmount()+" EUR"));
		}else{
			billTable.addCell(getBillRowCell(paidUsers2018.getUserPayedAmount()));
			billTable.addCell(getBillRowCell("1"));
			billTable.addCell(getBillRowCell(paidUsers2018.getUserPayedAmount()+" "+paidUsers2018.getUserCurrencyCode()));			
		}
		
		PdfPTable validity = new PdfPTable(1);
		validity.setWidthPercentage(100);	
		
		if(redeemingRequest.getProperty("type")!=null){
		  if(((String) redeemingRequest.getProperty("type")).equalsIgnoreCase("paypal")){
			validity.addCell(getValidityCell(" * Paid through "+(String) redeemingRequest.getProperty("type")));
		  }else{
			validity.addCell(getValidityCell(" * Paid through "+(String) redeemingRequest.getProperty("type")+" voucher"));  
		  }
		}
		PdfPCell summaryL = new PdfPCell (validity);
		summaryL.setColspan (2);
		summaryL.setPadding (1.0f);	                   
		billTable.addCell(summaryL);

		PdfPTable accounts = new PdfPTable(1);
		accounts.setWidthPercentage(100);

		String total;
		if(paidUsers2018.getUserCurrencyCode().equals("EUR")){
		  total=("Total: "+paidUsers2018.getPayedAmount()+" EUR");
		}else{
		  total=("Total: "+(paidUsers2018.getUserPayedAmount()+" "+paidUsers2018.getUserCurrencyCode()+" ("+ paidUsers2018.getPayedAmount()+" EUR)"));	
		}
		
		accounts.addCell(getAccountsCellR(total));
		PdfPCell summaryR = new PdfPCell (accounts);
		summaryR.setColspan (2);         
		billTable.addCell(summaryR);  
		
		document.open();
		document.add(title);
		document.add(topTable);
		document.add(addressTable);
		document.add(billTable);
		
		document.close();

		return  new ByteArrayInputStream(output.toByteArray());
	}
	
	public InputStream createInvoice(PayoutResult payoutResult,
			/*Entity user,*/
			String fullName,String fullAddress,String countryCode,String payPalAccount,
			String invoiceNumber) throws Exception{
	    ByteArrayOutputStream output=new ByteArrayOutputStream();
		Document document = new Document();
		PdfWriter.getInstance(document,output);

		String today=LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
	    
		Paragraph title=setHeader();
		
		PdfPTable topTable = new PdfPTable(2);
		topTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
		topTable.setWidthPercentage(160 / 4f);
		topTable.addCell(getIRDCell("Credit Note No"));
		topTable.addCell(getIRDCell("Date"));
		topTable.addCell(getIRDCell(invoiceNumber)); // pass invoice number
		topTable.addCell(getIRDCell(today)); // pass invoice date	
				
		PdfPTable addressTable = new PdfPTable(3);
		addressTable.setSpacingBefore(12);
		
		addressTable.setWidthPercentage(100);
		
		
		addressTable.addCell(getCell("To",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("From",12, PdfPCell.ALIGN_LEFT, Font.BOLD));	
		
		addressTable.addCell(getCell(fullName /*(String) user.getProperty("full_name")*/,10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("Soft Baked Apps GmbH (haftungsbeschränkt)",10, PdfPCell.ALIGN_LEFT));
		
		addressTable.addCell(getCell(Objects.toString( fullAddress /*(String) user.getProperty("full_address")*/, ""),10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("Schinkestrasse 14",10, PdfPCell.ALIGN_LEFT));
		
		
		addressTable.addCell(getCell("Country: "+Objects.toString(countryCode/*(String) user.getProperty("country_code")*/, ""),10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("12047, Berlin, Germany",10, PdfPCell.ALIGN_LEFT));
		
		
		addressTable.addCell(getCell(Objects.toString(payPalAccount/*(String) user.getProperty("paypal_account")*/, "") ,10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("VAT ID: DE300857037",10, PdfPCell.ALIGN_LEFT));
		
		PdfPTable billTable = new PdfPTable(4); //one page contains 15 records 
		billTable.setWidthPercentage(100);
		billTable.setWidths(new float[] {5,1,2,2 });
		billTable.setSpacingBefore(30.0f);
		billTable.addCell(getBillHeaderCell("Description"));
		billTable.addCell(getBillHeaderCell("Unit Price"));
		billTable.addCell(getBillHeaderCell("Qty"));
		billTable.addCell(getBillHeaderCell("Amount"));
		
		billTable.addCell(getBillRowCell("Interaction and usage of mobile app"));				
		billTable.addCell(getBillRowCell(payoutResult.getAmount().getValue()));
		billTable.addCell(getBillRowCell("1"));
		billTable.addCell(getBillRowCell(payoutResult.getAmount().getValue()+" "+ payoutResult.getAmount().getCurrency()));
		
		
		PdfPTable validity = new PdfPTable(1);
		validity.setWidthPercentage(100);				
		validity.addCell(getValidityCell(" * PayPal transaction id: "+payoutResult.getPayoutBatchId()));		    
		
		PdfPCell summaryL = new PdfPCell (validity);
		summaryL.setColspan (2);
		summaryL.setPadding (1.0f);	                   
		billTable.addCell(summaryL);

		PdfPTable accounts = new PdfPTable(2);
		accounts.setWidthPercentage(100);
		accounts.addCell(getAccountsCell("Subtotal:"));
		accounts.addCell(getAccountsCellR(payoutResult.getAmount().getValue()+" "+ payoutResult.getAmount().getCurrency()));
		accounts.addCell(getAccountsCell("Fee:"));
		accounts.addCell(getAccountsCellR(payoutResult.getFee().getValue()+" "+ payoutResult.getFee().getCurrency()));
		accounts.addCell(getAccountsCell("Total:"));
		accounts.addCell(getAccountsCellR(payoutResult.getTotal()+" "+ payoutResult.getFee().getCurrency()));			
		PdfPCell summaryR = new PdfPCell (accounts);
		summaryR.setColspan (2);         
		billTable.addCell(summaryR);  
		
		document.open();
		document.add(title);
		document.add(topTable);
		document.add(addressTable);
		document.add(billTable);
		
		document.close();

		return  new ByteArrayInputStream(output.toByteArray());
	}

	public Paragraph setHeader() {
	    Font fontbold = FontFactory.getFont(FontFactory.HELVETICA, 20);
	    Paragraph title = new Paragraph("Credit Note", fontbold);
	    title.setSpacingAfter(20);
	    title.setAlignment(1); // Center
	    return title;
	}
	public PdfPCell getCell(String text,int size, int alignment,int style) {
		FontSelector fs = new FontSelector();
		Font font = FontFactory.getFont(FontFactory.HELVETICA, size,style);
		/*	font.setColor(BaseColor.GRAY);*/
		fs.addFont(font);
		Phrase phrase = fs.process(text);
		PdfPCell cell = new PdfPCell(phrase);
		//cell.setPadding(5);
		cell.setHorizontalAlignment(alignment);
		cell.setBorder(PdfPCell.NO_BORDER);
		return cell;
	}
	public  PdfPCell getCell(String text,int size, int alignment) {
		FontSelector fs = new FontSelector();
		Font font = FontFactory.getFont(FontFactory.HELVETICA, size);
		/*	font.setColor(BaseColor.GRAY);*/
		fs.addFont(font);
		Phrase phrase = fs.process(text);
		PdfPCell cell = new PdfPCell(phrase);
		//cell.setPadding(5);
		cell.setHorizontalAlignment(alignment);
		cell.setBorder(PdfPCell.NO_BORDER);
		return cell;
	}
	public  PdfPCell getIRHCell(String text, int alignment) {
		FontSelector fs = new FontSelector();
		Font font = FontFactory.getFont(FontFactory.HELVETICA, 16);
		/*	font.setColor(BaseColor.GRAY);*/
		fs.addFont(font);
		Phrase phrase = fs.process(text);
		PdfPCell cell = new PdfPCell(phrase);
		cell.setPadding(5);
		cell.setHorizontalAlignment(alignment);
		cell.setBorder(PdfPCell.NO_BORDER);
		return cell;
	}

	public  PdfPCell getIRDCell(String text) {
		PdfPCell cell = new PdfPCell (new Paragraph (text));
		cell.setHorizontalAlignment (Element.ALIGN_CENTER);
		cell.setPadding (5.0f);
		cell.setBorderColor(BaseColor.LIGHT_GRAY);
		return cell;
	}

	public  PdfPCell getBillHeaderCell(String text) {
		FontSelector fs = new FontSelector();
		Font font = FontFactory.getFont(FontFactory.HELVETICA, 11);
		font.setColor(BaseColor.GRAY);
		fs.addFont(font);
		Phrase phrase = fs.process(text);
		PdfPCell cell = new PdfPCell (phrase);
		cell.setHorizontalAlignment (Element.ALIGN_CENTER);
		cell.setPadding (5.0f);
		return cell;
	}

	public  PdfPCell getBillRowCell(String text) {
		PdfPCell cell = new PdfPCell (new Paragraph (text));
		cell.setHorizontalAlignment (Element.ALIGN_CENTER);
		cell.setPadding (5.0f);
		cell.setBorderWidthBottom(0);
		cell.setBorderWidthTop(0);
		return cell;
	}

	public  PdfPCell getBillFooterCell(String text) {
		PdfPCell cell = new PdfPCell (new Paragraph (text));
		cell.setHorizontalAlignment (Element.ALIGN_CENTER);
		cell.setPadding (5.0f);
		cell.setBorderWidthBottom(0);
		cell.setBorderWidthTop(0);
		return cell;
	}

	public  PdfPCell getValidityCell(String text) {
		FontSelector fs = new FontSelector();
		Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
		font.setColor(BaseColor.GRAY);
		fs.addFont(font);
		Phrase phrase = fs.process(text);
		PdfPCell cell = new PdfPCell (phrase);		
		cell.setBorder(0);
		return cell;
	}

	public  PdfPCell getAccountsCell(String text) {
		FontSelector fs = new FontSelector();
		Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
		fs.addFont(font);
		Phrase phrase = fs.process(text);
		PdfPCell cell = new PdfPCell (phrase);		
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setPadding (5.0f);
		return cell;
	}
	public  PdfPCell getAccountsCellR(String text) {
		FontSelector fs = new FontSelector();
		Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
		fs.addFont(font);
		Phrase phrase = fs.process(text);
		PdfPCell cell = new PdfPCell (phrase);		
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthTop(0);
		cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
		cell.setPadding (5.0f);
		cell.setPaddingRight(20.0f);
		return cell;
	}

	public  PdfPCell getdescCell(String text) {
		FontSelector fs = new FontSelector();
		Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
		font.setColor(BaseColor.GRAY);
		fs.addFont(font);
		Phrase phrase = fs.process(text);
		PdfPCell cell = new PdfPCell (phrase);	
		cell.setHorizontalAlignment (Element.ALIGN_CENTER);
		cell.setBorder(0);
		return cell;
	}
	
}
