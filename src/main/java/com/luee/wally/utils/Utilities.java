package com.luee.wally.utils;

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
}
