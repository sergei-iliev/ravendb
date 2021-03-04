package com.luee.wally.json;

import java.util.HashMap;
import java.util.Map;

public class FirebaseNotificationEventVO implements ValueObject {
	
	private Map<String,String> data=new HashMap<>();
	private String to;	

	public FirebaseNotificationEventVO(String to,String title,String text,String iconUrl,String openUrl) {
	   this.to=to;
	   data.put("title",title);
	   data.put("text",title);
	   data.put("iconUrl",iconUrl);
	   data.put("openUrl",openUrl);
	   data.put("notification_type","open_webview_notification");
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}


}
