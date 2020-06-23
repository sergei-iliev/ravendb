package com.luee.wally.api.lock;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.Expiration;
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
	
	public void unlock(String key){
		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();		
		memcache.put(key,0L);		
	}
	
	/*
	 * Check if current PK is what next concurrent threads' PK request is 
	 */
	public boolean lockPrimaryKey(String entityPK){
		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
		Long value = (Long) memcache.get(entityPK);
		if (value == null) {	//no current processing
			memcache.put(entityPK,1L); 		 
			return true;
		}else{			
			return false;						
		}
	}
	public boolean lockPrimaryKeyTimeout(String entityPK,Expiration timeout){
		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
		Long value = (Long) memcache.get(entityPK);
		if (value == null) {	//no current processing
			memcache.put(entityPK,1L,timeout); 		 
			return true;
		}else{			
			return false;						
		}
	}	
	public void unlockPrimaryKey(String entityPK){
		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
		memcache.delete(entityPK); 
	}
	
	
	
	
}
