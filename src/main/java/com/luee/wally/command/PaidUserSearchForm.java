package com.luee.wally.command;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PaidUserSearchForm implements WebForm {
	private Date startDate, endDate;
	private String email;
	private String paypalAccount;
	private String userGuid;
	private Collection<String> types = new HashSet<>();
	private int activeTab;
	private Collection<String> countryCodes=new HashSet<>();	
	private Collection<String> packageNames=new HashSet<>();
	private BigDecimal amountFrom,amountTo; 
	
	public PaidUserSearchForm() {
		types.add("PayPal");
		types.add("Amazon");
		activeTab = 1;
		ZonedDateTime now = ZonedDateTime.now();
		ZonedDateTime yesterday = now.minusDays(7);
		startDate = Date.from(yesterday.toInstant());
	}

	public static PaidUserSearchForm parse(ServletRequest req) throws ServletException {
		PaidUserSearchForm form = new PaidUserSearchForm();
		form.types.clear();
		form.packageNames.clear();
		form.countryCodes.clear();
		
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
		
		if (req.getParameter("userGuid") != null) {
			form.userGuid = ((req.getParameter("userGuid").length() == 0 ? null : req.getParameter("userGuid")));
		}
		if (req.getParameter("email") != null) {
			form.email = ((req.getParameter("email").length() == 0 ? null : req.getParameter("email")));
		}
		if (req.getParameter("paypalAccount") != null) {
			form.paypalAccount = ((req.getParameter("paypalAccount").length() == 0 ? null
					: req.getParameter("paypalAccount")));
		}
		return form;
	}

	public String getEndDateAsText() {
		if (endDate != null) {
			return formatedDate(endDate, "yyyy-MM-dd");
		}
		return null;
	}

	public String getStartDateAsText() {
		if (startDate != null) {
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserGuid() {
		return userGuid;
	}

	public void setUserGuid(String userGuid) {
		this.userGuid = userGuid;
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

	public String getPaypalAccount() {
		return paypalAccount;
	}

	public void setPaypalAccount(String paypalAccount) {
		this.paypalAccount = paypalAccount;
	}

	public int getActiveTab() {
		return activeTab;
	}

	public void setActiveTab(int activeTab) {
		this.activeTab = activeTab;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("startDate:" + (startDate == null ? "" : startDate) + "\r\n");
		sb.append("endDate:" + (endDate == null ? "" : endDate) + "\r\n");
		return sb.toString();
	}

}
