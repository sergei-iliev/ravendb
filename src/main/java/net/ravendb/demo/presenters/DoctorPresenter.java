package net.ravendb.demo.presenters;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.ravendb.client.documents.session.IDocumentSession;
import net.ravendb.demo.command.DoctorVisit;
import net.ravendb.demo.db.RavenDBDocumentStore;
import net.ravendb.demo.model.Doctor;
import net.ravendb.demo.model.Patient;
import net.ravendb.demo.presenters.DoctorViewable.DoctorViewListener;

public class DoctorPresenter implements DoctorViewListener {

	private IDocumentSession session;

	public DoctorPresenter() {

	}

	@Override
	public Collection<Doctor> getDoctorsList() {
		return session.query(Doctor.class).toList();
	}

	@Override
	public Collection<String> getDepartments() {
		return Arrays.asList("LV", "SA", "PO", "BG", "ASU", "VO", "RD");

	}

	@Override
	public void save(Doctor doctor) {
		session.store(doctor);
		session.saveChanges();

	}

	@Override
	public void delete(Doctor doctor) {

		session.delete(doctor.getId());
		session.saveChanges();

	}

	@Override
	public Collection<DoctorVisit> getDoctorVisitsList() {
		List<DoctorVisit> results = session.query(Patient.class)
				.groupBy("visits[].doctorId")
				.selectKey("visits[].doctorId", "doctorId")
				.selectCount()
				.whereNotEquals("doctorId", null)
				.orderByDescending("count")
				.ofType(DoctorVisit.class)
				.include("visits[].doctorId")
				.toList();
		// fetch doctors by batch
		Set<String> doctorIds = results.stream().map(p -> p.getDoctorId()).collect(Collectors.toSet());
		Map<String, Doctor> map = session.load(Doctor.class, doctorIds);

		results.forEach(v -> {
			v.setDoctorName(map.get(v.getDoctorId()).getName());
		});
		
		assert (session.advanced().getNumberOfRequests() == 1);
		return results;

	}

	@Override
	public void openSession() {
		if(session==null){
			  session = RavenDBDocumentStore.INSTANCE.getStore().openSession();
		}
	}

	@Override
	public void releaseSession() {
		session.close();
	}
}
