package com.luee.wally.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

public class PaymentEligibleUserForm implements WebForm{

	private Date startDate, endDate;
	private String countryCode;
	private Collection<String> types=new HashSet<>();
	private Collection<String> packageNames=new HashSet<>();
	
	public PaymentEligibleUserForm() {
	    types.add("PayPal");
	    types.add("Amazon");
	    countryCode="US";
	    packageNames.add("com.moregames.makemoney");
	    packageNames.add("com.matchmine.app");
	    packageNames.add("com.gametrix.app");
	    try{
	    	startDate=parseDate("2019-09-20");
	    	endDate=parseDate("2019-09-21");
	    }catch(Exception e){
	    	
	    }
	}
	
	public static PaymentEligibleUserForm parse(ServletRequest req) throws ServletException{
		PaymentEligibleUserForm form = new PaymentEligibleUserForm();
		form.types.clear();
		form.packageNames.clear();
		
		form.setStartDate(form.parseDate(req.getParameter("startDate")));
		form.setEndDate(form.parseDate(req.getParameter("endDate")));
        
		form.setCountryCode((req.getParameter("country").length() == 0 ? null : req.getParameter("country")));
		
		
		form.setPackageNames(req.getParameter("packageNames"));

	    form.setTypes(req.getParameter("types"));
		
		return form;
	}


	public Date getStartDate() {
		return startDate;
	}

	public String getStartDateAsText() {
		if(startDate!=null){
			return formatedDate(startDate, "yyyy-MM-dd");
		}
		
		return null;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	public Date getEndDate() {
		return endDate;
	}
	public String getEndDateAsText() {
		if(endDate!=null){
			return formatedDate(endDate, "yyyy-MM-dd");
		}		
		return null;
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


	public Collection<String> getTypes() {
		return types;
	}
	
	public String getTypesAsText() {
		return types.stream().collect(Collectors.joining(","));
	}

	public void setTypes(String types) {
		if(types!=null&&types.length()>0){
			String[] items=types.split(","); 
			this.types.addAll(Arrays.asList(items));	
		}	
	}


	public Collection<String> getPackageNames() {
		return packageNames;
	}
    public String getPackageNamesAsText(){
    	return packageNames.stream().collect(Collectors.joining(","));    	
    }
   
	public void setPackageNames(String packageNames) {
		if(packageNames!=null&&packageNames.length()>0){
			String[] items=packageNames.split(","); 
			this.packageNames.addAll(Arrays.asList(items));	
		}		
	}
	
	
	@Override
	public String toString() {
		
		return packageNames+":"+countryCode+":"+types; 
	}
	
}
