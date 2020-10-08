package com.luee.wally.api.service;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.luee.wally.admin.repository.PaymentRepository;
import com.luee.wally.api.rule.redeemingrequest.RedeemingRequestEngine;
import com.luee.wally.api.rule.redeemingrequest.RuleResultType;
import com.luee.wally.command.payment.RedeemingRequestRuleValue;
import com.luee.wally.entity.RedeemingRequests;

public class PaymentRuleService extends AbstractService {

	private final Logger logger = Logger.getLogger(PaymentRuleService.class.getName());

	public Collection<RedeemingRequestRuleValue> executeRedeemingRequestRules(
			Collection<RedeemingRequests> redeemingRequests) {
		Collection<RedeemingRequestRuleValue> result = new ArrayList<>();

		RedeemingRequestEngine engine = new RedeemingRequestEngine();
		for (RedeemingRequests redeemingRequest : redeemingRequests) {
			List<RuleResultType> list = engine.execute(redeemingRequest, true);
			result.add(new RedeemingRequestRuleValue(redeemingRequest, list.isEmpty() ? null : list.get(0)));
		}
		return result;
	}

	public List<RuleResultType> executeRedeemingRequestRules(RedeemingRequests redeemingRequests) {
		RedeemingRequestEngine engine = new RedeemingRequestEngine();
		return engine.execute(redeemingRequests, false);
	}
	
	public Map<String,Object> getRedeemingRequestRuleResult(RedeemingRequests redeemingRequest){
		Map<String,Object> result=new HashMap<>();
		PaymentRepository paymentRepository=new PaymentRepository();
		//execute rules for a record
		List<RuleResultType> ruleResults=this.executeRedeemingRequestRules(redeemingRequest);
		
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
		
		Collection<Map<String,String>> objects=new ArrayList<>();
		result.put("differentuserlist",objects);
		
		for(RuleResultType ruleResult:ruleResults){
			if(ruleResult==RuleResultType.SUSPICIOUS_EMAIL_DOMAIN_RED||ruleResult==RuleResultType.SUSPICIOUS_EMAIL_DOMAIN_YELLOW){
				Map<String,String> object=new HashMap<>(3);			 					
				object.put("name", redeemingRequest.getFullName());
				object.put("text",String.format("Suspected email domain: {%s}/{%s}",redeemingRequest.getEmail(),redeemingRequest.getPaypalAccount())); 						
				objects.add(object);
			}
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
		}
		
		return result;
	}
}
