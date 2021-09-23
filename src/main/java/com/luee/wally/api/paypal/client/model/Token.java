package com.luee.wally.api.paypal.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Token {

	 @JsonProperty
	 private String scope;
	 @JsonProperty("access_token")
	 private String accessToken;
	 @JsonProperty("token_type")
	 private String tokenType;
	 @JsonProperty("app_id")
	 private String appId;
	 @JsonProperty("expires_in")
	 private Long expiresIn;
	 
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getTokenType() {
		return tokenType;
	}
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public Long getExpiresIn() {
		return expiresIn;
	}
	public void setExpiresIn(Long expiresIn) {
		this.expiresIn = expiresIn;
	}	    
	
	 
}
