package com.luee.wally.entity;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;

public class GiftCardCountryCode {
	private String countryCode;
	private String currency;
	private String brand;
	private String unitid;
	private String key;
	
	public static GiftCardCountryCode valueOf(Entity entity) {
		GiftCardCountryCode giftCardCountryCode=new GiftCardCountryCode();
		giftCardCountryCode.key = KeyFactory.keyToString(entity.getKey());
		giftCardCountryCode.setCountryCode((String) entity.getProperty("country_code"));
		giftCardCountryCode.setCurrency((String) entity.getProperty("currency"));
		giftCardCountryCode.setBrand((String) entity.getProperty("brand"));
		giftCardCountryCode.setUnitid((String) entity.getProperty("unitid"));
		return giftCardCountryCode;
	}
	
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getUnitid() {
		return unitid;
	}
	public void setUnitid(String unitid) {
		this.unitid = unitid;
	}

	
}
