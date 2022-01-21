package com.luee.wally.json;

import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JustPlayAmountVO implements ValueObject {
		
	private String type;	
	private String currencyCode;
	private String amount;
	private String fee;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getFee() {
		return fee;
	}
	public void setFee(String fee) {
		this.fee = fee;
	}
	
	public BigDecimal getNetAmount(){
		BigDecimal a=new BigDecimal(this.amount!=null?this.amount:"0.0");
		BigDecimal b=new BigDecimal(this.fee!=null?this.fee:"0.0");
	    
		return a.add(b);
	}
	
}
