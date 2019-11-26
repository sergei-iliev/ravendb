package com.luee.wally.command.invoice;

import java.math.BigDecimal;

public class PayoutResult {

	private String payoutBatchId; //result id from PayPal
	
	private Money amount;
	
	private Money fee;

	public String getPayoutBatchId() {
		return payoutBatchId;
	}

	public void setPayoutBatchId(String payoutBatchId) {
		this.payoutBatchId = payoutBatchId;
	}



	public Money getAmount() {
		return amount;
	}

	public void setAmount(Money amount) {
		this.amount = amount;
	}

	public Money getFee() {
		return fee;
	}

	public void setFee(Money fee) {
		this.fee = fee;
	}
	
	public String getTotal(){
		BigDecimal a=new BigDecimal(amount.getValue());
		BigDecimal f=new BigDecimal(fee.getValue());
		
	    return formatPrice(a.add(f)).toString();
	}
	
    private  BigDecimal formatPrice(BigDecimal price) {

        return price.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }
}
