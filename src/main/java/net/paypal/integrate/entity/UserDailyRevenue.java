package net.paypal.integrate.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class UserDailyRevenue {

	@Id
	private Long id;
	
	@Index
	private String gaid;

	@Parent
	private Key<Affs> affs;
		
	private Map<String,Set<String>> revenueCheckDates=new HashMap<>();  
	



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGaid() {
		return gaid;
	}

	public void setGaid(String gaid) {
		this.gaid = gaid;
	}



	public Map<String, Set<String>> getRevenueCheckDates() {
		return revenueCheckDates;
	}

	public void setRevenueCheckDates(Map<String, Set<String>> revenueCheckDates) {
		this.revenueCheckDates = revenueCheckDates;
	}

	public Key<Affs> getAffs() {
		return affs;
	}

	public void setAffs(Key<Affs> affs) {
		this.affs = affs;
	}

}
