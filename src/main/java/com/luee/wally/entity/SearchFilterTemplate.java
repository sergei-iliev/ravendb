package com.luee.wally.entity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.luee.wally.command.PaymentEligibleUserForm;
import com.luee.wally.json.JSONUtils;

public class SearchFilterTemplate {

	private String key;
	private Date date;
	private String name;
	private String form;
    private String dateAsText;
	
	public static SearchFilterTemplate valueOf(Entity entity){
		SearchFilterTemplate template=new SearchFilterTemplate();
		template.key= KeyFactory.keyToString(entity.getKey());
		template.date= (Date)entity.getProperty("date");
		template.form=((String)entity.getProperty("form"));
		template.name=((String)entity.getProperty("name"));		
		return template;
	}
	@JsonIgnore
	public PaymentEligibleUserForm create() throws IOException{
		return JSONUtils.readObject(form,PaymentEligibleUserForm.class);
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


	public String getKey() {
		return key;
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getForm() {
		return form;
	}
	public void setForm(String form) {
		this.form = form;
	}
	
}
