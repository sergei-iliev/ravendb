package com.paypal.integrate.admin.command;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

public class AffsSearchForm {
	private Date startDate, endDate;
	private String countryCode;
	private Collection<String> experiments=new HashSet<>(), packageNames=new HashSet<>();
    private boolean groupByExperiment;
    
	public static AffsSearchForm parse(ServletRequest req) throws ServletException{
		AffsSearchForm form = new AffsSearchForm();
		form.setStartDate(parseDate(req.getParameter("startDate")));
		form.setEndDate(parseDate(req.getParameter("endDate")));
        
		form.setCountryCode((req.getParameter("country").length() == 0 ? null : req.getParameter("country")));
		if(req.getParameter("groupByExperiment")!=null){
			form.groupByExperiment=true;
		}
		if(req.getParameterValues("experiments")!=null){
			form.setExperiments(req.getParameterValues("experiments"));
		}
		if(req.getParameterValues("packageNames")!=null){
			form.setPackageNames(req.getParameterValues("packageNames"));
		}
		return form;
	}
	private static Date parseDate(String value)throws ServletException{
		try{
		if(value!= null && value.length() != 0 ){	
			return new SimpleDateFormat("yyyy-MM-dd").parse(value);
		}else{
			return null;
		}
		}catch(ParseException e){
			throw new ServletException(e);
		}
	}
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Collection<String> getExperiments() {
		return experiments;
	}

	public void setExperiments(String[] experiments) {
		this.experiments.addAll(Arrays.asList(experiments));
	}

	public Collection<String> getPackageNames() {
		return packageNames;
	}

	public void setPackageNames(String[] packageNames) {
		this.packageNames.addAll(Arrays.asList(packageNames));
	}

	public boolean isEmpty(){
		return experiments.isEmpty()&&packageNames.isEmpty()&&countryCode.length()==1&&startDate==null&&endDate==null;
	}
	
	public boolean getGroupByExperiment(){
		return groupByExperiment;
	}
	 
}
