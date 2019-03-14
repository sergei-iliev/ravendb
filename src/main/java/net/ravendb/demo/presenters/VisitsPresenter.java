package net.ravendb.demo.presenters;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import net.ravendb.client.documents.session.IDocumentQuery;
import net.ravendb.client.documents.session.IDocumentSession;
import net.ravendb.client.documents.session.QueryStatistics;
import net.ravendb.client.primitives.Reference;
import net.ravendb.demo.command.PatientVisit;
import net.ravendb.demo.db.RavenDBDocumentStore;
import net.ravendb.demo.model.Patient;
import net.ravendb.demo.presenters.VisitsViewable.VisitsViewListener;

public class VisitsPresenter implements VisitsViewListener {

	private IDocumentSession session;

	public VisitsPresenter() {

	}


	@Override
	public Pair<Collection<PatientVisit>,Integer> getVisistsList(int offset, int limit, boolean order) {
		Reference<QueryStatistics> statsRef = new Reference<>();
		List<PatientVisit> list;
		IDocumentQuery<PatientVisit> visits = session.query(Patient.class)
				.groupBy("visits[].doctorName", "visits[].date", "firstName", "lastName", "visits[].visitSummery")
				.selectKey("visits[].doctorName", "doctorName").selectKey("visits[].date", "date")
				.selectKey("visits[].visitSummery", "visitSummery").selectKey("firstName", "firstName")
				.selectKey("lastName", "lastName")
				.selectCount()
				.ofType(PatientVisit.class)
				.whereNotEquals("date", null)
				.skip(offset)
				.take(limit);
		if (order) {
			list= visits
					.orderByDescending("date")
					.statistics(statsRef)
					.toList();
		} else {
			list= visits
					.orderBy("date")
					.statistics(statsRef)
					.toList();
		}
        
		int totalResults = statsRef.value.getTotalResults();
		
		return new ImmutablePair<Collection<PatientVisit>, Integer>(list, totalResults);
	}


	@Override
	public Pair<Collection<PatientVisit>,Integer> searchVisitsList(int offset, int limit, String term, boolean order) {
		Reference<QueryStatistics> statsRef = new Reference<>();
		List<PatientVisit> list;
		IDocumentQuery<PatientVisit> visits = session.advanced().documentQuery(Patient.class)
				.groupBy("visits[].doctorName", "visits[].date", "firstName", "lastName", "visits[].visitSummery")
				.selectKey("visits[].doctorName", "doctorName").selectKey("visits[].date", "date")
				.selectKey("visits[].visitSummery", "visitSummery").selectKey("firstName", "firstName")
				.selectKey("lastName", "lastName")
				.selectCount()
				.ofType(PatientVisit.class)
				.whereNotEquals("date", null)
				.whereStartsWith("doctorName", term)
				.skip(offset)
				.take(limit);
		if (order) {
			list= visits
					.orderByDescending("date")
					.statistics(statsRef)
					.toList();
		} else {
			list= visits
					.orderBy("date")
					.statistics(statsRef)
					.toList();
		}

		int totalResults = statsRef.value.getTotalResults();
		
		return new ImmutablePair<Collection<PatientVisit>, Integer>(list, totalResults);
	}

	@Override
	public void openSession() {
		if(session==null){
			  session = RavenDBDocumentStore.getStore().openSession();
		}
	}

	@Override
	public void releaseSession() {
		session.close();
	}
}
