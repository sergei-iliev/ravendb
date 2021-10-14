package com.luee.wally.api.rule.redeemingrequest;

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
			lookupMap=context.getSuspiciousIpAddresses().stream().collect(Collectors.toMap(e->(String)e.getProperty("ip_prefix"),e->(String)e.getProperty("level"),(e1,e2)->e2));
		}
		
		RuleStatusType ruleStatusType=RuleStatusType.None;
		
		String ipAddress=redeemingRequests.getIpAddress();
		if(ipAddress!=null){
			
			Map.Entry<String, String> found=lookupMap.entrySet().stream().filter(e->ipAddress.contains(e.getKey())).findFirst().orElse(null);									
			
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
