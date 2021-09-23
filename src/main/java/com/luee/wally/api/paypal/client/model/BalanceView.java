package com.luee.wally.api.paypal.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BalanceView {

	@JsonProperty("currency")
	private String currency;
	@JsonProperty("total_balance")
	private MoneyView totalBalance;
	
	@JsonProperty("available_balance")
	private MoneyView availableBalance;
	
	@JsonProperty("withheld_balance")
	private MoneyView  withheldBalance;

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public MoneyView getTotalBalance() {
		return totalBalance;
	}

	public void setTotalBalance(MoneyView totalBalance) {
		this.totalBalance = totalBalance;
	}

	public MoneyView getAvailableBalance() {
		return availableBalance;
	}

	public void setAvailableBalance(MoneyView availableBalance) {
		this.availableBalance = availableBalance;
	}

	public MoneyView getWithheldBalance() {
		return withheldBalance;
	}

	public void setWithheldBalance(MoneyView withheldBalance) {
		this.withheldBalance = withheldBalance;
	}
	
	

}
