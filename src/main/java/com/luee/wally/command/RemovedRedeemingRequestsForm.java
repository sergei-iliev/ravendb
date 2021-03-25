package com.luee.wally.command;

import java.time.ZonedDateTime;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

public class RemovedRedeemingRequestsForm implements WebForm {
	private Date startDate, endDate;	
	private String packageName;
	private boolean paid;


	public RemovedRedeemingRequestsForm() {		
		ZonedDateTime now = ZonedDateTime.now();		
		startDate = Date.from(now.toInstant());
		packageName="com.moregames.makemoney";
	}

	public static RemovedRedeemingRequestsForm parse(ServletRequest req) throws ServletException {
		RemovedRedeemingRequestsForm form = new RemovedRedeemingRequestsForm();
		
		form.setStartDate(form.parseDate(req.getParameter("startDate")));
		form.setEndDate(form.parseDate(req.getParameter("endDate")));

		form.paid=Boolean.parseBoolean(req.getParameter("paid"));	
		form.packageName=(req.getParameter("packageName").equals("")?null:req.getParameter("packageName"));
		return form;
	}

	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
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

	

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("startDate:" + (startDate == null ? "" : startDate) + "\r\n");
		sb.append("endDate:" + (endDate == null ? "" : endDate) + "\r\n");
		return sb.toString();
	}

}
