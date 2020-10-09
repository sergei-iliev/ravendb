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
		first=(new TimeToCashLess24OrganicRule());
		first.setNext(new TimeToCashLess24NonOrganicRule());
		first.setNext(new TimeToCashLess48OrganicRule());
		first.setNext(new FullNameDifferentUserRule());
		first.setNext(new CoinsPerGameRule());
		first.setNext(new FullAddressDifferentUserRule());
		first.setNext(new IpAddressDifferentUserRule());
		first.setNext(new EmailDomainUserRule());
		context = this.create();
	}

	public List<RuleResultType> execute(RedeemingRequests redeemingRequest,boolean exitOnResult) {
		context.reset();
		context.setExitOnResult(exitOnResult);
		first.execute(context,redeemingRequest);
		return context.getResult();
	}
	
	private RedeemingRequestRuleContext create(){
		RedeemingRequestRuleContext context = new RedeemingRequestRuleContext();
		SuspiciousEmailDomainRepository suspiciousEmailDomainRepository=new SuspiciousEmailDomainRepository();
		Collection<Entity> entities=suspiciousEmailDomainRepository.findEntities("suspicious_email_domains", null, null);
		context.setSuspiciousDomains(entities);
		
		return context;
	}

}
