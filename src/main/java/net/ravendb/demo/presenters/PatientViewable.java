package net.ravendb.demo.presenters;

import java.util.Collection;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.upload.SucceededEvent;

import net.ravendb.demo.model.Address;
import net.ravendb.demo.model.Patient;

public interface PatientViewable {

	public interface PatientViewListener{
	  int  getPatientsCount();
	  Collection<Patient>	getPatientsList(int offset,int limit,boolean order);
	  Collection<String>	getRegionsList();
	  void create(Patient patient);
	  void update(Patient patient);
	  void save(String patientId,Address address);
	  void delete(Patient patient);
	  
	  Collection<Patient> searchPatientsList(int offset,int limit,String term,boolean order);
	  int searchPatientsCount(String term);
	}
}
