package net.ravendb.demo.presenters;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import net.ravendb.client.documents.session.IDocumentQuery;
import net.ravendb.client.documents.session.IDocumentSession;
import net.ravendb.client.documents.session.QueryStatistics;
import net.ravendb.client.primitives.Reference;
import net.ravendb.demo.db.RavenDBDocumentStore;
import net.ravendb.demo.model.Condition;
import net.ravendb.demo.model.Patient;
import net.ravendb.demo.presenters.ConditionViewable.ConditionViewListener;

public class ConditionPresenter implements ConditionViewListener {
	private IDocumentSession session;

	public ConditionPresenter() {

	}

	@Override
	public void delete(Condition condition) {
		session.delete(condition.getId());
		session.saveChanges();

	}

	@Override
	public Patient getPatientById(String id) {

		Patient patient = session.load(Patient.class, id);
		return patient;

	}

	@Override
	public void save(Condition condition) {

		// clear the session
		session.advanced().clear();
		session.store(condition);
		session.saveChanges();

	}

	@Override
	public Condition getConditionById(String id) {
		return null;
	}

	@Override
	public Pair<Collection<Condition>, Integer> getConditionsList(int offset, int limit, String term) {
		IDocumentQuery<Condition> conditions = session.advanced().documentQuery(Condition.class).include("patientId");
		Reference<QueryStatistics> statsRef = new Reference<>();
		if (term != null) {
			conditions.whereStartsWith("description", term);
		}
		List<Condition> list = conditions.skip(offset).take(limit).statistics(statsRef).toList();
		int totalResults = statsRef.value.getTotalResults();

		return new ImmutablePair<Collection<Condition>, Integer>(list, totalResults);

	}

	// @Override
	// public int getConditionsCount(String term) {
	// IDocumentQuery<Condition> conditions =
	// session.advanced().documentQuery(Condition.class);
	//
	// if (term != null) {
	// conditions.whereStartsWith("description", term);
	// }
	//
	// return conditions.count();
	//
	// }

	@Override
	public void openSession() {
		session = RavenDBDocumentStore.INSTANCE.getStore().openSession();
	}

	@Override
	public void releaseSession() {
		session.close();
	}
}
