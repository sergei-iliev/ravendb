package com.paypal.integrate.admin.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.paypal.integrate.admin.api.route.Controller;

public class AdminController implements Controller{

	public void index(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		req.getRequestDispatcher("/jsp/index.jsp").forward(req, resp);
	}

}
