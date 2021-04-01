package com.luee.wally.admin.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.luee.wally.admin.repository.CloudStorageRepository;
import com.luee.wally.admin.repository.PaidUsersRepository;
import com.luee.wally.api.route.Controller;
import com.luee.wally.api.service.UserService;
import com.luee.wally.api.service.impex.ExportService;
import com.luee.wally.command.DeleteUserDataForm;
import com.luee.wally.entity.PaidUser;
import com.luee.wally.entity.PaidUserExternal;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.utils.Utilities;

public class UsersController implements Controller {
	private final Logger logger = Logger.getLogger(UsersController.class.getName());

	public void index(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.getRequestDispatcher("/jsp/delete_users_data.jsp").forward(req, resp);
	}

	public void deleteUserData(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		DeleteUserDataForm form = DeleteUserDataForm.parse(req);
		req.setAttribute("webform", form);
		if(form.getInput().trim().isEmpty()){
			req.setAttribute("error", "Input field is empty.");
			req.getRequestDispatcher("/jsp/delete_users_data.jsp").forward(req, resp);
			return;
		}
		
		
		UserService userService = new UserService();
		if (form.getSearchOption().equals("3")) { // gaid
			try{			
				int count=userService.deleteUserDataByGaid(form.getInput());
				String str=userService.convertNumberToText(count);
				req.setAttribute("message","GAID  '"+form.getInput()+"' user data was successfully deleted."+
				 " "+str+" user(s) affected.");
			}catch(IllegalAccessError e){
				req.setAttribute("error",e.getMessage());
			}
		}else if (form.getSearchOption().equals("2")) { // email
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
				req.setAttribute("message","GUID  '"+form.getInput()+"' user data was successfully deleted."+
				 " "+str+" user(s) affected.");
			}catch(IllegalAccessError e){
				req.setAttribute("error",e.getMessage());
			}
			
		}

		req.getRequestDispatcher("/jsp/delete_users_data.jsp").forward(req, resp);
	}
	public void userExists(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.getRequestDispatcher("/jsp/users_exist.jsp").forward(req, resp);
	}
	public void doUserExists(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String userGuids=req.getParameter("userGuids");
		String guids[] = userGuids.split("\r\n");

		ThreadManager.createBackgroundThread(new Runnable() {
			@Override
			public void run() {
				try {
					logger.log(Level.WARNING,
							"*************************START User Guid statistics********************");
                    UserService userService=new UserService();
                    Map<String,Long> existsGuidMap=userService.countUserGuids(Arrays.asList(guids));
                    Collection<String> notExistsGuids=new ArrayList<>();
                    for(String guid:guids){
                    	if(!existsGuidMap.containsKey(guid)){
                    		notExistsGuids.add(guid);
                    	}
                    }
					try (Writer writer = new StringWriter()) {
						userService.createUserGuidFile(writer,existsGuidMap,notExistsGuids);

						CloudStorageRepository cloudStorageRepository = new CloudStorageRepository();
						cloudStorageRepository.save(writer, "affs_user_guid_exists/report" + formatDate(new Date()));
					}
					logger.log(Level.WARNING,
							"*************************END User Guid statistics********************");					
				} catch (Exception e) {
					logger.log(Level.SEVERE, "user guid statistic:", e);
				}
			}
		}).start();		
		
		req.setAttribute("success", "User guid search successfully posted.");
		req.getRequestDispatcher("/jsp/users_exist.jsp").forward(req, resp);
	}

	
	
}
