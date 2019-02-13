package net.ravendb.demo.model;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

import net.ravendb.demo.command.ComboValue;
import net.ravendb.demo.model.Patient.Gender;

public class Visit implements Serializable{
    public enum Type{
    	HOUSE,
    	EMERGENCYROOM,
    	HOSPITAL;
        
    	@JsonCreator
        public static Type convert(String type){
            if(type==null){
                return Type.HOUSE;
            }
            
            return Type.valueOf(type);
        }
        
        @JsonValue
        public String getType() {        
            return this.toString();
        }    	
    }
	
    	
	private Date date;
	private String doctorId;	
	private Type type;
	private String conditionSummery;
	private String conditionId;
	private Doctor doctor;
	private String doctorName;
	
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
	
	public String getConditionSummery() {
		return conditionSummery;
	}
	public void setConditionSummery(String conditionSummery) {
		this.conditionSummery = conditionSummery;
	}
	public String getConditionId() {
		return conditionId;
	}
	public void setConditionId(String conditionId) {
		this.conditionId = conditionId;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	
	
	public String getDoctorName() {
		return doctorName;
	}
	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
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
