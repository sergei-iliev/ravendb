package com.luee.wally.admin.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.luee.wally.admin.repository.ApplicationSettingsRepository;
import com.luee.wally.api.service.ApplicationSettingsService;
import com.luee.wally.api.service.UserService;
import com.luee.wally.constants.Constants;
import com.luee.wally.entity.User;


public class LoginController implements com.luee.wally.api.route.Controller{
	
	public void index(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
    	req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
	}
	
	public void login(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
        String email=(String)req.getParameter("email");
        String password=(String)req.getParameter("password");
        
        UserService userService=new UserService();
        User user=userService.getUser(email, password);
        if(user!=null){	          
          req.getSession().setAttribute("user",user);
      	  req.getSession().setMaxInactiveInterval(60*15);
      	  if(user.isAdmin()){	
      		  resp.sendRedirect("/administration");
      	  }else{
      		resp.sendRedirect("/administration/qa");
      	  }
		}else{
		  req.setAttribute("error","Wrong user email or password.");
		  req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
		}
		
	}

	public void logout(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
		
	}
}
