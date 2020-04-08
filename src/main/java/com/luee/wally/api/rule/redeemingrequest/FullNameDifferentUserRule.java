package com.luee.wally.api.rule.redeemingrequest;

import com.luee.wally.admin.repository.PaymentRepository;
import com.luee.wally.entity.RedeemingRequests;

/**
 *
 * “redeeming_requests_new” table has entities with the same value of “full_name” with value “is_paid” = true and a different user_guid.
 */
public class FullNameDifferentUserRule extends RedeemingRequestRule {
    private PaymentRepository paymentRepository=new PaymentRepository();
    
	@Override
	public void execute(RedeemingRequestRuleContext context,RedeemingRequests redeemingRequests) {
	   	System.out.println(redeemingRequests.getFullName()+">>"+redeemingRequests.getUserGuid());
		int count=paymentRepository.countEligibleUsersByName(redeemingRequests.getFullName(),redeemingRequests.getUserGuid()); 
		if(count>0){
			   context.getResult().add(RuleResultType.FULL_NAME_DIFFERENT_USER);
			   if(context.isExitOnResult()){
					  return; 
			   }				
		}
		
		if (next != null) {
			next.execute(context,redeemingRequests);
		}

	}

}
