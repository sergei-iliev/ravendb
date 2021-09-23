package com.luee.wally.api.paypal.client.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionInfoView {
	@JsonProperty("paypal_account_id")
    private String paypalAccountId;
    @JsonProperty("transaction_id")
    private String transactionId;
    @JsonProperty("transaction_event_code")
    private String transactionEventCode;
    @JsonProperty("transaction_initiation_date")
    private Date transactionInitiationDate;
    @JsonProperty("transaction_updated_date")
    private Date transactionUpdatedDate;
    @JsonProperty("transaction_amount")
    private MoneyView transactionAmount;
    @JsonProperty("fee_amount")
    private MoneyView feeAmount;
    @JsonProperty("insurance_amount")
    private MoneyView insuranceAmount;
    @JsonProperty("shipping_amount")
    private MoneyView shippingAmount;
    @JsonProperty("shipping_discount_amount")
    private MoneyView shippingDiscountAmount;
    @JsonProperty("transaction_status")
    private String transactionStatus;
    @JsonProperty("transaction_subject")
    private String transactionSubject;
    @JsonProperty("transaction_note")
    private String transactionNote;
    @JsonProperty("invoice_id")
    private String invoiceId;
    
	public String getPaypalAccountId() {
		return paypalAccountId;
	}
	public void setPaypalAccountId(String paypalAccountId) {
		this.paypalAccountId = paypalAccountId;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getTransactionEventCode() {
		return transactionEventCode;
	}
	public void setTransactionEventCode(String transactionEventCode) {
		this.transactionEventCode = transactionEventCode;
	}
	public Date getTransactionInitiationDate() {
		return transactionInitiationDate;
	}
	public void setTransactionInitiationDate(Date transactionInitiationDate) {
		this.transactionInitiationDate = transactionInitiationDate;
	}
	public Date getTransactionUpdatedDate() {
		return transactionUpdatedDate;
	}
	public void setTransactionUpdatedDate(Date transactionUpdatedDate) {
		this.transactionUpdatedDate = transactionUpdatedDate;
	}
	public MoneyView getTransactionAmount() {
		return transactionAmount;
	}
	public void setTransactionAmount(MoneyView transactionAmount) {
		this.transactionAmount = transactionAmount;
	}
	public MoneyView getFeeAmount() {
		return feeAmount;
	}
	public void setFeeAmount(MoneyView feeAmount) {
		this.feeAmount = feeAmount;
	}
	public MoneyView getInsuranceAmount() {
		return insuranceAmount;
	}
	public void setInsuranceAmount(MoneyView insuranceAmount) {
		this.insuranceAmount = insuranceAmount;
	}
	public MoneyView getShippingAmount() {
		return shippingAmount;
	}
	public void setShippingAmount(MoneyView shippingAmount) {
		this.shippingAmount = shippingAmount;
	}
	public MoneyView getShippingDiscountAmount() {
		return shippingDiscountAmount;
	}
	public void setShippingDiscountAmount(MoneyView shippingDiscountAmount) {
		this.shippingDiscountAmount = shippingDiscountAmount;
	}
	public String getTransactionStatus() {
		return transactionStatus;
	}
	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}
	public String getTransactionSubject() {
		return transactionSubject;
	}
	public void setTransactionSubject(String transactionSubject) {
		this.transactionSubject = transactionSubject;
	}
	public String getTransactionNote() {
		return transactionNote;
	}
	public void setTransactionNote(String transactionNote) {
		this.transactionNote = transactionNote;
	}
	public String getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}
   
    
    
}
