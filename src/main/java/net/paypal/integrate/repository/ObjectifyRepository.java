package net.paypal.integrate.repository;

import java.util.Collection;

import org.springframework.stereotype.Repository;

import com.google.cloud.Timestamp;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import net.paypal.integrate.api.ShardedSequence;
import net.paypal.integrate.entity.Counter;
import net.paypal.integrate.entity.PayPalPayment;
import net.paypal.integrate.entity.PayPalUser;
import net.paypal.integrate.entity.RedeemingRequests;

@Repository
public class ObjectifyRepository {

	public void delete(long id){
			 PayPalUser user=ObjectifyService.ofy().load().type(PayPalUser.class).id(id).now();
			 if(user!=null){
				 Collection<PayPalPayment> payments=ObjectifyService.ofy().load().type(PayPalPayment.class).filter("userKey", user.getKey()).list();
				 if(payments.size()>0){
					 ObjectifyService.ofy().delete().entities(payments).now(); 
				 }
				 ObjectifyService.ofy().delete().entity(user).now();
			 }	
	}

	public PayPalUser getPayPalUserById(long id){
	  return	ObjectifyService.ofy().load().type(PayPalUser.class).id(id).now();
	}
	
	public Key<PayPalUser> save(PayPalUser user){
		return ObjectifyService.ofy().save().entity(user).now();		
	}
	
	public Key<PayPalPayment> save(PayPalPayment payment){
		return ObjectifyService.ofy().save().entity(payment).now();		
	}
	
	public Collection<PayPalUser> getAccountList(){
		return ObjectifyService.ofy().load().type(PayPalUser.class).list();		
	}
	public Collection<Counter> getIncList(){
		return ObjectifyService.ofy().load().type(Counter.class).list();		
	}
	public Collection<PayPalPayment> getPaymentList(){
		return ObjectifyService.ofy().load().type(PayPalPayment.class).list();		
	}	
	
	public long createInvoiceNumer(){
		  ShardedSequence sequence=new ShardedSequence("SEQUENCE");		
		  return sequence.next();
		  //return ObjectifyService.ofy().load().type(Counter.class).filterKey("<",Key.create(Counter.class,5634472569470976l)).count();			 		 
	}

	
}
