package com.luee.wally.admin.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;
import com.luee.wally.command.PaidUserForm;
import com.luee.wally.constants.Constants;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.entity.SearchFilterTemplate;

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

	public static final String TANGO_CARD_CUSTOMER_NAME ="TANGO_CARD_CUSTOMER_NAME"; //"BackedSoft";
	public static final String TANGO_CARD_ACCOUNT_NAME ="TANGO_CARD_ACCOUNT_NAME";// "BackedSoft";
	public static final String TANGO_CARD_ACCOUNT_EMAIL ="TANGO_CARD_ACCOUNT_EMAIL"; //"admin@softbakedapps.com";

	public static final String TANGO_CARD_PLATFORM_IDENTIFIER = "TANGO_CARD_PLATFORM_IDENTIFIER";//"SoftBakedAppsTest";
	public static final String TANGO_CARD_PLATFORM_KEY = "TANGO_CARD_PLATFORM_KEY";//"cJouiuWlNHJQQgKYXRCDoejyepZ$jeGnVSEx$KD?DdW";

	public static final String TANGO_CARD_EMAIL_TEMPLATE_SUBJECT ="TANGO_CARD_EMAIL_TEMPLATE_SUBJECT"; //"Your reward from %s!";
	public static final String TANGO_CARD_EMAIL_TEMPLATE_MESSAGE ="TANGO_CARD_EMAIL_TEMPLATE_MESSAGE"; //"Congratulations! You just received your reward from %s.";

	public static final String PAYMENT_REPORT_EMAIL_1="PAYMENT_REPORT_EMAIL_1";//"gil@softbakedapps.com";
	public static final String PAYMENT_REPORT_EMAIL_2="PAYMENT_REPORT_EMAIL_2";//"laterz.app@gmail.com";


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
		System.out.println(name);
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
