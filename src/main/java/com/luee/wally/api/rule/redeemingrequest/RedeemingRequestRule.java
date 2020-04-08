package com.luee.wally.api.rule.redeemingrequest;

import com.luee.wally.entity.RedeemingRequests;

public abstract class RedeemingRequestRule {

	protected RedeemingRequestRule next;
	
	public void setNext(RedeemingRequestRule next)
	{
		if(this.next==null){
			this.next = next;	
		}else{
			this.next.setNext(next);
		}
		
	}
		
	public abstract void  execute(RedeemingRequestRuleContext context,RedeemingRequests redeemingRequests);

	
}
