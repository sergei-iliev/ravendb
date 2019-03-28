package net.paypal.integrate;

import javax.servlet.ServletContextListener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.googlecode.objectify.ObjectifyFilter;

@SpringBootApplication
public class PaypalApplication {
	public static void main(String[] args) {
		SpringApplication.run(PaypalApplication.class, args);


	}
	@Bean
	public FilterRegistrationBean objectifyFilterRegistration() {
	    final FilterRegistrationBean registration = new FilterRegistrationBean();
	    registration.setFilter(new ObjectifyFilter());
	    registration.addUrlPatterns("/*");
	    registration.setOrder(1);
	    return registration;
	}
	  
	@Bean
	public ServletListenerRegistrationBean<ServletContextListener> listenerRegistrationBean() {
	    ServletListenerRegistrationBean<ServletContextListener> bean = 
	        new ServletListenerRegistrationBean<>();
	    bean.setListener(new ObjectifyServletContextListener());
	    return bean;
	}
	
}
