package net.paypal.integrate.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import net.paypal.integrate.api.Constants;
import net.paypal.integrate.command.Attachment;
import net.paypal.integrate.command.Email;
import net.paypal.integrate.service.MailService;
import net.paypal.integrate.service.SendGridService;

@Controller
@RequestMapping("/email")
public class EmailController {
	
	private final Logger logger = Logger.getLogger(EmailController.class.getName());

	@Autowired
	private MailService mailService;
	
	@Autowired
	private SendGridService sendGridService;
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ModelAndView start() throws IOException {
		   
		    //mailService.sendMail("sergei_iliev@yahoo.com", "Hello man", "This must be my first test");
		  return new ModelAndView("email", "email", new Email());					
	}
	
	@RequestMapping(value = "/send", method = RequestMethod.POST,consumes = "multipart/form-data" )
	public ModelAndView email(@RequestParam("attachmentFile") MultipartFile multipart,@Valid @ModelAttribute("email")Email email,BindingResult result, ModelMap model) throws IOException {
		
		Attachment attachment= mailService.readAttachment(multipart);
        email.setAttachment(attachment);
        if(attachment==null){
           mailService.sendMail(email);	
        }else{
           mailService.sendMultipartMail(email);	
        }
		if (result.hasErrors()) {
			model.addAttribute("error",true);
			return new ModelAndView("email", "email", email);
	    }
		
		return new ModelAndView("redirect:/email");
		
	}

	@RequestMapping(value = "sendgrid", method = RequestMethod.GET)
	public ModelAndView sendgrid() throws IOException {
		   
		    
		  return new ModelAndView("sendgrid", "email", new Email());					
	}

	
	@RequestMapping(value = "sendgrid/send", method = RequestMethod.POST)
	public ModelAndView email(@Valid @ModelAttribute("email")Email email,BindingResult result, ModelMap model) throws IOException {
		
        email.setTo(Constants.fromMail);
        email.setFrom(Constants.fromMail);
        email.setSubject("Cool email from SendGrid");
        email.setContent(Constants.DEMO_HTML);
        
        email.setToName("To Name");
        email.setFromName("From Name");
        try{
        	sendGridService.sendMail(email,Constants.ccMail);	
        }catch(IOException e){
        	logger.log(Level.SEVERE,"sendgrid", e);
        }
		if (result.hasErrors()) {
			model.addAttribute("error",true);
			return new ModelAndView("sendgrid", "email", email);
	    }
		
		return new ModelAndView("redirect:/email/sendgrid");
		
	}
}
