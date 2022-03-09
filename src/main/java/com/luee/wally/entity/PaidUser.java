package com.luee.wally.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.luee.wally.api.service.PaidUsersService;
import com.luee.wally.utils.Utilities;

public class PaidUser implements Payable{
	
	private String key;
	private String userGuid;
	private String amount;
	private String type;
	private String email;
	private Date date;
	private String paypalAccount;
	private String redeemingRequestKey, redeemingRequestId;
    private String paymentReferenceId;
	private String paidCurrency;
	private BigDecimal eurCurrency;
	private BigDecimal eurAmountNet=BigDecimal.ZERO;;
	private BigDecimal amountNet=BigDecimal.ZERO;
	private String invoiceNumber;
	private boolean paidUserSuccess,emailSentSuccess;
	private String link;   

	private static final Date NET_AMOUNT_DATE=Utilities.toDate("01/01/2022","dd/MM/yyyy");
	
	public static PaidUser valueOf(Entity entity) {
		
		
		PaidUser paidUser = new PaidUser();
		paidUser.key = KeyFactory.keyToString(entity.getKey());
		paidUser.setAmount((String) entity.getProperty("amount"));
		paidUser.setUserGuid((String) entity.getProperty("user_guid"));
		paidUser.setRedeemingRequestId((String) entity.getProperty("redeeming_request_id"));
		paidUser.setType((String) entity.getProperty("type"));
		paidUser.setDate((Date) entity.getProperty("date"));
		paidUser.setPaypalAccount((String) entity.getProperty("paypal_account"));
		
		/*
		 * We may store both types Key(old inherited) and String
		 */
		if(entity.getProperty("redeeming_request_key")==null){
			paidUser.redeemingRequestKey=null;	
		}else if(entity.getProperty("redeeming_request_key") instanceof String){
			paidUser.redeemingRequestKey=((String) entity.getProperty("redeeming_request_key"));	
		}else{									
			Key key=((Key) entity.getProperty("redeeming_request_key"));
			paidUser.redeemingRequestKey=(KeyFactory.keyToString(key));
		}
		
		paidUser.paidCurrency=((String) entity.getProperty("paid_currency"));
		paidUser.email=((String) entity.getProperty("email_address"));
		paidUser.eurCurrency=BigDecimal.valueOf((double)entity.getProperty("eur_currency"));
		paidUser.amountNet=BigDecimal.valueOf(entity.getProperty("amount_net")==null?0:(double)entity.getProperty("amount_net"));
		paidUser.eurAmountNet=BigDecimal.valueOf(entity.getProperty("amount_net_eur_currency")==null?0:(double)entity.getProperty("amount_net_eur_currency"));
		paidUser.paidUserSuccess= ((Boolean)entity.getProperty("paid_user_success"));
		paidUser.emailSentSuccess= ((Boolean)entity.getProperty("email_sent_success"));
		paidUser.paymentReferenceId=((String) entity.getProperty("payment_reference_id"));
		
		paidUser.link="https://console.cloud.google.com/datastore/entities;kind=redeeming_requests_new;ns=__$DEFAULT$__/query/kind;filter=%5B%2220%2Fredeeming_request_id%7CSTR%7CEQ%7C36%2F"+paidUser.redeemingRequestId+"%22%5D?project=luee-wally-v2-cpc";
		              		
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



	public String getRedeemingRequestKey() {
		return redeemingRequestKey;
	}

	public void setRedeemingRequestKey(String redeemingRequesKey) {
		this.redeemingRequestKey = redeemingRequesKey;
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

	public String getLink() {
			return link;
	}
	
	@JsonIgnore
	public boolean isAmazonType() {
		return type.equalsIgnoreCase("amazon");
	}
	@JsonIgnore
	public boolean isPayPalType() {
		return type.equalsIgnoreCase("paypal");
	}
	
	public BigDecimal getAmountNet() {
		return amountNet;
	}
	@Override
	public BigDecimal getCalculatedAmount(){
		 if(date.after(NET_AMOUNT_DATE)){
			 return this.amountNet;
		 }else{
			 return new BigDecimal(this.amount);
		 }
	}
	
	public void setAmountNet(BigDecimal amountNet) {
		this.amountNet = amountNet;
	}
	
	public BigDecimal getEurAmountNet() {
		return eurAmountNet;
	}
	
	public void setEurAmountNet(BigDecimal eurAmountNet) {
		this.eurAmountNet = eurAmountNet;
	}
}
