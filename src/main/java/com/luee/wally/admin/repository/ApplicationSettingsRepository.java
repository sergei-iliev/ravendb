package com.luee.wally.admin.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;

public class ApplicationSettingsRepository extends AbstractRepository {
	private final Logger logger = Logger.getLogger(ApplicationSettingsRepository.class.getName());


	public static final String SHOW_TANGO_GIFT_CARD = "SHOW_TANGO_GIFT_CARD";
	public static final String SHOW_PAYPAL_PAY = "SHOW_PAYPAL_PAY";

	public static final String FROM_MAIL = "FROM_MAIL"; // ="more.games.discovery@gmail.com";
	public static final String TO_INVOICE_MAIL = "TO_INVOICE_MAIL"; // toInvoiceMail
															// ="laterz.app@gmail.com";

	public static final String INVOICE_BASE = "INVOICE_BASE"; // 111111;

	public static final String LOGIN_EMAIL = "LOGIN_EMAIL";// "1@1";
	public static final String LOGIN_PASSWORD = "LOGIN_PASSWORD"; // "1";

	public static final String TANGO_CARD_ENVIRONMENT ="TANGO_CARD_ENVIRONMENT"; //{sandbox,production}
	public static final String TANGO_CARD_CUSTOMER_NAME ="TANGO_CARD_CUSTOMER_NAME"; //"BackedSoft";
	public static final String TANGO_CARD_ACCOUNT_NAME ="TANGO_CARD_ACCOUNT_NAME";// "BackedSoft";
	public static final String TANGO_CARD_ACCOUNT_EMAIL ="TANGO_CARD_ACCOUNT_EMAIL"; //"admin@softbakedapps.com";

	public static final String TANGO_CARD_PLATFORM_IDENTIFIER = "TANGO_CARD_PLATFORM_IDENTIFIER";//"SoftBakedAppsTest";
	public static final String TANGO_CARD_PLATFORM_KEY = "TANGO_CARD_PLATFORM_KEY";//"cJouiuWlNHJQQgKYXRCDoejyepZ$jeGnVSEx$KD?DdW";

	public static final String JP_TANGO_CARD_CUSTOMER_NAME="JP_TANGO_CARD_CUSTOMER_NAME";
	public static final String JP_TANGO_CARD_PLATFORM_IDENTIFIER="JP_TANGO_CARD_PLATFORM_IDENTIFIER";
	public static final String JP_TANGO_CARD_PLATFORM_KEY="JP_TANGO_CARD_PLATFORM_KEY";
	public static final String JP_TANGO_CARD_ACCOUNT_NAME="JP_TANGO_CARD_ACCOUNT_NAME";
	
	public static final String TANGO_CARD_EMAIL_TEMPLATE_SUBJECT ="TANGO_CARD_EMAIL_TEMPLATE_SUBJECT"; //"Your reward from %s!";
	public static final String TANGO_CARD_EMAIL_TEMPLATE_MESSAGE ="TANGO_CARD_EMAIL_TEMPLATE_MESSAGE"; //"Congratulations! You just received your reward from %s.";

	public static final String PAYMENT_REPORT_EMAIL_1="PAYMENT_REPORT_EMAIL_1";//"gil@softbakedapps.com";
	public static final String PAYMENT_REPORT_EMAIL_2="PAYMENT_REPORT_EMAIL_2";//"laterz.app@gmail.com";

	public static final String NO_REPLY_EMAIL="NO_REPLY_EMAIL";
	
	public static final String PAYPAL_CLIENT_ID="PAYPAL_CLIENT_ID";
	public static final String PAYPAL_CLIENT_SECRET="PAYPAL_CLIENT_SECRET";
	public static final String PAYPAL_MODE="PAYPAL_MODE";
	
	public static final String SECRET_AES_KEY ="SECRET_AES_KEY";
	
	public static final String SUPPORT_EMAIL ="SUPPORT_EMAIL";
	
	public static final String PAYPAL_MERCHANT_API_USERNAME="PAYPAL_MERCHANT_API_USERNAME";	
	public static final String PAYPAL_MERCHANT_API_PASSWORD="PAYPAL_MERCHANT_API_PASSWORD";	
	public static final String PAYPAL_MERCHANT_API_SIGNATURE="PAYPAL_MERCHANT_API_SIGNATURE";

	
	
	public Map<String, String> getApplicationSettings() {
		Map<String, String> map = new HashMap<>();

		DatastoreService ds = createDatastoreService(Consistency.EVENTUAL);
		Query query = new Query("application_settings");

		PreparedQuery pq = ds.prepare(query);
		QueryResultList<Entity> entities = pq.asQueryResultList(FetchOptions.Builder.withDefaults());

		for (Entity entity : entities) {
			map.put((String) entity.getProperty("name"), (String) entity.getProperty("value"));
		}

		return map;
	}
	public String getApplicationSetting(String name) {
		DatastoreService ds = createDatastoreService(Consistency.EVENTUAL);
		Query query = new Query("application_settings");
	    query.setFilter(new FilterPredicate("name", FilterOperator.EQUAL, name));
		PreparedQuery pq = ds.prepare(query);
		Entity entity=pq.asSingleEntity();
		if(entity!=null){
			return (String)entity.getProperty("value");
		}else{
		    return null;	
		}
			 		 		
	}
}
