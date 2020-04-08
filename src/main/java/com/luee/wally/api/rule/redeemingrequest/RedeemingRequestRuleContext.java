package com.luee.wally.api.rule.redeemingrequest;

import java.util.ArrayList;
import java.util.List;

public class RedeemingRequestRuleContext {

	private boolean exitOnResult;
	private List<RuleResultType> result=new ArrayList<>(4);

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

	
	
	
}
