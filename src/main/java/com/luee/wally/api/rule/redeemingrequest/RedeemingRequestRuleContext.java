package com.luee.wally.api.rule.redeemingrequest;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.appengine.api.datastore.Entity;
import com.luee.wally.admin.repository.PaymentRepository;
import com.luee.wally.admin.repository.SuspiciousEmailDomainRepository;

public class RedeemingRequestRuleContext {

	private boolean exitOnResult;
	private Set<RuleResultType> result=new TreeSet<>((o1,o2)->{
			return o2.getRuleStatusType().ordinal()-o1.getRuleStatusType().ordinal();			
		});

	private Collection<Entity> suspiciousDomains;

	private Map<String,String> payPalCountryCodeMap;
	
	private Map<String,String> tangoCardCountryCodeMap;
	
	public RedeemingRequestRuleContext() {
		 payPalCountryCodeMap=new HashMap<>();
		 tangoCardCountryCodeMap=new HashMap<>();
		 this.init();
	}
	
	private void init(){
		
		SuspiciousEmailDomainRepository suspiciousEmailDomainRepository=new SuspiciousEmailDomainRepository();
		Collection<Entity> entities=suspiciousEmailDomainRepository.findEntities("suspicious_email_domains", null, null);
		this.setSuspiciousDomains(entities);
		
		PaymentRepository paymentRepository=new PaymentRepository();
		Collection<Entity> giftCardMapping= paymentRepository.findEntities("tango_card_country_code_mapping",null, null);
		giftCardMapping.forEach(e->{
			tangoCardCountryCodeMap.put((String)e.getProperty("country_code"),(String)e.getProperty("currency"));	
		});
		
		Collection<Entity> payPalMapping= paymentRepository.findEntities("paypal_country_code_mapping",null, null);
		payPalMapping.forEach(e->{
			payPalCountryCodeMap.put((String)e.getProperty("country_code"),(String)e.getProperty("currency"));	
		});
		
		
		//fetch currency ratio
	}
	
	
	public void reset(){
		this.result.clear();
	}
	public boolean isExitOnResult() {
		return exitOnResult;
	}
	public void setExitOnResult(boolean exitOnResult) {
		this.exitOnResult = exitOnResult;
	}
	public Collection<RuleResultType> getResult() {
		return result;
	}
//	public void setResult(List<RuleResultType> result) {
//		this.result = result;
//	}
    
	public void setSuspiciousDomains(Collection<Entity> suspiciousDomains) {
		this.suspiciousDomains = suspiciousDomains;
	}
	
	public Collection<Entity> getSuspiciousDomains() {
		return suspiciousDomains;
	}
	
	public Map<String, String> getPayPalCountryCodeMap() {
		return payPalCountryCodeMap;
	}
	
	public Map<String, String> getTangoCardCountryCodeMap() {
		return tangoCardCountryCodeMap;
	}
	
}
