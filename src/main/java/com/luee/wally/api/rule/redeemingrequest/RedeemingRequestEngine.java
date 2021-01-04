package com.luee.wally.api.rule.redeemingrequest;

import java.util.Collection;
import java.util.List;

import com.google.appengine.api.datastore.Entity;
import com.luee.wally.admin.repository.SuspiciousEmailDomainRepository;
import com.luee.wally.entity.RedeemingRequests;

public class RedeemingRequestEngine {

	private RedeemingRequestRule first;
	private RedeemingRequestRuleContext context;
	
	public RedeemingRequestEngine() {
		first=new NoGaidInAffsRule();
		first.setNext(new UsingVPNRule());
		first.setNext(new UAChannelOrganicAmount());
		first.setNext(new TimeToCashLess24OrganicRule());		
		first.setNext(new AdRevRule());
		first.setNext(new TimeToCashLess24NonOrganicRule());
		first.setNext(new TimeToCashLess48OrganicRule());
		first.setNext(new FullNameDifferentUserRule());
		first.setNext(new CoinsPerGameRule());
		first.setNext(new FullAddressDifferentUserRule());
		first.setNext(new IpAddressDifferentUserRule());
		first.setNext(new EmailDomainUserRule());

		context = new RedeemingRequestRuleContext();
	}

	public Collection<RuleResultType> execute(RedeemingRequests redeemingRequest,boolean exitOnResult) {
		context.reset();
		context.setExitOnResult(exitOnResult);
		first.execute(context,redeemingRequest);
		return context.getResult();
	}
	

}
