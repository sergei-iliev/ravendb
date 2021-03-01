package com.luee.wally.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.luee.wally.constants.Constants;

public enum ConnectionMgr {
INSTANCE;

	public  String _postJSON(String _url,String content) throws MalformedURLException,IOException  {
		return "{}";
	}
	public  String postJSON(String _url,String content) throws MalformedURLException,IOException  {
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		conn.setDoOutput(true);
		
		// Set HTTP request method.
		conn.setRequestMethod("POST");	
		conn.setRequestProperty("User-Agent", Constants.AGENT_NAME);		
		conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty( "Content-Length", Integer.toString( content.length()));
		conn.setUseCaches( false );


		try(OutputStream os = conn.getOutputStream()) {
		    byte[] input = content.getBytes(StandardCharsets.UTF_8);
		    os.write(input);           
		}
		
		int responseCode = conn.getResponseCode();
		StringBuffer response = new StringBuffer();
		
		if(responseCode!=200&&responseCode!=202){
			try(BufferedReader in = new BufferedReader(
			        new InputStreamReader(conn.getErrorStream()))){
			 String inputLine;		 
			 while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
					response.append("\r\n");
			  }
			}
			throw new IOException("Invalid response from remote code:"+responseCode+", url:"+_url+", message:"+response);
		}
		
		
		
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
	public  String getJSON(String urlStr,Map<String,String> headers) throws MalformedURLException,IOException  {
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		
		// Set HTTP request method.
		conn.setRequestMethod("GET");		
		if(headers!=null){
		   headers.entrySet().forEach(e->{	
			conn.setRequestProperty(e.getKey(),e.getValue());
		   });
		}
		
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
	public  String getJSON(String urlStr) throws MalformedURLException,IOException  {
		//default
		Map<String,String> headers=new HashMap<String, String>();
		headers.put("Referrer Policy", "strict-origin-when-cross-origin");
		headers.put("Accept", "application/json");		
		
		return this.getJSON(urlStr, headers);
    }

	public  String getCSV(String urlStr) throws MalformedURLException,IOException  {
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		
		// Set HTTP request method.
		conn.setRequestMethod("GET");		
		conn.setRequestProperty("User-Agent", Constants.AGENT_NAME);
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
