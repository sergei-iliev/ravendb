package net.ravendb.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Condition{
	
	private String id;
	private String name;
	private String symptoms;
	private String recommendedTreatment;
	private Patient patient;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSymptoms() {
		return symptoms;
	}
	public void setSymptoms(String symptoms) {
		this.symptoms = symptoms;
	}
	public String getRecommendedTreatment() {
		return recommendedTreatment;
	}
	public void setRecommendedTreatment(String recommendedTreatment) {
		this.recommendedTreatment = recommendedTreatment;
	}
	@JsonIgnore
	public Patient getPatient() {
		return patient;
	}
	@JsonIgnore
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	
	
	
}
