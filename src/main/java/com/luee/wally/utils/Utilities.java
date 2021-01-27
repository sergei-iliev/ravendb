package com.luee.wally.utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
	
	public static String formatedDate(Date date,String format){		 
		  SimpleDateFormat formater = new SimpleDateFormat(format);//("yyyy-MM-dd");
		  return formater.format(date);
	}
	public static String formatedDate(ZonedDateTime date,String format){		 		  
		  DateTimeFormatter formater = DateTimeFormatter.ofPattern(format);
		  return formater.format(date);
	}
    public static String formatPrice(BigDecimal price) {
        if (price == null) {
            return BigDecimal.ZERO.toString();
        }
        return price.setScale(2, BigDecimal.ROUND_HALF_EVEN).toString();
    }
	
}
