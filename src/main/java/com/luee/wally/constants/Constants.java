package com.luee.wally.constants;

public interface Constants {

	public static final String clientId = "AafeAthS3PRG_dkpJPVTkCgVc-O9pQ6o2PldSIOceWsn7nIm0H404DHDFg4svXJa63Pe8OtM55ySzppG";
	public static final String clientSecret = "EC1ludnxStRx1M1VP9DDfz2xdTxMA8xcthmP_hRaQgWS09thWY2ihInfz5AA03DMxLjYZnR6sRQn-0XV";
	public static final String mode = "sandbox";

//	public static final String clientId = "ASkntpAvmDBMqRQagYpFF-Jlxevrk2lw_drkR2V2NnAIcgGxd8s-upcl2V_yZL1bGFk5vv0bJgT3cNz2";
//	public static final String clientSecret = "EBilMxHlyy8L2k8pICZ2axsFEvzdx5CPewei0TAIg7NYEeBrJXg6EptSxgJX02hhtsmtuOUe6OFkVHA9";
//	public static final String mode = "live";

	//google authorized -> see gae console settings tab
	public static final String fromMail ="more.games.discovery@gmail.com";
	//public static final String fromMail ="laterz.app@gmail.com";

	public static final String toInvoiceMail ="sergei.iliev@gmail.com";  //"laterz.app@gmail.com";
	public static final String SENDGRID_API_KEY = "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";
	
	public static final int INVOICE_BASE= 111111;

	public static final String IMPORT_CSV_FILE = "csv/paid_users_2018.csv";
	public static final String IMPORT_CSV_FILE_2019_eur_amount = "csv/paid_users_2019_eur_amount.csv";
	public static final String IMPORT_CSV_FILE_2019_currency_amount = "csv/paid_users_2019_currency_amount.csv";
	
	
	public static final String BUCKET_NAME_DEV="luee-wally-v2-cpc.appspot.com";
	public static final String BUCKET_NAME_PROD="luee-wally.com";
	
	public static final String EMAIL="1@1";
	public static final String PASSWORD="1";
	
	public static final int CURSOR_SIZE=1000;
	
	
	/*****************Gift Card*********************************/
	public static final String CUSTOMER_NAME="BackedSoft";	
	public static final String ACCOUNT_NAME="BackedSoft";
	
	public static final String ACCOUNT_EMAIL="admin@softbakedapps.com";
	
	public static final String PLATFORM_IDENTIFIER ="SoftBakedAppsTest";
	public static final String PLATFORM_KEY="cJouiuWlNHJQQgKYXRCDoejyepZ$jeGnVSEx$KD?DdW";
	
	public static final String EMAIL_TEMPLATE_SUBJECT ="Your reward from %s!";
	public static final String EMAIL_TEMPLATE_MESSAGE ="Congratulations! You just received your reward from %s.";
	
}
