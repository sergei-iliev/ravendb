package com.luee.wally.paypal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.fasterxml.jackson.core.JsonFactory;
import com.luee.wally.command.Attachment;
import com.luee.wally.constants.Constants;
import com.sendgrid.Content;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;






public class MailService {
	private final Logger logger = Logger.getLogger(MailService.class.getName());
    
	/*
	public Attachment readAttachment(MultipartFile file) throws IOException{
	        if (file.isEmpty()) {
		        return null;   
	        }
	        Attachment attachment=new Attachment();
	        attachment.setFileName(file.getOriginalFilename());
	        attachment.setContentType(file.getContentType());
            attachment.readFromStream(file.getInputStream());

            return attachment;
	}*/

	public void sendInvoice(String emailTo,Attachment attachment) {
	    Properties props = new Properties();
	    Session session = Session.getDefaultInstance(props, null);

	    try {
	      Message msg = new MimeMessage(session);
	      msg.setFrom(new InternetAddress(Constants.fromMail, "Admin"));
	      msg.addRecipient(Message.RecipientType.TO,
	                       new InternetAddress(Constants.toInvoiceMail, "Mr. User"));
	      msg.setSubject("Payout invoice");
	      msg.setText("You have an invoice");

	      
	
 
	      Multipart mp = new MimeMultipart();

	      MimeBodyPart mimeBodyPart = new MimeBodyPart();
	      InputStream attachmentDataStream = new ByteArrayInputStream(attachment.getBuffer());
	      mimeBodyPart.setFileName(attachment.getFileName());
	      mimeBodyPart.setContent(attachmentDataStream,attachment.getContentType());
	      mp.addBodyPart(mimeBodyPart);

	      msg.setContent(mp);

	      Transport.send(msg);
	      logger.log(Level.INFO,"Email was sent to: "+attachment.getContentType()+"::"+attachment.getFileName()+"::"+attachment.getBuffer().length);
	    } catch (AddressException e) {
	    	logger.log(Level.SEVERE,"email",e);
	    } catch (MessagingException e) {
	    	logger.log(Level.SEVERE,"email",e);
	    } catch (UnsupportedEncodingException e) {
	    	logger.log(Level.SEVERE,"email",e);
	    }
	 }
	/*
	public void sendInvoice(PayoutForm form) {
	    Properties props = new Properties();
	    Session session = Session.getDefaultInstance(props, null);

	    try {
	      Message msg = new MimeMessage(session);
	      msg.setFrom(new InternetAddress(Constants.fromMail, "Admin"));
	      msg.addRecipient(Message.RecipientType.TO,
	                       new InternetAddress("sergei_iliev@yahoo.com", "Mr. User"));
	      msg.setSubject("Payout invoice");
	      msg.setText("You have an invoice");

	      
	
 
	      Multipart mp = new MimeMultipart();

	      MimeBodyPart attachment = new MimeBodyPart();
	      InputStream attachmentDataStream = new ByteArrayInputStream(form.getAttachment().getBuffer());
	      attachment.setFileName(form.getAttachment().getFileName());
	      attachment.setContent(attachmentDataStream, form.getAttachment().getContentType());
	      mp.addBodyPart(attachment);

	      msg.setContent(mp);

	      Transport.send(msg);
	      logger.log(Level.INFO,"Email was sent to: "+form.getAttachment().getContentType()+"::"+form.getAttachment().getFileName()+"::"+form.getAttachment().getBuffer().length);
	    } catch (AddressException e) {
	    	logger.log(Level.SEVERE,"email",e);
	    } catch (MessagingException e) {
	    	logger.log(Level.SEVERE,"email",e);
	    } catch (UnsupportedEncodingException e) {
	    	logger.log(Level.SEVERE,"email",e);
	    }
	 }	
*/
	
	public void sendMultipartMail(Email email) {
	    Properties props = new Properties();
	    Session session = Session.getDefaultInstance(props, null);

	

	    try {
	      Message msg = new MimeMessage(session);
	      msg.setFrom(new InternetAddress(Constants.fromMail, "Admin"));
	      msg.addRecipient(Message.RecipientType.TO,
	                       new InternetAddress(email.getTo(), "Mr. User"));
	      msg.setSubject(email.getSubject());
	      msg.setText(email.getContent());

	       
	      Multipart mp = new MimeMultipart();

	      MimeBodyPart attachment = new MimeBodyPart();
	      InputStream attachmentDataStream = new ByteArrayInputStream(email.getAttachment().getBuffer());
	      attachment.setFileName(email.getAttachment().getFileName());
	      attachment.setContent(attachmentDataStream, email.getAttachment().getContentType());
	      mp.addBodyPart(attachment);

	      msg.setContent(mp);

	      Transport.send(msg);
	      logger.log(Level.INFO,"Email was sent to: "+email.getAttachment().getContentType()+"::"+email.getAttachment().getFileName()+"::"+email.getAttachment().getBuffer().length);

	    } catch (AddressException e) {
	    	logger.log(Level.SEVERE,"email",e);
	    } catch (MessagingException e) {
	    	logger.log(Level.SEVERE,"email",e);
	    } catch (UnsupportedEncodingException e) {
	    	logger.log(Level.SEVERE,"email",e);
	    }
	  }	
	
	public void sendMailGrid(Email email) throws IOException{
		
	    com.sendgrid.Email from = new com.sendgrid.Email(email.getFrom());
	    from.setName(email.getFromName());
	    
	    
	    com.sendgrid.Email to = new com.sendgrid.Email();
	    
	    Personalization personalization = new Personalization();
	    to.setName(email.getToName());
	    to.setEmail(email.getTo());
	    personalization.addTo(to);
	   
	    if(email.getCC()!=null) {
	    com.sendgrid.Email cc = new com.sendgrid.Email();
	    cc.setEmail(email.getCC());
	    cc.setName(email.getCCName());
	    personalization.addCc(cc);
	    }
	    
	    
	    Content content = new Content("text/html", email.getContent());
	    
	    Mail mail = new Mail();
	    mail.setFrom(from);
	    mail.setSubject(email.getSubject());	    
	    mail.addPersonalization(personalization);
	    mail.addContent(content);
	    
	    SendGrid sg = new SendGrid(Constants.SENDGRID_API_KEY);
	    Request request = new Request();

	    
	    request.setMethod(Method.POST);
	    request.setEndpoint("mail/send");
	    request.setBody(mail.build());
	    
	    Response response = sg.api(request);		    
	    logger.log(Level.INFO, response.getBody());
}
	
	public void sendMail(Email email) {
	    Properties props = new Properties();
	    Session session = Session.getDefaultInstance(props, null);

	    try {
	      Message msg = new MimeMessage(session);
	      msg.setFrom(new InternetAddress(Constants.fromMail, "Admin"));
	      msg.addRecipient(Message.RecipientType.TO,
	                       new InternetAddress(email.getTo(), "Mr. User"));
	      msg.setSubject(email.getSubject());
	      msg.setText(email.getContent());
	      Transport.send(msg);
	    } catch (AddressException e) {
	      logger.log(Level.SEVERE,"email",e);
	    } catch (MessagingException e) {
	    	logger.log(Level.SEVERE,"email",e);
	    } catch (UnsupportedEncodingException e) {
	    	logger.log(Level.SEVERE,"email",e);
	    }		
	}
}
