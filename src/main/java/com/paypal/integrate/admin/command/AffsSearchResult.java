package com.paypal.integrate.admin.command;

import java.math.BigDecimal;

public class AffsSearchResult {

	private final  BigDecimal totalAdRev;
	
	private final int count;
	
	private final BigDecimal offerwallRev;
	
	private final String experiment;
	
	public AffsSearchResult(String experiment,BigDecimal totalAdRev,BigDecimal offerwallRev,int count) {
	   this.experiment=experiment;
	   this.totalAdRev=totalAdRev;
	   this.count=count;
	   this.offerwallRev=offerwallRev;
	}

	public BigDecimal getTotalAdRev() {
		return totalAdRev;
	}

	public int getCount() {
		return count;
	}

	public BigDecimal getOfferwallRev() {
		return offerwallRev;
	}

	public String getExperiment() {
		return experiment;
	}

	
	
	
}
