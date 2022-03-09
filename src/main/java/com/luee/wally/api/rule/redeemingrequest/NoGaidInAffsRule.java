package com.luee.wally.api.rule.redeemingrequest;

import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery.TooManyResultsException;
import com.luee.wally.admin.repository.AffsRepository;
import com.luee.wally.api.service.impex.ExportService;
import com.luee.wally.entity.RedeemingRequests;

/*
 * In the affs table we have a field called “gaid”. That is the Google Advertising ID of the user. If a user uses a device that blocks their GAID we should mark those users with a red flag.
   For each entity of redeeming_requests_new , we should find the relevant affs entity based on the user_guid. If the affs entity doesn’t have a field for “gaid” (empty or null or “null” - as a string) 
   we should add a red flag and write in the popup: “User is missing Google Advertising ID”
 */
public class NoGaidInAffsRule extends RedeemingRequestRule {
	private final Logger logger = Logger.getLogger(NoGaidInAffsRule.class.getName());
	
	private AffsRepository affsRepository=new AffsRepository();
	@Override
	public void execute(RedeemingRequestRuleContext context, RedeemingRequests redeemingRequests) {		
		try{
		Entity affs=affsRepository.findEntity("affs", "user_guid",redeemingRequests.getUserGuid());
		if(affs!=null){
			   String gaid=(String)affs.getProperty("gaid");			   
			   if(gaid==null||gaid.trim().length()==0||"null".equals(gaid)){
			    context.getResult().add(RuleResultType.NO_GAID_VALUE);
			    if(context.isExitOnResult()){
					  return; 
			    }
			   }
		}
		}catch(TooManyResultsException e){
			logger.severe("More then a single record in affs table found for user_guid:"+redeemingRequests.getUserGuid());
		}
		if (next != null) {
			next.execute(context,redeemingRequests);
		}
	}

}
