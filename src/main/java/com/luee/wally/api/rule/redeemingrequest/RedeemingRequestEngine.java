package com.luee.wally.api.rule.redeemingrequest;

import java.util.List;

import com.luee.wally.entity.RedeemingRequests;

public class RedeemingRequestEngine {

	private RedeemingRequestRule first;

	public RedeemingRequestEngine() {
		first = new TimeToCashLess24OrganicRule();
		first.setNext(new TimeToCashLess24NonOrganicRule());
		first.setNext(new TimeToCashLess48OrganicRule());
		first.setNext(new FullNameDifferentUserRule());
		first.setNext(new CoinsPerGameRule());
		first.setNext(new FullAddressDifferentUserRule());
		first.setNext(new IpAddressDifferentUserRule());
	}

	public List<RuleResultType> execute(RedeemingRequests redeemingRequest,boolean exitOnResult) {
		RedeemingRequestRuleContext context = new RedeemingRequestRuleContext();
		context.setExitOnResult(exitOnResult);
		first.execute(context,redeemingRequest);
		return context.getResult();
	}

}
