package com.luee.wally.command.order;

import java.io.Serializable;
import java.math.BigDecimal;

public class OrderTransactionResult implements Serializable{
	
	private String currencyCode;
	private BigDecimal value=BigDecimal.ZERO;
	private String timestamp;
	private String transactionSubject;
	
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public BigDecimal getValue() {
		return value;
	}
	
	
	public void setValueAsStr(String value) {
		if(value==null){
			this.value=BigDecimal.ZERO;	
		}else{		
			this.value = new BigDecimal(value);
		}
		

	}
	
	public void setValueAsText(String value) {
		if(value==null){
		  this.value=BigDecimal.ZERO;	
		}else{
		  this.value = new BigDecimal(value);
		}
	}
	
	public void setValue(BigDecimal value) {
		if(value==null){
		  this.value=BigDecimal.ZERO;	
		}else{
		  this.value = value;
		}
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getTransactionSubject() {
		return transactionSubject;
	}
	public void setTransactionSubject(String transactionSubject) {
		this.transactionSubject = transactionSubject;
	}
	 
}
