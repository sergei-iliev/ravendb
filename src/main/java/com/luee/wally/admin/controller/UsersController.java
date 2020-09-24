package com.luee.wally.admin.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.luee.wally.api.route.Controller;
import com.luee.wally.api.service.UserService;
import com.luee.wally.command.DeleteUserDataForm;

public class UsersController implements Controller {
	private final Logger logger = Logger.getLogger(UsersController.class.getName());

	public void index(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.getRequestDispatcher("/jsp/delete_users_data.jsp").forward(req, resp);
	}

	public void deleteUserData(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		DeleteUserDataForm form = DeleteUserDataForm.parse(req);
		req.setAttribute("webform", form);
		if(form.getInput().trim().isEmpty()){
			req.setAttribute("error", "Email address was not found in our system.");
			req.getRequestDispatcher("/jsp/delete_users_data.jsp").forward(req, resp);
			return;
		}
		
		
		UserService userService = new UserService();
		if (form.getSearchOption().equals("2")) { // email
			try{
				int count=userService.deleteUserDataByEmail(form.getInput());
				String str=userService.convertNumberToText(count);
				req.setAttribute("message","Email address '"+form.getInput()+"' user data was successfully deleted."+
				 " "+str+" user(s) affected");
			}catch(IllegalAccessError e){
				req.setAttribute("error",e.getMessage());
			}
		} else {
			try{
				int count=userService.deleteUserDataByGuid(form.getInput());				
				String str=userService.convertNumberToText(count);
				req.setAttribute("message","Email address '"+form.getInput()+"' user data was successfully deleted."+
				 " "+str+" user(s) affected.");
			}catch(IllegalAccessError e){
				req.setAttribute("error",e.getMessage());
			}
			
		}

		req.getRequestDispatcher("/jsp/delete_users_data.jsp").forward(req, resp);
	}
	
	
}
