package net.ravendb.demo.command;

import net.ravendb.demo.model.Patient;

public class PatientAttachment {
	private Patient patient;
	private Attachment attachment;
	
	public PatientAttachment() {
		this.patient=new Patient(); 
	}
	public PatientAttachment(Patient patient) {
		this.patient=patient;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Attachment getAttachment() {
		return attachment;
	}

	public void setAttachment(Attachment attachment) {
		this.attachment = attachment;
	}
	
	
}
