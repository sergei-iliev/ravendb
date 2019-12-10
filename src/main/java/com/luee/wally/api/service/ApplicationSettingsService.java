package com.luee.wally.api.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.luee.wally.admin.repository.ApplicationSettingsRepository;

public class ApplicationSettingsService {
	private final Logger logger = Logger.getLogger(ApplicationSettingsService.class.getName());

	public void clearCache(){
		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService(); 
		memcache.clearAll();
	}
	
	public String getApplicationSettingCached(String name){
		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
		String value=(String)memcache.get(name);
		if(value==null){	 
			 ApplicationSettingsRepository applicationSettingsRepository=new ApplicationSettingsRepository();
			 value=applicationSettingsRepository.getApplicationSetting(name);
			 memcache.put(name, value);			 
		}
		if(value==null){
			logger.log(Level.SEVERE,"Unable to find setting by name: "+name);
		}
		return value;
	}
	
	public String getApplicationSetting(String name){
		 ApplicationSettingsRepository applicationSettingsRepository=new ApplicationSettingsRepository();
		 return applicationSettingsRepository.getApplicationSetting(name);
	}
	
	public int getApplicationSettingCachedAsInt(String name){
		String value =this.getApplicationSettingCached(name);	    
	    return Integer.parseInt(value);
	}
}
