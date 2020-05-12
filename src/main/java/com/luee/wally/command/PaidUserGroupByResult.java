package com.luee.wally.command;

import java.io.Serializable;
import java.math.BigDecimal;

public class PaidUserGroupByResult implements Serializable{

	
	private String groupByTimeValue; // day;month;year
	private String groupByLocaleValue; // country,currency
	private BigDecimal amount;
	
	public PaidUserGroupByResult(String groupByTimeValue,String groupByLocaleValue,BigDecimal amount) {
	  this.amount=amount;
	  this.groupByTimeValue=groupByTimeValue;
	  this.groupByLocaleValue=groupByLocaleValue;
	}
	
	public String getGroupByLocaleValue() {
		return groupByLocaleValue;
	}
	
	public String getGroupByTimeValue() {
		return groupByTimeValue;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
}
