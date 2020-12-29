package com.luee.wally.api.rule.redeemingrequest;

import java.util.List;

import com.luee.wally.entity.RedeemingRequests;

/*
 * If UA channel equals “organic” and amount >= 50 - yellow flag
   If UA channel equals “organic” and amount >= 100 - red flag
 */
		
public class UAChannelOrganicAmount extends RedeemingRequestRule {

	@Override
	public void execute(RedeemingRequestRuleContext context, RedeemingRequests redeemingRequests) {
		if (redeemingRequests.getUaChannel() != null && redeemingRequests.getUaChannel().equalsIgnoreCase("organic")) {
			double amount=Double.parseDouble(redeemingRequests.getAmount());					
			
			if (Double.compare(amount,100)>=0){
				context.getResult().add(RuleResultType.UA_CHANNEL_ORGANIC_AMOUNT_100);
			}else if(Double.compare(amount,50)>=0){
				context.getResult().add(RuleResultType.UA_CHANNEL_ORGANIC_AMOUNT_50);
			}
		
			if(context.isExitOnResult()){
				  return; 
		    }
		}
		
		if (next != null) {
			next.execute(context,redeemingRequests);
		}		
		
	}

}
