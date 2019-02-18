package net.ravendb.demo.command;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.ravendb.demo.model.Visit.Type;

public class PatientVisit {

	private String id;
	private String firstName,lastName;
	private Date date;
	private String doctorName;
	private String visitSummery;
	private Type type;
	private String conditionId;
	
	
	public String getConditionId() {
		return conditionId;
	}
	public void setConditionId(String conditionId) {
		this.conditionId = conditionId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getVisitSummery() {
		return visitSummery;
	}
	public void setVisitSummery(String visitSummery) {
		this.visitSummery = visitSummery;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getDoctorName() {
		return doctorName;
	}
	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}
	
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	@JsonIgnore
	public LocalDate getLocalDate() {
		 if(date!=null)
		  return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		 else
		  return null;	 
	}
	
}
