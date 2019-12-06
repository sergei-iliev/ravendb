package com.luee.wally.entity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class PaymentAmount {

	private BigDecimal totalAmountEur=BigDecimal.ZERO;
	private Map<String,BigDecimal> totalAmountByCurrencyMap=new HashMap<>();
	private Map<String,BigDecimal> totalAmountByTypeMap=new HashMap<>();
	
	
	public BigDecimal getTotalAmountEur() {
		return totalAmountEur;
	}
	public void setTotalAmountEur(BigDecimal totalAmountEur) {
		this.totalAmountEur = totalAmountEur;
	}
	public Map<String, BigDecimal> getTotalAmountByCurrencyMap() {
		return totalAmountByCurrencyMap;
	}
	public void setTotalAmountByCurrencyMap(Map<String, BigDecimal> totalAmountByCurrencyMap) {
		this.totalAmountByCurrencyMap = totalAmountByCurrencyMap;
	}
	public Map<String, BigDecimal> getTotalAmountByTypeMap() {
		return totalAmountByTypeMap;
	}
	public void setTotalAmountByTypeMap(Map<String, BigDecimal> totalAmountByTypeMap) {
		this.totalAmountByTypeMap = totalAmountByTypeMap;
	}

	public void addTotalAmountEur(double amount){
        
		BigDecimal a=BigDecimal.valueOf(amount);
		totalAmountEur=totalAmountEur.add(a);
	}
	
	public void addTotalAmountByTypeMap(String type,double amount){
		BigDecimal a=BigDecimal.valueOf(amount);
		BigDecimal sum=totalAmountByTypeMap.get(type);
		if(sum==null){
			totalAmountByTypeMap.put(type,a);	
		}else{
			sum=sum.add(a);
			totalAmountByTypeMap.put(type,sum);
		}
		
	}
	
	public void addTotalAmountByCurrencyMap(String currencyCode,String amount){
		BigDecimal a=new BigDecimal(amount);
		BigDecimal sum=totalAmountByCurrencyMap.get(currencyCode);
		if(sum==null){
			totalAmountByCurrencyMap.put(currencyCode,a);	
		}else{
			sum=sum.add(a);
			totalAmountByCurrencyMap.put(currencyCode,sum);
		}
		
	}
	
	
}
