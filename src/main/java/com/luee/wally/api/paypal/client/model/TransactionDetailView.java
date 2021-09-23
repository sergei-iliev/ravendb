package com.luee.wally.api.paypal.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionDetailView {
	@JsonProperty("transaction_info")
    private TransactionInfoView transactionInfo;
    //public PayerInfo payer_info;
    //public ShippingInfo shipping_info;
	private CartInfoView cartInfo;
    //public StoreInfo store_info;
    //public AuctionInfo auction_info;
    //public IncentiveInfo incentive_info;
	
	
	public TransactionInfoView getTransactionInfo() {
		return transactionInfo;
	}
	public void setTransactionInfo(TransactionInfoView transactionInfo) {
		this.transactionInfo = transactionInfo;
	}
	public CartInfoView getCartInfo() {
		return cartInfo;
	}
	public void setCartInfo(CartInfoView cartInfo) {
		this.cartInfo = cartInfo;
	}
    
    
    
}
