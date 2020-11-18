package com.luee.wally.api.rule.redeemingrequest;

import java.math.BigDecimal;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.luee.wally.admin.controller.AffsSearchController;
import com.luee.wally.admin.repository.PaymentRepository;
import com.luee.wally.api.service.impex.ImportService;
import com.luee.wally.command.payment.RuleStatusType;
import com.luee.wally.entity.RedeemingRequests;
/*
 * ad_rev < (amount in USD / 20) - yellow flag
   ad_rev < (amount in USD / 40) - red flag
 */
public class AdRevRule extends RedeemingRequestRule {
	private final Logger logger = Logger.getLogger(AffsSearchController.class.getName());
	
	private PaymentRepository paymentRepository=new PaymentRepository();
	
	@Override
	public void execute(RedeemingRequestRuleContext context, RedeemingRequests redeemingRequests) {
		BigDecimal maxRev=BigDecimal.valueOf(redeemingRequests.getMaxRev());
		double amount=Double.parseDouble(redeemingRequests.getAmount());
		
		if(Double.compare(amount,40)>0){
			amount=40;
		}
		BigDecimal usdAmount;
		
		if(redeemingRequests.getType().equalsIgnoreCase("PayPal")){
			String currencyCode=context.getPayPalCountryCodeMap().get(redeemingRequests.getCountryCode());
		
			try{
			  usdAmount=paymentRepository.convert(amount, currencyCode, "USD");
			}catch(Exception e){
				logger.log(Level.SEVERE,"Unable to extract currency value for "+currencyCode,e);
				throw new RuntimeException("Unable to extract currency value for "+currencyCode);
			}
	    }else{  //Amazon
	    	String currencyCode=context.getTangoCardCountryCodeMap().get(redeemingRequests.getCountryCode());			
			try{
			  usdAmount=paymentRepository.convert(amount, currencyCode, "USD");
			}catch(Exception e){
				logger.log(Level.SEVERE,"Unable to extract currency value for "+currencyCode,e);
				throw new RuntimeException("Unable to extract currency value for "+currencyCode);
			}				    
	    }
		BigDecimal v20=usdAmount.divide(BigDecimal.valueOf(20));
		BigDecimal v40=usdAmount.divide(BigDecimal.valueOf(40));
		
		if( maxRev.compareTo(v40)<0){
			   context.getResult().add(RuleResultType.TOTAL_AD_REV_LESS_THEN_40);
			   if(context.isExitOnResult()){
					  return; 
			   }
		}else if(maxRev.compareTo(v20)<0){
			   context.getResult().add(RuleResultType.TOTAL_AD_REV_LESS_THEN_20);
			   if(context.isExitOnResult()){
					  return; 
			   }
		}
		if (next != null) {
			next.execute(context,redeemingRequests);
		}		
	}

}
