package com.luee.wally.command;

import java.math.BigDecimal;

public class FBAffsSearchResult extends AffsSearchResult{


	private BigDecimal totalFBRev; 
	

	
	public FBAffsSearchResult(String experiment,BigDecimal totalAdRev,BigDecimal offerwallRev,BigDecimal totalPaidUsers,int count) {
	    this(experiment,totalAdRev,offerwallRev, totalPaidUsers, count,0);
	}
	public FBAffsSearchResult(String experiment,BigDecimal totalAdRev,BigDecimal offerwallRev,BigDecimal totalPaidUsers,int count,int minRevCount) {
          super(experiment,totalAdRev,offerwallRev,BigDecimal.ZERO,totalPaidUsers,count,minRevCount);
	}
	public BigDecimal getTotalFBRev() {
		return totalFBRev;
	}
	public void setTotalFBRev(BigDecimal totalFBRev) {
		this.totalFBRev = totalFBRev;
	}
	public BigDecimal getAvrTotalFBRev(){
		if(getCount()!=0){
          return totalFBRev.divide(new BigDecimal(getCount()),4, BigDecimal.ROUND_HALF_EVEN);
		}else{
		  return BigDecimal.ZERO;	
		}
	}	
	

}
