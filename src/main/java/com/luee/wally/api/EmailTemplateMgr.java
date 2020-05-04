package com.luee.wally.api;

import java.util.Map;
import java.util.regex.Pattern;

public enum EmailTemplateMgr {
INSTANCE;
	private static final String ST="${";  //start token
	private static final String ET="}";   //end token	
	
	
	
	public String processTemplate(String template,Map<String,String> variables){					   		
		for(Map.Entry<String, String> e:variables.entrySet()){
			String regex = Pattern.quote(ST+e.getKey()+ET);		
		    template = template.replaceAll(regex, e.getValue() != null ? e.getValue() : "");
		    
		};
		return template;
	}
}
