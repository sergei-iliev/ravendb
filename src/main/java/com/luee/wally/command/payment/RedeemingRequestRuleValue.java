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
			case COINS_PER_GAME_EQUAL_0:
				return RuleStatusType.Red;
			case COINS_PER_GAME_LESS_THEN_3:
				return RuleStatusType.Yellow;
			case SUSPICIOUS_EMAIL_DOMAIN_RED:
				return RuleStatusType.Red;
			case SUSPICIOUS_EMAIL_DOMAIN_YELLOW:
				return RuleStatusType.Yellow;
			case TOTAL_AD_REV_LESS_THEN_40:
				return RuleStatusType.Red;
			case TOTAL_AD_REV_LESS_THEN_20:
				return RuleStatusType.Yellow;
			case NO_GAID_VALUE:
				return RuleStatusType.Yellow;
			case UA_CHANNEL_ORGANIC_AMOUNT_100:
				return RuleStatusType.Red;
			case UA_CHANNEL_ORGANIC_AMOUNT_50:
				return RuleStatusType.Yellow;
			case USING_VPN:
				return RuleStatusType.Red;
			default:
				return RuleStatusType.Green;
			}
		}
		return RuleStatusType.Green;
	}
}
