package com.luee.wally.command;

import java.math.BigDecimal;

public class CampaignSearchResult {
	private final  BigDecimal totalAdRev;
	
	private final int affsCount,campaignCount;
	
	private final BigDecimal offerwallRev;
	
	private final BigDecimal appLikeRev;
	
	private BigDecimal totalPaidUsers;
	
	private String groupName,groupValue;
	
	private  BigDecimal rateValue;//EUR to USD rate
	private int minRevCount;
	
	public CampaignSearchResult(BigDecimal totalAdRev,BigDecimal offerwallRev,BigDecimal appLikeRev,BigDecimal totalPaidUsers,int affsCount,int campaignCount,int minRevCount) {
	   this.totalAdRev=totalAdRev;
	   this.affsCount=affsCount;
	   this.campaignCount=campaignCount;
	   this.offerwallRev=offerwallRev;
	   this.totalPaidUsers=totalPaidUsers; 
	   this.minRevCount=minRevCount;
	   this.appLikeRev=appLikeRev;
	}
	public BigDecimal getAvrTotalAdRev(){
		if(affsCount!=0){
          return getTotalAdRev().divide(new BigDecimal(getAffsCount()),4, BigDecimal.ROUND_HALF_EVEN);
		}else{
		  return BigDecimal.ZERO;	
		}
	}
	public BigDecimal getAvrOfferwallRev(){
		if(affsCount!=0){
		  return getOfferwallRev().divide(new BigDecimal(getAffsCount()),4, BigDecimal.ROUND_HALF_EVEN);
		}else{
		  return BigDecimal.ZERO;	
		}
		
	}
	
	public BigDecimal getAvrAppLikeRev(){
		if(affsCount!=0){
		  return getAppLikeRev().divide(new BigDecimal(getAffsCount()),4, BigDecimal.ROUND_HALF_EVEN);
		}else{
		  return BigDecimal.ZERO;	
		}		
	}	
	
	public BigDecimal getAvrTotalPaidUsers(){
		if(affsCount!=0){
		  return totalPaidUsers.divide(new BigDecimal(getAffsCount()),4, BigDecimal.ROUND_HALF_EVEN);
		}else{
		  return BigDecimal.ZERO;	
		}
		
	}
	public BigDecimal getTotalPaidUsers() {
		return totalPaidUsers;
	}
	public int getCampaignCount() {
		return campaignCount;
	}
	
	public BigDecimal getTotalAdRev() {
		return totalAdRev;
	}

	public int getAffsCount() {
		return affsCount;
	}

	public BigDecimal getOfferwallRev() {
		return offerwallRev;
	}

	public BigDecimal getAppLikeRev() {
		return appLikeRev;
	}
	
	public String getGroupName() {
		return groupName;
	}
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getGroupValue() {
		return groupValue;
	}
	public void setGroupValue(String groupValue) {
		this.groupValue = groupValue;
	}
	public BigDecimal getTotalPaidUsersUSD() {
		return getTotalPaidUsers().multiply(rateValue);		
	}
	
	public BigDecimal getAvrTotalPaidUsersUSD(){
		return getAvrTotalPaidUsers().multiply(rateValue);	
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
