package com.luee.wally.api.rule.redeemingrequest;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import com.luee.wally.entity.RedeemingRequests;

public class TimeToCashLess24NonOrganicRule extends RedeemingRequestRule {

	/**
	 * time_to_cash_out < 24 (ua_channel != “organic”)
	 */
	@Override
	public void execute(RedeemingRequestRuleContext context, RedeemingRequests redeemingRequests) {
		Date date = redeemingRequests.getDate();
		Date creationDate = redeemingRequests.getCreationDate();
		if (redeemingRequests.getUaChannel() != null && !redeemingRequests.getUaChannel().equalsIgnoreCase("organic")) {
		if(date!=null&&creationDate!=null){	
			ZonedDateTime d = date.toInstant().atZone(ZoneId.systemDefault());
			ZonedDateTime c = creationDate.toInstant().atZone(ZoneId.systemDefault());
			Duration duration = Duration.between(c, d);

			if (duration.toHours() < 24) {
				context.getResult().add(RuleResultType.TIME_TO_CACH_LESS_24_NON_ORGANIC);
				if (context.isExitOnResult()) {
					return;
				}
			}
		}
		}

		if (next != null) {
			next.execute(context, redeemingRequests);
		}

	}

}
