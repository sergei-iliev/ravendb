package com.luee.wally.command;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import com.luee.wally.entity.PaidUserExternal;
import com.luee.wally.entity.RedeemingRequests;

public class PayExternalForm implements WebForm {
	private String type; 
	private String packageName;
	private String countryCode;
	private String amount;
	private String currency;
	private String paypalAccount;
	private String emailAddress;
	private String fullName;
	private String address;
	private String redeemingRequestId; 

	public static PayExternalForm parse(ServletRequest req) throws ServletException{
		PayExternalForm form=new PayExternalForm();
		form.type=req.getParameter("type");
		form.packageName=req.getParameter("package_name");
		form.countryCode=req.getParameter("country_code");
		form.amount=req.getParameter("amount");
		form.currency=req.getParameter("currency");
		form.paypalAccount=req.getParameter("paypal_account");
		form.emailAddress=req.getParameter("email_address");
		form.fullName=req.getParameter("full_name");
		form.address=req.getParameter("address");
		form.redeemingRequestId=req.getParameter("redeeming_request_id");
		return form;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getPaypalAccount() {
		return paypalAccount;
	}

	public void setPaypalAccount(String paypalAccount) {
		this.paypalAccount = paypalAccount;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRedeemingRequestId() {
		return redeemingRequestId;
	}

	public void setRedeemingRequestId(String redeemingRequestId) {
		this.redeemingRequestId = redeemingRequestId;
	}
	
	public RedeemingRequests toRedeemingRequests(){
		RedeemingRequests redeemingRequests=new RedeemingRequests();
		redeemingRequests.setType(type);
		redeemingRequests.setAmount(amount);
		redeemingRequests.setCountryCode(countryCode); 
		redeemingRequests.setEmail(emailAddress);
		redeemingRequests.setFullName(fullName);
		redeemingRequests.setPackageName(packageName);
		redeemingRequests.setPaypalAccount(paypalAccount);
		redeemingRequests.setRedeemingRequestId(redeemingRequestId);
		return redeemingRequests;
	}
	
	public PaidUserExternal toPaidUserExternal(){
		PaidUserExternal paidUserExternal=new PaidUserExternal();
		paidUserExternal.setAddress(address);
		paidUserExternal.setAmount(amount);
		paidUserExternal.setCountryCode(countryCode);
		paidUserExternal.setEmail(emailAddress);
		paidUserExternal.setFullName(fullName);
		paidUserExternal.setPackageName(packageName);
		paidUserExternal.setPaidCurrency(currency);
		paidUserExternal.setPaypalAccount(paypalAccount);
		paidUserExternal.setRedeemingRequestId(redeemingRequestId);
	
		return paidUserExternal;
	}
}
