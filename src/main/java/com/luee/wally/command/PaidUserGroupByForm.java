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

public class PaidUserGroupByForm implements WebForm{
	public enum GroupByType{
		NONE,
		TIME,
		LOCALE,
		ALL
	}
	
	private Date startDate, endDate;
	private String email;
	private String paypalAccount;
	private String userGuid;
	private String type;
	private Collection<String> countryCodes = new HashSet<>();
	private Collection<String> packageNames = new HashSet<>();
	private BigDecimal amountFrom, amountTo;

	private String groupByTime; // day;month;year
	private String groupByLocale; // country,currency

	private GroupByType groupByType=GroupByType.NONE;
	
	
	public PaidUserGroupByForm() {
		type="PayPal";
		ZonedDateTime now = ZonedDateTime.now();
		ZonedDateTime yesterday = now.minusDays(7);
		startDate = Date.from(yesterday.toInstant());
	}

	public static PaidUserGroupByForm parse(ServletRequest req) throws ServletException {
		PaidUserGroupByForm form = new PaidUserGroupByForm();
		
		form.packageNames.clear();
		form.countryCodes.clear();

		form.setAmountFrom(req.getParameter("amountFrom"));
		form.setAmountTo(req.getParameter("amountTo"));

		form.setGroupByLocale(req.getParameter("groupByLocale"));
		form.setGroupByTime(req.getParameter("groupByTime"));

		form.setStartDate(form.parseDate(req.getParameter("startDate")));
		form.setEndDate(form.parseDate(req.getParameter("endDate")));

		if (req.getParameterValues("countries") != null) {
			form.setCountryCodes(req.getParameterValues("countries"));
		} else if (req.getParameterValues("countries[]") != null) {
			form.setCountryCodes(req.getParameterValues("countries[]"));

		}

		form.setPackageNames(req.getParameter("packageNames"));
		form.setType(req.getParameter("types"));

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
		
		form.resolveGroupByType();
		return form;
	}
	private void resolveGroupByType(){
		if(this.groupByLocale==null&&groupByTime==null){
			this.groupByType=GroupByType.NONE;
		}else if(this.groupByLocale!=null&&this.groupByTime!=null){
			this.groupByType=GroupByType.ALL;
		}else if(this.groupByLocale!=null){
			this.groupByType=GroupByType.LOCALE; 
		}else{
			this.groupByType=GroupByType.TIME;
		}
	}
	public GroupByType getGroupByType() {
		return groupByType;
	}
	public void setGroupByType(GroupByType groupByType) {
		this.groupByType = groupByType;
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


	@JsonIgnore
	public String getType() {
		return type;
	}

	@JsonIgnore
	public void setType(String type) {
		this.type=type;
	}

	public void setPackageNames(Collection<String> packageNames) {
		this.packageNames = packageNames;
	}

	@JsonProperty
	public Collection<String> getPackageNames() {
		return packageNames;
	}

	@JsonIgnore
	public String getPackageNamesAsText() {
		return packageNames.stream().collect(Collectors.joining(","));
	}

	@JsonIgnore
	public void setPackageNames(String packageNames) {
		if (packageNames != null && packageNames.length() > 0) {
			String[] items = packageNames.split(",");
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
		if (amountTo != null && amountTo.length() > 0) {
			this.amountTo = new BigDecimal(amountTo);
		} else {
			this.amountTo = null;
		}
	}

	@JsonIgnore
	public void setAmountFrom(String amountFrom) {
		if (amountFrom != null && amountFrom.length() > 0) {
			this.amountFrom = new BigDecimal(amountFrom);
		} else {
			this.amountFrom = null;
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

	public String getGroupByTime() {
		return groupByTime;
	}

	public void setGroupByTime(String groupByTime) {
		this.groupByTime = groupByTime;
	}

	public String getGroupByLocale() {
		return groupByLocale;
	}

	public void setGroupByLocale(String groupByLocale) {
		this.groupByLocale = groupByLocale;
	}
}
