package com.luee.wally.csv;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.cloud.Timestamp;

public class PaidUsers2018 {
	private String userGuid;
	private String countryCode;
	private String currencyCode="EUR";
	private String payedAmount;
	private String paymentMethod;
	private String date;
	private String invoiceNumber;
	
	private String userCurrencyCode="EUR";
	private String userPayedAmount;
	
	public String getUserGuid() {
		return userGuid;
	}
	public void setUserGuid(String userGuid) {
		this.userGuid = userGuid;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getPayedAmount() {
		return payedAmount;
	}
	public void setPayedAmount(String payedAmount) {
		this.payedAmount = payedAmount;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getCurrencyCode() {
		return currencyCode;
	}
	
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getUserCurrencyCode() {
		return userCurrencyCode;
	}
	public void setUserCurrencyCode(String userCurrencyCode) {
		this.userCurrencyCode = userCurrencyCode;
	}
	public String getUserPayedAmount() {
		return userPayedAmount;
	}
	public void setUserPayedAmount(String userPayedAmount) {
		this.userPayedAmount = userPayedAmount;
	}
	
	public String getInvoiceNumber() {
		return invoiceNumber;
	}
	
	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}
	public Timestamp getDateToTimestamp() throws ParseException {		
		Date _date=new SimpleDateFormat("MM/dd/yyyy").parse(this.date); 
		return Timestamp.of(_date);		
	}
	
	public String getFormatedDate(String format) throws ParseException{		 
		Date _date=new SimpleDateFormat("MM/dd/yyyy").parse(this.date); 
		SimpleDateFormat formater = new SimpleDateFormat(format);//("yyyy-MM-dd");
		return formater.format(_date);
	}
	
}
