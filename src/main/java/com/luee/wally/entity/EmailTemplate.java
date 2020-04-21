package com.luee.wally.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;

public class EmailTemplate {
	private String key;
	private String name;
	private Date date;
	private String type;
	private String subject;
	private String content;   

	public static EmailTemplate valueOf(Entity entity) {
		EmailTemplate emailTemplate = new EmailTemplate();
		emailTemplate.key = KeyFactory.keyToString(entity.getKey());
		emailTemplate.setSubject((String)entity.getProperty("subject"));
		emailTemplate.setName((String)entity.getProperty("name"));
		emailTemplate.setDate((Date) entity.getProperty("date"));
		emailTemplate.setType((String) entity.getProperty("type"));
		return emailTemplate;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	@JsonProperty
	public String getDateAsText() {
		if(date!=null){
		   SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		   return formater.format(date);
		}
		
		return "";
	}
	public void setDateAsText(String date){
		
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}



	
}
