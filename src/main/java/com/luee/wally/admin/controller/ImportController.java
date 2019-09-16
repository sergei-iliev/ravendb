package com.luee.wally.admin.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.luee.wally.api.route.Controller;
import com.luee.wally.api.service.impex.ImportService;
import com.luee.wally.csv.PaidUsers2018;

public class ImportController implements Controller{
	private static final Logger logger = Logger.getLogger(ImportController.class.getName());
	
	public void importUserRevenue2019(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		
		
  	    Queue queue = QueueFactory.getDefaultQueue();
  	    queue.add(TaskOptions.Builder.withUrl("/administration/import/user/revenue/2019/background").method(Method.POST));
  	  
		req.setAttribute("success", "Job 2019 user revenue successfully posted.");				
		req.getRequestDispatcher("/jsp/user_revenue_2019.jsp").forward(req, resp);
	}

	public void importUserRevenue2019InBackground(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		ImportService importService = new ImportService();
		try{
		   Collection<PaidUsers2018> users2019EUR = importService.importCSVFile2019(ImportService.IMPORT_CSV_FILE_2019_eur_amount, true);
		   Collection<PaidUsers2018> users2019Currency = importService.importCSVFile2019(ImportService.IMPORT_CSV_FILE_2019_currency_amount,false);
		   
		}catch(Exception e){
			logger.log(Level.SEVERE, "import csv:", e);
		}
	}

}
