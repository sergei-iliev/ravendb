package com.luee.wally.command;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;

public interface WebForm {

	public default Date parseDate(String value)throws ServletException{
		try{
		if(value!= null && value.length() != 0 ){	
			return new SimpleDateFormat("yyyy-MM-dd").parse(value);
		}else{
			return null;
		}
		}catch(ParseException e){
			throw new ServletException(e);
		}
	}
	
	public default String formatedDate(Date date,String format){		 
		  SimpleDateFormat formater = new SimpleDateFormat(format);//("yyyy-MM-dd");
		  return formater.format(date);
	}
}
