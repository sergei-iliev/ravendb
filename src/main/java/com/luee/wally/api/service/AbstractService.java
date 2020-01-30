package com.luee.wally.api.service;

import java.io.PrintWriter;
import java.io.StringWriter;

public class AbstractService {
	
	public String convert(Throwable throwable){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		throwable.printStackTrace(pw);
		return sw.toString();		
	}
}
