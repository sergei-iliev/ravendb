package com.luee.wally.api.rule.redeemingrequest;
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
public enum RuleResultType {
    TIME_TO_CACH_LESS_24_ORGANIC,
    TIME_TO_CACH_LESS_24_NON_ORGANIC,
    TIME_TO_CACH_LESS_48_ORGANIC,
    FULL_NAME_DIFFERENT_USER,
    FULL_ADDRESS_DIFFERENT_USER,
    IP_ADDRESS_DIFFERENT_USER;		
}
