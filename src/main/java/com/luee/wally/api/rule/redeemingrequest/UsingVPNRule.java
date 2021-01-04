package com.luee.wally.api.rule.redeemingrequest;

import com.luee.wally.entity.RedeemingRequests;

public class UsingVPNRule extends RedeemingRequestRule {
	
	
	@Override
	public void execute(RedeemingRequestRuleContext context, RedeemingRequests redeemingRequests) {
		if(redeemingRequests.isUsingVPN()){						
			context.getResult().add(RuleResultType.USING_VPN);			
			if(context.isExitOnResult()){
			  return; 
			}
		}
		
		if (next != null) {
			next.execute(context,redeemingRequests);
		}		
	}

}
