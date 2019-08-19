package com.paypal.integrate.admin;

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

import com.paypal.integrate.admin.api.route.Router;

public class AdminFilter implements Filter {
	private final Logger logger = Logger.getLogger(AdminFilter.class.getName());
	
    public AdminFilter() {
    }
 
    @Override
    public void destroy() {
    }
 
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        
        if(!request.isSecure()){
        	resp.getWriter().print("HTTPS required!");
        	resp.flushBuffer();
        	return;
        }
        
        String loginedUser = (String) request.getSession().getAttribute("login");
        logger.log(Level.WARNING, "Current session:"+loginedUser);
 
        //let resource go on
        if(request.getRequestURI().matches(".*(css|jpg|png|gif|js)")){        	
            chain.doFilter(request, response);
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

//        HttpServletRequest wrapRequest = request;
// 
//        if (loginedUser != null) {
//            // User Name
//            String userName = loginedUser.getUserName();
// 
//            // Roles
//            List<String> roles = loginedUser.getRoles();
// 
//            // Wrap old request by a new Request with userName and Roles information.
//            wrapRequest = new UserRoleRequestWrapper(userName, roles, request);
//        }
// 
//        // Pages must be signed in.
//        if (SecurityUtils.isSecurityPage(request)) {
// 
//            // If the user is not logged in,
//            // Redirect to the login page.
//            if (loginedUser == null) {
// 
//                String requestUri = request.getRequestURI();
// 
//                // Store the current page to redirect to after successful login.
//                int redirectId = AppUtils.storeRedirectAfterLoginUrl(request.getSession(), requestUri);
// 
//                response.sendRedirect(wrapRequest.getContextPath() + "/login?redirectId=" + redirectId);
//                return;
//            }
// 
//            // Check if the user has a valid role?
//            boolean hasPermission = SecurityUtils.hasPermission(wrapRequest);
//            if (!hasPermission) {
// 
//                RequestDispatcher dispatcher //
//                        = request.getServletContext().getRequestDispatcher("/WEB-INF/views/accessDeniedView.jsp");
// 
//                dispatcher.forward(request, response);
//                return;
//            }
//        }
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
