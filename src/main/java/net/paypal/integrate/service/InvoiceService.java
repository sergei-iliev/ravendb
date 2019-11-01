package net.paypal.integrate.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

import net.paypal.integrate.api.Constants;
import net.paypal.integrate.command.PdfAttachment;
import net.paypal.integrate.command.csv.PaidUsers2018;
import net.paypal.integrate.command.invoice.Money;
import net.paypal.integrate.command.invoice.PayoutResult;
import net.paypal.integrate.entity.Counter;
import net.paypal.integrate.entity.PayPalUser;
import net.paypal.integrate.entity.RedeemingRequests;
import net.paypal.integrate.repository.ObjectifyRepository;

@Service
public class InvoiceService {
	@Autowired
	private ObjectifyRepository objectifyRepository;
	
	public static void main(String[] arg) throws Exception{
	 
	 InvoiceService service= new InvoiceService(); 
	 
	 PdfAttachment att=new PdfAttachment();
	 
	 PayoutResult payoutResult=new PayoutResult();
	 payoutResult.setFee(new Money( "0.35","CAD"));
	 payoutResult.setAmount(new Money("10","CAD"));
	 payoutResult.setPayoutBatchId("0001YUSC");
	 
	 try(InputStream in=service.createInvoice(payoutResult,new PayPalUser(),"201911111")){
	     //att.readFromStream(service.createInvoiceDemo());
	     //System.out.println(att.getContentType()+"::"+att.getFileName()+"::"+att.getBuffer().length);
 
	    //FileUtils.writeByteArrayToFile(new File("D:\\ginvoice.pdf"), att.getBuffer());
	    
		 Files.copy(in, Paths.get("D:\\uinvoice.pdf"),StandardCopyOption.REPLACE_EXISTING);   
	 }catch(Exception e){
	  	 e.printStackTrace();
	 }
	}
	
	
	public String generateInvoiceNumber(PayPalUser user){
		LocalDate date = LocalDate.now();
		int year=date.getYear();
		String result=String.valueOf(year);
		
		Counter incrementer=new Counter();
		incrementer.setUserKey(user.getKey());
		
		long number=objectifyRepository.createInvoiceNumer();
		System.out.println(number);
		result+=String.valueOf(Constants.INVOICE_BASE+number);
		
		return result;
	}
	/*
	 * CSV file import to pdf
	 */
	public InputStream createInvoice(RedeemingRequests redeemingRequests,PaidUsers2018 paidUsers2018,String invoiceNumber) throws Exception{
	    ByteArrayOutputStream output=new ByteArrayOutputStream();
		Document document = new Document();
		PdfWriter.getInstance(document,output);

		
	    
		Paragraph title=setHeader();
		
		long diffInSec = Math.abs(redeemingRequests.getDate().getSeconds() - redeemingRequests.getCreationDate().getSeconds());
		long days = TimeUnit.DAYS.convert(diffInSec, TimeUnit.SECONDS);
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
		
		addressTable.addCell(getCell(redeemingRequests.getFullName(),10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("Soft Baked Apps UG (haftungsbeschränkt)",10, PdfPCell.ALIGN_LEFT));
		
		addressTable.addCell(getCell(Objects.toString(redeemingRequests.getFullAddress(), ""),10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("Schinkestrasse 14",10, PdfPCell.ALIGN_LEFT));
		
		
		addressTable.addCell(getCell("Country: "+Objects.toString(redeemingRequests.getCountryCode(), ""),10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("12047, Berlin, Germany",10, PdfPCell.ALIGN_LEFT));
		
		
		addressTable.addCell(getCell(Objects.toString(redeemingRequests.getEmail(), "") ,10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("VAT ID: DE300857037",10, PdfPCell.ALIGN_LEFT));
		
		addressTable.addCell(getCell("Internal user ID:"+Objects.toString(redeemingRequests.getUserGuid(), "") ,10, PdfPCell.ALIGN_LEFT));
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
		
		if(redeemingRequests.getType()!=null){
		  if(redeemingRequests.getType().equalsIgnoreCase("paypal")){
			validity.addCell(getValidityCell(" * Paid through "+redeemingRequests.getType()));
		  }else{
			validity.addCell(getValidityCell(" * Paid through "+redeemingRequests.getType()+" voucher"));  
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
	public InputStream createInvoice(PayoutResult payoutResult,PayPalUser user,String invoiceNumber) throws Exception{
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
		
		addressTable.addCell(getCell(user.getFullName(),10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("Soft Baked Apps UG (haftungsbeschränkt)",10, PdfPCell.ALIGN_LEFT));
		
		addressTable.addCell(getCell(Objects.toString(user.getFullAddress(), ""),10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("Schinkestrasse 14",10, PdfPCell.ALIGN_LEFT));
		
		
		addressTable.addCell(getCell("Country: "+Objects.toString(user.getCountryCode(), ""),10, PdfPCell.ALIGN_LEFT));
		addressTable.addCell(getCell("",12, PdfPCell.ALIGN_LEFT, Font.BOLD));
		addressTable.addCell(getCell("12047, Berlin, Germany",10, PdfPCell.ALIGN_LEFT));
		
		
		addressTable.addCell(getCell(Objects.toString(user.getPaypalAccount(), "") ,10, PdfPCell.ALIGN_LEFT));
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
