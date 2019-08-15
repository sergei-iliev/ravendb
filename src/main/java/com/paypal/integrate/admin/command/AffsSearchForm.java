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
	private Collection<String> experiments=new HashSet<>();
	private String packageName;
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
		form.setCountryCode((req.getParameter("country").length() == 0 ? null : req.getParameter("country")));
		form.setPackageName((req.getParameter("packageName").length() == 0 ? null : req.getParameter("packageName")));
		

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

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName=packageName;
	}

	public boolean isEmpty(){
		return experiments.isEmpty()&&packageName==null&&countryCode.length()==1&&startDate==null&&endDate==null;
	}
	
	public boolean getGroupByExperiment(){
		return groupByExperiment;
	}
	 
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append("startDate:"+startDate+", endDate:"+endDate+", country"+countryCode+"\r\n");
		sb.append("experiment:"+experiments+"\r\n");
		sb.append("group by experiment:"+groupByExperiment+"\r\n");
		sb.append("packageName:"+packageName+"\r\n");
		return sb.toString();
	}
}
