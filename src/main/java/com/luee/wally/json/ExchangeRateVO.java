package com.luee.wally.json;

import java.util.Map;

public class ExchangeRateVO implements ValueObject {
	private Map<String,Double> rates;
	private String base;
	private String date;

	// Getter Methods

	public Map<String,Double> getRates() {
		return rates;
	}

	public String getBase() {
		return base;
	}

	public String getDate() {
		return date;
	}

	// Setter Methods

	public void setRates(Map<String,Double> rates) {
		this.rates = rates;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
