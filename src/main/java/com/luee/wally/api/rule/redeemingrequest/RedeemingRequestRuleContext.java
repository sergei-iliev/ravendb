package com.luee.wally.api.rule.redeemingrequest;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import com.google.appengine.api.datastore.Entity;

public class RedeemingRequestRuleContext {

	private boolean exitOnResult;
	private Set<RuleResultType> result=new TreeSet<>((o1,o2)->{
			return o2.getRuleStatusType().ordinal()-o1.getRuleStatusType().ordinal();			
		});

	private Collection<Entity> suspiciousDomains;
	
	public void reset(){
		this.result.clear();
	}
	public boolean isExitOnResult() {
		return exitOnResult;
	}
	public void setExitOnResult(boolean exitOnResult) {
		this.exitOnResult = exitOnResult;
	}
	public Collection<RuleResultType> getResult() {
		return result;
	}
//	public void setResult(List<RuleResultType> result) {
//		this.result = result;
//	}
    
	public void setSuspiciousDomains(Collection<Entity> suspiciousDomains) {
		this.suspiciousDomains = suspiciousDomains;
	}
	
	public Collection<Entity> getSuspiciousDomains() {
		return suspiciousDomains;
	}
	
	
	
}
