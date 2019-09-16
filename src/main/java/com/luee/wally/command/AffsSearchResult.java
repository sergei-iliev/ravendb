package com.luee.wally.command;

import java.math.BigDecimal;

public class AffsSearchResult {

	private final  BigDecimal totalAdRev;
	
	private final int count;
	
	private final BigDecimal offerwallRev;
	
	private String experiment;
	
	public AffsSearchResult(String experiment,BigDecimal totalAdRev,BigDecimal offerwallRev,int count) {
	   this.experiment=experiment;
	   this.totalAdRev=totalAdRev;
	   this.count=count;
	   this.offerwallRev=offerwallRev;
	}
	public BigDecimal getAvrTotalAdRev(){
		if(count!=0){
          return getTotalAdRev().divide(new BigDecimal(getCount()),4, BigDecimal.ROUND_HALF_EVEN);
		}else{
		  return BigDecimal.ZERO;	
		}
	}
	public BigDecimal getAvrOfferwallRev(){
		if(count!=0){
		  return getOfferwallRev().divide(new BigDecimal(getCount()),4, BigDecimal.ROUND_HALF_EVEN);
		}else{
		  return BigDecimal.ZERO;	
		}
		
	}
	
	public void setExperiment(String experiment){
		this.experiment=experiment;
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
