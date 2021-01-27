package com.luee.wally.api.tangocard.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;



public abstract class ClientApi {


	
	private final String platformName;
    private final String platformKey;
    
    
    public ClientApi(String platformName,String platformKey) {
	   this.platformKey=platformKey;
	   this.platformName=platformName;
	}
    
	
	public  String getJSON(String urlStr) throws MalformedURLException,IOException  {
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		conn.setDoOutput(true);
		// Set HTTP request method.
		conn.setRequestMethod("GET");				
		conn.setRequestProperty("Accept", "application/json");		
		conn.setRequestProperty("Authorization", this.getBasicAuthString(platformName, platformKey));			    				
		conn.setUseCaches( false );
		
		
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
			throw new IOException("Invalid response from remote server:"+responseCode+", url:"+urlStr+", message:"+response);
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
	
	private String getBasicAuthString(String user, String password) {
        return String.format("Basic %s", Base64.getEncoder()
                .encodeToString(String.format("%s:%s", user, password)
                        .getBytes()));
    }	
}
