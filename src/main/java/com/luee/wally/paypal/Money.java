package com.luee.wally.paypal;

public class Money {
	
	private String value;
	
	private String currency;

	public Money(String value,String currency) {
	  this.currency=currency;
	  this.value=value;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}


	
	
}
