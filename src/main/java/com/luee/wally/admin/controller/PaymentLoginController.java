package com.luee.wally.admin.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.luee.wally.api.route.Controller;
import com.luee.wally.constants.Constants;
import com.paypal.api.openidconnect.Tokeninfo;
import com.paypal.api.openidconnect.Userinfo;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

public class PaymentLoginController implements Controller{
	private final Logger logger = Logger.getLogger(PaymentLoginController.class.getName());
	/*
	 * Comes from an email that user recieved to get payed via 
	 * PayPal or Amazon
	 * 1.Connect To PayPal
	 * 2.LWA
	 */
	public void loginRequest(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
    	String type =req.getParameter("type");
    	if(type!=null&&type.equalsIgnoreCase("paypal")){
		  req.getRequestDispatcher("/jsp/login/paypal_login.jsp").forward(req, resp);
    	}else{
    	  req.getRequestDispatcher("/jsp/login/amazon_login.jsp").forward(req, resp);	
    	}
	}
	public void amazonLoginToken(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		System.out.println(req.getParameterMap());
    	req.getRequestDispatcher("/jsp/index.jsp").forward(req, resp);
	}	
	public void payPalLoginToken(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		String code=req.getParameter("code");
		System.out.println(req.getParameterMap());
		
		String clientId="AeIMl71AyLnR3sgUL341TD22bfGTdp-KZFw1ZxQ7QJmQtjE4AdD3CiR9W6oxvHXMNbTXa_rNtQw6ZDZR";
		String secret="EH4OM0TDVRIw0LcQMKyjVM_FJee8s6sT67ephDCi3PnUQ6NZAYK7UQE56sSUdGaViFNgDNSPmbCIGecf";
		try{
		// Initialize apiContext with proper credentials and environment.
		APIContext context = new APIContext(clientId, secret, "sandbox");
     
		// Replace the code with the code value returned from the redirect on previous step.
		Tokeninfo info = Tokeninfo.createFromAuthorizationCode(context, code);
		String accessToken = info.getAccessToken();
		String refreshToken = info.getRefreshToken();
		
		// Initialize apiContext with proper credentials and environment. Also, set the refreshToken retrieved from previous step.
		APIContext userAPIContext = new APIContext(clientId, secret, "sandbox").setRefreshToken(info.getRefreshToken());

		Userinfo userinfo = Userinfo.getUserinfo(userAPIContext);
		logger.log(Level.WARNING,"*************************USER INFO*****************");
		System.out.println(userinfo);
		}catch(PayPalRESTException ppe){
			logger.log(Level.SEVERE,"PayPal",ppe);
		}
    	req.getRequestDispatcher("/jsp/index.jsp").forward(req, resp);
	}

}
