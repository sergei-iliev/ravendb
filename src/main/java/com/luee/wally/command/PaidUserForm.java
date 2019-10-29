package com.luee.wally.command;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

public class PaidUserForm implements WebForm{

	private String key,currencyCode;
	private String paymentType;
	private double amount;
	private boolean emailSentSuccess,paidUserSuccess;
	
	public static PaidUserForm parse(ServletRequest req) throws ServletException{
		PaidUserForm form=new PaidUserForm();
		form.setKey(req.getParameter("key"));
		form.setCurrencyCode(req.getParameter("currencyCode"));
        form.setPaymentType(req.getParameter("paymentType"));
	    form.setAmount(Double.parseDouble(req.getParameter("amount")));
	   
        form.setPaidUserSuccess(Boolean.parseBoolean(req.getParameter("paid_user_success")));
        form.setEmailSentSuccess(Boolean.parseBoolean(req.getParameter("email_sent_success")));
        
	    return form;
	}

	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	public boolean isEmailSentSuccess() {
		return emailSentSuccess;
	}

	public void setEmailSentSuccess(boolean emailSentSuccess) {
		this.emailSentSuccess = emailSentSuccess;
	}

	public boolean isPaidUserSuccess() {
		return paidUserSuccess;
	}

	public void setPaidUserSuccess(boolean paidUserSuccess) {
		this.paidUserSuccess = paidUserSuccess;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	
	
	
}
