package com.luee.wally.entity;

import java.util.Date;

public class Affs {

	private String userGuid;
	private Date date;
	private String experiment;
	private double offerwallRev;
	private double totalAdRev;
	
	
	public String getUserGuid() {
		return userGuid;
	}
	public void setUserGuid(String userGuid) {
		this.userGuid = userGuid;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getExperiment() {
		return experiment;
	}
	public void setExperiment(String experiment) {
		this.experiment = experiment;
	}
	public double getOfferwallRev() {
		return offerwallRev;
	}
	public void setOfferwallRev(double offerwallRev) {
		this.offerwallRev = offerwallRev;
	}
	public double getTotalAdRev() {
		return totalAdRev;
	}
	public void setTotalAdRev(double totalAdRev) {
		this.totalAdRev = totalAdRev;
	}
	
	
	
	
}
