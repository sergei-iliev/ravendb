package com.luee.wally.api.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.google.appengine.api.datastore.Entity;
import com.luee.wally.admin.repository.PaidUsersRepository;
import com.luee.wally.admin.repository.UserRepository;

public class UserService {
	private final Logger logger = Logger.getLogger(UserService.class.getName());

	private UserRepository userRepository=new UserRepository();		
	private PaidUsersRepository paidUsersRepository=new PaidUsersRepository();
	
	public int deleteUserDataByEmail(String email){
	    int count=0;
		Collection<String> emails=(this.convertToEmails(email));
		//1. affs
		Collection<Entity> affs = userRepository.getRecordsByEmails(emails, "affs", "email");
		// 2.search RR email
		Collection<Entity> redeemingRequestEmails = userRepository.getRecordsByEmails(emails,
				"redeeming_requests_new", "email");
		
		// 3.search RR paypal
		Collection<Entity> redeemingRequestPayPals = userRepository.getRecordsByEmails(emails,
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

	public int deleteUserDataByGuid(String guid){
		int count=0;
		//1. affs
		Collection<Entity> affs = userRepository.getRecordsByEmails(Collections.singleton(guid), "affs", "user_guid");
		
		// 2.redeeming_requests_new 
		Collection<Entity> redeemingRequests = userRepository.getRecordsByEmails(Collections.singleton(guid), "redeeming_requests_new", "user_guid");
		
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
	
}
