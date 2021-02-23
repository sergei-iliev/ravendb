package com.luee.wally.admin.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Entity;
import com.luee.wally.admin.repository.AffsRepository;
import com.luee.wally.admin.repository.UserRepository;
import com.luee.wally.command.ExperimentForm;


public class QAController implements com.luee.wally.api.route.Controller{
	
	public void index(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
    	req.getRequestDispatcher("/jsp/qa/index.jsp").forward(req, resp);
	}
	
	public void experiment(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		ExperimentForm form=ExperimentForm.parse(req);
		
		req.setAttribute("webform", form);
    	req.getRequestDispatcher("/jsp/qa/qa_experiment.jsp").forward(req, resp);
	}
	public void doExperiment(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		UserRepository userRepository=new UserRepository();
		ExperimentForm form=ExperimentForm.parse(req);
		if(form.getUserGuid()==null||form.getExperiment()==null){
			req.setAttribute("error", "Form input params are required.");
			req.setAttribute("webform", form);
	    	req.getRequestDispatcher("/jsp/qa/qa_experiment.jsp").forward(req, resp);
	    	return;
		}
		if(req.getParameter("submit").equals("set-experiment")){
		
		}else if(req.getParameter("submit").equals("remove-experiment")){
		
		}else{//view
			Collection<Entity> affs=userRepository.findAffsByUserGuids(Arrays.asList(form.getUserGuid()));
			req.setAttribute("affs", affs);
		}
		req.setAttribute("webform", form);
    	req.getRequestDispatcher("/jsp/qa/qa_experiment.jsp").forward(req, resp);
	}	
}
