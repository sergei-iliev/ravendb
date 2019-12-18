package com.luee.wally.admin.controller;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.luee.wally.api.route.Controller;
import com.luee.wally.command.PaidUserSearchForm;

public class PaidUsersController implements Controller {
	private final Logger logger = Logger.getLogger(PaidUsersController.class.getName());


	public void index(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		PaidUserSearchForm form = new PaidUserSearchForm();

		req.setAttribute("webform", form);
		req.getRequestDispatcher("/jsp/paid_users.jsp").forward(req, resp);
	}
	
}
