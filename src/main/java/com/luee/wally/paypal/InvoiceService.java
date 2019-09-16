package com.luee.wally.paypal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
import com.luee.wally.csv.PaidUsers2018;


public class InvoiceService {
	
	
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
		topTable.addCell(getIRDCell("Credit Voice No"));
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
		addressTable.addCell(getCell("Soft Baked Apps UG (haftungsbeschränkt)",10, PdfPCell.ALIGN_LEFT));
		
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
		billTable.addCell(getBillRowCell(paidUsers2018.getPayedAmount()));
		billTable.addCell(getBillRowCell("1"));
		billTable.addCell(getBillRowCell(paidUsers2018.getPayedAmount()+" EUR"));
		
		
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

		PdfPTable accounts = new PdfPTable(2);
		accounts.setWidthPercentage(100);

		accounts.addCell(getAccountsCell("Total:"));
		accounts.addCell(getAccountsCellR(paidUsers2018.getPayedAmount()+" EUR"));			
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
	
	private long createInvoiceNumer(){
		  ShardedSequence sequence=new ShardedSequence("SEQUENCE");		
		  return sequence.next();
	}

	
	public String generateInvoiceNumber(){
		LocalDate date = LocalDate.now();
		int year=date.getYear();
		String result=String.valueOf(year);
		
		
		long number= createInvoiceNumer();
		result+=String.valueOf(Constants.INVOICE_BASE+number);
		
		return result;
	}
	public InputStream createInvoice(PayoutResult payoutResult,Entity user,String invoiceNumber) throws Exception{
	    ByteArrayOutputStream output=new ByteArrayOutputStream();
		Document document = new Document();
		PdfWriter.getInstance(document,output);

		String today=LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
	    
		Paragraph title=setHeader();
		
		PdfPTable topTable = new PdfPTable(2);
		topTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
		topTable.setWidthPercentage(160 / 4f);
		topTable.addCell(getIRDCell("Credit Voice No"));
		topTable.addCell(getIRDCell("Date"));
		topTable.addCell(getIRDCell(invoiceNumber)); // pass invoice number
		topTable.addCell(getIRDCell(today)); // pass invoice date	
				
		PdfPTable addressTable = new PdfPTable(3);
		addressTable.setSpacingBefore(12);
		
		addressTable.setWidthPercentage(100);
		
		
		addressTable.addCell(getCell("To",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("From",12, PdfPCell.ALIGN_LEFT, Font.BOLD));	
		
		addressTable.addCell(getCell((String) user.getProperty("full_name"),10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("Soft Baked Apps UG (haftungsbeschränkt)",10, PdfPCell.ALIGN_LEFT));
		
		addressTable.addCell(getCell(Objects.toString((String) user.getProperty("full_address"), ""),10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("Schinkestrasse 14",10, PdfPCell.ALIGN_LEFT));
		
		
		addressTable.addCell(getCell("Country: "+Objects.toString((String) user.getProperty("country_code"), ""),10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("12047, Berlin, Germany",10, PdfPCell.ALIGN_LEFT));
		
		
		addressTable.addCell(getCell(Objects.toString((String) user.getProperty("paypal_account"), "") ,10, PdfPCell.ALIGN_LEFT));
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
		accounts.addCell(getAccountsCell("Tax:"));
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
/*
	public InputStream createInvoice (PayoutResult payoutResult,Payee payee,String invoiceNumber) throws Exception{
		    ByteArrayOutputStream output=new ByteArrayOutputStream();
			Document document = new Document();
			PdfWriter.getInstance(document,output);

			String today=LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

			PdfPTable irdTable = new PdfPTable(2);
			irdTable.addCell(getIRDCell("Invoice No"));
			irdTable.addCell(getIRDCell("Invoice Date"));
			irdTable.addCell(getIRDCell(invoiceNumber)); // pass invoice number
			irdTable.addCell(getIRDCell(today)); // pass invoice date				

			PdfPTable irhTable = new PdfPTable(3);
			irhTable.setWidthPercentage(100);

			irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
			irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
			irhTable.addCell(getIRHCell("Invoice", PdfPCell.ALIGN_RIGHT));
			irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
			irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
			PdfPCell invoiceTable = new PdfPCell (irdTable);
			invoiceTable.setBorder(0);
			irhTable.addCell(invoiceTable);

			FontSelector fs = new FontSelector();
			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 13, Font.BOLD);
			fs.addFont(font);
			Phrase bill = fs.process("Bill To"); // customer information
			Paragraph name = new Paragraph("Mr.Venkateswara Rao");
			name.setIndentationLeft(20);
			Paragraph contact = new Paragraph("9652886877");
			contact.setIndentationLeft(20);
			Paragraph address = new Paragraph("Kuchipudi,Movva");
			address.setIndentationLeft(20);

			PdfPTable billTable = new PdfPTable(6); //one page contains 15 records 
			billTable.setWidthPercentage(100);
			billTable.setWidths(new float[] { 1, 2,5,2,1,2 });
			billTable.setSpacingBefore(30.0f);
			billTable.addCell(getBillHeaderCell("Index"));
			billTable.addCell(getBillHeaderCell("Item"));
			billTable.addCell(getBillHeaderCell("Description"));
			billTable.addCell(getBillHeaderCell("Unit Price"));
			billTable.addCell(getBillHeaderCell("Qty"));
			billTable.addCell(getBillHeaderCell("Amount"));

			billTable.addCell(getBillRowCell("1"));
			billTable.addCell(getBillRowCell("Mobile"));
			billTable.addCell(getBillRowCell("Nokia Lumia 610 \n IMI:WQ361989213 "));
			billTable.addCell(getBillRowCell("12000.0"));
			billTable.addCell(getBillRowCell("1"));
			billTable.addCell(getBillRowCell("12000.0"));

			billTable.addCell(getBillRowCell("2"));
			billTable.addCell(getBillRowCell("Accessories"));
			billTable.addCell(getBillRowCell("Nokia Lumia 610 Panel \n Serial:TIN3720 "));
			billTable.addCell(getBillRowCell("200.0"));
			billTable.addCell(getBillRowCell("1"));
			billTable.addCell(getBillRowCell("200.0"));


			billTable.addCell(getBillRowCell("3"));
			billTable.addCell(getBillRowCell("Other"));
			billTable.addCell(getBillRowCell("16Gb Memorycard \n Serial:UR8531 "));
			billTable.addCell(getBillRowCell("420.0"));
			billTable.addCell(getBillRowCell("1"));
			billTable.addCell(getBillRowCell("420.0"));

			billTable.addCell(getBillRowCell(" "));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));

			billTable.addCell(getBillRowCell(" "));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));

			billTable.addCell(getBillRowCell(" "));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));

			billTable.addCell(getBillRowCell(" "));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));

			billTable.addCell(getBillRowCell(" "));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));

			billTable.addCell(getBillRowCell(" "));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));

			billTable.addCell(getBillRowCell(" "));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));

			billTable.addCell(getBillRowCell(" "));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));

			billTable.addCell(getBillRowCell(" "));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));

			billTable.addCell(getBillRowCell(" "));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));

			billTable.addCell(getBillRowCell(" "));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));

			billTable.addCell(getBillRowCell(" "));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));
			billTable.addCell(getBillRowCell(""));

			PdfPTable validity = new PdfPTable(1);
			validity.setWidthPercentage(100);
			validity.addCell(getValidityCell(" "));
			validity.addCell(getValidityCell("Warranty"));
			validity.addCell(getValidityCell(" * Products purchased comes with 1 year national warranty \n   (if applicable)"));
			validity.addCell(getValidityCell(" * Warranty should be claimed only from the respective manufactures"));		    
			PdfPCell summaryL = new PdfPCell (validity);
			summaryL.setColspan (3);
			summaryL.setPadding (1.0f);	                   
			billTable.addCell(summaryL);

			PdfPTable accounts = new PdfPTable(2);
			accounts.setWidthPercentage(100);
			accounts.addCell(getAccountsCell("Subtotal"));
			accounts.addCell(getAccountsCellR("12620.00"));
			accounts.addCell(getAccountsCell("Discount (10%)"));
			accounts.addCell(getAccountsCellR("1262.00"));
			accounts.addCell(getAccountsCell("Tax(2.5%)"));
			accounts.addCell(getAccountsCellR("315.55"));
			accounts.addCell(getAccountsCell("Total"));
			accounts.addCell(getAccountsCellR("11673.55"));			
			PdfPCell summaryR = new PdfPCell (accounts);
			summaryR.setColspan (3);         
			billTable.addCell(summaryR);  

			PdfPTable describer = new PdfPTable(1);
			describer.setWidthPercentage(100);
			describer.addCell(getdescCell(" "));
			describer.addCell(getdescCell("Goods once sold will not be taken back or exchanged || Subject to product justification || Product damage no one responsible || "
					+ " Service only at concarned authorized service centers"));	

			document.open();//PDF document opened........	

			//document.add(image);
			document.add(irhTable);
			document.add(bill);
			document.add(name);
			document.add(contact);
			document.add(address);			
			document.add(billTable);
			document.add(describer);

			document.close();

			return  new ByteArrayInputStream(output.toByteArray());
		
	}
*/
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
