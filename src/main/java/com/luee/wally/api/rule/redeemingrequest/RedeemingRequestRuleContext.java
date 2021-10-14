package com.luee.wally.api.rule.redeemingrequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.appengine.api.datastore.Entity;
import com.luee.wally.admin.repository.PaymentRepository;
import com.luee.wally.admin.repository.SuspiciousEmailDomainRepository;

public class RedeemingRequestRuleContext {

	private boolean exitOnResult;
//	private Set<RuleResultType> result=new TreeSet<>((o1,o2)->{
//			return o2.getRuleStatusType().ordinal()-o1.getRuleStatusType().ordinal();			
//		});
	
	private Set<RuleResultType> result=EnumSet.noneOf(RuleResultType.class); 
	
	private Collection<Entity> suspiciousDomains;
	
	private Collection<Entity> suspiciousIpAddresses;
	
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
		
		entities=suspiciousEmailDomainRepository.findEntities("suspicious_ip_addresses", null, null);
		this.setSuspiciousIpAddresses(entities);
		
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
	
	public void setSuspiciousIpAddresses(Collection<Entity> suspiciousIpAddresses) {
		this.suspiciousIpAddresses = suspiciousIpAddresses;
	}
	
	public Collection<Entity> getSuspiciousIpAddresses() {
		return suspiciousIpAddresses;
	}
	
}
