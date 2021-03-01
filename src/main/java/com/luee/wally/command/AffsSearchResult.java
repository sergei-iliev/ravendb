package com.luee.wally.command;

import java.math.BigDecimal;

public class AffsSearchResult {

	private final  BigDecimal totalAdRev;
	
	private BigDecimal totalPaidUsers;
	
	private final int count;
	
	private final BigDecimal offerwallRev;
	
	private final BigDecimal appLikeRev;
	
	private  BigDecimal rateValue;//EUR to USD rate
	
	private String experiment;
	
	private int minRevCount;
	
	public AffsSearchResult(String experiment,BigDecimal totalAdRev,BigDecimal offerwallRev,BigDecimal appLikeRev,BigDecimal totalPaidUsers,int count) {
	    this(experiment,totalAdRev,offerwallRev,appLikeRev, totalPaidUsers, count,0);
	}
	public AffsSearchResult(String experiment,BigDecimal totalAdRev,BigDecimal offerwallRev,BigDecimal appLikeRev,BigDecimal totalPaidUsers,int count,int minRevCount) {
	   this.appLikeRev=appLikeRev;
	   this.experiment=experiment;
	   this.totalAdRev=totalAdRev;
	   this.count=count;
	   this.offerwallRev=offerwallRev;
	   this.totalPaidUsers=totalPaidUsers;	
	   this.minRevCount=minRevCount;
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
	public BigDecimal getAvrAppLikeRev(){
		if(count!=0){
		  return getAppLikeRev().divide(new BigDecimal(getCount()),4, BigDecimal.ROUND_HALF_EVEN);
		}else{
		  return BigDecimal.ZERO;	
		}
		
	}	
	public BigDecimal getAvrTotalPaidUsers(){
		if(count!=0){
		  return totalPaidUsers.divide(new BigDecimal(getCount()),4, BigDecimal.ROUND_HALF_EVEN);
		}else{
		  return BigDecimal.ZERO;	
		}
		
	}

	public BigDecimal getAvrTotalPaidUsersUSD(){
		return getAvrTotalPaidUsers().multiply(rateValue);	
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

	public BigDecimal getAppLikeRev() {
		return appLikeRev;
	}
	
	public BigDecimal getOfferwallRev() {
		return offerwallRev;
	}

	public String getExperiment() {
		return experiment;
	}

	
	public BigDecimal getTotalPaidUsers() {
		return totalPaidUsers;
	}

	public BigDecimal getTotalPaidUsersUSD() {
		return getTotalPaidUsers().multiply(rateValue);		
	}

	
	public void setRateValue(BigDecimal rateValue) {
		this.rateValue = rateValue;
	}
	
	public int getMinRevCount() {
		return minRevCount;
	}
	
	public void setMinRevCount(int minRevCount) {
		this.minRevCount = minRevCount;
	}
}
