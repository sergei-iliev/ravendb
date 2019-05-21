package net.paypal.integrate.controller;

import java.io.IOException;
import java.util.logging.Logger;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import net.paypal.integrate.api.ShardedSequence;
import net.paypal.integrate.entity.PayPalUser;
import net.paypal.integrate.repository.ObjectifyRepository;
import net.paypal.integrate.service.MailService;

@Controller
@RequestMapping("/account")
public class AccountController {
	
	private final Logger logger = Logger.getLogger(AccountController.class.getName());

	@Autowired
	private MailService mailService;
	
	@Autowired
	private ObjectifyRepository objectifyRepository;
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ModelAndView start(ModelMap model) throws IOException {
		  model.addAttribute("accounts",objectifyRepository.getAccountList());
		  model.addAttribute("payments",objectifyRepository.getPaymentList());
		  return new ModelAndView("accounts", "payPalUser", new PayPalUser());					
	}

	@RequestMapping(value = "delete/{id}", method = RequestMethod.GET)
	public String delete(@PathVariable("id") long id) {
		objectifyRepository.delete(id);
		return "redirect:/account";
	}
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ModelAndView email(@Valid @ModelAttribute("payPalUser")PayPalUser payPalUser,BindingResult result, ModelMap model) throws IOException {
		if (result.hasErrors()) {
			model.addAttribute("error",true);
			return new ModelAndView("accounts", "payPalUser", payPalUser);
	    }
		objectifyRepository.save(payPalUser);
				

		model.addAttribute("accounts",objectifyRepository.getAccountList());
		model.addAttribute("payments",objectifyRepository.getPaymentList());
		return new ModelAndView("accounts", "payPalUser", new PayPalUser());	
		
	}

}
