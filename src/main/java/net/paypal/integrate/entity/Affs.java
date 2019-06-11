package net.paypal.integrate.entity;

import java.math.BigDecimal;
import java.util.Set;

import com.google.cloud.Timestamp;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Affs {
	@Id
	private Long id;

	@Index
	private String gaid;

	@Index
	private double totalRevenue=0;

	@Index
	private Timestamp date;

	public Affs() {
	   date=Timestamp.now();
	}
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
	public double getTotalRevenue() {
		return totalRevenue;
	}
	public void setTotalRevenue(double totalRevenue) {
		this.totalRevenue = totalRevenue;
	}




}
