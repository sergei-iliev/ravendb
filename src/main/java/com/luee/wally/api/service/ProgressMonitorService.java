package com.luee.wally.api.service;

import java.util.Date;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.luee.wally.admin.repository.ProgressMonitorRepository;
import com.luee.wally.entity.ProgressMonitor;

public class ProgressMonitorService extends AbstractService{
	private final Logger logger = Logger.getLogger(ProgressMonitorService.class.getName());
	
    private ProgressMonitorRepository progressMonitorRepository=new ProgressMonitorRepository(); 

	public ProgressMonitor createProgressMonitor(String name){
	  Entity entity=new Entity("progress_monitor"); 
	  entity.setProperty("name",name);
	  entity.setProperty("created", new Date());
	  entity.setProperty("status", ProgressMonitor.Status.STARTED.name());
	  entity.setProperty("created", new Date());
	  entity.setProperty("lastUpdated", new Date());
	  entity.setProperty("total", 0l);
	  entity.setProperty("progress", 0l);
	  progressMonitorRepository.save(entity);
	  
	  return ProgressMonitor.valueOf(entity);	
	}
	
	public ProgressMonitor getProgressMonitor(String id){
		Entity entity=progressMonitorRepository.findEntityByKey(id);
		return entity==null?null:ProgressMonitor.valueOf(entity);	
	}
	
	public void updateProgressMonitor(String id,long progress,long total,String status){
		Entity entity=progressMonitorRepository.findEntityByKey(id);
		entity.setProperty("lastUpdated", new Date());
		entity.setProperty("total",total);
		entity.setProperty("progress",progress);
		entity.setProperty("status", status);
		progressMonitorRepository.save(entity);
	}
	
	public void updateProgressMonitor(String id,long progress,String status){
		Entity entity=progressMonitorRepository.findEntityByKey(id);
		entity.setProperty("lastUpdated", new Date());
		entity.setProperty("progress", progress);		
		entity.setProperty("status", status);
		progressMonitorRepository.save(entity);
	}
	
	public void updateProgressMonitor(String id,String status){
		Entity entity=progressMonitorRepository.findEntityByKey(id);
		entity.setProperty("lastUpdated", new Date());
		entity.setProperty("status", status);		
		progressMonitorRepository.save(entity);
	}
	
	
	
	
}
