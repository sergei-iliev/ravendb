package com.luee.wally.api.paypal.client.model.payout;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BatchPayoutView {
	@JsonProperty("batch_header")
	private BatchHeaderView batchHeader;

	@JsonProperty("items")
	private List<BatchPayoutItem> items;
	
	public BatchHeaderView getBatchHeader() {
		return batchHeader;
	}
	public void setBatchHeader(BatchHeaderView batchHeader) {
		this.batchHeader = batchHeader;
	}
	
	public List<BatchPayoutItem> getItems() {
		return items;
	}
	public void setItems(List<BatchPayoutItem> items) {
		this.items = items;
	}
}
