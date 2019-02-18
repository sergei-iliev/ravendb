package net.ravendb.demo.presenters;

import java.util.Collection;

import net.ravendb.client.documents.session.IDocumentQuery;
import net.ravendb.client.documents.session.IDocumentSession;
import net.ravendb.demo.command.PatientVisit;
import net.ravendb.demo.db.RavenDBDocumentStore;
import net.ravendb.demo.model.Patient;
import net.ravendb.demo.presenters.VisitsViewable.VisitsViewListener;

public class VisitsPresenter implements VisitsViewListener {
	private final VisitsViewable view;

	public VisitsPresenter(VisitsViewable view) {
		this.view = view;
	}

	@Override
	public int getVisistsCount() {
		try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {
		   return   session.advanced().documentQuery(Patient.class).
		    		groupBy("visits[].doctorName","visits[].date","firstName","lastName")
		    		.selectKey("visits[].doctorName", "doctorName")
		    		.selectKey("visits[].date", "date")
		    		.selectKey("firstName", "firstName")
		    		.selectKey("lastName", "lastName")
		    		.selectCount()		
		    		.whereNotEquals("date",null)
		    		.count();
		}
	}
	@Override
	public Collection<PatientVisit> getVisistsList(int offset,int limit, boolean order) {
		try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {
			
			IDocumentQuery<PatientVisit> visits = session.advanced().documentQuery(Patient.class).
		    		groupBy("visits[].doctorName","visits[].date","firstName","lastName","visits[].visitSummery")
		    		.selectKey("visits[].doctorName", "doctorName")
		    		.selectKey("visits[].date", "date")
		    		.selectKey("visits[].visitSummery","visitSummery")
		    		.selectKey("firstName", "firstName")
		    		.selectKey("lastName", "lastName")
		    		.selectCount()
		    		.ofType(PatientVisit.class)
		    		.whereNotEquals("date",null)
		    		.skip(offset)
		    		.take(limit);
		    if(order){
	    		return visits.orderByDescending("date").toList();
		    }else{
		    	return visits.orderBy("date").toList();
		    }
			

		}
	}
	
	@Override
	public int searchVisitsCount(String term) {
		try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {
			   return   session.advanced().documentQuery(Patient.class).
			    		groupBy("visits[].doctorName","visits[].date","firstName","lastName")
			    		.selectKey("visits[].doctorName", "doctorName")
			    		.selectKey("visits[].date", "date")
			    		.selectKey("firstName", "firstName")
			    		.selectKey("lastName", "lastName")
			    		.selectCount()		
			    		.whereNotEquals("date",null)
			    		.whereStartsWith("doctorName",term)
			    		.count();
			}
	}
	@Override
	public Collection<PatientVisit> searchVisitsList(int offset, int limit, String term, boolean order) {
		try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {
			
			IDocumentQuery<PatientVisit> visits = session.advanced().documentQuery(Patient.class).
		    		groupBy("visits[].doctorName","visits[].date","firstName","lastName","visits[].visitSummery")
		    		.selectKey("visits[].doctorName", "doctorName")
		    		.selectKey("visits[].date", "date")
		    		.selectKey("visits[].visitSummery","visitSummery")
		    		.selectKey("firstName", "firstName")
		    		.selectKey("lastName", "lastName")
		    		.selectCount()
		    		.ofType(PatientVisit.class)
		    		.whereNotEquals("date",null)
		    		.whereStartsWith("doctorName",term)		    		
		    		.skip(offset)
		    		.take(limit);
		    if(order){
	    		return visits.orderByDescending("date").toList();
		    }else{
		    	return visits.orderBy("date").toList();
		    }
			

		}
	}
}
