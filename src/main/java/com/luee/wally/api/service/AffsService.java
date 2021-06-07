package com.luee.wally.api.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.datastore.Entity;
import com.luee.wally.admin.repository.AffsRepository;
import com.luee.wally.admin.repository.ApplicationSettingsRepository;
import com.luee.wally.api.ConnectionMgr;
import com.luee.wally.api.lock.MemoryCacheLock;
import com.luee.wally.constants.Constants;

public class AffsService extends AbstractService{
	private final Logger logger = Logger.getLogger(AffsService.class.getName());

	private AffsRepository affsRepository=new AffsRepository();
	
	//GAID==advertisingId
	public void trackTenjinEventForGaidAsync(String advertisingId,String bundleId,String event,boolean isOneTimeOnly){
    	ThreadManager.createBackgroundThread(new Runnable() {
			@Override
			public void run() {
				try {
					//read api key
					ApplicationSettingsService applicationSettingsService = new ApplicationSettingsService();
					String apiKey = applicationSettingsService
							.getApplicationSettingCached(ApplicationSettingsRepository.TENJIN_APP_KEY);
					Objects.requireNonNull(apiKey,"Tejin api key must not be NULL");
				
					AffsService affsService = new AffsService();
					try {
						if(!MemoryCacheLock.INSTANCE.lock(advertisingId)){
							Logger.getLogger(AffsService.class.getName()).log(Level.SEVERE, "Distributed LOCK TIMEOUT:");	
							return;
						}	
						Entity entity=affsRepository.findEntity("tenjin_events","gaid",advertisingId);
						if(entity!=null&&isOneTimeOnly){
							ArrayList<String> events = (ArrayList<String>) entity.getProperty("events");
							//event should not repeat
							if(events.contains(event)){
								return;
							}
						}
						Entity affs=affsRepository.findEntity("affs","gaid", advertisingId);
						Objects.requireNonNull(affs,"affs entity  must not be null for gaid="+advertisingId);
						
						affsService.sendTenjinEvent(advertisingId,bundleId,event, apiKey,(String)affs.getProperty("country_code"));
						
						affsService.saveTenjinEvent(advertisingId, event);

					} finally {			
						MemoryCacheLock.INSTANCE.unlock(advertisingId);
					}	
					
					
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Tenjin service:", e);
				}
			}
		}).start();		
	}
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
    
    public void sendTenjinEvent(String advertisingId,String bundelId,String event,String apiKey,String countryCode)throws Exception{
		Map<String,String> requestHeader=new HashMap<String,String>();
		requestHeader.put("Authorization", "Basic "+Base64.getEncoder().encodeToString(apiKey.getBytes()));				
		requestHeader.put("User-Agent", Constants.AGENT_NAME);		
		requestHeader.put("Content-Type", "application/json; charset=UTF-8");
		requestHeader.put("Accept", "application/json");		

		String url=String.format(Constants.TENJIN_CUSTOM_EVENT_URL,advertisingId,bundelId,event,countryCode);
		ConnectionMgr.INSTANCE.postJSON(url, "", requestHeader);					
    }
    /*
     * TRUE if event is added to the entity
     */
    private void saveTenjinEvent(String advertisingId,String event){
		Entity entity=affsRepository.findEntity("tenjin_events","gaid",advertisingId);
		if(entity==null){ //no record
			  ArrayList<String> list=new ArrayList<String>(1);
			  list.add(event);
	          entity=new Entity("tenjin_events");
	          entity.setIndexedProperty("gaid",advertisingId);
	          entity.setProperty("events",list);
	          affsRepository.save(entity);	          
		}else{
			ArrayList<String> events = (ArrayList<String>) entity.getProperty("events");				        	  			
			events.add(event);
			entity.setProperty("events",events);
        	affsRepository.save(entity);        	
		}
			
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
