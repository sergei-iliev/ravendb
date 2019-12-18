package com.luee.wally.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;

public class PaidUser {
	private String key;
	private String userGuid;
	private String amount;
	private String type;
	private String email;
	private Date date;
	private String paypalAccount;
	private String redeemingRequesKey, redeemingRequestId;
    private String paymentReferenceId;
	private String paidCurrency;
	private BigDecimal eurCurrency;
	private String invoiceNumber;
	private boolean paidUserSuccess,emailSentSuccess;
	   

	public static PaidUser valueOf(Entity entity) {
		PaidUser paidUser = new PaidUser();
		paidUser.key = KeyFactory.keyToString(entity.getKey());
		paidUser.setAmount((String) entity.getProperty("amount"));
		paidUser.setUserGuid((String) entity.getProperty("user_guid"));
		paidUser.setRedeemingRequestId((String) entity.getProperty("redeeming_request_id"));
		paidUser.setType((String) entity.getProperty("type"));
		paidUser.setDate((Date) entity.getProperty("date"));
		paidUser.setPaypalAccount((String) entity.getProperty("paypal_account"));
		paidUser.redeemingRequesKey=((String) entity.getProperty("redeeming_request_key"));
		paidUser.paidCurrency=((String) entity.getProperty("paid_currency"));
		paidUser.email=((String) entity.getProperty("email"));
		paidUser.eurCurrency=BigDecimal.valueOf((double)entity.getProperty("eur_currency"));
		paidUser.paidUserSuccess= ((Boolean)entity.getProperty("paid_user_success"));
		paidUser.emailSentSuccess= ((Boolean)entity.getProperty("email_sent_success"));
		paidUser.paymentReferenceId=((String) entity.getProperty("payment_reference_id"));
		return paidUser;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}



	public String getRedeemingRequestId() {
		return redeemingRequestId;
	}

	public void setRedeemingRequestId(String redeemingRequestId) {
		this.redeemingRequestId = redeemingRequestId;
	}


	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getPaypalAccount() {
		return paypalAccount;
	}

	public void setPaypalAccount(String paypalAccount) {
		this.paypalAccount = paypalAccount;
	}



	public String getRedeemingRequesKey() {
		return redeemingRequesKey;
	}

	public void setRedeemingRequesKey(String redeemingRequesKey) {
		this.redeemingRequesKey = redeemingRequesKey;
	}

	public String getPaymentReferenceId() {
		return paymentReferenceId;
	}

	public void setPaymentReferenceId(String paymentReferenceId) {
		this.paymentReferenceId = paymentReferenceId;
	}

	public String getPaidCurrency() {
		return paidCurrency;
	}

	public void setPaidCurrency(String paidCurrency) {
		this.paidCurrency = paidCurrency;
	}

	public BigDecimal getEurCurrency() {
		return eurCurrency;
	}

	public void setEurCurrency(BigDecimal eurCurrency) {
		this.eurCurrency = eurCurrency;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public boolean isPaidUserSuccess() {
		return paidUserSuccess;
	}

	public void setPaidUserSuccess(boolean paidUserSuccess) {
		this.paidUserSuccess = paidUserSuccess;
	}

	public boolean isEmailSentSuccess() {
		return emailSentSuccess;
	}

	public void setEmailSentSuccess(boolean emailSentSuccess) {
		this.emailSentSuccess = emailSentSuccess;
	}

	@JsonIgnore
	public boolean isAmazonType() {
		return type.equalsIgnoreCase("amazon");
	}
	@JsonIgnore
	public boolean isPayPalType() {
		return type.equalsIgnoreCase("paypal");
	}	
}
