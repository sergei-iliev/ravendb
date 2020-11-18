package com.luee.wally.api.rule.redeemingrequest;

import java.util.Comparator;

import com.luee.wally.command.payment.RuleStatusType;

/**
 * 
 Red Flag:
		time_to_cash_out < 24 hours AND ua_channel = “organic”.

	Yellow Flag:
		time_to_cash_out < 24 (ua_channel != “organic”)
		time_to_cash_out < 48 hours AND ua_channel = “organic”.
		“redeeming_requests_new” table has entities with the same value of “full_name” with value “is_paid” = true and a different user_guid.
		“redeeming_requests_new” table has entities with the same value of “full_address” with value “is_paid” = true and a different user_guid.
		“redeeming_requests_new” table has entities with the same value of “ip_address” with value “is_paid” = true and a different user_guid.

*
 */
public enum RuleResultType{	
    TIME_TO_CACH_LESS_24_ORGANIC(RuleStatusType.Red),
    TIME_TO_CACH_LESS_24_NON_ORGANIC(RuleStatusType.Yellow),
    TIME_TO_CACH_LESS_48_ORGANIC(RuleStatusType.Yellow),
    FULL_NAME_DIFFERENT_USER(RuleStatusType.Yellow),
    FULL_ADDRESS_DIFFERENT_USER(RuleStatusType.Yellow),
    IP_ADDRESS_DIFFERENT_USER(RuleStatusType.Yellow),
    COINS_PER_GAME_EQUAL_0(RuleStatusType.Red),
    COINS_PER_GAME_LESS_THEN_3(RuleStatusType.Yellow),
    SUSPICIOUS_EMAIL_DOMAIN_RED(RuleStatusType.Red),
    SUSPICIOUS_EMAIL_DOMAIN_YELLOW(RuleStatusType.Yellow),
    TOTAL_AD_REV_LESS_THEN_20(RuleStatusType.Yellow),
    TOTAL_AD_REV_LESS_THEN_40(RuleStatusType.Red)
    ;	
	private RuleStatusType ruleStatusType; 
	private RuleResultType(RuleStatusType ruleStatusType) {
		this.ruleStatusType=ruleStatusType;
	}
	
	public RuleStatusType getRuleStatusType() {
		return ruleStatusType;
	}
	
}
