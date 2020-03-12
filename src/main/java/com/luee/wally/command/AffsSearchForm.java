package com.luee.wally.command;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AffsSearchForm implements WebForm{
	private Date startDate, endDate;
	private String countryCode;
	private Collection<String> experiments=new HashSet<>();
	private String packageName;
    private String submitType="search";
    
	public static AffsSearchForm parse(ServletRequest req) throws ServletException{
		AffsSearchForm form = new AffsSearchForm();
		form.submitType=(String)req.getParameter("submit");
		form.setStartDate(form.parseDate(req.getParameter("startDate")));
		form.setEndDate(form.parseDate(req.getParameter("endDate")));
        
		form.setCountryCode((req.getParameter("country").length() == 0 ? null : req.getParameter("country")));

		if(req.getParameterValues("experiments")!=null){
			form.setExperiments(req.getParameterValues("experiments"));
		}
		form.setCountryCode((req.getParameter("country").length() == 0 ? null : req.getParameter("country")));
		form.setPackageName((req.getParameter("packageName").length() == 0 ? null : req.getParameter("packageName")));
		

		return form;
	}

	public String getEndDateAsText() {
		if(endDate!=null){
			return formatedDate(endDate, "yyyy-MM-dd");
		}		
		return null;
	}
	public String getStartDateAsText() {
		if(startDate!=null){
			return formatedDate(startDate, "yyyy-MM-dd");
		}
		
		return null;
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
	
	public String getSubmitType() {
		return submitType;
	}
	 
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		
		sb.append("startDate:"+(startDate==null?"":startDate)+"\r\n");
		sb.append("endDate:"+(endDate==null?"":endDate)+"\r\n");
		sb.append("country:"+(countryCode==null?"":countryCode)+"\r\n");
		sb.append("experiment:"+experiments+"\r\n");
		sb.append("packageName:"+(packageName==null?"":packageName)+"\r\n");
		return sb.toString();
	}
}
