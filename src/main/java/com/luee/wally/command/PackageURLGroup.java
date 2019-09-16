package com.luee.wally.command;

public class PackageURLGroup {

	private String url;
	private String apiKey;
	private String packageName;

	public PackageURLGroup(String url, String apiKey, String packageName) {
		this.url = url;
		this.apiKey = apiKey;
		this.packageName = packageName;
	}

	public String getPackageName() {
		return packageName;
	}

	public String createLink(String date) {
		StringBuilder sb = new StringBuilder(url);
		sb.append(this.apiKey);
		sb.append("&date=");
		sb.append(date);
		sb.append("&platform=android&application=");
		sb.append(packageName);
		return sb.toString();
	}


}
