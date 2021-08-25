package com.luee.wally.entity;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.luee.wally.json.ValueObject;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProgressMonitor implements ValueObject {
	public enum Status{
		STARTED,
		PROCESSING,
		ERROR,
		FINISHED
	}
	private String key;
	private String name;
	private Date created;
	private long total;
	private long progress;	
	private String status;
	private Date lastUpdated;
	
	public static ProgressMonitor valueOf(Entity entity) {				
		ProgressMonitor vo = new ProgressMonitor();
		vo.key = KeyFactory.keyToString(entity.getKey());
		vo.name=((String) entity.getProperty("name"));
		vo.setCreated((Date) entity.getProperty("created"));
		vo.total=((Long) entity.getProperty("total"));
		vo.progress=((Long) entity.getProperty("progress"));
		vo.setLastUpdated((Date) entity.getProperty("lastUpdated"));
		vo.status=(String) entity.getProperty("status");
		return vo;
	}
	
    public String getKey() {
		return key;
	}
    public void setKey(String key) {
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}


	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getProgress() {
		return progress;
	}

	public void setProgress(long progress) {
		this.progress = progress;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	
	

}
