package com.luee.wally.api.rule.redeemingrequest;

import java.util.Map;
import java.util.stream.Collectors;

import com.luee.wally.command.payment.RuleStatusType;
import com.luee.wally.entity.RedeemingRequests;

public class EmailDomainUserRule extends RedeemingRequestRule {
	private Map<String,String> lookupMap;
	
	@Override
	public void execute(RedeemingRequestRuleContext context, RedeemingRequests redeemingRequests) {

		if(lookupMap==null){						
			lookupMap=context.getSuspiciousDomains().stream().collect(Collectors.toMap(e->(String)e.getProperty("domain"),e->(String)e.getProperty("level"),(e1,e2)->e2));
		}
		RuleStatusType ruleStatusType=RuleStatusType.None;
		String email=redeemingRequests.getEmail();
		if(email!=null){
			String domain=email.substring(email.indexOf("@") + 1);
			String level=lookupMap.get(domain);
			
			if(level!=null){
				ruleStatusType=level.equalsIgnoreCase(RuleStatusType.Yellow.name().toLowerCase())?RuleStatusType.Yellow:RuleStatusType.Red;
			}
		}
		email=redeemingRequests.getPaypalAccount();
		if(email!=null){
			String domain=email.substring(email.indexOf("@") + 1);
			String level=lookupMap.get(domain);
			if(level!=null){
			   if(ruleStatusType!=ruleStatusType.Red){   //no need to evaluate if already red
				   ruleStatusType=level.equalsIgnoreCase(RuleStatusType.Yellow.name().toLowerCase())?RuleStatusType.Yellow:RuleStatusType.Red;   
			   }
			}		
		}
			
		if(ruleStatusType!=ruleStatusType.None){
			   context.getResult().add(ruleStatusType==RuleStatusType.Red?RuleResultType.SUSPICIOUS_EMAIL_DOMAIN_RED:RuleResultType.SUSPICIOUS_EMAIL_DOMAIN_YELLOW);
			   //System.out.println(ruleStatusType==RuleStatusType.Red?RuleResultType.SUSPICIOUS_EMAIL_DOMAIN_RED:RuleResultType.SUSPICIOUS_EMAIL_DOMAIN_YELLOW);	
			   if(context.isExitOnResult()){
					  return; 
			   }
		}	
			
		
		if (next != null) {
			next.execute(context,redeemingRequests);
		}		
	}

}
