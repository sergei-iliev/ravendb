package net.paypal.integrate.command;

public class PackageURLGroup {

	private String url;
	private String apiKey;
	private String packageName;

	public PackageURLGroup(String url, String apiKey, String packageName) {
		this.url = url;
		this.apiKey = apiKey;
		this.packageName = packageName;
	}

	// public abstract String createLink(String date);
	//
	//
	// public static class PackageURLGroupA extends PackageURLGroup{
	// public PackageURLGroupA(String url,String apiKey,String packageName) {
	// super(url,apiKey,packageName);
	// }
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

	// }

}
