package com.luee.wally.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;

public class PaidUserExternal implements Payable{
		private String key;				
		private String amount;
		private String type;
		private String email;
		private Date date;
		private String paypalAccount;
		private String redeemingRequestId;
	    private String paymentReferenceId;
		private String paidCurrency;
		private BigDecimal eurCurrency;
		private String invoiceNumber;		
		private String packageName;
		private String countryCode;		
		private String fullName;
		private String address;
		
		public static PaidUserExternal valueOf(Entity entity) {
			PaidUserExternal paidUser = new PaidUserExternal();
			paidUser.key = KeyFactory.keyToString(entity.getKey());
			paidUser.setAmount((String) entity.getProperty("amount"));
			paidUser.setDate((Date) entity.getProperty("date"));
			paidUser.setCountryCode((String) entity.getProperty("country_code"));
			paidUser.paidCurrency=((String) entity.getProperty("paid_currency"));
			paidUser.setType((String) entity.getProperty("type"));
			paidUser.eurCurrency=BigDecimal.valueOf((double)entity.getProperty("eur_currency"));			
			paidUser.email=((String) entity.getProperty("email_address"));
			paidUser.setPaypalAccount((String) entity.getProperty("paypal_account"));			
			paidUser.setRedeemingRequestId((String) entity.getProperty("redeeming_request_id"));
			paidUser.packageName=(String) entity.getProperty("package_name");
			paidUser.address=(String) entity.getProperty("address");
			paidUser.paymentReferenceId=((String) entity.getProperty("payment_reference_id"));
			paidUser.invoiceNumber=((String) entity.getProperty("invoice_number"));		
			paidUser.fullName=((String) entity.getProperty("full_name"));
			return paidUser;
		}
		
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
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
		public Date getDate() {
			return date;
		}
		public void setDate(Date date) {
			this.date = date;
		}
		public String getPaypalAccount() {
			return paypalAccount;
		}
		public void setPaypalAccount(String paypalAccount) {
			this.paypalAccount = paypalAccount;
		}
		public String getRedeemingRequestId() {
			return redeemingRequestId;
		}
		public void setRedeemingRequestId(String redeemingRequestId) {
			this.redeemingRequestId = redeemingRequestId;
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
		public BigDecimal getAmountNet(){
			if(amount==null){
				return BigDecimal.ZERO;
			}else{
				return new BigDecimal(amount);
			}
		}

	
}
