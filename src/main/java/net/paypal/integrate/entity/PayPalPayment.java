package net.paypal.integrate.entity;

import java.io.Serializable;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class PayPalPayment implements Serializable{

	  @Id
	  private Long id;
	  
	  private String paypalTransactionId;
	  private String creditNoteNo;
	  private String paypalAccount;
	  private String amount;
	  private String currency;
	  private String countryCode;
	  
	  @Index
	  private Key<PayPalUser> userKey;

	public PayPalPayment() {
	
		
	}  
	
	public PayPalPayment(PayPalUser user) {
	    userKey=user.getKey();
		amount=user.getAmount();
		currency=user.getCurrency();
		countryCode=user.getCountryCode();
		paypalAccount=user.getPaypalAccount();		
	}  
	public Key<PayPalUser> getUserKey() {
		return userKey;
	}

	public void setUserKey(Key<PayPalUser> userKey) {
		this.userKey = userKey;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPaypalTransactionId() {
		return paypalTransactionId;
	}

	public void setPaypalTransactionId(String paypalTransactionId) {
		this.paypalTransactionId = paypalTransactionId;
	}

	public String getCreditNoteNo() {
		return creditNoteNo;
	}

	public void setCreditNoteNo(String creditNoteNo) {
		this.creditNoteNo = creditNoteNo;
	}

	public String getPaypalAccount() {
		return paypalAccount;
	}

	public void setPaypalAccount(String paypalAccount) {
		this.paypalAccount = paypalAccount;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}


	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}


	  
	  
}
