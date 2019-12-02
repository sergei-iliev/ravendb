package com.luee.wally.command;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentEligibleUserForm implements WebForm{

	private Date startDate, endDate;
	private Collection<String> countryCodes=new HashSet<>();
	private Collection<String> types=new HashSet<>();
	private Collection<String> packageNames=new HashSet<>();
	private Boolean  confirmedEmail;
	private BigDecimal amountFrom,amountTo; 
	
	public PaymentEligibleUserForm() {
	    types.add("PayPal");
	    types.add("Amazon");
	    countryCodes.add("US");
	    packageNames.add("com.moregames.makemoney");
	    packageNames.add("com.matchmine.app");
	    packageNames.add("com.gametrix.app");
	    try{
	    	startDate=parseDate("2019-09-20");
	    	endDate=parseDate("2019-09-21");
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	}
	@JsonIgnore
	public static PaymentEligibleUserForm parse(ServletRequest req) throws ServletException{
		PaymentEligibleUserForm form = new PaymentEligibleUserForm();
		form.types.clear();
		form.packageNames.clear();
		form.countryCodes.clear();
		
		form.setConfirmedEmail(req.getParameter("confirmedEmail"));
		form.setAmountFrom(req.getParameter("amountFrom"));
		form.setAmountTo(req.getParameter("amountTo"));
		
		form.setStartDate(form.parseDate(req.getParameter("startDate")));
		form.setEndDate(form.parseDate(req.getParameter("endDate")));

		if(req.getParameterValues("countries")!=null){
			form.setCountryCodes(req.getParameterValues("countries"));
		}else if(req.getParameterValues("countries[]")!=null){
				form.setCountryCodes(req.getParameterValues("countries[]"));
			
		}
		
		form.setPackageNames(req.getParameter("packageNames"));

	    form.setTypes(req.getParameter("types"));
		
		return form;
	}
	
	public void setCountryCodes(Collection<String> countryCodes) {
		this.countryCodes = countryCodes;
	}
	@JsonProperty
	public Collection<String> getCountryCodes() {
		return countryCodes;
	}

	@JsonIgnore
	public void setCountryCodes(String[] countryCodes) {
		this.countryCodes.addAll(Arrays.asList(countryCodes));
	}
	
	public void setTypes(Collection<String> types) {
		this.types = types;
	}
	@JsonProperty
	public Collection<String> getTypes() {
		return types;
	}
	
	@JsonIgnore
	public String getTypesAsText() {
		return types.stream().collect(Collectors.joining(","));
	}
	
	@JsonIgnore
	public void setTypes(String types) {
		if(types!=null&&types.length()>0){
			String[] items=types.split(","); 
			this.types.addAll(Arrays.asList(items));	
		}	
	}	
	
	public void setPackageNames(Collection<String> packageNames) {
		this.packageNames = packageNames;
	}
	@JsonProperty
	public Collection<String> getPackageNames() {
		return packageNames;
	}
	
	@JsonIgnore
    public String getPackageNamesAsText(){
    	return packageNames.stream().collect(Collectors.joining(","));    	
    }
	@JsonIgnore
	public void setPackageNames(String packageNames) {
		if(packageNames!=null&&packageNames.length()>0){
			String[] items=packageNames.split(","); 
			this.packageNames.addAll(Arrays.asList(items));	
		}		
	}
	@JsonProperty
	public Boolean getConfirmedEmail() {
		return confirmedEmail;
	}
	public void setConfirmedEmail(Boolean confirmedEmail){
		this.confirmedEmail=confirmedEmail;
	}
	
	@JsonIgnore
	public void setConfirmedEmail(String confirmedEmail) {
		if(confirmedEmail.equalsIgnoreCase("true")||confirmedEmail.equalsIgnoreCase("false")){
			this.confirmedEmail=Boolean.parseBoolean(confirmedEmail);
		}else{
			this.confirmedEmail = null;
		}
		
	}
	@JsonProperty
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	@JsonIgnore
	public String getStartDateAsText() {
		if(startDate!=null){
			return formatedDate(startDate, "yyyy-MM-dd");
		}
		
		return null;
	}
	


	@JsonProperty
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	@JsonIgnore
	public String getEndDateAsText() {
		if(endDate!=null){
			return formatedDate(endDate, "yyyy-MM-dd");
		}		
		return null;
	}
	@JsonIgnore
	public void setAmountTo(String amountTo) {
		if(amountTo!=null&&amountTo.length()>0){
			this.amountTo=new BigDecimal(amountTo);
		}else{
			this.amountTo=null;
		}
	}	
	@JsonIgnore
	public void setAmountFrom(String amountFrom) {
		if(amountFrom!=null&&amountFrom.length()>0){
			this.amountFrom=new BigDecimal(amountFrom);
		}else{
			this.amountFrom=null;
		}
	}
	public BigDecimal getAmountFrom() {
		return amountFrom;
	}

	public BigDecimal getAmountTo() {
		return amountTo;
	}
	
	@JsonIgnore
	@Override
	public String toString() {		
		return packageNames+":"+countryCodes+":"+types+":"+confirmedEmail; 
	}

	
}
