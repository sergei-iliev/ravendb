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
import net.paypal.integrate.repository.ExportRepository;
import net.paypal.integrate.service.ExportService;
import net.paypal.integrate.service.PayPalService;

@Controller
@RequestMapping("/export")
public class ExportController {
	
	private final Logger logger = Logger.getLogger(ExportController.class.getName());

	@Autowired
	private ExportService exportService; 

	@Autowired
	private ExportRepository exportRepository;
	
	@RequestMapping(value = "RedeemingRequestsNew", method = RequestMethod.GET)
	public void RedeemingRequestsNew(HttpServletResponse response,HttpServletRequest request) throws IOException {
		  Timestamp startDate=Timestamp.parseTimestamp("2018-05-17T10:15:30Z");		
		  Collection<RedeemingRequests> list= exportService.find(false,"BG","com.moregames.makemoney",true,"Amazon","20",startDate);
		
		 System.out.println(list.size());
		  response.setContentType("text/csv");
		  response.setHeader("Content-Disposition", "attachment; filename=\"RedeemingRequestsNew.csv\"");
		   
		  exportService.convertToCSV(response.getWriter(),list);
		    
//		response.setContentType("application/pdf");
//		response.setHeader("Content-Disposition", "attachment; filename=\"test.pdf\"");
//		InputStream inputStream = new FileInputStream(new File("D:\\8.pdf"));
//		   int nRead;
//		   while ((nRead = inputStream.read()) != -1) {
//		       response.getWriter().write(nRead);
//		   }	
	}

	@RequestMapping(value = "generateData", method = RequestMethod.GET)
	public ModelAndView generateData(ModelMap model) throws IOException {
		exportService.generateData();
		model.addAttribute("count", exportRepository.count());
		return new ModelAndView("export");
	}

	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ModelAndView start(ModelMap model) throws IOException {		    
		
//		Timestamp d=Timestamp.parseTimestamp("2019-05-17T10:15:30Z");
//		
//		Collection<RedeemingRequestsNew> list= exportService.find(true,"US","com.moregames.makemoney",false,"Amazon","20",d);
//		for(RedeemingRequestsNew x:list){
//			System.out.println(x);
//		}
		
		model.addAttribute("count", exportRepository.count());
		return new ModelAndView("export");		
	}

}
