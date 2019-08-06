package com.paypal.integrate.admin.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.paypal.integrate.admin.api.route.Controller;

public class AdminController implements Controller{

	public void index(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
	    
		req.setAttribute("countries", this.getCountries());
		req.getRequestDispatcher("/jsp/index.jsp").forward(req, resp);
	}
	
	public void search(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		System.out.println(req.getParameter("startDate")); 
		System.out.println(req.getParameter("endDate")); 
		System.out.println(req.getParameter("country")); 
		System.out.println(req.getParameter("experiment")); 
		System.out.println(req.getParameter("packageName")); 
		
		req.setAttribute("countries", this.getCountries());
		req.getRequestDispatcher("/jsp/index.jsp").forward(req, resp);
	}
	
	private Collection<String> getCountries(){
		String[] locales = Locale.getISOCountries();
		Collection<String> countries=new ArrayList<String>();
		countries.add("");
		for (String countryCode : locales) {
			Locale locale = new Locale("", countryCode);
			countries.add(locale.getCountry());
		}

		return countries;
	}
	
	private Collection<String> getPackagesName(){
		return Arrays.asList("com.moregames.makemoney", "com.coinmachine.app",
		"com.matchmine.app");		
	}

}
