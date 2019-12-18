package usecase;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Test;

import com.luee.wally.api.service.MailService;
import com.luee.wally.command.PdfAttachment;
import com.luee.wally.constants.Constants;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

public class MailServiceTest {

	private PdfAttachment createAttachment()throws Exception{
		File file=new File("d:\\S1.pdf");
		PdfAttachment attachment=new PdfAttachment();
        attachment.setFileName("S1.pdf");
        attachment.setContentType("application/pdf");          
        attachment.setBuffer(Files.readAllBytes(file.toPath()));
        System.out.println(attachment.getBuffer().length);         

        return attachment;
	}
	public static Mail buildHelloEmail() throws IOException {
	    Email from = new Email("sergei_iliev@yahoo.com");
	    String subject = "Hello World from the Twilio SendGrid Java Library";
	    Email to = new Email("sergei.iliev@gmail.com");
	    Content content = new Content("text/plain", "some text here");
	    // Note that when you use this constructor an initial personalization object
	    // is created for you. It can be accessed via
	    // mail.personalization.get(0) as it is a List object
	    Mail mail = new Mail(from, subject, to, content);
//	    Email email = new Email("test2@example.com");
//	    mail.personalization.get(0).addTo(email);

	    return mail;
	 }
	@Test
	public void sendGridMailAttachmentTest() throws Exception{
		MailService mailService=new MailService();
		mailService.sendGridInvoice("sergei.iliev@gmail.com","sergei_iliev@yahoo.com", createAttachment());		
	}
	
	@Test
	public void sendGridHelloWorldTest() throws Exception{
		SendGrid sg = new SendGrid(Constants.SENDGRID_API_KEY);
	    sg.addRequestHeader("X-Mock", "true");

	    Request request = new Request();
	    Mail helloWorld = buildHelloEmail();

	      request.setMethod(Method.POST);
	      request.setEndpoint("mail/send");
	      request.setBody(helloWorld.build());
	      Response response = sg.api(request);
	      System.out.println(response.getStatusCode());
	      System.out.println(response.getBody());
	      System.out.println(response.getHeaders());

	}
	
}
