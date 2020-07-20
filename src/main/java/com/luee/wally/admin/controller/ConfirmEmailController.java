package com.luee.wally.admin.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.tuple.Pair;

import com.luee.wally.api.route.Controller;
import com.luee.wally.api.service.ConfirmEmailService;
import com.luee.wally.api.service.PaidUsersService;
import com.luee.wally.command.PaidUserGroupByForm;
import com.luee.wally.command.PaidUserGroupByForm.GroupByType;
import com.luee.wally.command.PaidUserGroupByResult;
import com.luee.wally.command.PaidUserSearchForm;
import com.luee.wally.command.viewobject.PaidUserGroupByVO;
import com.luee.wally.entity.RedeemingRequests;

public class ConfirmEmailController implements Controller {
	private final Logger logger = Logger.getLogger(ConfirmEmailController.class.getName());


	public void index(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{	
	    req.getRequestDispatcher("/jsp/confirm_email.jsp").forward(req, resp);	
	}

	public void confirmEmail(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{	
	    String email=req.getParameter("email").trim();
	    if(email.isEmpty()){
	       req.setAttribute("error", "Email is not provided.");	
	       req.getRequestDispatcher("/jsp/confirm_email.jsp").forward(req, resp);	
	    }else{
			ConfirmEmailService confirmEmailService=new ConfirmEmailService();
			Collection<RedeemingRequests> entities=confirmEmailService.confirmEmail(email);
			Pair<String,Boolean> message=null;
			if(entities.size()==0){
			 	//message="No cash out requests found for this email address.";
			 	message=Pair.of("No cash out requests found for this email address.", Boolean.FALSE);
			}else{
				RedeemingRequests redeemingRequests=entities.iterator().next();
				if(redeemingRequests.isPaid()){
					 //message="Email address address was already paid for user guid: "+redeemingRequests.getUserGuid()+".";
					message=Pair.of("Email address was already paid for user guid: "+redeemingRequests.getUserGuid()+".", Boolean.FALSE);
					
				}else if(redeemingRequests.isConfirmedEmail()){
					//message="Email address was already confirmed in latest cash out request for user guid: "+redeemingRequests.getUserGuid()+".";
					message=Pair.of("Email address was already confirmed in latest cash out request for user guid: "+redeemingRequests.getUserGuid()+".", Boolean.FALSE);
				}else{
					confirmEmailService.setConfirmedEmail(redeemingRequests.getKey(),true);
					//message="Email confirmed for user guid:  "+redeemingRequests.getUserGuid()+".";
					message=Pair.of("Email confirmed successfully for user guid:  "+redeemingRequests.getUserGuid()+".", Boolean.TRUE);

				}
			}
			req.setAttribute("message", message);
	    	req.getRequestDispatcher("/jsp/confirm_email.jsp").forward(req, resp);
	    }
	}
}
