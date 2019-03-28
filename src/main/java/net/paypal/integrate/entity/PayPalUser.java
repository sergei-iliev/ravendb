package net.paypal.integrate.entity;

import java.beans.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class PayPalUser implements Serializable {
	@Id
	private Long uuid;

	@NotEmpty
	private String amount;
	@NotEmpty
	private String countryCode;
	@NotEmpty
	private String fullAddress;
	@NotEmpty
	private String fullName;
	@NotEmpty
	private String paypalAccount;
	@NotEmpty
	private String currency;
	
	
	public Long getUuid() {
		return uuid;
	}

	public void setUuid(Long uuid) {
		this.uuid = uuid;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getPaypalAccount() {
		return paypalAccount;
	}

	public void setPaypalAccount(String paypalAccount) {
		this.paypalAccount = paypalAccount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
	@Transient
	public Key<PayPalUser> getKey() {
	   return Key.create(PayPalUser.class, uuid);
	}
	@Transient
	public List<String> getCurrencyList() {
        List<String> list = new ArrayList<>();
        list.add("EUR");
        list.add("USD");
        list.add("GBR");
        list.add("CAD");
        list.add("CHF");
        return list;
    }
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append(uuid+"\r\n");
		sb.append(fullName+"\r\n");
		sb.append(fullAddress+"\r\n");
		sb.append(paypalAccount+"\r\n");
		sb.append(currency+"\r\n");
		sb.append(countryCode+"\r\n");
		return sb.toString();
	}
}
