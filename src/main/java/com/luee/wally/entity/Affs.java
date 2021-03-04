package com.luee.wally.entity;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;

public class Affs {

	private String userGuid;
	private Date date;
	private String experiment;
	private double offerwallRev;
	private double totalAdRev;
	private double applikeRev;
	private String firebaseInstanceId;
	
	public static Affs valueOf(Entity entity) {
		Affs affs=new Affs();
		affs.setApplikeRev(entity.getProperty("applike_rev") == null ? 0 : (double) entity.getProperty("applike_rev"));
		affs.setDate((Date) entity.getProperty("date"));
		affs.setTotalAdRev(entity.getProperty("total_ad_rev") == null ? 0 : (double) entity.getProperty("total_ad_rev"));
		affs.setExperiment((String) entity.getProperty("experiment"));
		affs.setOfferwallRev(entity.getProperty("offerwall_rev") == null ? 0 : (double) entity.getProperty("offerwall_rev"));
		affs.setUserGuid((String)entity.getProperty("user_guid"));
		affs.setFirebaseInstanceId((String)entity.getProperty("firebase_instance_id"));
		return affs;
	}
	
	public String getFirebaseInstanceId() {
		return firebaseInstanceId;
	}
	public void setFirebaseInstanceId(String firebaseInstanceId) {
		this.firebaseInstanceId = firebaseInstanceId;
	}
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
	public double getApplikeRev() {
		return applikeRev;
	}
	
	public void setApplikeRev(double applikeRev) {
		this.applikeRev = applikeRev;
	}
	
	
	
}
