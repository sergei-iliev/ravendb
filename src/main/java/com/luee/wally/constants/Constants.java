package com.luee.wally.constants;

import com.luee.wally.api.service.AffsSearchService;

public interface Constants {

	//public static final String clientId = "AafeAthS3PRG_dkpJPVTkCgVc-O9pQ6o2PldSIOceWsn7nIm0H404DHDFg4svXJa63Pe8OtM55ySzppG";
	//public static final String clientSecret = "EC1ludnxStRx1M1VP9DDfz2xdTxMA8xcthmP_hRaQgWS09thWY2ihInfz5AA03DMxLjYZnR6sRQn-0XV";
	//public static final String mode = "sandbox";

//	public static final String clientId = "ASkntpAvmDBMqRQagYpFF-Jlxevrk2lw_drkR2V2NnAIcgGxd8s-upcl2V_yZL1bGFk5vv0bJgT3cNz2";
//	public static final String clientSecret = "EBilMxHlyy8L2k8pICZ2axsFEvzdx5CPewei0TAIg7NYEeBrJXg6EptSxgJX02hhtsmtuOUe6OFkVHA9";
//	public static final String mode = "live";

	//google authorized -> see gae console settings tab
	//public static final String fromMail ="more.games.discovery@gmail.com";
	//public static final String fromMail ="laterz.app@gmail.com";

	//public static final String toInvoiceMail ="sergei.iliev@gmail.com";  //"laterz.app@gmail.com";
	
	public static final String SENDGRID_API_KEY = "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";
	
	//public static final int INVOICE_BASE= 111111;

	public static final String IMPORT_CSV_FILE = "csv/paid_users_2018.csv";
	public static final String IMPORT_CSV_FILE_2019_eur_amount = "csv/paid_users_2019_eur_amount.csv";
	public static final String IMPORT_CSV_FILE_2019_currency_amount = "csv/paid_users_2019_currency_amount.csv";
	
	
	public static final String BUCKET_NAME_DEV="luee-wally-v2-cpc.appspot.com";
	public static final String BUCKET_NAME_PROD="luee-wally.com";
	
	//public static final String EMAIL="1@1";
	//public static final String PASSWORD="1";
	
	public static final int CURSOR_SIZE=900;
	
	public static final int PAYPAL_LOOP_COUNT=25;
	public static final int LOCK_LOOP_COUNT=10;
	
	public static final String TANGO_CARD_PRODUCTION="production";
	
	/*****************Gift Card*********************************/
	public static final String CUSTOMER_NAME="BackedSoft";	
	public static final String ACCOUNT_NAME="BackedSoft";
	
	public static final String ACCOUNT_EMAIL="admin@softbakedapps.com";
	
	public static final String PLATFORM_IDENTIFIER ="PLATFORM_IDENTIFIER";
	public static final String PLATFORM_KEY="PLATFORM_KEY";
	public static final String TANGO_CARD_CUSTOMER="TANGO_CARD_CUSTOMER";
	
	public static final String PROD_PLATFORM_IDENTIFIER ="SoftBakedAppsGmbH-920";
	public static final String PROD_PLATFORM_KEY="ipPKTHdSRqtgJF!hIRsR$jFJDKEnmYdDJfD$tiBppoK";
	public static final String PROD_PLATFORM_CUSTOMER ="G71971146";
	public static final String PROD_PLATFORM_ACCOUNT ="A88393817";
	
	public static final double TANGO_CARD_ACCOUNT_BALANCE_THRESHOLD =8000.0;
	
	public static final String TANGO_CARD_API_URL="https://api.tangocard.com/raas/v2";
	
	//public static final String EMAIL_TEMPLATE_SUBJECT ="Your reward from %s!";
	//public static final String EMAIL_TEMPLATE_MESSAGE ="Congratulations! You just received your reward from %s.";
	
	
	/******************Payment Report**************************/
	
	//public static final String PAYMENT_REPORT_EMAIL_1="gil@softbakedapps.com";
	//public static final String PAYMENT_REPORT_EMAIL_2="laterz.app@softbakedapps.com";
	
	/******************AES Sequrity***************************/
	//public static final String SECRET_AES_KEY = "!SoftBacked!";
	
	public static final int PENDING =1;
	public static final int SENT =2;
	
	public static final String ENTITY_REDEEMING_REQUEST_ID ="REDEEMING_REQUEST_ENTITY";
	
	/***********************Facebook AIR Audience impression metrics *************************************/
	public static final String AGENT_NAME="BackedSoft";	
	public static final String FB_AIR_ACCESS_TOKEN="EAAIONUgazeUBADBmxXcernhbZAOsg1t3rtzqdHCaA0iVZCazflv7CkpS3WmUMC2iLxwuD2lKgfixe5ePGFyytb0YB6jKg7Feg1f3XBxrXu4gkLYFSWVNXPkuT2h0E4ngqhUpd9XVUCzoKbOZBGMhR2LvJAMg7Lw8MByL3pxDPRJIqKkZBEeVZCO810Nelg1AXn5Od0C3hGgZDZD";
	public static final String FB_AIR_APP_ID="578571959193061";
	
	public static final String VPN_SERVICE_URL="https://ps-forwarding-server.appspot.com/c/s?ip=%s&country=%s";
	
	/******************Firebase*******************************/
	public static final String FIREBASE_NOTIFICATION_URL="https://fcm.googleapis.com/fcm/send";
	
	
}
