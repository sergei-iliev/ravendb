package com.luee.wally.entity;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.luee.wally.utils.Utilities;

public class RedeemingRequests {
	private String userGuid;
	private String amount;
	private String type;
	private String email;
	private String countryCode;
	private Date date;
	private String paypalAccount;
	private String link1, link2;
	private String key;

	public static RedeemingRequests valueOf(Entity entity) {
		RedeemingRequests redeemingRequests = new RedeemingRequests();
		redeemingRequests.key = KeyFactory.keyToString(entity.getKey());
		redeemingRequests.setAmount((String) entity.getProperty("amount"));
		redeemingRequests.setUserGuid((String) entity.getProperty("user_guid"));
		redeemingRequests.setType((String) entity.getProperty("type"));
		redeemingRequests.setDate((Date) entity.getProperty("date"));
		redeemingRequests.setPaypalAccount((String) entity.getProperty("paypal_account"));
		redeemingRequests.setCountryCode((String) entity.getProperty("country_code"));
		redeemingRequests.setEmail((String) entity.getProperty("email"));
		// redeemingRequests.setLink1("/administration/payment/test?amount="+redeemingRequests.amount+"&user_guid="+redeemingRequests.userGuid);
		// redeemingRequests.setLink2("/administration/payment/test?amount="+redeemingRequests.amount+"&user_guid="+redeemingRequests.userGuid);

		redeemingRequests.setLink1(
				"https://console.cloud.google.com/datastore/entities;kind=redeeming_requests_new;ns=__$DEFAULT$__/query/kind;filter=%5B%229%2Fuser_guid%7CSTR%7CEQ%7C36%2F"
						+ redeemingRequests.userGuid + "%22%5D?project=luee-wally-v2-cpc");
		if (Utilities.isDevEnv()) {
			redeemingRequests.setLink2("https://luee-wally-dev.appspot.com/paid_user?amount=" + redeemingRequests.amount
					+ "&user_guid=" + redeemingRequests.userGuid);
		} else {
			redeemingRequests.setLink2("https://luee-wally-v2-cpc.appspot.com/paid_user?amount="
					+ redeemingRequests.amount + "&user_guid=" + redeemingRequests.userGuid);
		}
		return redeemingRequests;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public String getUserGuid() {
		return userGuid;
	}

	public void setUserGuid(String userGuid) {
		this.userGuid = userGuid;
	}

	public String getPaypalAccount() {
		return paypalAccount;
	}

	public void setPaypalAccount(String paypalAccount) {
		this.paypalAccount = paypalAccount;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getLink1() {
		return link1;
	}

	public void setLink1(String link1) {
		this.link1 = link1;
	}

	public String getLink2() {
		return link2;
	}

	public void setLink2(String link2) {
		this.link2 = link2;
	}

}
