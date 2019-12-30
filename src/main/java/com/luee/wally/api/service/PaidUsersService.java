package com.luee.wally.api.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.luee.wally.admin.repository.PaidUsersRepository;
import com.luee.wally.command.PaidUserSearchForm;
import com.luee.wally.entity.PaidUser;
import com.luee.wally.entity.RedeemingRequests;

public class PaidUsersService {
	private final Logger logger = Logger.getLogger(PaidUsersService.class.getName());

	public Collection<PaidUser> search(PaidUserSearchForm form){
		Collection<Entity> output=new ArrayList<>();
		Collection<Entity> result=new ArrayList<>();
	    PaidUsersRepository paidUsersRepository=new PaidUsersRepository();
	    
	    /*
	     * Split filtering to first fetching data from paid_users and then filtering over redeeming_requests
	     */
	    String cursor=null;
	    do{
	       output.clear();	
	       //read batch from paid users
	       cursor= paidUsersRepository.findPaidUsers(cursor,output,form.getTypes().isEmpty()?null:form.getTypes().iterator().next(), form.getStartDate(), form.getEndDate());
	       if(cursor==null){
	         break;
	       }

	       if(form.getCountryCodes().size()>0||form.getPackageNames().size()>0){
	       //get FK to redeeming_requests
  	          Collection<Key> keys=output.stream().map(e->KeyFactory.stringToKey((String)e.getProperty("redeeming_request_key"))).collect(Collectors.toSet());

	    	  Collection<Key> redeemingRequestKeys= paidUsersRepository.findRedeemingRequests(keys, form.getCountryCodes(), form.getPackageNames());
	          Collection<Entity> o=output.stream().filter(e->redeemingRequestKeys.contains(KeyFactory.stringToKey((String)e.getProperty("redeeming_request_key")))).collect(Collectors.toSet());
	          result.addAll(o);  
	       }else{
	    	  result.addAll(output);
	       }
       
	    }while(cursor!=null);
	    
	    //filter out amount
	    
	  Collection<PaidUser> paidUsers=result.stream().map(PaidUser::valueOf).collect(Collectors.toList());
	  return applyAmountFilter(paidUsers,form.getAmountFrom(),form.getAmountTo());
	  
	}
    /*
     * In memory filter
     */
    private Collection<PaidUser> applyAmountFilter(Collection<PaidUser> list,BigDecimal amountFrom,BigDecimal amountTo){
    	if(amountFrom==null&&amountTo==null){
    		return list;
    	}
    	Collection<PaidUser> result;
    	if(amountFrom!=null&&amountTo!=null){
    		result=list.stream().filter(rr->{
    				if((rr.getEurCurrency()).compareTo(amountFrom)>=0&&(rr.getEurCurrency()).compareTo(amountTo)<=0){
    		            return true;    		            
    		        }else{
    		        	return false;
    		        }
    		}).collect(Collectors.toList());
    	}else if(amountFrom!=null){
    		result=list.stream().filter(rr->{
    				if(rr.getEurCurrency().compareTo(amountFrom)>=0){
    		            return true;    		            
    		        }else{
    		        	return false;
    		        }
    		}).collect(Collectors.toList());
    	}else{
    		result=list.stream().filter(rr->{
    				if(rr.getEurCurrency().compareTo(amountTo)<=0){
    		            return true;    		            
    		        }else{
    		        	return false;
    		        }    				
    		}).collect(Collectors.toList());
    	}
    	
    	return result;
    }
	public Collection<PaidUser> searchByEmail(PaidUserSearchForm form){
	    PaidUsersRepository paidUsersRepository=new PaidUsersRepository();
	    Collection<PaidUser> list= paidUsersRepository.findPaidUsersByEmail(form.getEmail(),form.getPaypalAccount());
	    return list.stream().sorted(Comparator.comparing(PaidUser::getDate)).collect(Collectors.toList());
	    
	}
	
	public Collection<PaidUser> searchByGuid(PaidUserSearchForm form){
	    PaidUsersRepository paidUsersRepository=new PaidUsersRepository();
	    Collection<PaidUser> list= paidUsersRepository.findPaidUsersByGuid(form.getUserGuid());
	    return list.stream().sorted(Comparator.comparing(PaidUser::getDate)).collect(Collectors.toList());
	}
}
