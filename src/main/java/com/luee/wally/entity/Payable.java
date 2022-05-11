package com.luee.wally.entity;

import java.math.BigDecimal;

public interface Payable {
	public BigDecimal getCalculatedAmount();
	//public default BigDecimal getAmountNet(){
	//	return BigDecimal.ZERO;
	//}
	
	public String getPaidCurrency();
	
	public String getType();
	
	public default void setError(String error){
		
	}
	
	public default String getError(){
		return null;
	}
	

}
