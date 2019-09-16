package com.luee.wally.paypal;

import com.luee.wally.command.Attachment;

public class PdfAttachment extends Attachment {

	public PdfAttachment() {
	   setContentType("application/pdf");
	   setFileName("Invoice.pdf");	   
	}
}
