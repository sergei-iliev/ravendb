package com.luee.wally.exception;

public class RestResponseException extends Exception{

	private final int responseCode;
	
	private final String responseMessage;
	
	public RestResponseException(int responseCode,String responseMessage) {
	   this.responseCode=responseCode;
	   this.responseMessage=responseMessage;
	}
	
	public RestResponseException(Throwable cause, int responseCode,String responseMessage) {
		   super(cause);
		   this.responseCode=responseCode;
		   this.responseMessage=responseMessage;
	}
	
	public int getResponseCode() {
		return responseCode;
	}
	
	public String getResponseMessage() {
		return responseMessage;
	}
	
}
