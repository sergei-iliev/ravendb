package com.luee.wally.admin;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.luee.wally.api.route.Router;
import com.luee.wally.utils.TestDatabase;
import com.luee.wally.utils.Utilities;



public class AdminFilter implements Filter {
	private final Logger logger = Logger.getLogger(AdminFilter.class.getName());
	
    public AdminFilter() {
    	TestDatabase.INSTANCE.generateDB();
    }
 
    @Override
    public void destroy() {
    }
 
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        
//        if(!request.isSecure()){
//        	resp.getWriter().print("HTTPS required!");
//        	resp.flushBuffer();
//        	return;
//        }
        
        Utilities.domain= request.getServerName();
        String loginedUser = (String) request.getSession().getAttribute("login");
 
        //let resource go on
        if(request.getRequestURI().matches(".*(css|jpg|png|gif|js)")){        	
            chain.doFilter(request, response);
            return;
        }
        //skip security for api calls
        if (request.getRequestURI().startsWith("/administration/api")) {        	
        	Router.INSTANCE.execute(request.getRequestURI(), request, response);
        	return;
        }        
        //skip security for jobs
        if (request.getRequestURI().startsWith("/administration/job")) {        	
        	Router.INSTANCE.execute(request.getRequestURI(), request, response);
        	return;
        }
        if (request.getRequestURI().startsWith("/administration/login")) {        	
            Router.INSTANCE.execute("/administration/login", request, response);
        	return;
        }
        if (request.getRequestURI().startsWith("/administration/logout")) {            
            request.getSession().invalidate();
            //chain.doFilter(request, response);
        	RequestDispatcher rd = request.getServletContext().getRequestDispatcher("/jsp/login.jsp");	
        	rd.forward(request, response);
            return;        	
        }
        if(loginedUser==null){					
			response.sendRedirect("/administration/login");        	
        	return;
        }        
        
        if(Router.INSTANCE.hasPath(request.getMethod(),request.getRequestURI())){
          Router.INSTANCE.execute(request.getRequestURI(), request, response);
        }else{
          throw new RuntimeException("Unable to find path controller for: "+request.getRequestURI());	
        }
        
    }
 
    @Override
    public void init(FilterConfig fConfig) throws ServletException {

    }
 
}
