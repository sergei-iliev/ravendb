package net.paypal.integrate.repository;

import org.springframework.stereotype.Repository;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import net.paypal.integrate.entity.Affs;
import net.paypal.integrate.entity.UserDailyRevenue;
import net.paypal.integrate.entity.UserRevPackage;

@Repository
public class RevenueRepository {

	
	public Key<UserDailyRevenue> save(UserDailyRevenue entity) {
		return ObjectifyService.ofy().save().entity(entity).now();
	}

	public Affs getAffsByGaid(String gaid){
	  return	ObjectifyService.ofy().load().type(Affs.class).filter("gaid =",gaid).first().now();
	}
	
	public Key<Affs> save(Affs entity) {
		return ObjectifyService.ofy().save().entity(entity).now();
	}
	
	public UserDailyRevenue getUserDailyRevenueByGaid(String gaid){
	  return	ObjectifyService.ofy().load().type(UserDailyRevenue.class).filter("gaid =",gaid).first().now();
	}
	
	public UserDailyRevenue getUserDailyRevenue(){
		  return	ObjectifyService.ofy().load().type(UserDailyRevenue.class).first().now();
	}
	
	public void save(Affs affs,UserDailyRevenue userDailyRevenue) {
		ObjectifyService.ofy().transact(()->{
			 Key<Affs> key= ObjectifyService.ofy().save().entity(affs).now();
			 if(userDailyRevenue.getAffs()==null){
				 userDailyRevenue.setAffs(key);  
			 }
			 
			 ObjectifyService.ofy().save().entity(userDailyRevenue).now();			 			
		});
		
	
	}
	
	public UserRevPackage getUserRevPackage(String packageName){
		return	ObjectifyService.ofy().load().type(UserRevPackage.class).filter("packageName =",packageName).first().now();
	}
	
	public void save(UserRevPackage entity){
		 ObjectifyService.ofy().save().entity(entity).now();
	}
}
