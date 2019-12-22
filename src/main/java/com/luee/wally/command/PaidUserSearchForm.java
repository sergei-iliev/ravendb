package com.luee.wally.command;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PaidUserSearchForm implements WebForm {
	private Date startDate, endDate;
	private String packageName;
	private String email;
	private String paypalAccount;
	private String userGuid;
	private Collection<String> types = new HashSet<>();
	private int activeTab;

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

		form.setStartDate(form.parseDate(req.getParameter("startDate")));
		form.setEndDate(form.parseDate(req.getParameter("endDate")));
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
		
		form.setTypes(req.getParameter("types"));
		
		// form.setPackageName((req.getParameter("packageName").length() == 0 ?
		// null : req.getParameter("packageName")));
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

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
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

	public Collection<String> getTypes() {
		return types;
	}

	@JsonIgnore
	public String getTypesAsText() {
		return types.stream().collect(Collectors.joining(","));
	}
	public void setTypes(Collection<String> types) {
		this.types = types;
	}
	@JsonIgnore
	public void setTypes(String types) {
		if(types!=null&&types.length()>0){
			String[] items=types.split(","); 
			this.types.addAll(Arrays.asList(items));	
		}	
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
