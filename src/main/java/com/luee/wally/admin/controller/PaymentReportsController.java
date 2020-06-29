package com.luee.wally.admin.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.luee.wally.admin.repository.ApplicationSettingsRepository;
import com.luee.wally.api.route.Controller;
import com.luee.wally.api.service.ApplicationSettingsService;
import com.luee.wally.api.service.MailService;
import com.luee.wally.api.service.PaymentReportsService;
import com.luee.wally.command.Email;
import com.luee.wally.entity.PaymentAmount;
import com.luee.wally.utils.Utilities;

public class PaymentReportsController implements Controller{
	private final Logger logger = Logger.getLogger(PaymentReportsController.class.getName());
	
	public void getPaymentReportForYesterday(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
  	    logger.log(Level.WARNING, "***********************Start Payment Reports Job ********************");

		PaymentReportsService paymentReportsService=new PaymentReportsService();
		PaymentAmount paymentAmount=paymentReportsService.getPaymentReportForYesterday();		
		
		 paymentReportsService.sendReportToMail(paymentAmount);
		 
		 logger.log(Level.WARNING, "*************************End Payment Reports Job ********************");
	}
	
	
	public void getExternalPaymentReportForYesterday(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
  	    logger.log(Level.WARNING, "***********************Start External Payment Reports Job ********************");

		PaymentReportsService paymentReportsService=new PaymentReportsService();
		PaymentAmount paymentAmount=paymentReportsService.getExternalPaymentReportForYesterday();
		
		paymentReportsService.sendReportToMail(paymentAmount);
		 
		 logger.log(Level.WARNING, "*************************End External Payment Reports Job ********************");
	}

}
