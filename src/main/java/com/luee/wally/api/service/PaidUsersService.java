package com.luee.wally.api.service;

import java.util.Collection;
import java.util.logging.Logger;

import com.luee.wally.admin.repository.PaidUsersRepository;
import com.luee.wally.command.PaidUserSearchForm;
import com.luee.wally.entity.PaidUser;

public class PaidUsersService {
	private final Logger logger = Logger.getLogger(PaidUsersService.class.getName());

	public Collection<PaidUser> search(PaidUserSearchForm form){
	    PaidUsersRepository paidUsersRepository=new PaidUsersRepository();
	    return paidUsersRepository.findPaidUsers(null, form.getStartDate(), form.getEndDate(),null,null);
	}
	
	public Collection<PaidUser> searchByEmail(PaidUserSearchForm form){
	    PaidUsersRepository paidUsersRepository=new PaidUsersRepository();
	    return paidUsersRepository.findPaidUsersByEmail(form.getEmail(),form.getPaypalAccount());
	}
	
	public Collection<PaidUser> searchByGuid(PaidUserSearchForm form){
	    PaidUsersRepository paidUsersRepository=new PaidUsersRepository();
	    return paidUsersRepository.findPaidUsersByGuid(form.getUserGuid());
	}
}
