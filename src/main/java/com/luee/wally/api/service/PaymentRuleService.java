package com.luee.wally.api.service;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.appengine.api.datastore.Entity;
import com.luee.wally.admin.repository.PaymentRepository;
import com.luee.wally.admin.repository.SuspiciousEmailDomainRepository;
import com.luee.wally.api.rule.redeemingrequest.RedeemingRequestEngine;
import com.luee.wally.api.rule.redeemingrequest.RuleResultType;
import com.luee.wally.command.payment.RedeemingRequestRuleValue;
import com.luee.wally.constants.Constants;
import com.luee.wally.entity.RedeemingRequests;

public class PaymentRuleService extends AbstractService {

	private final Logger logger = Logger.getLogger(PaymentRuleService.class.getName());

	public Collection<RedeemingRequestRuleValue> executeRedeemingRequestRules(
			Collection<RedeemingRequests> redeemingRequests) {
		Collection<RedeemingRequestRuleValue> result = new ArrayList<>();

		RedeemingRequestEngine engine = new RedeemingRequestEngine();
		for (RedeemingRequests redeemingRequest : redeemingRequests) {
			Collection<RuleResultType> list = engine.execute(redeemingRequest, false);
			result.add(new RedeemingRequestRuleValue(redeemingRequest, list.isEmpty() ? null : list.iterator().next()));
		}
		return result;
	}


	
	public Map<String,Object> getRedeemingRequestRuleResult(RedeemingRequests redeemingRequest){
		Map<String,Object> result=new HashMap<>();
		PaymentRepository paymentRepository=new PaymentRepository();
		//execute rules for a record
		RedeemingRequestEngine engine = new RedeemingRequestEngine();
		Collection<RuleResultType> ruleResults= engine.execute(redeemingRequest, false);
		
		Date date = redeemingRequest.getDate();
		Date creationDate = redeemingRequest.getCreationDate();
		if (date != null && creationDate != null) {
				ZonedDateTime d = date.toInstant().atZone(ZoneId.systemDefault());
				ZonedDateTime c = creationDate.toInstant().atZone(ZoneId.systemDefault());
				Duration duration = Duration.between(c, d);
                result.put("cashout",duration.toHours()+" hours");			
		}else{
			result.put("cashout","missing");
		}
		if(redeemingRequest.getUaChannel()!=null){
		  result.put("uachannel",redeemingRequest.getUaChannel().equals("organic")?"Organic":"Not Organic");
		}else{
		  result.put("uachannel","none");	
		}
		
		//coins
		for(RuleResultType ruleResult:ruleResults){
			if(ruleResult==RuleResultType.COINS_PER_GAME_EQUAL_0||ruleResult==RuleResultType.COINS_PER_GAME_LESS_THEN_3){
				long count=redeemingRequest.getCoinsPerGame().stream().filter(i->i>=100).count();
				result.put("coinspergame",count);
				break;
			}			
		}
		for(RuleResultType ruleResult:ruleResults){
			if(ruleResult==RuleResultType.TOTAL_AD_REV_LESS_THEN_20){
				Map<String,String> object=new HashMap<>(1);
				object.put("name","Ad rev low");
				object.put("text",redeemingRequest.getMaxRev()+" USD");
				result.put("totaladdrev", object);	
			}else if(ruleResult==RuleResultType.TOTAL_AD_REV_LESS_THEN_40){
				Map<String,String> object=new HashMap<>(1);
				object.put("name","Ad rev very low");
				object.put("text",redeemingRequest.getMaxRev()+" USD");				
				result.put("totaladdrev",object);	
			}else if(ruleResult==RuleResultType.NO_GAID_VALUE){
				result.put("nogaidvalue","User is missing  Google Advertising ID");					
			}else if(ruleResult==RuleResultType.UA_CHANNEL_ORGANIC_AMOUNT_50||ruleResult==RuleResultType.UA_CHANNEL_ORGANIC_AMOUNT_100){
				result.put("uachannelorganicamount","Amount is high and user is organic");
			}else if(ruleResult==RuleResultType.USING_VPN){
				result.put("usingvpn","User was using a VPN");
			}
			
		}
		
		//illegal domains
		
		for(RuleResultType ruleResult:ruleResults){			
			if(ruleResult==RuleResultType.SUSPICIOUS_EMAIL_DOMAIN_RED||ruleResult==RuleResultType.SUSPICIOUS_EMAIL_DOMAIN_YELLOW){		 					
				SuspiciousEmailDomainRepository suspiciousEmailDomainRepository=new SuspiciousEmailDomainRepository();
				Collection<Entity> entities=suspiciousEmailDomainRepository.findSuspiciousDomainByEmail(redeemingRequest.getEmail());
				boolean suspiciousEmail=!entities.isEmpty();	
				
				entities=suspiciousEmailDomainRepository.findSuspiciousDomainByEmail(redeemingRequest.getPaypalAccount());
				boolean suspiciousPayPalAccount=!entities.isEmpty();
				

				if(suspiciousEmail&&suspiciousPayPalAccount){
				    if(redeemingRequest.getEmail().equalsIgnoreCase(redeemingRequest.getPaypalAccount())){
				    	result.put("suspiciousdomain",String.format("%s",redeemingRequest.getEmail()));
				    }else{
				    	result.put("suspiciousdomain",String.format("%s/%s",redeemingRequest.getEmail(),redeemingRequest.getPaypalAccount()));	
				    }
				}else{
					result.put("suspiciousdomain",String.format("%s",redeemingRequest.getEmail()!=null?redeemingRequest.getEmail():redeemingRequest.getPaypalAccount()));
				}
				break;
			}			
		}		
		Collection<Object> objects=new ArrayList<>();
		result.put("differentuserlist",objects);
		
		for(RuleResultType ruleResult:ruleResults){
			if(ruleResult==RuleResultType.FULL_NAME_DIFFERENT_USER){
				Map<String,String> object=new HashMap<>(3);
				int count=paymentRepository.countEligibleUsersByName(redeemingRequest.getFullName(),redeemingRequest.getUserGuid()); 		
				
				object.put("name", redeemingRequest.getFullName());
				object.put("text",
						"There are "+count+" redeeming requests with the same Full Name: '"+redeemingRequest.getFullName()+"', and a different user_guid."						
						);
				object.put("url", redeemingRequest.fullNameLink());
				objects.add(object);
			}
			if(ruleResult==RuleResultType.FULL_ADDRESS_DIFFERENT_USER){
				Map<String,String> object=new HashMap<>(3);
				int count=paymentRepository.countEligibleUsersByAddress(redeemingRequest.getFullAddress(),redeemingRequest.getUserGuid()); 		

				object.put("name", redeemingRequest.getFullAddress());
				object.put("text",
						"There are "+count+" redeeming requests with the same Full Address: '"+redeemingRequest.getFullAddress()+"', and a different user_guid."
						);
				object.put("url", redeemingRequest.fullAddressLink());
				objects.add(object);
			}
			if(ruleResult==RuleResultType.IP_ADDRESS_DIFFERENT_USER){
				Map<String,String> object=new HashMap<>(3);
				int count=paymentRepository.countEligibleUsersByIP(redeemingRequest.getIpAddress(),redeemingRequest.getUserGuid());
				
				object.put("name", redeemingRequest.getIpAddress());
				object.put("text",
						"There are "+count+" redeeming requests with the same IP Address: '"+redeemingRequest.getIpAddress()+"', and a different user_guid."						
						);
				object.put("url", redeemingRequest.ipAddressLink());
				objects.add(object);
			}
			if(ruleResult==RuleResultType.USER_COUNTRIES_CONNECTED_FROM_RED||ruleResult==RuleResultType.USER_COUNTRIES_CONNECTED_FROM_YELLOW){
				List<String> countriesConnectedFrom=redeemingRequest.getUserCountriesConnectedFrom();												
				Set<String> unique=new HashSet<>(countriesConnectedFrom);
				Collection<String> forbidden= countriesConnectedFrom.stream().filter(element -> !Constants.ALLOWED_USER_COUNTIES_CONNECTION_FROM.contains(element)).collect(Collectors.toList());
				if(forbidden.size()>0){				  	
				  result.put("usercountriesfrom","User is from an unsupported country. Was found connected from: "+forbidden);		
				}else if(unique.size()>=2){				   	
				  result.put("usercountriesfrom","User was found connected from "+unique.size()+" countries: "+unique); 			    	
				}				 							
			}
		}

		
		return result;
	}
}
