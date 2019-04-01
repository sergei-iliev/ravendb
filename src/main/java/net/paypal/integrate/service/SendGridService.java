package net.paypal.integrate.service;

import java.io.IOException;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.sendgrid.Content;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import net.paypal.integrate.api.Constants;

import net.paypal.integrate.command.Email;

@Service
public class SendGridService {
	private final Logger logger = Logger.getLogger(SendGridService.class.getName());
	
	public void sendMail(Email email) throws IOException{
		    com.sendgrid.Email from = new com.sendgrid.Email(email.getFrom());
		    com.sendgrid.Email to = new com.sendgrid.Email(email.getTo());
		    
		    Content content = new Content("text/html", email.getContent());
		    
		    Mail mail = new Mail(from, email.getSubject(), to, content);
  
		    SendGrid sg = new SendGrid(Constants.SENDGRID_API_KEY);
		    Request request = new Request();
		    System.out.println(from.getEmail() );
		    System.out.println(to.getEmail() );
		    
		    request.setMethod(Method.POST);
		    request.setEndpoint("mail/send");
		    request.setBody(mail.build());
		    
		    Response response = sg.api(request);		   
	}
}
