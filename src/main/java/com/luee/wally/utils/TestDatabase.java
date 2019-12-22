package com.luee.wally.utils;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
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
		createPayPalCurrencyMap();
		crearePayedUsersEntity();
		
	}
	private  void createRedeemingRequests(){
		createRedeemingRequestEntity("Sergey Iliev","15b5-4e3a-b398-8792a9a9f530","48bb2675-a072-4b6b-ab66-cb599a29147d", "1", new Date(), "com.moregames.makemoney", "PayPal", "sergei.iliev-facilitator@gmail.com", "US");		
		createRedeemingRequestEntity("Minko","696f-4258-baef-55b6aa6550b1","ffff2675-a072-4b6b-ab66-cb599a29147d", "1", new Date(), "com.moregames.makemoney", "PayPal", "sergei.iliev-facilitator@gmail.com", "US");
		createRedeemingRequestEntity("Viola","e701-4678-8d39-0c2485204f3b","aaaa2675-a072-4b6b-ab66-cb599a29147d", "0.1", new Date(), "com.moregames.makemoney", "Amazon", "sergei_iliev@yahoo.com", "GB");		
		createRedeemingRequestEntity("Spas","90dd-47a1-9b47-a8892a20c7e9","bbbb2675-a072-4b6b-ab66-cb599a29147d", "1.1", new Date(), "com.moregames.makemoney", "Amazon", "sergei.iliev@gmail.com", "US");
		createRedeemingRequestEntity("Gurmen","8957-48bb-a08c-de0adca6a91e","cccc1675-a072-4b6b-ab66-cb599a29147d", "1.1", new Date(), "com.moregames.makemoney", "Amazon", "sergei.iliev@gmail.com", "DE");		
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
	private void crearePayedUsersEntity(){
		   DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		   
		   ZonedDateTime now=ZonedDateTime.now();
		   ZonedDateTime yesterday=now.minusDays(1);
		   
		   ZonedDateTime yesterdayStart=yesterday.truncatedTo(ChronoUnit.DAYS);
		   ZonedDateTime yesterdayEnd=yesterdayStart.plusHours(24);
		   
		   Entity entity=new Entity("paid_users");	
		   entity.setProperty("date", Date.from(yesterday.toInstant()));
		   entity.setProperty("user_guid","48bb2675-a072-4b6b-ab66-cb599a29147d");
		   entity.setProperty("paid_currency","USD");
		   entity.setProperty("amount", "13.2");
		   entity.setProperty("type", "PayPal");
		   entity.setProperty("eur_currency",12.0);
		   entity.setProperty("email_address","mikelo@yahoo.com");
		   entity.setProperty("paypal_account","hristo@yahoo.com");
		   entity.setProperty("paid_user_success", true);
		   entity.setProperty("email_sent_success",true);
		   entity.setProperty("redeeming_request_key","");
		   entity.setProperty("redeeming_request_id","15b5-4e3a-b398-8792a9a9f530");
		   entity.setProperty("payment_reference_id","ref-number");
		   entity.setProperty("invoice_number","111");		
		   ds.put(entity);
		   
		   entity=new Entity("paid_users");	
		   entity.setProperty("date", Date.from(yesterday.toInstant()));
		   entity.setProperty("user_guid","ffff2675-a072-4b6b-ab66-cb599a29147d");
		   entity.setProperty("paid_currency","USD");
		   entity.setProperty("amount", "3.2");
		   entity.setProperty("type", "PayPal");
		   entity.setProperty("eur_currency",2.0);
		   entity.setProperty("email_address","sergio@yahoo.com");
		   entity.setProperty("paypal_account","mikelo@yahoo.com");
		   entity.setProperty("paid_user_success", true);
		   entity.setProperty("email_sent_success",true);
		   entity.setProperty("redeeming_request_key","");
		   entity.setProperty("redeeming_request_id","15b5-4e3a-b398-8792a9a9f530");
		   entity.setProperty("payment_reference_id","ref-number");
		   entity.setProperty("invoice_number","111");		
		   ds.put(entity);
	}
	
	
	private  void createPayPalCurrencyMap(){
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Entity entity = new Entity("paypal_country_code_mapping");	
		entity.setIndexedProperty("country_code","US");
		entity.setIndexedProperty("currency","USD");
		ds.put(entity);
		
		entity = new Entity("paypal_country_code_mapping");	
		entity.setIndexedProperty("country_code","GB");
		entity.setIndexedProperty("currency","GBP");
		ds.put(entity);
		
		entity = new Entity("paypal_country_code_mapping");	
		entity.setIndexedProperty("country_code","UK");
		entity.setIndexedProperty("currency","GBP");
		ds.put(entity);

		entity = new Entity("paypal_country_code_mapping");	
		entity.setIndexedProperty("country_code","CA");
		entity.setIndexedProperty("currency","CAD");
		ds.put(entity);
		
		entity = new Entity("paypal_country_code_mapping");	
		entity.setIndexedProperty("country_code","DE");
		entity.setIndexedProperty("currency","EUR");
		ds.put(entity);
		
		entity = new Entity("paypal_country_code_mapping");	
		entity.setIndexedProperty("country_code","FR");
		entity.setIndexedProperty("currency","EUR");
		ds.put(entity);

		entity = new Entity("paypal_country_code_mapping");	
		entity.setIndexedProperty("country_code","ES");
		entity.setIndexedProperty("currency","EUR");
		ds.put(entity);

		entity = new Entity("paypal_country_code_mapping");	
		entity.setIndexedProperty("country_code","IT");
		entity.setIndexedProperty("currency","EUR");
		ds.put(entity);
		
		entity = new Entity("paypal_country_code_mapping");	
		entity.setIndexedProperty("country_code","RU");
		entity.setIndexedProperty("currency","USD");
		ds.put(entity);		

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
		entity.setIndexedProperty("value","true");
		ds.put(entity);
		
		entity = new Entity("application_settings");	
		entity.setIndexedProperty("name","SHOW_PAYPAL_PAY");
		entity.setIndexedProperty("value","true");
		ds.put(entity);
		
		entity = new Entity("application_settings");	
		entity.setIndexedProperty("name","FROM_MAIL");
		entity.setIndexedProperty("value","more.games.discovery@gmail.com");
		ds.put(entity);
		
		entity = new Entity("application_settings");	
		entity.setIndexedProperty("name","LOGIN_EMAIL");
		entity.setIndexedProperty("value","1@1");
		ds.put(entity);
		
		entity = new Entity("application_settings");	
		entity.setIndexedProperty("name","LOGIN_PASSWORD");
		entity.setIndexedProperty("value","1");
		ds.put(entity);
		
		
		entity = new Entity("application_settings");	
		entity.setIndexedProperty("name","TO_INVOICE_MAIL");
		entity.setIndexedProperty("value","laterz.app@gmail.com");
		ds.put(entity);
		
		entity = new Entity("application_settings");	
		entity.setIndexedProperty("name","INVOICE_BASE");
		entity.setIndexedProperty("value","111111");
		ds.put(entity);
		
		entity = new Entity("application_settings");	
		entity.setIndexedProperty("name","TANGO_CARD_CUSTOMER_NAME");
		entity.setIndexedProperty("value","BackedSoft");
		ds.put(entity);
		
		entity = new Entity("application_settings");	
		entity.setIndexedProperty("name","TANGO_CARD_ACCOUNT_NAME");
		entity.setIndexedProperty("value","BackedSoft");
		ds.put(entity);
		
		
		entity = new Entity("application_settings");	
		entity.setIndexedProperty("name","TANGO_CARD_ACCOUNT_EMAIL");
		entity.setIndexedProperty("value","admin@softbakedapps.com");
		ds.put(entity);
		
		entity = new Entity("application_settings");	
		entity.setIndexedProperty("name","TANGO_CARD_PLATFORM_IDENTIFIER");
		entity.setIndexedProperty("value","SoftBakedAppsTest");
		ds.put(entity);
		
		entity = new Entity("application_settings");	
		entity.setIndexedProperty("name","TANGO_CARD_PLATFORM_KEY");
		entity.setIndexedProperty("value","cJouiuWlNHJQQgKYXRCDoejyepZ$jeGnVSEx$KD?DdW");
		ds.put(entity);
		
		
		entity = new Entity("application_settings");	
		entity.setIndexedProperty("name","TANGO_CARD_EMAIL_TEMPLATE_SUBJECT");
		entity.setIndexedProperty("value","Your reward from %s!");
		ds.put(entity);
		
		entity = new Entity("application_settings");	
		entity.setIndexedProperty("name","TANGO_CARD_EMAIL_TEMPLATE_MESSAGE");
		entity.setIndexedProperty("value","Congratulations! You just received your reward from %s.");
		ds.put(entity);
		
		entity = new Entity("application_settings");	
		entity.setIndexedProperty("name","PAYMENT_REPORT_EMAIL_1");
		entity.setIndexedProperty("value","gil@softbakedapps.com");
		ds.put(entity);
		
		entity = new Entity("application_settings");	
		entity.setIndexedProperty("name","PAYMENT_REPORT_EMAIL_2");
		entity.setIndexedProperty("value","laterz.app@gmail.com");
		ds.put(entity);
			
		entity = new Entity("application_settings");	
		entity.setIndexedProperty("name","TANGO_CARD_ENVIRONMENT");
		entity.setIndexedProperty("value","sandbox");
		ds.put(entity);	
		
		entity = new Entity("application_settings");	
		entity.setIndexedProperty("name","NO_REPLY_EMAIL");
		entity.setIndexedProperty("value","no-reply@softbakedapps.com");
		ds.put(entity);	
		
	}
}
