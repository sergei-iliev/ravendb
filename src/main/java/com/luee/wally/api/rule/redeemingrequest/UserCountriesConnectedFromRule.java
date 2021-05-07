package com.luee.wally.api.rule.redeemingrequest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.appengine.api.datastore.Entity;
import com.luee.wally.admin.repository.AffsRepository;
import com.luee.wally.constants.Constants;
import com.luee.wally.entity.RedeemingRequests;
/*
1. If the user was found connected from 2 countries : set Yellow flag with text:
"User was found connected from 2 countries: {LIST_OF_COUNTRIES}"

2. If the user was found connected from 3+ countries : set Red flag with text:
"User was found connected from X countries: {LIST_OF_COUNTRIES}"

3. If the user was found connected from unsupported countries (based on "affs_countries" table) set Red flag with text: 
"User is from an unsupported country. Was found connected from: {LIST OF UNSUPPORTED COUNTRIES}


List of supported countries:
"FR","US","DE","CA","AU","GB","UK","RU","IT","ES","PT","NL","BE","AT","IE","CH","NZ","SE","FI","DK","PL","NO","UA","PR","ZZ"
*/ 
public class UserCountriesConnectedFromRule extends RedeemingRequestRule {

	
	@Override
	public void execute(RedeemingRequestRuleContext context, RedeemingRequests redeemingRequests) {				
		List<String> countriesConnectedFrom=redeemingRequests.getUserCountriesConnectedFrom();
		
		if(countriesConnectedFrom!=null){
			boolean found=false;
			Set<String> unique=new HashSet<>(countriesConnectedFrom);
			boolean forbidden = countriesConnectedFrom.stream().anyMatch(element -> !Constants.ALLOWED_USER_COUNTIES_CONNECTION_FROM.contains(element));			
		    if(forbidden){
		    	found=true;		    	
		    	context.getResult().add(RuleResultType.USER_COUNTRIES_CONNECTED_FROM_RED);	
		    }else if(unique.size()==2){
		    	found=true;
		    	context.getResult().add(RuleResultType.USER_COUNTRIES_CONNECTED_FROM_YELLOW);		    	
		    }else if(unique.size()>=3){
		    	found=true;
		    	context.getResult().add(RuleResultType.USER_COUNTRIES_CONNECTED_FROM_RED);
		    }
		    if(found&&context.isExitOnResult()){
		    	return;
		    }
		}
		
		if (next != null) {
			next.execute(context,redeemingRequests);
		}
	}

}
