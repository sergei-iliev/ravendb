package com.luee.wally.utils;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.json.simple.JSONArray;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.luee.wally.command.payment.RuleStatusType;

public enum TestDatabase {
	INSTANCE;
	
	public  void generateDB(){
		createAffs();
		createRedeemingRequests();
		createAmazonGiftCardMap();
		createPackageNameTitleMapping();
		createApplicationSettings();
		createPayPalCurrencyMap();	
		createEmailTemplates();
		createSuspiciousEmailDomainsTable();
	}
	private  void createRedeemingRequests(){
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Entity entity=createRedeemingRequestEntity("Sergey Iliev","15b5-4e3a-b398-8792a9a9f530","48bb2675-a072-4b6b-ab66-cb599a29147d", "2.1", new Date(), "com.moregames.makemoney1", "PayPal", "sergei.iliev-facilitator@gmail1.com", "DE");		
		entity.setProperty("ua_channel", "organic");
		entity.setProperty("ip_address", "123.0.0.23");		
		entity.setProperty("full_address", "baba tonka 6 for accson");
		
		ds.put(entity);		
		crearePaiedUsersEntity(entity,1.8,"EUR",createDate(30, 12, 2019));
		
		entity=createRedeemingRequestEntity("Minko","696f-4258-baef-55b6aa6550b1","ffff2675-a072-4b6b-ab66-cb599a29147d", "1",new Date(), "com.moregames.makemoney", "PayPal", "sergei.iliev-facilitator@gmail.com", "US");
		entity.setProperty("ua_channel", "supersonic");
		entity.setProperty("ip_address", "123.0.0.23");
		entity.setProperty("full_address", "baba tonka 6 for accson");
		ds.put(entity);
		crearePaiedUsersEntity(entity,0.8,"USD",createDate(1, 12, 2020));
		
		entity=createRedeemingRequestEntity("Minko1 and Macarena","696f-4258-baef-55b6aa6550b11","ffff2675-a072-4b6b-ab66-cb599a29147d1", "1", new Date(), "com.moregames.makemoney2", "PayPal", "sergei.iliev-facilitator@gmail.com", "US");
		entity.setProperty("ua_channel", "vungle");
		entity.setProperty("ip_address", "123.0.0.23");
		ds.put(entity);
		crearePaiedUsersEntity(entity,0.9,"USD",createDate(14, 1, 2020));
		
		entity=createRedeemingRequestEntity("Minko1 and Macarena","696f-4258-baef-55b6aa6550b12","ffff2675-a072-4b6b-ab66-cb599a29147d2", "1", new Date(), "com.moregames.makemoney", "PayPal", "sergei.iliev-facilitator@gmail.com", "US");		
		ds.put(entity);		
		crearePaiedUsersEntity(entity,0.9,"USD",createDate(12, 1, 2020));	

		entity=createRedeemingRequestEntity("Minko1 and Macarena","696f-4258-baef-55b6aa6550b13","ffff2675-a072-4b6b-ab66-cb599a29147d3", "1", new Date(), "com.moregames.makemoney", "PayPal", "sergei.iliev-facilitator@gmail.com", "US");		
		entity.setProperty("is_paid", false);
		ds.put(entity);		
		crearePaiedUsersEntity(entity,0.9,"USD",createDate(15, 1, 2020));	
		createRedeemingRequestsEmailJob(entity.getKey());
		
		createRedeemingRequestEntity("Viola","e701-4678-8d39-0c2485204f3b","aaaa2675-a072-4b6b-ab66-cb599a29147d", "0.1", new Date(), "com.moregames.makemoney", "Amazon", "sergei_iliev@yahoo.com", "GB");		
		
		entity=createRedeemingRequestEntity("Spas","90dd-47a1-9b47-a8892a20c7e9","bbbb2675-a072-4b6b-ab66-cb599a29147d", "3.1", new Date(), "com.moregames.makemoney", "Amazon", "sergei.iliev@gmail.com", "US");
		entity.setProperty("ua_channel", "organic");
		//put create in the past
		ZonedDateTime now=ZonedDateTime.now();
		ZonedDateTime yesterday=now.minusHours(22);		
		entity.setProperty("creation_date",Date.from(yesterday.toInstant()));
		entity.setProperty("ip_address", "77.98.102.111");
		ds.put(entity);
		crearePaiedUsersEntity(entity,2.9,"USD",createDate(1, 1, 2022));
		createRedeemingRequestsEmailJob(entity.getKey());
		
		entity=createRedeemingRequestEntity("Gurmen","8957-48bb-a089-de0adca6a91e","cccc1675-a072-4b6b-ab66-cb599a291BAA", "1.1", new Date(), "com.moregames.makemoney", "Amazon", "sergei.iliev@gmail.com", "DE");		
		crearePaiedUsersEntity(entity,15,"EUR",createDate(12, 1, 2020));
		createRedeemingRequestsEmailJob(entity.getKey());
		
		
	}
	private  Entity createRedeemingRequestEntity(String fullName,String redeemingRequestId,String userGuid,String amount, Date date,String packageName,String type,String paypalAccount,String countryCode){
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
	    JSONArray  coins=new JSONArray();		
	    coins.add(new Integer(2345));
	    coins.add(new Integer(39051));
	    
		Entity redeeming = new Entity("redeeming_requests_new");		
		redeeming.setIndexedProperty("amount", amount);
		redeeming.setIndexedProperty("full_name", fullName);
		redeeming.setIndexedProperty("user_guid",userGuid);
		redeeming.setIndexedProperty("package_name", packageName);
		redeeming.setIndexedProperty("date",date);
		redeeming.setIndexedProperty("creation_date",date);
		redeeming.setIndexedProperty("type", type);
		redeeming.setIndexedProperty("redeeming_request_id", redeemingRequestId);
		redeeming.setIndexedProperty("paypal_account", paypalAccount);
		redeeming.setIndexedProperty("country_code", countryCode);
		redeeming.setIndexedProperty("is_paid", false);		
		redeeming.setIndexedProperty("coins_per_game",coins);
		redeeming.setIndexedProperty("email", "sergei_iliev@yahoo.com");
		redeeming.setIndexedProperty("confirmed_email", false);
		ds.put(redeeming);
       
		return redeeming;
		
	}
	private void crearePaiedUsersEntity(Entity redeeminRequest,double eurCurrency,String currencyCode,Date date){
		   DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		   
		   ZonedDateTime now=ZonedDateTime.now();
		   ZonedDateTime yesterday=now.minusDays(1);
		   
		   ZonedDateTime yesterdayStart=yesterday.truncatedTo(ChronoUnit.DAYS);
		   ZonedDateTime yesterdayEnd=yesterdayStart.plusHours(24);
		   
		   Entity entity=new Entity("paid_users");	
		   //entity.setProperty("date", Date.from(yesterday.toInstant()));
		   entity.setProperty("date", date);
		   entity.setProperty("user_guid",redeeminRequest.getProperty("user_guid"));
		   entity.setProperty("paid_currency",currencyCode);
		   entity.setProperty("amount", String.valueOf(eurCurrency));
		   entity.setProperty("type", redeeminRequest.getProperty("type"));
		   entity.setProperty("eur_currency",eurCurrency);
		   entity.setProperty("email_address","mikelo@yahoo.com");
		   entity.setProperty("paypal_account","hristo@yahoo.com");
		   entity.setProperty("paid_user_success", false);
		   entity.setProperty("email_sent_success",false);
		   entity.setProperty("redeeming_request_key",KeyFactory.keyToString(redeeminRequest.getKey()));
		   entity.setProperty("redeeming_request_id",redeeminRequest.getProperty("redeeming_request_id"));
		   entity.setProperty("payment_reference_id","ref-number");
		   entity.setProperty("invoice_number","1111");		
		   ds.put(entity);
		   
	}
	
	
	private  void createAffs(){
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		   Entity entity = new Entity("affs");	
		   entity.setProperty("date", new Date());
		   entity.setProperty("experiment","surprise_update|dismiss_notif|gradual_game_release|game_coins_formula|balance_formula|threshold_20");
		   entity.setProperty("country_code","US");
		   entity.setProperty("package_name","com.moregames.makemoney");
		   entity.setProperty("gaid","c292bc0e-7eaf-4452-896f-46ffba6b229e");
		   entity.setProperty("user_guid","cccc1675-a072-4b6b-ab66-cb599a291BAA");
		   entity.setProperty("email","sergei.iliev@gmail.com");
		   ds.put(entity);
		   
		   
		   entity = new Entity("affs");	
		   entity.setProperty("date", new Date());
		   entity.setProperty("experiment","surprise_update|dismiss_notif|gradual_game_release|game_coins_formula|balance_formula|threshold_20");
		   entity.setProperty("country_code","US");
		   entity.setProperty("package_name","com.moregames.makemoney");
		   entity.setProperty("gaid","c292bc0e-7eaf-4452-896f-46ffba6b229e");
		   entity.setProperty("user_guid","bbbb2675-a072-4b6b-ab66-cb599a29147d");
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
		
		
		entity = new Entity("tango_card_country_code_mapping_external");	
		entity.setIndexedProperty("country_code","US");
		entity.setIndexedProperty("currency","USD");
		entity.setIndexedProperty("brand","Amazon.com");
		entity.setIndexedProperty("unitid","U666425");
		ds.put(entity);
		
		entity = new Entity("tango_card_country_code_mapping_external");	
		entity.setIndexedProperty("country_code","US");
		entity.setIndexedProperty("currency","EUR");
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
		entity.setIndexedProperty("name","PAYPAL_CLIENT_ID");
		entity.setIndexedProperty("value","AafeAthS3PRG_dkpJPVTkCgVc-O9pQ6o2PldSIOceWsn7nIm0H404DHDFg4svXJa63Pe8OtM55ySzppG");
		ds.put(entity);
		
		entity = new Entity("application_settings");	
		entity.setIndexedProperty("name","PAYPAL_CLIENT_SECRET");
		entity.setIndexedProperty("value","EC1ludnxStRx1M1VP9DDfz2xdTxMA8xcthmP_hRaQgWS09thWY2ihInfz5AA03DMxLjYZnR6sRQn-0XV");
		ds.put(entity);

		entity = new Entity("application_settings");	
		entity.setIndexedProperty("name","PAYPAL_MODE");
		entity.setIndexedProperty("value","sandbox");
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
		entity.setIndexedProperty("value","sergei.iliev@gmail.com");
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
		
		entity = new Entity("application_settings");	
		entity.setIndexedProperty("name","SUPPORT_EMAIL");
		entity.setIndexedProperty("value","sergei.iliev@gmail.com");
		ds.put(entity);	
	}
	
	private void createEmailTemplates(){
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Entity entity = new Entity("email_templates");	
		entity.setIndexedProperty("name","first template");
		entity.setProperty("date",new Date());
		entity.setProperty("subject","Your subject");
		entity.setIndexedProperty("type","SEND_ELIGIBLE_USER_EMAIL_TEMPLATE");		
		entity.setProperty("content",new Text("<p><b>Hello from me!</b></p><p><font color=\"#00ff00\"><b>What is up with you?</b></font></p><p><b><font color=\"#ff00ff\">Thank you!</font></b></p><p><br></p>"));
		ds.put(entity);	
		
		 
		entity = new Entity("email_templates");	
		entity.setIndexedProperty("name","confirm_email_reminder_paypal");
		entity.setProperty("date",new Date());
		entity.setProperty("subject","Your subject");
		entity.setIndexedProperty("type","CONFIRM_EMAIL_REMINDER");		
		entity.setProperty("content",new Text("<p><b>Hello ${full_name}!</b></p><p><font color=\"#00ff00\"><b>What is up with you? ${email}</b></font></p><p><b><font color=\"#ff00ff\">Thank you!</font></b></p><p><br></p>"));
		ds.put(entity);	
		
		entity = new Entity("email_templates");	
		entity.setIndexedProperty("name","confirm_email_alert_paypal");
		entity.setProperty("date",new Date());
		entity.setProperty("subject","Your subject reminder");
		entity.setIndexedProperty("type","CONFIRM_EMAIL_REMINDER");		
		entity.setProperty("content",new Text("<p><b>Alert ${full_name}!</b></p><p><font color=\"#00ff00\"><b>What is up with you? ${email}</b></font></p><p><b><font color=\"#ff00ff\">Thank you!</font></b></p><p><br></p>"));
		ds.put(entity);	
		
		entity = new Entity("email_templates");	
		entity.setIndexedProperty("name","confirm_email_reminder_amazon");
		entity.setProperty("date",new Date());
		entity.setProperty("subject","Your subject reminder");
		entity.setIndexedProperty("type","CONFIRM_EMAIL_REMINDER");		
		entity.setProperty("content",new Text("<p><b>AMAZON ${full_name}!</b></p><p><font color=\"#00ff00\"><b>What is up with you? ${email}</b></font></p><p><b><font color=\"#ff00ff\">Thank you!</font></b></p><p><br></p>"));
		ds.put(entity);	
		
		entity = new Entity("email_templates");	
		entity.setIndexedProperty("name","external_payment_cashout_notification");
		entity.setProperty("date",new Date());
		entity.setProperty("subject","External Payment Subject");
		entity.setIndexedProperty("type","EXTERNAL_PAYMENT_EMAIL");		
		entity.setProperty("content",new Text("<p><b>External Payment ${full_name}!</b></p><p><font color=\"#00ff00\"><b>What is up with you? ${email}</b></font></p><p><b><font color=\"#ff00ff\">Thank you!</font></b></p><p><br></p>"));
		ds.put(entity);	
		
	}
	private void createSuspiciousEmailDomainsTable(){
		Entity entity = new Entity("suspicious_email_domains");
		entity.setIndexedProperty("domain", "yahoo.com");
		entity.setIndexedProperty("level",RuleStatusType.Yellow.toString().toLowerCase());
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		ds.put(entity);
	}
	
	private void createRedeemingRequestsEmailJob(Key key){
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Entity entity = new Entity("redeeming_requests_email_job");	
		entity.setIndexedProperty("redeeming_request_key",KeyFactory.keyToString(key));
		entity.setProperty("created_date",new Date());				
		ds.put(entity);		
	}
	
	public static Date createDate(int day,int month,int year){
		LocalDate localDate = new LocalDate(year, month, day);
		return localDate.toDate();
	}
	
}
