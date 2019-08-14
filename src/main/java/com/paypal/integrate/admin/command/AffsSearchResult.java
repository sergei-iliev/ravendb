package com.paypal.integrate.admin.command;

import java.math.BigDecimal;

public class AffsSearchResult {

	private final  BigDecimal sum;
	
	private final int count;
	
	public AffsSearchResult(BigDecimal sum,int count) {
	   this.sum=sum;
	   this.count=count;
	}

	public BigDecimal getSum() {
		return sum;
	}

	public int getCount() {
		return count;
	}
	
	
	
}
