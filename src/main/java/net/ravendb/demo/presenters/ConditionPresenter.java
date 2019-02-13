package net.ravendb.demo.presenters;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import net.ravendb.client.documents.queries.IndexQuery;
import net.ravendb.client.documents.session.IDocumentQuery;
import net.ravendb.client.documents.session.IDocumentSession;
import net.ravendb.demo.command.PatientVisit;
import net.ravendb.demo.db.RavenDBDocumentStore;
import net.ravendb.demo.model.Condition;
import net.ravendb.demo.model.Patient;
import net.ravendb.demo.model.Visit;
import net.ravendb.demo.model.Visit.Type;
import net.ravendb.demo.presenters.ConditionViewable.ConditionViewListener;

public class ConditionPresenter implements ConditionViewListener {
	private final ConditionViewable view;

	public ConditionPresenter(ConditionViewable view) {
		this.view = view;
	}

	@Override
	public Patient getPatientById(String id) {
		try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {
			Patient patient = session.load(Patient.class, id);
			return patient;
		}

	}

	@Override
	public void save(String patientId, Date visitDate, Condition condition) {
		try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {
			Patient patient = session.load(Patient.class, patientId);
			Visit visit = (Patient.getVisit(patient.getVisits(), visitDate));
			Objects.requireNonNull(visit);
			// clear the session
			session.advanced().clear();
			session.advanced().evict(patient);
			if (condition.getId() == null) {
				condition.setPatientId(patientId);
				session.store(condition);

				visit.setConditionId(condition.getId());
				// re save visits
				session.store(patient);
			} else {
				session.store(condition);
			}
			session.saveChanges();

		}
	}

	@Override
	public Condition getConditionById(String id) {	
		return null;
	}

	@Override
	public Collection<Condition> getConditionsList(int offset, int limit, String term) {
		try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {
			IDocumentQuery<Condition> conditions = session.advanced().documentQuery(Condition.class).include("patientId");
				
			if(term!=null){
				conditions.whereStartsWith("description", term);
			}
    		conditions.skip(offset)
    		.take(limit);
			
    		Collection<Condition> list=conditions.toList();
    		//gather patients
    		List<String> ids=list.stream().map(c->c.getPatientId()).collect(Collectors.toList());
    		
    		Map<String,Patient> patients=session.load(Patient.class, ids);
    		
    		for(Condition condition:list){
    			condition.setPatient(patients.get(condition.getPatientId()));
    		}
    		return list; 
		}
	}

	@Override
	public int getConditionsCount(String term) {
		try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {
			IDocumentQuery<Condition> conditions = session.advanced().documentQuery(Condition.class);
				
			if(term!=null){
				conditions.whereStartsWith("description", term);
			}
    		
			
    		return conditions.count();

		}
	}
}
