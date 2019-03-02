package net.ravendb.demo.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.ravendb.demo.assets.Location;
import net.ravendb.demo.command.ComboValue;

public class Visit{	
	private Date date;
	private String doctorId;	
	private Location type;
	private String visitSummery;
	private String conditionId;
	private Doctor doctor;
	private String doctorName;
	private Condition condition;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}
	@JsonIgnore
	public ComboValue getDoctorValue() {
	  return new ComboValue(doctorId,null);
	}
	
	@JsonIgnore
	public void setDoctorValue(ComboValue value) {
		this.doctorId = value.getId();
		this.doctorName=value.getName();
	}	

	public String getVisitSummery() {
		return visitSummery;
	}
	public void setVisitSummery(String visitSummery) {
		this.visitSummery = visitSummery;
	}
	public String getConditionId() {
		return conditionId;
	}
	public void setConditionId(String conditionId) {
		this.conditionId = conditionId;
	}
	public Location getType() {
		return type;
	}
	public void setType(Location type) {
		this.type = type;
	}
	
	
	public String getDoctorName() {
		return doctorName;
	}
	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}
	@JsonIgnore
	public ComboValue getConditionValue() {
	  return new ComboValue(conditionId,null);
	}
	
	@JsonIgnore
	public void setConditionValue(ComboValue value) {
		this.conditionId = value.getId();		
	}	
	@JsonIgnore
	public Condition getCondition() {
		return condition;
	}
	@JsonIgnore
	public void setCondition(Condition condition) {
		this.condition = condition;
	}
	@JsonIgnore
	public void setLocalDate(LocalDate localDate) {
		if(localDate==null){
			return;
		}
		date=Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
	@JsonIgnore
	public LocalDate getLocalDate() {
		 if(date!=null)
		  return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		 else
		  return null;	 
	}
	@JsonIgnore
	public Doctor getDoctor() {
		return doctor;
	}
	@JsonIgnore
	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}
	
}
