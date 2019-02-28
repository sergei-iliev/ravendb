package net.ravendb.demo.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

public class Condition{
	public enum Type{
		SEVIER,MINOR,CHRONIC,NORMAL;
    	@JsonCreator
        public static Type convert(String status){
            if(status==null){
                return Type.NORMAL;
            }
            
            return Type.valueOf(status);
        }
        
        @JsonValue
        public String getType() {        
            return this.toString();
        }  
	}
	
	private String id;
	private Type severity;
	private String prescription;
	private String description;
	private Patient patient;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Type getSeverity() {
		return severity;
	}
	public void setSeverity(Type severity) {
		this.severity = severity;
	}
	public String getPrescription() {
		return prescription;
	}
	public void setPrescription(String prescription) {
		this.prescription = prescription;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
