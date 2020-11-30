package com.luee.wally.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public enum ConnectionMgr {
INSTANCE;

	private final String USER_AGENT = "BackedSoft";
	
	
	public  String getJSON(String urlStr) throws MalformedURLException,IOException  {
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		
		// Set HTTP request method.
		conn.setRequestMethod("GET");		
		conn.setRequestProperty("Referrer Policy", "strict-origin-when-cross-origin");
		conn.setRequestProperty("Accept", "application/json");		

		
		int responseCode = conn.getResponseCode();
		
		if(responseCode!=200){
			throw new IOException("Invalid response from remote code:"+responseCode+", url:"+urlStr);
		}
		
		StringBuffer response = new StringBuffer();
		
		try(BufferedReader in = new BufferedReader(
		        new InputStreamReader(conn.getInputStream()))){
		 String inputLine;		 
		 while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
			response.append("\r\n");
		  }
		}
		
		return response.toString();
    }

	public  String getCSV(String urlStr) throws MalformedURLException,IOException  {
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		
		// Set HTTP request method.
		conn.setRequestMethod("GET");		
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
				

		
		int responseCode = conn.getResponseCode();
		if(responseCode!=200){
			throw new IOException("Invalid response from remote code:"+responseCode+", url:"+urlStr);
		}
		
		StringBuffer response = new StringBuffer();
		
		try(BufferedReader in = new BufferedReader(
		        new InputStreamReader(conn.getInputStream()))){
		 String inputLine;		 
		 while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
			response.append("\r\n");
		  }
		}
		
		return response.toString();
    }
}
