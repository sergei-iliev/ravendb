package net.paypal.integrate.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

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

import net.paypal.integrate.api.Constants;
import net.paypal.integrate.command.Attachment;
import net.paypal.integrate.command.Email;
import net.paypal.integrate.command.PayoutForm;
import net.paypal.integrate.command.PdfAttachment;
import net.paypal.integrate.command.invoice.Payee;
import net.paypal.integrate.command.invoice.PayoutResult;
import net.paypal.integrate.entity.PayPalPayment;
import net.paypal.integrate.entity.PayPalUser;
import net.paypal.integrate.service.InvoiceService;
import net.paypal.integrate.service.MailService;
import net.paypal.integrate.service.ObjectifyRepository;
import net.paypal.integrate.service.PayPalService;

@Controller
@RequestMapping("/payout")
public class PayoutController {
	
	private final Logger logger = Logger.getLogger(PayoutController.class.getName());

	@Autowired
	private PayPalService payPalService;
	@Autowired
	private MailService mailService;
	@Autowired
	private InvoiceService invoiceService;
	@Autowired
	private ObjectifyRepository objectifyRepository;
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ModelAndView start() throws IOException {
		  return new ModelAndView("payout", "payoutForm", new PayoutForm());					
	}
	/*
	 * Recieve id of a registered pay pal user
	 * 1.Find user by id
	 * 2.Payout
	 * 3.Payment record
	 * 4.Create Invoice
	 * 5.Send to admin
	 */
	@RequestMapping(value = "pay/{id}", method = RequestMethod.GET)
	public ModelAndView pay(@PathVariable("id") long id, ModelMap model) {
		PayPalUser payPalUser=  objectifyRepository.getPayPalUserById(id);
		if(payPalUser==null){
			model.addAttribute("error",true);
			return new ModelAndView("redirect:/account"); 
		}
		try{
	          PayoutResult payoutResult= payPalService.payout(payPalUser);
	          String invoiceNumber=invoiceService.generateInvoiceNumber(payPalUser);
	          
	          System.out.println(invoiceNumber);
              
	          PayPalPayment payPalPayment=new PayPalPayment(payPalUser);
	          payPalPayment.setCreditNoteNo(invoiceNumber);
	          payPalPayment.setPaypalTransactionId(payoutResult.getPayoutBatchId());
	          objectifyRepository.save(payPalPayment);
	          
	          PdfAttachment attachment=new PdfAttachment();
	          attachment.readFromStream(invoiceService.createInvoice(payoutResult,payPalUser,invoiceNumber));   
	          	          
	          mailService.sendInvoice(Constants.toInvoiceMail,attachment);
	          
	          
			}catch(Exception e){
				logger.log(Level.SEVERE,"payout:", e);
				//send alert email message
				Email email=new Email();
				email.setSubject("Error alert!");
				email.setContent(e.getMessage());
				email.setFrom(Constants.fromMail);
				email.setTo(Constants.toInvoiceMail);
				mailService.sendMail(email);
				
				model.addAttribute("error",true);
				new ModelAndView("redirect:/account");		
		    }
		
		return new ModelAndView("redirect:/account");
	}
	
//	@RequestMapping(value = "/send", method = RequestMethod.POST)
//	public ModelAndView email(@Valid @ModelAttribute("payoutForm")PayoutForm payoutForm,BindingResult result, ModelMap model) throws IOException {
//        
//        
//        if (result.hasErrors()) {
//			model.addAttribute("error",true);
//			return new ModelAndView("payout", "payoutForm", payoutForm);
//	    }
//
//		try{
//          PayoutResult payoutResult= payPalService.payout(payoutForm);
//          Payee payee=Payee.create(payoutForm.getReceiver(),payoutForm.getCurrency());
//                    
//          
//          PdfAttachment attachment=new PdfAttachment();
//          attachment.readFromStream(invoiceService.createInvoice(payoutResult,payee,"2017111111"));   
//          payoutForm.setAttachment(attachment);
//          
//          mailService.sendInvoice(payoutForm);
//          
//		}catch(Exception e){
//			logger.log(Level.SEVERE,"payout:", e);	
//			model.addAttribute("error",true);
//			return new ModelAndView("payout", "payoutForm", payoutForm);			
//		}
//		return new ModelAndView("redirect:/payout");
//		
//	}

}
