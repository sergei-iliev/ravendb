package com.paypal.integrate.admin.api.route;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

public interface Controller {

	public default Collection<String> getCountries(){
		String[] locales = Locale.getISOCountries();
		Collection<String> countries=new ArrayList<String>();
		countries.add("");
		for (String countryCode : locales) {
			Locale locale = new Locale("", countryCode);
			countries.add(locale.getCountry());
		}
		return countries;
	}
	
	public default String formatDate(Date date){
	    
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_hh_mm_ss");
	    ZonedDateTime sd = date.toInstant().atZone(ZoneId.systemDefault());	    
	    return formatter.format(sd);		
	}
}
