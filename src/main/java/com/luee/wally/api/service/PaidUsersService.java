package com.luee.wally.api.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.luee.wally.admin.repository.PaidUsersRepository;
import com.luee.wally.admin.repository.PaymentRepository;
import com.luee.wally.api.ConnectionMgr;
import com.luee.wally.command.PaidUserGroupByForm;
import com.luee.wally.command.PaidUserSearchForm;
import com.luee.wally.command.UnremoveUserForm;
import com.luee.wally.command.PaidUserGroupByForm.GroupByType;
import com.luee.wally.command.PaidUserGroupByResult;
import com.luee.wally.command.viewobject.PaidUserGroupByVO;
import com.luee.wally.constants.Constants;
import com.luee.wally.entity.PaidUser;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.json.JSONUtils;

public class PaidUsersService {
	private final Logger logger = Logger.getLogger(PaidUsersService.class.getName());

	PaidUsersRepository paidUsersRepository=new PaidUsersRepository();
	PaymentRepository paymentRepository=new PaymentRepository();
	
	public Collection<RedeemingRequests> getRedeemingRequestsRemoved(String userGuid,String removalReason){
		Collection<Entity> entities= paymentRepository.findRedeemingRequestsRemoved(userGuid, removalReason);
		return entities.stream().map(RedeemingRequests::valueOf).collect(Collectors.toList());
	}
	public Collection<PaidUserGroupByVO> searchGroupBy(PaidUserGroupByForm form){
		
	    /*
	     * Split filtering to first fetching data from paid_users and then filtering over redeeming_requests
	     * and then grouping
	     */
		Collection<Entity> output=new ArrayList<>();
		Collection<Entity> result=new ArrayList<>();
	    
	    
	    /*
	     * Split filtering to first fetching data from paid_users and then filtering over redeeming_requests
	     */
	    String cursor=null;
	    do{
	       output.clear();	
	       //read batch from paid users
	       cursor= paidUsersRepository.findPaidUsers(cursor,output,form.getType(), form.getStartDate(), form.getEndDate());
	       if(cursor==null){
	         break;
	       }
	       /* FIX this !!!!!!!!!!!!!!!!!
	        * BAAAAAAAAAAAAAAADDDDDDDDDDDDDDDDDD
	        * BAD
	        */
	       Collection<Key> keys=output.stream().map(e->{
	    	   if(e.getProperty("redeeming_request_key") instanceof Key){
	    		 return  (Key)e.getProperty("redeeming_request_key"); 
	    	   }else{
	    	     return KeyFactory.stringToKey((String)e.getProperty("redeeming_request_key"));
	    	   }	    	   
	       }).collect(Collectors.toSet());
	       Map<Key,Entity> redeemingRequestsMap= paidUsersRepository.findRedeemingRequestEntities(keys, form.getCountryCodes(), form.getPackageNames());
	       output.removeIf(e->{
	    	   Key k;
	    	   if(e.getProperty("redeeming_request_key") instanceof Key){
	    		   k=(Key)e.getProperty("redeeming_request_key"); 
	    	   }else{
	    	       k=KeyFactory.stringToKey((String)e.getProperty("redeeming_request_key"));
	    	   }	    	   
	    	   
	    	   if(redeemingRequestsMap.keySet().contains(k)){
	    		   e.setProperty("country_code", redeemingRequestsMap.get(k).getProperty("country_code"));
	    		   return false; 
	    	   }
        	   return  true;
           });
           
    	   result.addAll(output);
       
	    }while(cursor!=null);
	    
	  //filter out amount
	    
	  Collection<PaidUserGroupByVO> paidUsers=result.stream().map(PaidUserGroupByVO::valueOf).collect(Collectors.toList());
	  Collection<PaidUserGroupByVO> filtered=applyAmountFilterGroupBy(paidUsers,form.getAmountFrom(),form.getAmountTo());

	  //group by 
	  
	  return filtered;
	}
	
    /*
     * In memory  grouping
     */
	public List<PaidUserGroupByResult> groupBy(Collection<PaidUserGroupByVO> list,GroupByType groupByType,String groupByTime,String groupByLocale){
		List<PaidUserGroupByResult> output=new ArrayList<>();
		switch(groupByType){
		case TIME:
		  if(groupByTime.equals("day")){
			  
			  Map<Integer,List<PaidUserGroupByVO>> result=list.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getDayTime));
			  for(List<PaidUserGroupByVO> groups:result.values()){
				  double sum=groups.stream().mapToDouble(PaidUserGroupByVO::getEurCurrency).sum();
			      output.add(new PaidUserGroupByResult(groups.get(0).getDayTimeStr(), null,BigDecimal.valueOf(sum),groups.get(0).getDayTime(),groups.size()));
			  }			  
			  return output;
		  }else if(groupByTime.equals("month")){
			  Map<Integer,List<PaidUserGroupByVO>> result=list.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getMonthTime));
			  for(List<PaidUserGroupByVO> groups:result.values()){
				  double sum=groups.stream().mapToDouble(PaidUserGroupByVO::getEurCurrency).sum();
			      output.add(new PaidUserGroupByResult(groups.get(0).getMonthTimeStr(), null,BigDecimal.valueOf(sum),groups.get(0).getDayTime(),groups.size()));
			  }			  
			  return output;
			  
		  }else{  //year			  
			  Map<Integer,List<PaidUserGroupByVO>> result=list.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getYearTime));
			  for(List<PaidUserGroupByVO> groups:result.values()){				  
				  double sum=groups.stream().mapToDouble(PaidUserGroupByVO::getEurCurrency).sum();
			      output.add(new PaidUserGroupByResult(groups.get(0).getYearTimeStr(), null,BigDecimal.valueOf(sum),groups.get(0).getDayTime(),groups.size()));
			  }			  
			  return output;			  
		  }
		case LOCALE:
		  if(groupByLocale.equals("country")){
			    Map<String,Long> counting=list.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getCountryCode,Collectors.counting()));
				Map<String,Double> result=list.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getCountryCode,Collectors.summingDouble(PaidUserGroupByVO::getEurCurrency)));
				return result.entrySet().stream().map(e->new PaidUserGroupByResult(null,e.getKey(),BigDecimal.valueOf(e.getValue()),null,counting.get(e.getKey()))).collect(Collectors.toList());				  
		  }else{  //currency
			  	Map<String,Long> counting=list.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getCurrencyCode,Collectors.counting()));
				Map<String,Double> result=list.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getCurrencyCode,Collectors.summingDouble(PaidUserGroupByVO::getEurCurrency)));
				return result.entrySet().stream().map(e->new PaidUserGroupByResult(null,e.getKey(),BigDecimal.valueOf(e.getValue()),null,counting.get(e.getKey()))).collect(Collectors.toList());
		  }
		  
		case ALL:
		  
			if(groupByTime.equals("day")){
			    Map<Integer,List<PaidUserGroupByVO>> result=list.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getDayTime));
			    for(List<PaidUserGroupByVO> groups:result.values()){				  
				  Map<String,Double> subgroups;
				  Map<String,Long> counting;
				  if(groupByLocale.equals("country")){//day:country
					 counting= groups.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getCountryCode,Collectors.counting())); 
			    	 subgroups=groups.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getCountryCode,Collectors.summingDouble(PaidUserGroupByVO::getEurCurrency)));
				  }else{  ////day:currency
					  counting= groups.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getCurrencyCode,Collectors.counting()));
					  subgroups=groups.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getCurrencyCode,Collectors.summingDouble(PaidUserGroupByVO::getEurCurrency)));
				  }
			      output.addAll(subgroups.entrySet().stream().map(e->new PaidUserGroupByResult(groups.get(0).getDayTimeStr(),e.getKey(),BigDecimal.valueOf(e.getValue()),groups.get(0).getDayTime(),counting.get(e.getKey()))).collect(Collectors.toList()));
			    }
			    return output;
			}else if(groupByTime.equals("month")){
				  Map<Integer,List<PaidUserGroupByVO>> result=list.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getMonthTime));
				  for(List<PaidUserGroupByVO> groups:result.values()){
					  Map<String,Double> subgroups;
					  Map<String,Long> counting;
				      if(groupByLocale.equals("country")){//month:country 
				    	  counting= groups.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getCountryCode,Collectors.counting())); 
				    	  subgroups=groups.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getCountryCode,Collectors.summingDouble(PaidUserGroupByVO::getEurCurrency)));
					  }else{  //month:currency
						 counting= groups.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getCurrencyCode,Collectors.counting())); 
						 subgroups=groups.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getCurrencyCode,Collectors.summingDouble(PaidUserGroupByVO::getEurCurrency)));
					  }
				      output.addAll(subgroups.entrySet().stream().map(e->new PaidUserGroupByResult(groups.get(0).getMonthTimeStr(),e.getKey(),BigDecimal.valueOf(e.getValue()),groups.get(0).getDayTime(),counting.get(e.getKey()))).collect(Collectors.toList()));
				  }			  
				  return output;				
			}else{  //year
				  Map<Integer,List<PaidUserGroupByVO>> result=list.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getYearTime));
				  for(List<PaidUserGroupByVO> groups:result.values()){
					  Map<String,Double> subgroups;
					  Map<String,Long> counting;
				      if(groupByLocale.equals("country")){//month:country
				    	 counting= groups.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getCountryCode,Collectors.counting())); 
				    	 subgroups=groups.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getCountryCode,Collectors.summingDouble(PaidUserGroupByVO::getEurCurrency)));
					  }else{  //month:currency
						 counting= groups.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getCurrencyCode,Collectors.counting())); 
						 subgroups=groups.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getCurrencyCode,Collectors.summingDouble(PaidUserGroupByVO::getEurCurrency)));
					  }
				      output.addAll(subgroups.entrySet().stream().map(e->new PaidUserGroupByResult(groups.get(0).getYearTimeStr(),e.getKey(),BigDecimal.valueOf(e.getValue()),groups.get(0).getDayTime(),counting.get(e.getKey()))).collect(Collectors.toList()));
				  }			  
				  return output;					
			}

		case NONE:
		  return null;	
		}
		return null;
	}
	
	public void sortBy(List<PaidUserGroupByResult> records){
	   records.sort(Comparator.comparing(o -> o.getDayTime()));	   
	}	
    /*
     * In memory filter
     */
    private Collection<PaidUserGroupByVO> applyAmountFilterGroupBy(Collection<PaidUserGroupByVO> list,BigDecimal amountFrom,BigDecimal amountTo){
    	if(amountFrom==null&&amountTo==null){
    		return list;
    	}
    	Collection<PaidUserGroupByVO> result;
    	if(amountFrom!=null&&amountTo!=null){
    		result=list.stream().filter(rr->{
    				if((rr.getEurCurrencyBigDecimal()).compareTo(amountFrom)>=0&&(rr.getEurCurrencyBigDecimal()).compareTo(amountTo)<=0){
    		            return true;    		            
    		        }else{
    		        	return false;
    		        }
    		}).collect(Collectors.toList());
    	}else if(amountFrom!=null){
    		result=list.stream().filter(rr->{
    				if(rr.getEurCurrencyBigDecimal().compareTo(amountFrom)>=0){
    		            return true;    		            
    		        }else{
    		        	return false;
    		        }
    		}).collect(Collectors.toList());
    	}else{
    		result=list.stream().filter(rr->{
    				if(rr.getEurCurrencyBigDecimal().compareTo(amountTo)<=0){
    		            return true;    		            
    		        }else{
    		        	return false;
    		        }    				
    		}).collect(Collectors.toList());
    	}
    	
    	return result;
    }	
    
	public Collection<PaidUser> search(PaidUserSearchForm form){
		Collection<Entity> output=new ArrayList<>();
		Collection<Entity> result=new ArrayList<>();

	    
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
	    Collection<PaidUser> list= paidUsersRepository.findPaidUsersByEmail(form.getEmail(),form.getPaypalAccount());
	    return list.stream().sorted(Comparator.comparing(PaidUser::getDate)).collect(Collectors.toList());
	    
	}
	
	public Collection<PaidUser> searchByGuid(PaidUserSearchForm form){
	    Collection<PaidUser> list= paidUsersRepository.findPaidUsersByGuid(form.getUserGuid());
	    return list.stream().sorted(Comparator.comparing(PaidUser::getDate)).collect(Collectors.toList());
	}
	
	public void checkVPNUsageAsync(String key,String ipAddress,String countryCode) throws IOException{
		
		String url=String.format(Constants.VPN_SERVICE_URL,ipAddress,countryCode);
		String result = ConnectionMgr.INSTANCE.getJSON(url);
		
		Map<String, Object> map = JSONUtils.readObject(result, Map.class);
		Boolean isUsingVpn=(Boolean)map.get("isUsingVpn");
		
		Entity redeemingRequest=paidUsersRepository.findEntityByKey(key);
		if(redeemingRequest==null){
			throw new IOException("Unable to find entity by key :"+key);
		}
		
		redeemingRequest.setProperty("is_using_vpn", isUsingVpn);
		paidUsersRepository.save(redeemingRequest);
	}
}
