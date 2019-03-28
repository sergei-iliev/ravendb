package net.paypal.integrate.command;

public class PdfAttachment extends Attachment {

	public PdfAttachment() {
	   setContentType("application/pdf");
	   setFileName("Invoice.pdf");	   
	}
}
