package com.paypal.integrate.admin.api.route;

import java.util.ArrayList;
import java.util.Collection;
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
}
