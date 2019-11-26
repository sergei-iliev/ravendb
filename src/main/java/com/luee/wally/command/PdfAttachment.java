package com.luee.wally.command;

public class PdfAttachment extends Attachment {

	public PdfAttachment() {
	   setContentType("application/pdf");
	   setFileName("Invoice.pdf");	   
	}
}
