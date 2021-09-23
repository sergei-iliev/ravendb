package com.luee.wally.api.paypal.client.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionView {
    @JsonProperty("transaction_details")
    public List<TransactionDetailView> transactionDetails;
    @JsonProperty("account_number")
    public String accountNumber;
    //public Date lastRefreshedDatetime;
    @JsonProperty("page")
    public int page;
    @JsonProperty("total_items")
    public int totalItems;
    @JsonProperty("total_pages")
    public int totalPages;
	public List<TransactionDetailView> getTransactionDetails() {
		return transactionDetails;
	}
	public void setTransactionDetails(List<TransactionDetailView> transactionDetails) {
		this.transactionDetails = transactionDetails;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getTotalItems() {
		return totalItems;
	}
	public void setTotalItems(int totalItems) {
		this.totalItems = totalItems;
	}
	public int getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
    
    
    //public List<Link> links;
    
    
    
	
}
