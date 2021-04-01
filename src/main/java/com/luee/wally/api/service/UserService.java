package com.luee.wally.api.service;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.appengine.api.datastore.Entity;
import com.luee.wally.admin.repository.AffsRepository;
import com.luee.wally.admin.repository.PaidUsersRepository;
import com.luee.wally.admin.repository.UserRepository;
import com.luee.wally.entity.User;

public class UserService {
	private final Logger logger = Logger.getLogger(UserService.class.getName());

	private UserRepository userRepository=new UserRepository();	
	private AffsRepository affsRepository=new AffsRepository();	
	private PaidUsersRepository paidUsersRepository=new PaidUsersRepository();
	
	public Map<String,Long> countUserGuids(List<String> userGuids){		
		Collection<Entity> entities=new ArrayList<>();
		Collection<String> sublist=new ArrayList<>(10);
		int i=0;
		for(String userGuid:userGuids){
		    sublist.add(userGuid);		   
			i++;
			if(i==10){
		        entities.addAll(affsRepository.findAffsByUserGuids(sublist)); //accumulate all		        
				sublist.clear();
				i=0;
			}
		}
		if(i>0){ //do the left over
		   entities.addAll(affsRepository.findAffsByUserGuids(sublist)); //accumulate all    		   
		}
		
		return entities.stream().collect(Collectors.groupingBy(e->(String)e.getProperty("user_guid"), Collectors.counting()));		
	}
	
	public void createUserGuidFile(Writer writer,Map<String,Long> existsGuidMap,Collection<String>  notExistsGiuids)throws IOException{
		writer.append("Existing user_guids: ");
		writer.append(existsGuidMap.size()+"\r\n");
		
		writer.append("Non existing user_guids: ");
		writer.append(notExistsGiuids.size()+"\r\n");
		for(String userGuid:notExistsGiuids){
			writer.append(userGuid+"\r\n");
		}
						
	}

	public int deleteUserDataByEmail(String email){
	    int count=0;
		Collection<String> emails=(this.convertToEmails(email));
		//1. affs
		Collection<Entity> affs = paidUsersRepository.getRecordsByEmails(emails, "affs", "email");
		// 2.search RR email
		Collection<Entity> redeemingRequestEmails = paidUsersRepository.getRecordsByEmails(emails,
				"redeeming_requests_new", "email");
		
		// 3.search RR paypal
		Collection<Entity> redeemingRequestPayPals = paidUsersRepository.getRecordsByEmails(emails,
				"redeeming_requests_new", "paypal_account");
		
		if(affs.isEmpty()&&redeemingRequestEmails.isEmpty()&&redeemingRequestPayPals.isEmpty()){
			throw new IllegalAccessError("Email address '"+email+"' was not found in our system.");
		}
		
		
		redeemingRequestEmails.forEach(e->{
			e.setProperty("full_name","Removed per request from user");
			e.setProperty("full_address","Removed per request from user");
			e.setProperty("email","removed@removed.com");
			e.setProperty("paypal_account","removed@removed.com");			
			userRepository.createOrUpdateEntity(e);
		});
		
		redeemingRequestPayPals.forEach(e->{
			e.setProperty("paypal_account","removed@removed.com");
			userRepository.createOrUpdateEntity(e);
		});
		
		
		//paid_user
		redeemingRequestEmails.forEach(rr->{
			Collection<Entity> paidUsers=  paidUsersRepository.findEntities("paid_users","user_guid",(String)rr.getProperty("user_guid"));
			paidUsers.forEach(pu->{
				pu.setProperty("paypal_account", "removed@removed.com");
				pu.setProperty("email_address","removed@removed.com");
				paidUsersRepository.createOrUpdateEntity(pu);
			});
		});
		
		redeemingRequestPayPals.forEach(rr->{
			Collection<Entity> paidUsers=  paidUsersRepository.findEntities("paid_users","user_guid",(String)rr.getProperty("user_guid"));
			paidUsers.forEach(pu->{
				pu.setProperty("email_address","removed@removed.com");
				pu.setProperty("paypal_account", "removed@removed.com");
				paidUsersRepository.createOrUpdateEntity(pu);
			});
		});
		for(Entity aff:affs){
			userRepository.deleteEntity(aff.getKey());
			count++;
		}
		return count;
	}
	public int deleteUserDataByGaid(String gaid){
		int count=0;
		//1. affs
		Collection<Entity> affs = paidUsersRepository.findEntities("affs", "gaid",gaid);
		Collection<String> guids=affs.stream().map(e->(String)e.getProperty("user_guid")).collect(Collectors.toList());
		if(affs.isEmpty()){
			throw new IllegalAccessError("Gaid '"+gaid+"' was not found in our system.");
		}		
		// 2.redeeming_requests_new 
		Collection<Entity> redeemingRequests = paidUsersRepository.getRecordsByEmails(guids, "redeeming_requests_new", "user_guid");		
		if(redeemingRequests.isEmpty()){
			throw new IllegalAccessError("No redeeming requests for Gaid '"+gaid+"' found in our system.");
		}
		
		redeemingRequests.forEach(e->{
			e.setProperty("full_name","Removed per request from user");
			e.setProperty("full_address","Removed per request from user");
			e.setProperty("email","removed@removed.com");
			e.setProperty("paypal_account","removed@removed.com");
			userRepository.createOrUpdateEntity(e);
		});
		
		//paid_user
		redeemingRequests.forEach(rr->{
			Collection<Entity> paidUsers=  paidUsersRepository.findEntities("paid_users","user_guid",(String)rr.getProperty("user_guid"));
			paidUsers.forEach(pu->{
				pu.setProperty("paypal_account", "removed@removed.com");
				pu.setProperty("email_address","removed@removed.com");
				paidUsersRepository.createOrUpdateEntity(pu);
			});
		});

		for(Entity aff:affs){
			userRepository.deleteEntity(aff.getKey());
			count++;
		}
		return count;		
	}
	public int deleteUserDataByGuid(String guid){
		int count=0;
		//1. affs
		Collection<Entity> affs = paidUsersRepository.getRecordsByEmails(Collections.singleton(guid), "affs", "user_guid");
		
		// 2.redeeming_requests_new 
		Collection<Entity> redeemingRequests = paidUsersRepository.getRecordsByEmails(Collections.singleton(guid), "redeeming_requests_new", "user_guid");
		
		if(affs.isEmpty()&&redeemingRequests.isEmpty()){
			throw new IllegalAccessError("User guid '"+guid+"' was not found in our system.");
		}
		
		redeemingRequests.forEach(e->{
			e.setProperty("full_name","Removed per request from user");
			e.setProperty("full_address","Removed per request from user");
			e.setProperty("email","removed@removed.com");
			e.setProperty("paypal_account","removed@removed.com");
			userRepository.createOrUpdateEntity(e);
		});
		
		//paid_user
		redeemingRequests.forEach(rr->{
			Collection<Entity> paidUsers=  paidUsersRepository.findEntities("paid_users","user_guid",(String)rr.getProperty("user_guid"));
			paidUsers.forEach(pu->{
				pu.setProperty("paypal_account", "removed@removed.com");
				pu.setProperty("email_address","removed@removed.com");
				paidUsersRepository.createOrUpdateEntity(pu);
			});
		});

		for(Entity aff:affs){
			userRepository.deleteEntity(aff.getKey());
			count++;
		}
		return count;
		
	}
	public Collection<String> convertToEmails(String email){
		Collection<String> result=new HashSet<>();
		String lower=email.toLowerCase();		
		result.add(lower);
		result.add(email);
		result.add(StringUtils.capitalize(lower));
				
		return result;
	}
	
	public String convertNumberToText(int number){
		if(number<=0){
			return "0";
		}else if(number==1){
			return "One";
		}else if(number==2){
			return "Two";
		}else if(number==3){
			return "Three";
		}else{
			return String.valueOf(number); 
		}		
	}
	
	public User getUser(String email,String password){		
		Entity entity=userRepository.findEntity("user","email",email);
		if(entity!=null&&entity.getProperty("password").equals(password)){
		   return User.valueOf(entity);
		}else{
		   return null;
		}
	}
}
