package com.luee.wally.api.lock;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.luee.wally.constants.Constants;

public enum MemoryCacheLock {
	INSTANCE;
	private final Logger logger = Logger.getLogger(MemoryCacheLock.class.getName());

	
	public static final String EXTERNAL_PAYMENT_LOCK="external_payment_lock";
	
	public boolean lock(String key){
		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();		
		long result=0;
		for(int i=0;i<Constants.LOCK_LOOP_COUNT;i++){
			result=memcache.increment(key, 1L, 0L);			
			if(result==1){								
			    return true;	//lock taken					
			}
			try{				
				Thread.currentThread().sleep(500);
			}catch(InterruptedException e){	
				logger.log(Level.SEVERE,"",e);
				return false;
			}			
		}
		return false;		
		
	}
	/*
	 * Check if current PK is what next concurrent threads' PK request is 
	 */
	public boolean lockPrimaryKey(String entityTypeId,String entityPK){
		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
		String value = (String) memcache.get(entityTypeId);
		if (value == null) {	//first one
			memcache.put(entityTypeId,entityPK); 		 
			return true;
		}else{
			if(entityPK.equals(value)){  //same record is processsed
				return false;
			}else{
				memcache.put(entityTypeId,entityPK);
				return true;
			}				
		}
	}
	public void unlockPrimaryKey(String entityTypeId){
		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
		memcache.put(entityTypeId,null); 
	}
	
	
	
	public void unlock(String key){
		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();		
		memcache.put(key,0L);		
	}
	
}
