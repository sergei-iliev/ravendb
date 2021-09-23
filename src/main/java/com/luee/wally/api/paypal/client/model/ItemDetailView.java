package com.luee.wally.api.paypal.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ItemDetailView {
	@JsonProperty("item_code")
	private String itemCode;
	@JsonProperty("item_name")
	private String itemName;
	@JsonProperty("item_description")
	private String itemDescription;
	@JsonProperty("item_quantity")
	private String itemQuantity;
	@JsonProperty("item_unit_price")
	private MoneyView itemUnitPrice;
	@JsonProperty("item_amount")
	private MoneyView itemAmount;
    //public List<TaxAmount> tax_amounts;
	@JsonProperty("total_item_amount")
	private MoneyView totalItemAmount;
	@JsonProperty("invoice_number")
	private String invoiceNumber;
	
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getItemDescription() {
		return itemDescription;
	}
	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}
	public String getItemQuantity() {
		return itemQuantity;
	}
	public void setItemQuantity(String itemQuantity) {
		this.itemQuantity = itemQuantity;
	}
	public MoneyView getItemUnitPrice() {
		return itemUnitPrice;
	}
	public void setItemUnitPrice(MoneyView itemUnitPrice) {
		this.itemUnitPrice = itemUnitPrice;
	}
	public MoneyView getItemAmount() {
		return itemAmount;
	}
	public void setItemAmount(MoneyView itemAmount) {
		this.itemAmount = itemAmount;
	}
	public MoneyView getTotalItemAmount() {
		return totalItemAmount;
	}
	public void setTotalItemAmount(MoneyView totalItemAmount) {
		this.totalItemAmount = totalItemAmount;
	}
	public String getInvoiceNumber() {
		return invoiceNumber;
	}
	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}
	
	
	
}
