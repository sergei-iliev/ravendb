package net.paypal.integrate.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.api.client.util.DateTime;
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
import net.paypal.integrate.command.csv.PaidUsers2018;
import net.paypal.integrate.entity.RedeemingRequests;
import net.paypal.integrate.repository.CloudStorageRepository;
import net.paypal.integrate.repository.ImpexRepository;
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
	private ImpexRepository impexRepository;


	@RequestMapping(value = "", method = RequestMethod.GET)
	public ModelAndView start(ModelMap model) throws IOException {

		// cloudStorageRepository.saveFile();
		model.addAttribute("count", impexRepository.count());
		return new ModelAndView("import");
	}

	@RequestMapping(value = "csv/RedeemingRequests", method = RequestMethod.GET)
	public ModelAndView importFile(ModelMap model) throws IOException {
		model.addAttribute("count", impexRepository.count());
		String prefix = "2018111111";
		int count = 1;
		try {
			Collection<PaidUsers2018> users = importService.importCSVFile();
			for (PaidUsers2018 paidUsers2018 : users) {
				
				RedeemingRequests redeemingRequests = impexRepository.findByUserGuid(paidUsers2018.getUserGuid());
				if (redeemingRequests != null) {
					// create pdf in cloud store							
					importService.createPDFInCloudStore(redeemingRequests, paidUsers2018,
							prefix + String.valueOf(count++));
				} else {
					logger.log(Level.SEVERE, "No user entry found for - " + paidUsers2018.getUserGuid());
				}

				//if(count%2==0){
				//	break;
				//}
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, "import csv:", e);
			model.addAttribute("message", e.getMessage());
			model.addAttribute("error", true);
			return new ModelAndView("import", model);
		}

		return new ModelAndView("import");
	}

	@RequestMapping(value = "generateDemoRedeemingRequests", method = RequestMethod.GET)
	public ModelAndView generateData(ModelMap model) throws IOException {
		try {
			Collection<PaidUsers2018> users = importService.importCSVFile();
			importService.generateData(users);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "import csv:", e);
			model.addAttribute("message", e.getMessage());
			model.addAttribute("error", true);
			return new ModelAndView("import", model);
		}

		model.addAttribute("count", impexRepository.count());
		return new ModelAndView("import");
	}

	@RequestMapping(value = "createDemoPDFInCloudStore", method = RequestMethod.GET)
	public ModelAndView createDemoPDFInCloudStore(ModelMap model) throws IOException {
		try {

			importService.createDemoPDFInCloudStore();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "pdf demo create:", e);
			model.addAttribute("message", e.getMessage());
			model.addAttribute("error", true);
			return new ModelAndView("import", model);
		}
		model.addAttribute("count", impexRepository.count());
		return new ModelAndView("import");
	}

}
