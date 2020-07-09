package com.luee.wally.api.service;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.appengine.api.datastore.Entity;
import com.luee.wally.admin.repository.PaymentRepository;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.utils.TestDatabase;






public class ConfirmEmailService extends AbstractService{
	private final Logger logger = Logger.getLogger(ConfirmEmailService.class.getName());
    
	public Collection<RedeemingRequests> confirmEmail(String email) throws IOException{
		
		PaymentRepository paidUsersRepository=new PaymentRepository();
		Collection<Entity> entities=paidUsersRepository.getEligibleUsersByEmail(email);
		return entities.stream().map(RedeemingRequests::valueOf).collect(Collectors.toList());
	}

	public void setConfirmedEmail(String key, boolean value){
		PaymentRepository paymentRepository=new PaymentRepository();
		Entity entity=paymentRepository.findEntityByKey(key);
		entity.setProperty("confirmed_email", value);
		paymentRepository.save(entity);		
	}
}
