package com.luee.wally.api.rule.redeemingrequest;

import com.luee.wally.admin.repository.PaymentRepository;
import com.luee.wally.entity.RedeemingRequests;

public class IpAddressDifferentUserRule extends RedeemingRequestRule {
    private PaymentRepository paymentRepository=new PaymentRepository();
    
	@Override
	public void execute(RedeemingRequestRuleContext context,RedeemingRequests redeemingRequests) {	   	
		int count=paymentRepository.countEligibleUsersByIP(redeemingRequests.getIpAddress(),redeemingRequests.getUserGuid()); 
		if(count>0){
			if(count>2){
			   context.getResult().add(RuleResultType.IP_ADDRESS_DIFFERENT_USER_3);	
			}else{
			   context.getResult().add(RuleResultType.IP_ADDRESS_DIFFERENT_USER);			   
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
