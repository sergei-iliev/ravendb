package com.luee.wally.admin.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.luee.wally.api.route.Controller;
import com.luee.wally.api.service.GiftCardService;


public class GiftCardController implements Controller{
	private final Logger logger = Logger.getLogger(GiftCardController.class.getName());	
	
	
	public void checkGiftCardAccountBalanceJob(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{                 
  	    logger.log(Level.WARNING, "***********************Start Tango Card Account Balance Job ********************");

		GiftCardService giftCardService=new GiftCardService();
		giftCardService.checkGiftCardAccountBalance();
		 
		logger.log(Level.WARNING, "*************************End Tango Card Account Balance Job ********************");        		
	}

	
}
