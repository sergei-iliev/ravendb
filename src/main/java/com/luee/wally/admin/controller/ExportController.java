package com.luee.wally.admin.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.google.appengine.api.datastore.Entity;
import com.luee.wally.admin.repository.PaidUsersRepository;
import com.luee.wally.api.route.Controller;
import com.luee.wally.api.service.impex.ExportService;
import com.luee.wally.command.ExportPaidUsersForm;
import com.luee.wally.csv.PaidUsers2018;
import com.luee.wally.entity.PaidUser;
import com.luee.wally.entity.PaidUserExternal;
import com.luee.wally.entity.RedeemingRequests;

public class ExportController implements Controller{
  
	private static final Logger logger = Logger.getLogger(ExportController.class.getName());
	
	public void index(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		req.getRequestDispatcher("/jsp/export_paid_users.jsp").forward(req, resp);
	}
	
	public void exportPaidUsers(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String prefix = "2019111111";
		int count = 0;
		String invoiceNumber;
		
		
		ExportPaidUsersForm form=ExportPaidUsersForm.parse(req);
		ExportService exportService=new ExportService();
		PaidUsersRepository paidUsersRepository=new PaidUsersRepository();
		
		Collection<PaidUser> paidUsers=null;
		Collection<PaidUserExternal> paidUserExternals=null;
		if(form.isExternal()){			
			paidUserExternals=exportService.findPaidUsersExternalByDate(form.getStartDate(), form.getEndDate());
			for (PaidUserExternal user : paidUserExternals) {
					
			}
			
		}else{
			paidUsers=exportService.findPaidUsersByDate(form.getStartDate(), form.getEndDate());
			for (PaidUser user : paidUsers) {
				Collection<Entity> entities=paidUsersRepository.findEntities("redeeming_requests_new", "user_guid", user.getUserGuid());
			    if(entities.size()>1){
			    	throw new ServletException("Too many entities for user_guid: "+user.getUserGuid());
			    }
			    
			    if(entities.size()==0){
			    	logger.log(Level.SEVERE, "No user entry found for - " + user.getUserGuid());
			        continue;
			    }
			    
			    RedeemingRequests redeemingRequests= RedeemingRequests.valueOf(entities.iterator().next());
				invoiceNumber=prefix + String.valueOf(count++);
				user.setInvoiceNumber(invoiceNumber);
				//entities.add(new ImmutablePair<>(user, redeemingRequests)); 
				// create pdf in cloud store
				//importService.createPDFInCloudStore(redeemingRequest, user,"user_credit_notes_2019_with_id/PaidUsers2019_",
				//		invoiceNumber);
			    
			    
			}
		}
		
		
		
		
	}
	
	
}
