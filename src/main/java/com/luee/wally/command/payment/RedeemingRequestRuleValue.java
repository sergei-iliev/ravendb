package com.luee.wally.command.payment;

import com.luee.wally.api.rule.redeemingrequest.RuleResultType;
import com.luee.wally.entity.RedeemingRequests;

public class RedeemingRequestRuleValue {
	private final RedeemingRequests redeemingRequests;
	private final RuleResultType ruleResultType;

	public RedeemingRequestRuleValue(RedeemingRequests redeemingRequests, RuleResultType ruleResultType) {
		this.redeemingRequests = redeemingRequests;
		this.ruleResultType = ruleResultType;
	}

	public RedeemingRequests getRedeemingRequest() {
		return redeemingRequests;
	}

	public RuleResultType getRuleResultType() {
		return ruleResultType;
	}

	public RuleStatusType getRuleStatus() {
		if (ruleResultType != null) {
			switch (ruleResultType) {
			case TIME_TO_CACH_LESS_24_ORGANIC:
				return RuleStatusType.Red;
			case TIME_TO_CACH_LESS_24_NON_ORGANIC:
			case TIME_TO_CACH_LESS_48_ORGANIC:
			case FULL_NAME_DIFFERENT_USER:
			case FULL_ADDRESS_DIFFERENT_USER:
			case IP_ADDRESS_DIFFERENT_USER:
				return RuleStatusType.Yellow;
			default:
				return RuleStatusType.Green;
			}
		}
		return RuleStatusType.Green;
	}
}
