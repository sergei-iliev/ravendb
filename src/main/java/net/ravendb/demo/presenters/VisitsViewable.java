package net.ravendb.demo.presenters;

import java.util.Collection;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.upload.SucceededEvent;

import net.ravendb.demo.command.PatientVisit;
import net.ravendb.demo.model.Doctor;
import net.ravendb.demo.model.Patient;

public interface VisitsViewable {

	public interface VisitsViewListener{
		  int getVisistsCount();
		  Collection<PatientVisit>	getVisistsList(int offset,int limit,boolean order);
		  
		  Collection<PatientVisit> searchVisitsList(int offset,int limit,String term,boolean order);
		  int searchVisitsCount(String term);
		  
		  void openSession();
		  void releaseSession();
	}
}
