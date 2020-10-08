package com.luee.wally.api.rule.redeemingrequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.appengine.api.datastore.Entity;

public class RedeemingRequestRuleContext {

	private boolean exitOnResult;
	private List<RuleResultType> result=new ArrayList<>(4);

	private Collection<Entity> suspiciousDomains;
	
	public boolean isExitOnResult() {
		return exitOnResult;
	}
	public void setExitOnResult(boolean exitOnResult) {
		this.exitOnResult = exitOnResult;
	}
	public List<RuleResultType> getResult() {
		return result;
	}
	public void setResult(List<RuleResultType> result) {
		this.result = result;
	}
    
	public void setSuspiciousDomains(Collection<Entity> suspiciousDomains) {
		this.suspiciousDomains = suspiciousDomains;
	}
	
	public Collection<Entity> getSuspiciousDomains() {
		return suspiciousDomains;
	}
	
	
	
}
