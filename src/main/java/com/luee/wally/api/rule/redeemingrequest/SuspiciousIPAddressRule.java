package com.luee.wally.api.rule.redeemingrequest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.luee.wally.command.payment.RuleStatusType;
import com.luee.wally.entity.RedeemingRequests;

public class SuspiciousIPAddressRule extends RedeemingRequestRule {
	/*
	 * This could be 100 items, the more the slower the rule engine
	 */
	private Map<String,String> lookupMap;
	
	@Override
	public void execute(RedeemingRequestRuleContext context, RedeemingRequests redeemingRequests) {
        
		if(lookupMap==null){						
			Map<String,String> entryMap=context.getSuspiciousIpAddresses().stream().collect(Collectors.toMap(e->(String)e.getProperty("ip_prefix"),e->(String)e.getProperty("level"),(e1,e2)->e2));
			//order map items by their value so that "red" is before "yellow" 
			lookupMap=entryMap.entrySet().stream().sorted(Map.Entry.<String, String>comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(e1,e2)->e1,LinkedHashMap::new));
		}
		
		RuleStatusType ruleStatusType=RuleStatusType.None;
		
		String ipAddress=redeemingRequests.getIpAddress();
		if(ipAddress!=null){
			Map.Entry<String, String> found=null;
			for(Map.Entry<String, String> entry:lookupMap.entrySet()){ //iterate starting from red once garanteed by LinkedHashMap
				if(ipAddress.contains(entry.getKey())){
					found=entry;
					break;
				}				 
			}
											
			
			if(found!=null){
				ruleStatusType=found.getValue().equalsIgnoreCase(RuleStatusType.Yellow.name().toLowerCase())?RuleStatusType.Yellow:RuleStatusType.Red;
			}
		}
		
			
		if(ruleStatusType!=ruleStatusType.None){
			   context.getResult().add(ruleStatusType==RuleStatusType.Red?RuleResultType.SUSPICIOUS_IP_ADDRESS_RED:RuleResultType.SUSPICIOUS_IP_ADDRESS_YELLOW);	
			   if(context.isExitOnResult()){
					  return; 
			   }
		}	
			
		
		if (next != null) {
			next.execute(context,redeemingRequests);
		}		
	}

}
