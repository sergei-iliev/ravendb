package net.ravendb.demo.presenters;

import java.util.Collection;

import org.apache.commons.lang3.tuple.Pair;

import net.ravendb.demo.command.PatientAttachment;
import net.ravendb.demo.model.Address;
import net.ravendb.demo.model.Patient;

public interface PatientViewable {

	public interface PatientViewListener{
	  Pair<Collection<PatientAttachment>,Integer>	getPatientsList(int offset,int limit,boolean order);
	  Collection<String>	getRegionsList();
	  void create(PatientAttachment patient);
	  void update(PatientAttachment patient);
	  void save(String patientId,Address address);
	  void delete(PatientAttachment patient);
	  
	  Pair<Collection<PatientAttachment>,Integer> searchPatientsList(int offset,int limit,String term,boolean order);
	  
	  void openSession();
	  void releaseSession();
	}
}
