package com.luee.wally.command;

import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

public class ExportPaidUsersForm implements WebForm{
	private Date startDate, endDate;
	private boolean external;
	
    
	public static ExportPaidUsersForm parse(ServletRequest req) throws ServletException{
		ExportPaidUsersForm form = new ExportPaidUsersForm();
		
		form.setStartDate(form.parseDate(req.getParameter("startDate")));
		form.setEndDate(form.parseDate(req.getParameter("endDate")));
        form.external=Boolean.parseBoolean(req.getParameter("external"));		

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

	public boolean isExternal() {
		return external;
	}
	
	public void setExternal(boolean external) {
		this.external = external;
	}
	
}
