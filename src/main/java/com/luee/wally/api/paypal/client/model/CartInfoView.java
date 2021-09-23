package com.luee.wally.api.paypal.client.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CartInfoView {
	 @JsonProperty("item_details")
	 private List<ItemDetailView> itemDetails;

	public List<ItemDetailView> getItemDetails() {
		return itemDetails;
	}

	public void setItemDetails(List<ItemDetailView> itemDetails) {
		this.itemDetails = itemDetails;
	}
	
	 
}
