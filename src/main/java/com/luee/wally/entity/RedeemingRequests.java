package com.luee.wally.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.luee.wally.utils.Utilities;

public class RedeemingRequests {
	private String userGuid;
	private String amount;
	private String type;
	private String email, fullName, fullAddress;
	private String countryCode;
	private Date date, creationDate;
	private String paypalAccount;
	private String link1, link2;
	private String key, redeemingRequestId, packageName;
	private String uaChannel;
	private String ipAddress;
	private boolean paid;
	private List<Long> coinsPerGame;
	private boolean confirmedEmail;
	private double maxRev; // always in dollars
	private boolean usingVPN;
	private List<String> userCountriesConnectedFrom;

	public static RedeemingRequests valueOf(Entity entity) {

		RedeemingRequests redeemingRequests = new RedeemingRequests();
		redeemingRequests.key = KeyFactory.keyToString(entity.getKey());
		redeemingRequests.setAmount((String) entity.getProperty("amount"));
		redeemingRequests.setUsingVPN((Boolean) entity.getProperty("is_using_vpn"));
		redeemingRequests.setMaxRev((Double) entity.getProperty("max_rev"));
		redeemingRequests.setFullName((String) entity.getProperty("full_name"));
		redeemingRequests.setUserGuid((String) entity.getProperty("user_guid"));
		redeemingRequests.setRedeemingRequestId((String) entity.getProperty("redeeming_request_id"));
		redeemingRequests.setType((String) entity.getProperty("type"));
		redeemingRequests.setDate((Date) entity.getProperty("date"));
		redeemingRequests.setCreationDate((Date) entity.getProperty("creation_date"));
		redeemingRequests.setPaypalAccount((String) entity.getProperty("paypal_account"));
		redeemingRequests.setCountryCode((String) entity.getProperty("country_code"));
		redeemingRequests.setEmail((String) entity.getProperty("email"));
		redeemingRequests.setPackageName((String) entity.getProperty("package_name"));
		redeemingRequests.setUaChannel((String) entity.getProperty("ua_channel"));
		redeemingRequests.setIpAddress((String) entity.getProperty("ip_address"));
		redeemingRequests.setFullAddress((String) entity.getProperty("full_address"));
		redeemingRequests.setPaid((Boolean) entity.getProperty("is_paid"));
		redeemingRequests.setCoinsPerGame((List) entity.getProperty("coins_per_game"));
		redeemingRequests.setUserCountriesConnectedFrom((List) entity.getProperty("user_countries"));
		redeemingRequests.setConfirmedEmail((Boolean) entity.getProperty("confirmed_email"));
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

	public void setUserCountriesConnectedFrom(List<String> userCountriesConnectedFrom) {
		this.userCountriesConnectedFrom = userCountriesConnectedFrom;
	}

	public List<String> getUserCountriesConnectedFrom() {
		return userCountriesConnectedFrom;
	}

	public boolean isConfirmedEmail() {
		return confirmedEmail;
	}

	public void setConfirmedEmail(boolean confirmedEmail) {
		this.confirmedEmail = confirmedEmail;
	}

	public double getMaxRev() {
		return maxRev;
	}

	public void setMaxRev(double maxRev) {
		this.maxRev = maxRev;
	}

	public List<Long> getCoinsPerGame() {
		return coinsPerGame;
	}

	public void setCoinsPerGame(List<Long> coinsPerGame) {
		this.coinsPerGame = coinsPerGame;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	public String getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getUaChannel() {
		return uaChannel;
	}

	public void setUaChannel(String uaChannel) {
		this.uaChannel = uaChannel;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getRedeemingRequestId() {
		return redeemingRequestId;
	}

	public void setRedeemingRequestId(String redeemingRequestId) {
		this.redeemingRequestId = redeemingRequestId;
	}

	public String getFirstName() {
		String[] s = fullName.split(" ");
		return s[0];
	}

	public String getLastName() {
		String[] s = fullName.split(" ");
		return s.length > 1 ? s[1] : null;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getFullName() {
		return fullName;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
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

	public boolean isUsingVPN() {
		return usingVPN;
	}

	public void setUsingVPN(Boolean usingVPN) {
		if (usingVPN == null) {
			this.usingVPN = false;
		} else {
			this.usingVPN = usingVPN;
		}
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

	@JsonIgnore
	public String fullNameLink() {
		if (Utilities.isDevEnv()) {
			return "https://console.cloud.google.com/datastore/entities;kind=redeeming_requests_new;ns=__$DEFAULT$__/query/kind;filter=%5B%229%2Ffull_name%7CSTR%7CEQ%7C7%2F"
					+ this.fullName + "%22%5D?project=luee-wally-dev";
		} else {
			return "https://console.cloud.google.com/datastore/entities;kind=redeeming_requests_new;ns=__$DEFAULT$__/query/kind;filter=%5B%229%2Ffull_name%7CSTR%7CEQ%7C7%2F"
					+ this.fullName + "%22%5D?project=luee-wally-v2-cpc";
		}
	}

	@JsonIgnore
	public String fullAddressLink() {
		if (Utilities.isDevEnv()) {
			// return
			// "https://console.cloud.google.com/datastore/entities;kind=redeeming_requests_new;ns=__$DEFAULT$__/query/kind;filter=%5B%229%2Ffull_address%7CSTR%7CEQ%7C7%2F"+this.fullAddress+"%22%5D?project=luee-wally-dev";
			return "https://console.cloud.google.com/datastore/entities;kind=redeeming_requests_new;ns=__$DEFAULT$__/query/kind;filter=%5B%2212%2Ffull_address%7CSTR%7CEQ%7C5%2F"
					+ this.fullAddress + "%22%5D?project=luee-wally-dev";

		} else {
			// return
			// "https://console.cloud.google.com/datastore/entities;kind=redeeming_requests_new;ns=__$DEFAULT$__/query/kind;filter=%5B%229%2Ffull_address%7CSTR%7CEQ%7C7%2F"+this.fullAddress+"%22%5D?project=luee-wally-v2-cpc";
			return "https://console.cloud.google.com/datastore/entities;kind=redeeming_requests_new;ns=__$DEFAULT$__/query/kind;filter=%5B%2212%2Ffull_address%7CSTR%7CEQ%7C5%2F"
					+ this.fullAddress + "%22%5D?project=luee-wally-v2-cpc";
		}

	}

	@JsonIgnore
	public String ipAddressLink() {
		if (Utilities.isDevEnv()) {
			// return
			// "https://console.cloud.google.com/datastore/entities;kind=redeeming_requests_new;ns=__$DEFAULT$__/query/kind;filter=%5B%229%2Fip_address%7CSTR%7CEQ%7C7%2F"+this.ipAddress+"%22%5D?project=luee-wally-dev";
			return "https://console.cloud.google.com/datastore/entities;kind=redeeming_requests_new;ns=__$DEFAULT$__/query/kind;filter=%5B%2210%2Fip_address%7CSTR%7CEQ%7C5%2F"
					+ this.ipAddress + "%22%5D?project=luee-wally-dev";

		} else {
			// return
			// "https://console.cloud.google.com/datastore/entities;kind=redeeming_requests_new;ns=__$DEFAULT$__/query/kind;filter=%5B%229%2Fip_address%7CSTR%7CEQ%7C7%2F"+this.ipAddress+"%22%5D?project=luee-wally-v2-cpc";
			return "https://console.cloud.google.com/datastore/entities;kind=redeeming_requests_new;ns=__$DEFAULT$__/query/kind;filter=%5B%2210%2Fip_address%7CSTR%7CEQ%7C5%2F"
					+ this.ipAddress + "%22%5D?project=luee-wally-v2-cpc";
		}
	}

	@JsonIgnore
	public boolean isAmazonType() {
		return type.equalsIgnoreCase("amazon");
	}

	@JsonIgnore
	public boolean isPayPalType() {
		return type.equalsIgnoreCase("paypal");
	}
}
