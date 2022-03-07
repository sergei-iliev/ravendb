package com.luee.wally.entity;

import java.math.BigDecimal;

public interface Payable {
	public default BigDecimal getAmountNet(){
		return BigDecimal.ZERO;
	}
	
	public String getPaidCurrency();
	
	public String getType();

}
