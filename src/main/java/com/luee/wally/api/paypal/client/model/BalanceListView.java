package com.luee.wally.api.paypal.client.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BalanceListView {

	@JsonProperty("balances")
	private List<BalanceView> balances;
	
	@JsonProperty("account_id")
	private String accountId;
	
	@JsonProperty("as_of_time")
	private String asOfTime;



	public List<BalanceView> getBalances() {
		return balances;
	}

	public void setBalances(List<BalanceView> balances) {
		this.balances = balances;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getAsOfTime() {
		return asOfTime;
	}

	public void setAsOfTime(String asOfTime) {
		this.asOfTime = asOfTime;
	}
	
	
	
	
}
