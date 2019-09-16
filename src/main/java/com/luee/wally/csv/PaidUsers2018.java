package com.luee.wally.csv;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.cloud.Timestamp;

public class PaidUsers2018 {
	private String userGuid;
	private String countryCode;
	private String payedAmount;
	private String paymentMethod;
	private String date;
	
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
	
	public Timestamp getDateToTimestamp() throws ParseException {		
		Date _date=new SimpleDateFormat("MM/dd/yyyy").parse(this.date); 
		System.out.println(date+"::"+_date);;
		return Timestamp.of(_date);		
	}
	
	
	
}
