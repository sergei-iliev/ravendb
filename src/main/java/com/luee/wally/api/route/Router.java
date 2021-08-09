package com.luee.wally.api.route;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.luee.wally.admin.controller.AffsSearchController;
import com.luee.wally.admin.controller.ApplicationSettingsController;
import com.luee.wally.admin.controller.CampaignSearchController;
import com.luee.wally.admin.controller.ConfirmEmailController;
import com.luee.wally.admin.controller.EmailTemplateController;
import com.luee.wally.admin.controller.ExportController;
import com.luee.wally.admin.controller.FBAffsSearchController;
import com.luee.wally.admin.controller.GiftCardController;
import com.luee.wally.admin.controller.ImportController;
import com.luee.wally.admin.controller.LoginController;
import com.luee.wally.admin.controller.PaidUsersController;
import com.luee.wally.admin.controller.PaymentController;
import com.luee.wally.admin.controller.PaymentOrderTransactionController;
import com.luee.wally.admin.controller.PaymentReportsController;
import com.luee.wally.admin.controller.QAController;
import com.luee.wally.admin.controller.SearchFilterTemplateController;
import com.luee.wally.admin.controller.UserRevenueReportsController;
import com.luee.wally.admin.controller.UsersController;

/*
 * Based on
 * https://github.com/Rezve/kodvel/blob/master/src/java/kodvel/core/route/DoRouting.java
 */
public enum Router {
INSTANCE;
	
    private  Map<String, Route> httpGetRouteList=new HashMap<>();
    private  Map<String, Route> httpPostRouteList=new HashMap<>();
    private  Map<String, Route> httpPutRouteList=new HashMap<>();
    private  Map<String, Route> httpDeleteRouteList=new HashMap<>();
    

    private Router() {
        initRoutes();
    }
    
    /**
     * Initialize HashMaps.
     */
    private void initRoutes() {
        //get
    	 httpGetRouteList.put("/administration/login", new Route(new LoginController(),"index"));
    	 httpGetRouteList.put("/administration", new Route(new AffsSearchController(),"index"));
    	 httpGetRouteList.put("/administration/revenue/fb", new Route(new FBAffsSearchController(),"index"));
    	 httpGetRouteList.put("/administration/exportgaid", new Route(new AffsSearchController(),"exportGaid"));

    	 
    	 httpGetRouteList.put("/administration/logout", new Route(new LoginController(),"logout"));
    	 httpGetRouteList.put("/administration/campaign", new Route(new CampaignSearchController(),"index"));
    	 httpGetRouteList.put("/administration/import/user/revenue/2019", new Route(new ImportController(),"importUserRevenue2019"));
    	 httpGetRouteList.put("/administration/import/user/revenue/2019/background", new Route(new ImportController(),"importUserRevenue2019InBackground"));
    	 httpGetRouteList.put("/administration/payment/eligibleusers", new Route(new PaymentController(),"index"));
    	 httpGetRouteList.put("/administration/search/templates", new Route(new SearchFilterTemplateController(),"getSearchFilterTemplates"));
    	 httpGetRouteList.put("/administration/search/template/filter", new Route(new PaymentController(),"searchByFilterTemplate"));
    	 httpGetRouteList.put("/administration/payment/test", new Route(new PaymentController(),"test"));
    	 httpGetRouteList.put("/administration/job/reports/payment/yesterday", new Route(new PaymentReportsController(),"getPaymentReportForYesterday"));
    	 httpGetRouteList.put("/administration/job/reports/payment/external/yesterday", new Route(new PaymentReportsController(),"getExternalPaymentReportForYesterday"));
    	 httpGetRouteList.put("/administration/job/redeemingrequest/email/reminder", new Route(new EmailTemplateController(),"redeemingRequestEmailJob"));
    	 httpGetRouteList.put("/administration/job/giftcard/account/balance", new Route(new GiftCardController(),"checkGiftCardAccountBalanceJob"));
    	 httpGetRouteList.put("/administration/job/user/revenue/daily",new Route(new UserRevenueReportsController(),"runUserAdRevenueDailyReport"));
    	 httpGetRouteList.put("/administration/job/user/revenue/daily/fb",new Route(new UserRevenueReportsController(),"runfbUserAdRevenueDailyReport"));
    	 httpGetRouteList.put("/administration/job/user/revenue/run",new Route(new UserRevenueReportsController(),"runUserAdRevenueDailyReportByDate"));
    	 httpGetRouteList.put("/administration/job/user/revenue/run/fb",new Route(new UserRevenueReportsController(),"runfbUserAdRevenueDailyReportByDate"));
    	 httpGetRouteList.put("/administration/job/order/transactions", new Route(new PaymentOrderTransactionController(),"runOrderTransactionReport"));
    	 httpGetRouteList.put("/administration/job/order/paypal/balance/alert/threshold", new Route(new PaymentOrderTransactionController(),"runPayPalBalanceThreshold"));
    	 httpGetRouteList.put("/administration/job/order/paypal/balance/alert/daily", new Route(new PaymentOrderTransactionController(),"runPayPalBalanceDaily"));
	 
    	 
    	 
    	 
    	 httpGetRouteList.put("/administration/payment/paidusers/export",new Route(new ExportController(),"index"));
    	 httpGetRouteList.put("/administration/payment/paidusers/removed",new Route(new PaidUsersController(),"removed"));
    	 
    	 
    	 httpGetRouteList.put("/administration/payment/paidusers", new Route(new PaidUsersController(),"index"));
    	 httpGetRouteList.put("/administration/payment/user/unremove", new Route(new PaidUsersController(),"unremove"));
    	 httpGetRouteList.put("/administration/payment/confirmemail", new Route(new ConfirmEmailController(),"index"));
    	 
    	 httpGetRouteList.put("/administration/settings", new Route(new ApplicationSettingsController(),"index"));
    	 httpGetRouteList.put("/administration/settings/cache/clear", new Route(new ApplicationSettingsController(),"clearCache"));    	     	 
    	 
    	 httpGetRouteList.put("/administration/email/template/content", new Route(new EmailTemplateController(),"getEmailTemplateContent"));
    	 httpGetRouteList.put("/administration/email/template/list", new Route(new EmailTemplateController(),"getEmailTemplates"));
    	 
    	 httpGetRouteList.put("/administration/users/data/delete", new Route(new UsersController(),"index"));
    	 httpGetRouteList.put("/administration/users/guid/exists", new Route(new UsersController(),"userExists"));
    	 //QA
    	 httpGetRouteList.put("/administration/qa", new Route(new QAController(),"index"));
    	 httpGetRouteList.put("/administration/qa/experiment", new Route(new QAController(),"experiment"));
    	 
    	 //*********post*******
    	 httpPostRouteList.put("/administration/search", new Route(new AffsSearchController(),"search"));
    	 httpPostRouteList.put("/administration/exportgaid/run", new Route(new AffsSearchController(),"runExportGaid"));    	
    	 httpPostRouteList.put("/administration/search/fb", new Route(new FBAffsSearchController(),"search"));
    	 
    	 httpPostRouteList.put("/administration/campaign/search", new Route(new CampaignSearchController(),"search"));
    	 httpPostRouteList.put("/administration/login", new Route(new LoginController(),"login"));
    	 httpPostRouteList.put("/administration/import/user/revenue/2019/background", new Route(new ImportController(),"importUserRevenue2019InBackground"));
    	 httpPostRouteList.put("/administration/payment/eligibleusers/search", new Route(new PaymentController(),"search"));
    	 httpPostRouteList.put("/administration/payment/eligibleusers/edit/email", new Route(new PaymentController(),"editEmail"));
    	 httpPostRouteList.put("/administration/payment/eligibleusers/edit/paypal", new Route(new PaymentController(),"editPayPalAccount"));
    	 httpPostRouteList.put("/administration/payment/eligibleusers/rules/status", new Route(new PaymentController(),"getRedeemingRequestRuleResult"));
    	 httpPostRouteList.put("/administration/payment/confirmemail", new Route(new ConfirmEmailController(),"confirmEmail"));
    	 httpPostRouteList.put("/administration/job/user/revenue/background/fb",new Route(new UserRevenueReportsController(),"runfbUserAdRevenueDailyReportInBackground"));
    	 httpPostRouteList.put("/administration/job/user/revenue/background",new Route(new UserRevenueReportsController(),"runUserAdRevenueDailyReportInBackground"));
    	 
    	 httpPostRouteList.put("/administration/search/template", new Route(new SearchFilterTemplateController(),"saveSearchFilterTemplate"));
    	 httpPostRouteList.put("/administration/payment/removal/reason", new Route(new PaymentController(),"saveUserPaymentRemovalReason"));
    	 httpPostRouteList.put("/administration/payment/user/pay", new Route(new PaymentController(),"pay"));
    	 httpPostRouteList.put("/administration/payment/user/giftcard", new Route(new PaymentController(),"sendGiftCard"));
    	 httpPostRouteList.put("/administration/payment/user/paypal", new Route(new PaymentController(),"sendPayPal"));
    	 httpPostRouteList.put("/administration/payment/user/paypal/validate", new Route(new PaymentController(),"validatePayPalAccount"));
    	 
    	 httpPostRouteList.put("/administration/payment/paidusers/export",new Route(new ExportController(),"exportPaidUsers"));
    	 
    	 
    	 httpPostRouteList.put("/administration/job/payment/user/external/email", new Route(new EmailTemplateController(),"externalPaymentSentEmail")); 

    	 
    	 httpPostRouteList.put("/administration/payment/paidusers/search/general", new Route(new PaidUsersController(),"search")); 
    	 httpPostRouteList.put("/administration/payment/paidusers/search/byemail", new Route(new PaidUsersController(),"searchByEmail"));
    	 httpPostRouteList.put("/administration/payment/paidusers/search/byemail", new Route(new PaidUsersController(),"searchByEmail"));    	 
    	 httpPostRouteList.put("/administration/payment/paidusers/search/byguid", new Route(new PaidUsersController(),"searchByGuid"));    	 
    	 httpPostRouteList.put("/administration/payment/paidusers/search/groupby",new Route(new PaidUsersController(),"searchGroupBy"));
    	 httpPostRouteList.put("/administration/job/payment/paidusers/checkvpnusage", new Route(new PaidUsersController(),"checkVPNUsageAsync"));
    	 httpPostRouteList.put("/administration/job/payment/paidusers/checkcountrylist", new Route(new PaidUsersController(),"checkUserCountriesAsync"));
    	 httpPostRouteList.put("/administration/payment/user/unremoveuser", new Route(new PaidUsersController(),"unremoveUser"));
    	 httpPostRouteList.put("/administration/payment/paidusers/search/removed",new Route(new PaidUsersController(),"getRemovedRedeemingRequests"));
    	 
    	 //email template
    	 httpPostRouteList.put("/administration/email/template/send", new Route(new EmailTemplateController(),"sendEmailTemplate"));
    
    	 httpPostRouteList.put("/administration/users/data/delete", new Route(new UsersController(),"deleteUserData"));
    	 httpPostRouteList.put("/administration/users/guid/exists", new Route(new UsersController(),"doUserExists"));
    	 
    	 
    	 //QA
    	 httpPostRouteList.put("/administration/qa/experiment", new Route(new QAController(),"doExperiment"));

    }
    
    public boolean hasPath(String httpMethod,String url){
    	if(httpMethod.equalsIgnoreCase("GET")){
    		return httpGetRouteList.containsKey(url);
    	}else if(httpMethod.equalsIgnoreCase("POST")){
    		return httpPostRouteList.containsKey(url);
    	}
    	return false;
    }

    public Route getRoute(String httpMethod,String url){
    	if(httpMethod.equalsIgnoreCase("GET")){
    		return httpGetRouteList.get(url);
    	}else if(httpMethod.equalsIgnoreCase("POST")){
    		return httpPostRouteList.get(url);
    	}
    	return null;
    }

    
    public void execute(String url, HttpServletRequest request, HttpServletResponse response){
    	String httpMethod=request.getMethod();
        Route route = getRoute(httpMethod, url) ; 
        load(route, request, response);
    }
    
    private void load(Route route,HttpServletRequest request, HttpServletResponse response){
    	try {
            Method method = route.getController().getClass().getDeclaredMethod(route.getMethod(), HttpServletRequest.class, HttpServletResponse.class);
            Object[] argument = new Object[2];
            argument[0] =  request;
            argument[1] =  response;
            method.invoke(route.getController(), argument);
            return;
        } catch (IllegalAccessException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);            
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);            
        } catch (NoSuchMethodException ex) {
        	Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
        	Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    	try{
    		response.sendError(HttpServletResponse.SC_CONFLICT);
    	}catch(IOException e){
    		//swallow
    	}
    }    	
    
}
