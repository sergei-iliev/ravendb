package com.luee.wally.admin.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class LoginController implements com.luee.wally.api.route.Controller{
	private static final String EMAIL="1@1";
	private static final String PASSWORD="1";
	
	public void index(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
    	req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
	}
	
	public void login(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
        String email=(String)req.getParameter("email");
        String password=(String)req.getParameter("password");
		if(email.equals(EMAIL)&&password.equals(PASSWORD)){
          req.getSession().setAttribute("login","ADMIN_USER");
      	  req.getSession().setMaxInactiveInterval(60*15);
  		  resp.sendRedirect("/administration");
		}else{
		  req.setAttribute("error","Wrong user name or password.");
		  req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
		}
        
        
        
		
	}

	public void logout(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
		
	}
}
