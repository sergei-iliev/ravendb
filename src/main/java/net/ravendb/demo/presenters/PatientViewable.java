package net.ravendb.demo.presenters;

import java.util.Collection;

import org.apache.commons.lang3.tuple.Pair;

import net.ravendb.demo.model.Address;
import net.ravendb.demo.model.Patient;

public interface PatientViewable {

	public interface PatientViewListener{
	  Pair<Collection<Patient>,Integer>	getPatientsList(int offset,int limit,boolean order);
	  Collection<String>	getRegionsList();
	  void create(Patient patient);
	  void update(Patient patient);
	  void save(String patientId,Address address);
	  void delete(Patient patient);
	  
	  Pair<Collection<Patient>,Integer> searchPatientsList(int offset,int limit,String term,boolean order);
	  
	  void openSession();
	  void releaseSession();
	}
}
