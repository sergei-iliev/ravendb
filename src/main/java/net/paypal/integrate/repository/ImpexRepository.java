package net.paypal.integrate.repository;

import java.util.Collection;

import org.springframework.stereotype.Repository;

import com.google.cloud.Timestamp;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.LoadResult;
import com.googlecode.objectify.ObjectifyService;

import net.paypal.integrate.api.ShardedSequence;
import net.paypal.integrate.entity.Counter;
import net.paypal.integrate.entity.PayPalPayment;
import net.paypal.integrate.entity.PayPalUser;
import net.paypal.integrate.entity.RedeemingRequests;

@Repository
public class ImpexRepository {

	
	public Key<RedeemingRequests> save(RedeemingRequests entity) {
		return ObjectifyService.ofy().save().entity(entity).now();
	}

	public long count() {
		return ObjectifyService.ofy().load().type(RedeemingRequests.class).count();
	}

	public RedeemingRequests findByUserGuid(String userGuid){
		
		return ObjectifyService.ofy().load().type(RedeemingRequests.class).filter("userGuid =",userGuid).first().now();		
	}
	
	public Collection<RedeemingRequests> find(Collection<String> userGuids){
		return ObjectifyService.ofy().load().type(RedeemingRequests.class).filter("userGuid in",userGuids).list();
	}
	public Collection<RedeemingRequests> find(boolean paid, String countryCode, String packageName,
			boolean confirmedEmail, String type, String amount, Timestamp date) {
		return ObjectifyService.ofy().load().type(RedeemingRequests.class)
				.filter("amount =",amount)
				.filter("type =",type)
				.filter("confirmedEmail =",confirmedEmail)
				.filter("packageName =",packageName)
				.filter("countryCode =", countryCode)
				.filter("paid =", paid)
				.filter("date >=", date)
				.order("- date")
				.list();
	}

}
