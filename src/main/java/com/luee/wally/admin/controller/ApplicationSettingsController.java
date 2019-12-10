package com.luee.wally.admin.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.luee.wally.api.route.Controller;
import com.luee.wally.api.service.ApplicationSettingsService;
import com.luee.wally.api.service.MailService;
import com.luee.wally.api.service.PaymentReportsService;
import com.luee.wally.command.Email;
import com.luee.wally.constants.Constants;
import com.luee.wally.entity.PaymentAmount;
import com.luee.wally.utils.Utilities;

public class ApplicationSettingsController implements Controller{
	private final Logger logger = Logger.getLogger(ApplicationSettingsController.class.getName());
	
	public void index(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.getRequestDispatcher("/jsp/application_settings.jsp").forward(req, resp);
	}
	public void clearCache(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ApplicationSettingsService applicationSettingsService=new ApplicationSettingsService();
		applicationSettingsService.clearCache();
		
		req.setAttribute("success", "Memcache successfully purged!");
		req.getRequestDispatcher("/jsp/application_settings.jsp").forward(req, resp);
	}
	
}
