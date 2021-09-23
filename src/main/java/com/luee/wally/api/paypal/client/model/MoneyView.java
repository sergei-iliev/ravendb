package com.luee.wally.api.paypal.client.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MoneyView {
	@JsonProperty("currency_code")
	private String currencyCode;
	@JsonProperty("value")
	private String value;
	
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@JsonIgnore
	public BigDecimal asBigDecimal(){
		return new BigDecimal(value);
	}
	
}
