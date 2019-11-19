package com.luee.wally.utils;

import java.util.Date;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

public enum TestDatabase {
	INSTANCE;
	
	public  void generateDB(){
		createRedeemingRequests();
		createAmazonGiftCardMap();
		createPackageNameTitleMapping();
		createApplicationSettings();
	}
	private  void createRedeemingRequests(){
		createRedeemingRequestEntity("Sergey Iliev","15b5-4e3a-b398-8792a9a9f530","48bb2675-a072-4b6b-ab66-cb599a29147d", "12", new Date(), "com.moregames.makemoney", "PayPal", "sergei_iliev@yahoo.com", "US");		
		createRedeemingRequestEntity("Minko","696f-4258-baef-55b6aa6550b1","ffff2675-a072-4b6b-ab66-cb599a29147d", "14", new Date(), "com.moregames.makemoney", "PayPal", "sergei_iliev@yahoo.com", "US");
		createRedeemingRequestEntity("Viola","e701-4678-8d39-0c2485204f3b","aaaa2675-a072-4b6b-ab66-cb599a29147d", "24", new Date(), "com.moregames.makemoney", "Amazon", "sergei_iliev@yahoo.com", "GB");		
		createRedeemingRequestEntity("Spas","90dd-47a1-9b47-a8892a20c7e9","bbbb2675-a072-4b6b-ab66-cb599a29147d", "27", new Date(), "com.moregames.makemoney", "Amazon", "sergei.iliev@gmail.com", "US");
		createRedeemingRequestEntity("Gurmen","8957-48bb-a08c-de0adca6a91e","cccc1675-a072-4b6b-ab66-cb599a29147d", "3", new Date(), "com.moregames.makemoney", "Amazon", "sergei.iliev@gmail.com", "DE");		
	}
	private  void createRedeemingRequestEntity(String fullName,String redeemingRequestId,String userGuid,String amount,Date date,String packageName,String type,String paypalAccount,String countryCode){
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
	
		Entity redeeming = new Entity("redeeming_requests_new");		
		redeeming.setIndexedProperty("amount", amount);
		redeeming.setIndexedProperty("full_name", fullName);
		redeeming.setIndexedProperty("user_guid",userGuid);
		redeeming.setIndexedProperty("package_name", packageName);
		redeeming.setIndexedProperty("date",date);		
		redeeming.setIndexedProperty("type", type);
		redeeming.setIndexedProperty("redeeming_request_id", redeemingRequestId);
		redeeming.setIndexedProperty("paypal_account", paypalAccount);
		redeeming.setIndexedProperty("country_code", countryCode);
		redeeming.setIndexedProperty("is_paid", false);
		redeeming.setIndexedProperty("email", paypalAccount);
		redeeming.setIndexedProperty("package_name","com.matchmine.app");
		ds.put(redeeming);

		
	}
	
	private  void createAmazonGiftCardMap(){
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Entity entity = new Entity("tango_card_country_code_mapping");	
		entity.setIndexedProperty("country_code","US");
		entity.setIndexedProperty("currency","USD");
		entity.setIndexedProperty("brand","Amazon.com");
		entity.setIndexedProperty("unitid","U666425");
		ds.put(entity);
		
		entity = new Entity("tango_card_country_code_mapping");	
		entity.setIndexedProperty("country_code","GB");
		entity.setIndexedProperty("currency","GBP");
		entity.setIndexedProperty("brand","Amazon.co.uk");
		entity.setIndexedProperty("unitid","U666425");
		ds.put(entity);
		
		entity = new Entity("tango_card_country_code_mapping");	
		entity.setIndexedProperty("country_code","UK");
		entity.setIndexedProperty("currency","GBP");
		entity.setIndexedProperty("brand","Amazon.co.uk");
		entity.setIndexedProperty("unitid","U666425");
		ds.put(entity);

		entity = new Entity("tango_card_country_code_mapping");	
		entity.setIndexedProperty("country_code","CA");
		entity.setIndexedProperty("currency","CAD");
		entity.setIndexedProperty("brand","Amazon.ca");
		entity.setIndexedProperty("unitid","U666425");
		ds.put(entity);
		
		entity = new Entity("tango_card_country_code_mapping");	
		entity.setIndexedProperty("country_code","DE");
		entity.setIndexedProperty("currency","EUR");
		entity.setIndexedProperty("brand","Amazon.de");
		entity.setIndexedProperty("unitid","U666425");
		ds.put(entity);
		
		entity = new Entity("tango_card_country_code_mapping");	
		entity.setIndexedProperty("country_code","FR");
		entity.setIndexedProperty("currency","EUR");
		entity.setIndexedProperty("brand","Amazon.fr");
		entity.setIndexedProperty("unitid","U666425");
		ds.put(entity);

		entity = new Entity("tango_card_country_code_mapping");	
		entity.setIndexedProperty("country_code","ES");
		entity.setIndexedProperty("currency","EUR");
		entity.setIndexedProperty("brand","Amazon.es");
		entity.setIndexedProperty("unitid","U666425");
		ds.put(entity);

		entity = new Entity("tango_card_country_code_mapping");	
		entity.setIndexedProperty("country_code","IT");
		entity.setIndexedProperty("currency","EUR");
		entity.setIndexedProperty("brand","Amazon.it");
		entity.setIndexedProperty("unitid","U666425");
		ds.put(entity);
		
		entity = new Entity("tango_card_country_code_mapping");	
		entity.setIndexedProperty("country_code","RU");
		entity.setIndexedProperty("currency","USD");
		entity.setIndexedProperty("brand","Amazon.com");
		entity.setIndexedProperty("unitid","U666425");
		ds.put(entity);		

	}
	
	public  void createPackageNameTitleMapping(){
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Entity entity = new Entity("package_name_title_mapping");	
		entity.setIndexedProperty("package_name","com.moregames.makemoney");
		entity.setIndexedProperty("title","PlaySpot");
		ds.put(entity);
		
		entity = new Entity("package_name_title_mapping");	
		entity.setIndexedProperty("package_name","com.gametrix.app");
		entity.setIndexedProperty("title","Gametrix");
		ds.put(entity);
		
		entity = new Entity("package_name_title_mapping");	
		entity.setIndexedProperty("package_name","com.matchmine.app");
		entity.setIndexedProperty("title","Match Mine");
		ds.put(entity);
	}
	
	public  void createApplicationSettings(){
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Entity entity = new Entity("application_settings");	
		entity.setIndexedProperty("name","SHOW_TANGO_GIFT_CARD");
		entity.setIndexedProperty("value","false");
		ds.put(entity);
	}
}
