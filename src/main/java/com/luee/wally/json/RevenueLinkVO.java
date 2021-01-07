package com.luee.wally.json;

public class RevenueLinkVO implements ValueObject{

	private String status;
	
	private String url;

	private String packageName;
	
	private String ad_revenue_report_url;
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public String getAd_revenue_report_url() {
		return ad_revenue_report_url;
	}
	
	public void setAd_revenue_report_url(String ad_revenue_report_url) {
		this.ad_revenue_report_url = ad_revenue_report_url;
	}
}
