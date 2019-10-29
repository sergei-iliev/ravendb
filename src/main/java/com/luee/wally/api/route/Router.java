package com.luee.wally.api.route;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.luee.wally.admin.controller.AffsSearchController;
import com.luee.wally.admin.controller.CampaignSearchController;
import com.luee.wally.admin.controller.ImportController;
import com.luee.wally.admin.controller.LoginController;
import com.luee.wally.admin.controller.PaymentController;
import com.luee.wally.admin.controller.SearchFilterTemplateController;

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
    	 httpGetRouteList.put("/administration/logout", new Route(new LoginController(),"logout"));
    	 httpGetRouteList.put("/administration/campaign", new Route(new CampaignSearchController(),"index"));
    	 httpGetRouteList.put("/administration/import/user/revenue/2019", new Route(new ImportController(),"importUserRevenue2019"));
    	 httpGetRouteList.put("/administration/import/user/revenue/2019/background", new Route(new ImportController(),"importUserRevenue2019InBackground"));
    	 httpGetRouteList.put("/administration/payment/eligibleusers", new Route(new PaymentController(),"index"));
    	 httpGetRouteList.put("/administration/search/templates", new Route(new SearchFilterTemplateController(),"getSearchFilterTemplates"));
    	 httpGetRouteList.put("/administration/search/template/filter", new Route(new PaymentController(),"searchByFilterTemplate"));
    	 httpGetRouteList.put("/administration/payment/test", new Route(new PaymentController(),"test"));
    	 
    	 //post
    	 httpPostRouteList.put("/administration/search", new Route(new AffsSearchController(),"search"));
    	 httpPostRouteList.put("/administration/campaign/search", new Route(new CampaignSearchController(),"search"));
    	 httpPostRouteList.put("/administration/login", new Route(new LoginController(),"login"));
    	 httpPostRouteList.put("/administration/import/user/revenue/2019/background", new Route(new ImportController(),"importUserRevenue2019InBackground"));
    	 httpPostRouteList.put("/administration/payment/eligibleusers/search", new Route(new PaymentController(),"search"));
    	 httpPostRouteList.put("/administration/search/template", new Route(new SearchFilterTemplateController(),"saveSearchFilterTemplate"));
    	 httpPostRouteList.put("/administration/payment/removal/reason", new Route(new PaymentController(),"saveUserPaymentRemovalReason"));
    	 httpPostRouteList.put("/administration/payment/user/pay", new Route(new PaymentController(),"pay"));

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
    }    	
    
}
