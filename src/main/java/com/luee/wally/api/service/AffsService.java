package com.luee.wally.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.datastore.Entity;
import com.luee.wally.admin.repository.AffsRepository;
import com.luee.wally.api.lock.MemoryCacheLock;

public class AffsService extends AbstractService{
	private final Logger logger = Logger.getLogger(AffsService.class.getName());

	private AffsRepository affsRepository=new AffsRepository();
	
	
    public static void saveUserAccessCountryAsync(String userGuid,String countryCode){
    	
    	
    	ThreadManager.createBackgroundThread(new Runnable() {
			@Override
			public void run() {
				try {
					try {
						if(!MemoryCacheLock.INSTANCE.lock(userGuid)){
							Logger.getLogger(AffsService.class.getName()).log(Level.SEVERE, "Distributed LOCK TIMEOUT:");	
							return;
						}	
						
						AffsService affsService = new AffsService();
						affsService.saveUserAccessCountry(userGuid, countryCode);
					
					} finally {			
						MemoryCacheLock.INSTANCE.unlock(userGuid);
					}					
				} catch (Exception e) {
					Logger.getLogger(AffsService.class.getName()).log(Level.SEVERE, "affs user country service:", e);
				}
			}
		}).start();
    	
    	
    }

    
    public boolean saveUserAccessCountry(String userGuid,String countryCode){
        Entity entity=affsRepository.findEntity("affs_user_countries","user_guid",userGuid);
        if(entity==null){
          ArrayList<String> list=new ArrayList<String>(1);
          list.add(countryCode);
          entity=new Entity("affs_user_countries");
          entity.setIndexedProperty("user_guid", userGuid);
          entity.setProperty("country_code",list);
          affsRepository.save(entity);
          return true;
        }else{
          ArrayList<String> countryCodes = (ArrayList<String>) entity.getProperty("country_code");
          if(countryCodes.contains(countryCode)){
        	  return false;
          }else{
        	  countryCodes.add(countryCode);
        	  entity.setProperty("country_code", countryCodes);
        	  affsRepository.save(entity);
        	  return true;
          }
          			    
        }    	
    }
    
    public List<String> getAffsUserCountries(String userGuid)throws IllegalStateException{
    	List<String> result=new ArrayList<>();
    	Entity entity=affsRepository.findEntity("affs","user_guid",userGuid);
    	if(entity==null){
    	 throw new IllegalStateException("affs entity for user_guid: "+userGuid+" does not exist");	
    	}
    	    	
    	String countryCode=(String)entity.getProperty("country_code");
    	result.add(countryCode);
    	entity=affsRepository.findEntity("affs_user_countries","user_guid",userGuid);
    	if(entity!=null){
        	ArrayList<String> countryCodes = (ArrayList<String>) entity.getProperty("country_code");
        	result.addAll(countryCodes);	
       	}
    	return result;
    }
}
