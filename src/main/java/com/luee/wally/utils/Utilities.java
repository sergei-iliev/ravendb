package com.luee.wally.utils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;

import com.luee.wally.constants.Constants;

public final class Utilities {
    
	public static String domain="";
	
	public static boolean isDevEnv(){		
	   return (domain.contains("luee-wally-dev"));	
	}
	
	public static boolean isTestEnv(){
		return true;
	}
	public static String getBucketName(){
		if(isDevEnv()){
			return Constants.BUCKET_NAME_DEV;
		}else{
			return Constants.BUCKET_NAME_PROD;
		}
	}
	//If your answer is a negative number, then this is a percentage decrease.
	public static double findPercentageDifferenceBetween(double a,double b){
		double c=(a-b);		
		return (c/b)*100;
	}
	public static String formatedDate(Date date,String format){		 
		  SimpleDateFormat formater = new SimpleDateFormat(format);//("yyyy-MM-dd");
		  return formater.format(date);
	}
	public static String formatedDate(ZonedDateTime date,String format){		 		  
		  DateTimeFormatter formater = DateTimeFormatter.ofPattern(format);
		  return formater.format(date);
	}
	
	public static ZonedDateTime toCETZoneDateTime(Date date){
		  ZonedDateTime zdt=date.toInstant().atZone(ZoneId.systemDefault());						 
		  return zdt.withZoneSameInstant(ZoneId.of("CET"));		  		  
	}
	
    public static String formatPrice(BigDecimal price) {
        if (price == null) {
            return BigDecimal.ZERO.toString();
        }
        return price.setScale(2, BigDecimal.ROUND_HALF_EVEN).toString();
    }
	
	public static String createBasicAuthString(String user, String password) {
        return String.format("Basic %s", Base64.getEncoder()
                .encodeToString(String.format("%s:%s", user, password)
                        .getBytes()));
    }	
	
	public static Date toDate(String date,String format){	       	       
	       SimpleDateFormat formatter = new SimpleDateFormat(format);      	       
	       try {
			return formatter.parse(date);
		} catch (ParseException e) {			
			e.printStackTrace();
			return null;
		}      	       
	}
	
	/*
	 * replace every "@" with a "9" and every "." with a "1"
	 */
	public static String encodeEmail(String email){
		if(email==null){
			return null;
		}
		
		return email.replace("@","9").replace(".", "1");
	}
}
