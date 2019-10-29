package com.luee.wally.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.luee.wally.constants.Constants;

public final class Utilities {
    
	public static String domain=null;
	
	public static boolean isDevEnv(){		
	   return (domain.contains("luee-wally-dev"));	
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
}
