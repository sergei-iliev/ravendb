package com.luee.wally.command.viewobject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

public class PaidUserGroupByVO implements ViewObject {
	private Key key;
	private String amount;
	// private BigDecimal amountBG;
	private Date date;

	private String redeemingRequestKey;
	private String currencyCode, countryCode;
	private double eurCurrency;
	private BigDecimal eurCurrencyBigDecimal;
	private Integer dayTime, monthTime, yearTime;

	private int day,month,year;
	
	public static PaidUserGroupByVO valueOf(Entity entity) {
		PaidUserGroupByVO paidUser = new PaidUserGroupByVO();
		paidUser.date = ((Date) entity.getProperty("date"));
		paidUser.countryCode = (String) entity.getProperty("country_code");
		paidUser.key = entity.getKey();
		paidUser.redeemingRequestKey = ((String) entity.getProperty("redeeming_request_key"));
		paidUser.currencyCode = ((String) entity.getProperty("paid_currency"));
		// paidUser.amount=(String) entity.getProperty("amount");
		// paidUser.amountBG=new BigDecimal(paidUser.amount);
		paidUser.eurCurrency = ((double) entity.getProperty("eur_currency"));
		paidUser.eurCurrencyBigDecimal = BigDecimal.valueOf(((double) entity.getProperty("eur_currency")));
		paidUser.resolveDate();
		return paidUser;
	}

	private void resolveDate() {
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		year = localDate.getYear();
		month = localDate.getMonthValue();
		day = localDate.getDayOfMonth();

		this.dayTime = 1000 * day + month * 100 + year;
		this.monthTime = month * 100 + year;
		this.yearTime = year;

	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public double getEurCurrency() {
		return eurCurrency;
	}
	public BigDecimal getEurCurrencyBigDecimal() {
		return eurCurrencyBigDecimal;
	}
	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getRedeemingRequestKey() {
		return redeemingRequestKey;
	}

	public void setRedeemingRequestKey(String redeemingRequestKey) {
		this.redeemingRequestKey = redeemingRequestKey;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Integer getDayTime() {
		return dayTime;
	}

	public Integer getMonthTime() {
		return monthTime;
	}

	public Integer getYearTime() {
		return yearTime;
	}

	public String getDayTimeStr(){
		StringBuilder sb=new StringBuilder();
		sb.append(day);
		sb.append("/");
		sb.append(month);
		sb.append("/");
		sb.append(year);		
		return sb.toString();
	}
	
	public String getMonthTimeStr(){
		StringBuilder sb=new StringBuilder();		
		sb.append(month);
		sb.append("/");
		sb.append(year);		
		return sb.toString();
	}
	
	public String getYearTimeStr(){		
		return String.valueOf(year);
	}
	
}
