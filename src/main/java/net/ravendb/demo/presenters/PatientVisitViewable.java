package net.ravendb.demo.presenters;

import java.util.Collection;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.upload.SucceededEvent;

import net.ravendb.demo.command.PatientVisit;
import net.ravendb.demo.model.Address;
import net.ravendb.demo.model.Condition;
import net.ravendb.demo.model.Doctor;
import net.ravendb.demo.model.Patient;
import net.ravendb.demo.model.Visit;

public interface PatientVisitViewable {

	public interface PatientVisitViewListener{
	  
	  Collection<PatientVisit>	getVisistsList(String patientId,String term,boolean order);	  
	  
	  Condition getConditionById(String conditionId);
	  
	  void save(String patientId,Visit visit);
	  Patient getPatientById(String id);
	  Collection<Doctor> getDoctorsList();
	}
}
