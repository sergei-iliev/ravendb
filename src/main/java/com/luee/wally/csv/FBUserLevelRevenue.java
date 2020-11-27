package com.luee.wally.csv;

import java.math.BigDecimal;


public class FBUserLevelRevenue {

	private String date;
	private String adUnitID; // ID of the MAX ad unit from which the revenue was
								// generated.
	private String name;
	private String adFormat;  //INTER, BANNER, REWARD 
	private String placement;
	private String country; 
	private String deviceType;
	
	private String IDFA; // Advertising identifier: IDFA for iOS devices, GAID
							// for Android devices.

	private String userID; // Optional ID of the user set via Applovin SDK (see
							// the ‘Setting Internal User ID’ section below)
	private String encryptedCPM;
	
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getAdUnitID() {
		return adUnitID;
	}
	public void setAdUnitID(String adUnitID) {
		this.adUnitID = adUnitID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAdFormat() {
		return adFormat;
	}
	public void setAdFormat(String adFormat) {
		this.adFormat = adFormat;
	}
	public String getPlacement() {
		return placement;
	}
	public void setPlacement(String placement) {
		this.placement = placement;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getIDFA() {
		return IDFA;
	}
	public void setIDFA(String iDFA) {
		IDFA = iDFA;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getEncryptedCPM() {
		return encryptedCPM;
	}
	public void setEncryptedCPM(String encryptedCPM) {
		this.encryptedCPM = encryptedCPM;
	}
	
	
	 

}
