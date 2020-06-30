package com.luee.wally.api.rule.redeemingrequest;

import java.util.List;

import com.luee.wally.entity.RedeemingRequests;


public class CoinsPerGameRule extends RedeemingRequestRule {
    
	@Override
	public void execute(RedeemingRequestRuleContext context,RedeemingRequests redeemingRequests) {
	   	
		List<Long> coinsPerGame= redeemingRequests.getCoinsPerGame();
		if(coinsPerGame!=null&&coinsPerGame.size()>0){
			long count=coinsPerGame.stream().filter(i->i>=100).count();
			if(count==0){
			   context.getResult().add(RuleResultType.COINS_PER_GAME_EQUAL_0);	
			   if(context.isExitOnResult()){
					  return; 
			   }
			}else if(count<=3){
			   context.getResult().add(RuleResultType.COINS_PER_GAME_LESS_THEN_3);	
			   if(context.isExitOnResult()){
					  return; 
			   }				
			}
		}
		if (next != null) {
			next.execute(context,redeemingRequests);
		}

	}

}
