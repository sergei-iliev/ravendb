package com.luee.wally.admin.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.luee.wally.api.route.Controller;
import com.luee.wally.api.service.PaidUsersService;
import com.luee.wally.command.PaidUserSearchForm;
import com.luee.wally.entity.PaidUser;

public class PaidUsersController implements Controller {
	private final Logger logger = Logger.getLogger(PaidUsersController.class.getName());


	public void index(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		PaidUserSearchForm form = new PaidUserSearchForm();

		req.setAttribute("webform", form);
		req.getRequestDispatcher("/jsp/paid_users.jsp").forward(req, resp);
	}

	public void search(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		
		PaidUserSearchForm form=PaidUserSearchForm.parse(req);
		form.setActiveTab(1);
		
		if(form.getStartDate()==null&&form.getEndDate()==null){
		}else{	
		  PaidUsersService paidUsersService=new PaidUsersService();
		  req.setAttribute("entities", paidUsersService.search(form));
		}
		
		req.setAttribute("webform", form);
		req.getRequestDispatcher("/jsp/paid_users.jsp").forward(req, resp);
	}
	
	public void searchByEmail(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		
		PaidUserSearchForm form=PaidUserSearchForm.parse(req);
		form.setActiveTab(2);
		if(form.getEmail()==null){
			req.setAttribute("entities",null);
		}else{	
		  PaidUsersService paidUsersService=new PaidUsersService();
		  req.setAttribute("entities", paidUsersService.searchByEmail(form));
		}

		req.setAttribute("webform", form);
		req.getRequestDispatcher("/jsp/paid_users.jsp").forward(req, resp);
	}
	
	public void searchByGuid(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		
		PaidUserSearchForm form=PaidUserSearchForm.parse(req);
		form.setActiveTab(3);
		if(form.getUserGuid()==null){
		  req.setAttribute("entities",null);
		}else{	
		  PaidUsersService paidUsersService=new PaidUsersService();
		  req.setAttribute("entities", paidUsersService.searchByGuid(form));
		}
		
		req.setAttribute("webform", form);
		req.getRequestDispatcher("/jsp/paid_users.jsp").forward(req, resp);
	}
	
}
