package com.luee.wally.admin.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.luee.wally.admin.repository.SearchFilterTemplateRepository;
import com.luee.wally.api.route.Controller;
import com.luee.wally.command.PaymentEligibleUserForm;
import com.luee.wally.entity.SearchFilterTemplate;
import com.luee.wally.json.JSONUtils;

public class SearchFilterTemplateController  implements Controller{

	public void saveSearchFilterTemplate(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{

		PaymentEligibleUserForm form=PaymentEligibleUserForm.parse(req);
		
		//String name=(String)req.getParameter("name");
		//String v=JSONUtils.writeObject(form, PaymentEligibleUserForm.class);

		
		SearchFilterTemplate searchFilterTemplate=new SearchFilterTemplate();
		searchFilterTemplate.setName(req.getParameter("name"));
		searchFilterTemplate.setForm(JSONUtils.writeObject(form, PaymentEligibleUserForm.class));
		searchFilterTemplate.setDate(new Date());
		
		SearchFilterTemplateRepository searchFilterTemplateRepository=new SearchFilterTemplateRepository();
		searchFilterTemplateRepository.saveSearchFilterTemplates(searchFilterTemplate);
		

		resp.getWriter().write("OK");
	}
	
	public void getSearchFilterTemplates(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		SearchFilterTemplateRepository searchFilterTemplateRepository=new SearchFilterTemplateRepository();
		Collection<SearchFilterTemplate> list=searchFilterTemplateRepository.findSearchFilterTemplates(); 		
		
		Map<String,Object> value=new HashMap<String, Object>();
		value.put("result",list);
			
		resp.getWriter().write(JSONUtils.writeObject(value));
	}
	
}
