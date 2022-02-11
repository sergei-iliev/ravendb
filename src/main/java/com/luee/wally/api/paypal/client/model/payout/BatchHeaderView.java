package com.luee.wally.api.paypal.client.model.payout;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;


public class BatchHeaderView {

	@JsonProperty("payout_batch_id")
	private String payoutBatchId;
    
	@JsonProperty("batch_status")
	private String batchStatus;
    
	@JsonProperty("time_created")
	private Date timeCreated;
	
	@JsonProperty("time_completed")
	private Date timeCompleted;

	@JsonProperty("amount")
	private MoneyView amount;

	@JsonProperty("fees")
	private MoneyView fees;

	public String getPayoutBatchId() {
		return payoutBatchId;
	}

	public void setPayoutBatchId(String payoutBatchId) {
		this.payoutBatchId = payoutBatchId;
	}

	public String getBatchStatus() {
		return batchStatus;
	}

	public void setBatchStatus(String batchStatus) {
		this.batchStatus = batchStatus;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}

	public Date getTimeCompleted() {
		return timeCompleted;
	}

	public void setTimeCompleted(Date timeCompleted) {
		this.timeCompleted = timeCompleted;
	}

	public MoneyView getAmount() {
		return amount;
	}
	
	public MoneyView getFees() {
		return fees;
	}
}
