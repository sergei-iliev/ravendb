package com.luee.wally.api.paypal.client.model.payout;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BatchPayoutItem {
    @JsonProperty("transaction_id")
    private String transactionId;
    
    @JsonProperty("transaction_status")
    private String transactionStatus;
        
    @JsonProperty("time_processed")
    private Date timeProcessed;

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public Date getTimeProcessed() {
		return timeProcessed;
	}

	public void setTimeProcessed(Date timeProcessed) {
		this.timeProcessed = timeProcessed;
	}

    
    
}
