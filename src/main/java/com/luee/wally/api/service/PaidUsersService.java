package com.luee.wally.api.service;

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
import com.luee.wally.command.PaidUserGroupByForm;
import com.luee.wally.command.PaidUserSearchForm;
import com.luee.wally.command.PaidUserGroupByForm.GroupByType;
import com.luee.wally.command.PaidUserGroupByResult;
import com.luee.wally.command.viewobject.PaidUserGroupByVO;
import com.luee.wally.entity.PaidUser;
import com.luee.wally.entity.RedeemingRequests;

public class PaidUsersService {
	private final Logger logger = Logger.getLogger(PaidUsersService.class.getName());

	public Collection<PaidUserGroupByVO> searchGroupBy(PaidUserGroupByForm form){
		
	    /*
	     * Split filtering to first fetching data from paid_users and then filtering over redeeming_requests
	     * and then grouping
	     */
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
			      output.add(new PaidUserGroupByResult(groups.get(0).getDayTimeStr(), null,BigDecimal.valueOf(sum),groups.get(0).getDayTime()));
			  }			  
			  return output;
		  }else if(groupByTime.equals("month")){
			  Map<Integer,List<PaidUserGroupByVO>> result=list.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getMonthTime));
			  for(List<PaidUserGroupByVO> groups:result.values()){
				  double sum=groups.stream().mapToDouble(PaidUserGroupByVO::getEurCurrency).sum();
			      output.add(new PaidUserGroupByResult(groups.get(0).getMonthTimeStr(), null,BigDecimal.valueOf(sum),groups.get(0).getDayTime()));
			  }			  
			  return output;
			  
		  }else{  //year			  
			  Map<Integer,List<PaidUserGroupByVO>> result=list.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getYearTime));
			  for(List<PaidUserGroupByVO> groups:result.values()){				  
				  double sum=groups.stream().mapToDouble(PaidUserGroupByVO::getEurCurrency).sum();
			      output.add(new PaidUserGroupByResult(groups.get(0).getYearTimeStr(), null,BigDecimal.valueOf(sum),groups.get(0).getDayTime()));
			  }			  
			  return output;			  
		  }
		case LOCALE:
		  if(groupByLocale.equals("country")){ 
				Map<String,Double> result=list.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getCountryCode,Collectors.summingDouble(PaidUserGroupByVO::getEurCurrency)));
				return result.entrySet().stream().map(e->new PaidUserGroupByResult(null,e.getKey(),BigDecimal.valueOf(e.getValue()),null)).collect(Collectors.toList());				  
		  }else{  //currency
				Map<String,Double> result=list.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getCurrencyCode,Collectors.summingDouble(PaidUserGroupByVO::getEurCurrency)));
				return result.entrySet().stream().map(e->new PaidUserGroupByResult(null,e.getKey(),BigDecimal.valueOf(e.getValue()),null)).collect(Collectors.toList());
		  }
		  
		case ALL:
		  
			if(groupByTime.equals("day")){
			    Map<Integer,List<PaidUserGroupByVO>> result=list.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getDayTime));
			    for(List<PaidUserGroupByVO> groups:result.values()){				  
				  Map<String,Double> subgroups;
			      if(groupByLocale.equals("country")){//day:country 
			    	 subgroups=groups.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getCountryCode,Collectors.summingDouble(PaidUserGroupByVO::getEurCurrency)));
				  }else{  ////day:currency
					 subgroups=groups.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getCurrencyCode,Collectors.summingDouble(PaidUserGroupByVO::getEurCurrency)));
				  }
			      output.addAll(subgroups.entrySet().stream().map(e->new PaidUserGroupByResult(groups.get(0).getDayTimeStr(),e.getKey(),BigDecimal.valueOf(e.getValue()),groups.get(0).getDayTime())).collect(Collectors.toList()));
			    }
			    return output;
			}else if(groupByTime.equals("month")){
				  Map<Integer,List<PaidUserGroupByVO>> result=list.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getMonthTime));
				  for(List<PaidUserGroupByVO> groups:result.values()){
					  Map<String,Double> subgroups;
				      if(groupByLocale.equals("country")){//month:country 
				    	 subgroups=groups.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getCountryCode,Collectors.summingDouble(PaidUserGroupByVO::getEurCurrency)));
					  }else{  //month:currency
						 subgroups=groups.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getCurrencyCode,Collectors.summingDouble(PaidUserGroupByVO::getEurCurrency)));
					  }
				      output.addAll(subgroups.entrySet().stream().map(e->new PaidUserGroupByResult(groups.get(0).getMonthTimeStr(),e.getKey(),BigDecimal.valueOf(e.getValue()),groups.get(0).getDayTime())).collect(Collectors.toList()));
				  }			  
				  return output;				
			}else{  //year
				  Map<Integer,List<PaidUserGroupByVO>> result=list.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getYearTime));
				  for(List<PaidUserGroupByVO> groups:result.values()){
					  Map<String,Double> subgroups;
				      if(groupByLocale.equals("country")){//month:country 
				    	 subgroups=groups.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getCountryCode,Collectors.summingDouble(PaidUserGroupByVO::getEurCurrency)));
					  }else{  //month:currency
						 subgroups=groups.stream().collect(Collectors.groupingBy(PaidUserGroupByVO::getCurrencyCode,Collectors.summingDouble(PaidUserGroupByVO::getEurCurrency)));
					  }
				      output.addAll(subgroups.entrySet().stream().map(e->new PaidUserGroupByResult(groups.get(0).getYearTimeStr(),e.getKey(),BigDecimal.valueOf(e.getValue()),groups.get(0).getDayTime())).collect(Collectors.toList()));
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
