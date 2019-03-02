package net.ravendb.demo.presenters;

import java.util.Collection;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.upload.SucceededEvent;

import net.ravendb.demo.command.DoctorVisit;
import net.ravendb.demo.model.Doctor;
import net.ravendb.demo.model.Patient;

public interface DoctorViewable {

	public interface DoctorViewListener{
	  Collection<Doctor>	getDoctorsList();
	  Collection<String>    getDepartments();
	  void save(Doctor doctor);
	  void delete(Doctor doctor);
	  
	  Collection<DoctorVisit>  getDoctorVisitsList();
	  
	  void openSession();
	  void releaseSession();
	}
}
