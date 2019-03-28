package net.paypal.integrate.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.Payout;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import net.paypal.integrate.command.Attachment;
import net.paypal.integrate.command.Email;
import net.paypal.integrate.entity.PayPalUser;
import net.paypal.integrate.service.MailService;
import net.paypal.integrate.service.PayPalService;

@Controller
@RequestMapping("/email")
public class EmailController {
	
	private final Logger logger = Logger.getLogger(EmailController.class.getName());

	@Autowired
	private MailService mailService;
	
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

}
