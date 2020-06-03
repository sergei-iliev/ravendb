package com.luee.wally.constants;

import java.math.BigDecimal;

public interface PaymentConstants {
	
	
	public static final BigDecimal  SINGLE_PAYMENT=new BigDecimal(30.0);	
	public static final BigDecimal  TOTAL_PAYMENTS_PER_USER=new BigDecimal(100.0);
	public static final BigDecimal  TOTAL_DAILY_PAYMENT=new BigDecimal(1000.0);
	
	public static final Double  BLOCK_PAYMENT_VALUE=new Double(5.0);

	
}
