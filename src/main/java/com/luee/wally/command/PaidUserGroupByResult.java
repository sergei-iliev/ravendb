package com.luee.wally.command;

import java.io.Serializable;
import java.math.BigDecimal;

public class PaidUserGroupByResult implements Serializable{

	
	private String groupByTimeValue; // day;month;year
	private String groupByLocaleValue; // country,currency
	private BigDecimal amount;
	private Integer dayTime; //used for ordering
	private long count;
	
	public PaidUserGroupByResult(String groupByTimeValue,String groupByLocaleValue,BigDecimal amount,Integer dayTime,long count) {
	  this.amount=amount;
	  this.groupByTimeValue=groupByTimeValue;
	  this.groupByLocaleValue=groupByLocaleValue;
	  this.dayTime=dayTime;
	  this.count=count;
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
	
	public Integer getDayTime() {
		return dayTime;
	}
	
	public Long getCount() {
		return count;
	} 
}
