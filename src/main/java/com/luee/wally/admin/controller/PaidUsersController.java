package com.luee.wally.admin.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.luee.wally.api.route.Controller;
import com.luee.wally.api.service.PaidUsersService;
import com.luee.wally.command.PaidUserGroupByForm;
import com.luee.wally.command.PaidUserGroupByResult;
import com.luee.wally.command.PaidUserSearchForm;
import com.luee.wally.command.viewobject.PaidUserGroupByVO;

public class PaidUsersController implements Controller {
	private final Logger logger = Logger.getLogger(PaidUsersController.class.getName());


	public void index(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{

		if(req.getParameter("groupby")==null){
		  req.setAttribute("webform", new PaidUserSearchForm());
		  req.setAttribute("countries", this.getCountries());
		  req.getRequestDispatcher("/jsp/paid_users.jsp").forward(req, resp);
		}else{
			
			req.setAttribute("webform", new PaidUserGroupByForm());
			req.setAttribute("countries", this.getCountries());	
		  req.getRequestDispatcher("/jsp/paid_users_groupby.jsp").forward(req, resp);	
		}
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
		req.setAttribute("countries", this.getCountries());
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
		req.setAttribute("countries", this.getCountries());
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
		req.setAttribute("countries", this.getCountries());
		req.getRequestDispatcher("/jsp/paid_users.jsp").forward(req, resp);
	}
	
	public void searchGroupBy(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		PaidUserGroupByForm form=PaidUserGroupByForm.parse(req);
		PaidUsersService paidUsersService=new PaidUsersService();
		Collection<PaidUserGroupByVO> list=paidUsersService.searchGroupBy(form);
		
		Collection<PaidUserGroupByResult> groupBy=paidUsersService.groupBy(list,form.getGroupByType(),form.getGroupByTime(),form.getGroupByLocale());
		req.setAttribute("entities", groupBy);
		
		req.setAttribute("webform", form);
		req.setAttribute("countries", this.getCountries());
		req.getRequestDispatcher("/jsp/paid_users_groupby.jsp").forward(req, resp);
	}
}
