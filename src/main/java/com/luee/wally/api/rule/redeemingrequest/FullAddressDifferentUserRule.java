package com.luee.wally.api.rule.redeemingrequest;

import com.luee.wally.admin.repository.PaymentRepository;
import com.luee.wally.entity.RedeemingRequests;

/**
 * 
 *“redeeming_requests_new” table has entities with the same value of “full_address” and a different user_guid.
 */
public class FullAddressDifferentUserRule extends RedeemingRequestRule {
    private PaymentRepository paymentRepository=new PaymentRepository();
    
	@Override
	public void execute(RedeemingRequestRuleContext context,RedeemingRequests redeemingRequests) {
	   	
		int count=paymentRepository.countEligibleUsersByAddress(redeemingRequests.getFullAddress(),redeemingRequests.getUserGuid()); 
		if(count>0){
			   context.getResult().add(RuleResultType.FULL_ADDRESS_DIFFERENT_USER);
			   if(context.isExitOnResult()){
					  return; 
			   }				
		}
		
		if (next != null) {
			next.execute(context,redeemingRequests);
		}

	}

}
