package net.paypal.integrate.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.appengine.api.datastore.DataTypeUtils;
import com.google.cloud.Timestamp;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import net.paypal.integrate.api.Constants;
import net.paypal.integrate.entity.RedeemingRequests;
import net.paypal.integrate.repository.CloudStorageRepository;
import net.paypal.integrate.repository.ExportRepository;
import net.paypal.integrate.service.ExportService;
import net.paypal.integrate.service.ImportService;
import net.paypal.integrate.service.PayPalService;

@Controller
@RequestMapping("/import")
public class ImportController {
	
	private final Logger logger = Logger.getLogger(ImportController.class.getName());

	@Autowired
	private ImportService importService; 

	
    @Autowired
	private CloudStorageRepository cloudStorageRepository;
    
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ModelAndView start(ModelMap model) throws IOException {		    
		
		//cloudStorageRepository.saveFile();
		try{
		importService.createDemoPDF();
		}catch(Exception e){
			logger.log(Level.SEVERE,"pdf demo create:", e);
			model.addAttribute("error",e.getMessage());
			return new ModelAndView("import");	
		}
		return new ModelAndView("import");		
	}
	
	@RequestMapping(value = "import/csv", method = RequestMethod.GET)
	public ModelAndView importFile(ModelMap model) throws IOException {		    
		
		
		return new ModelAndView("import");		
	}
	
//	@RequestMapping(value = "import/csv", method = RequestMethod.GET)
//	public ModelAndView importFile(ModelMap model) throws IOException {		    
//		
//		
//		return new ModelAndView("import");		
//	}

}
