package com.paypal.integrate.admin.command;

import java.math.BigDecimal;

public class CampaignSearchResult {
	private final  BigDecimal totalAdRev;
	
	private final int affsCount,campaignCount;
	
	private final BigDecimal offerwallRev;
	
	private String groupName,groupValue;
	
	public CampaignSearchResult(BigDecimal totalAdRev,BigDecimal offerwallRev,int affsCount,int campaignCount) {
	   this.totalAdRev=totalAdRev;
	   this.affsCount=affsCount;
	   this.campaignCount=campaignCount;
	   this.offerwallRev=offerwallRev;
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
	
	
}
