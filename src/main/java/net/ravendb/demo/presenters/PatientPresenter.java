package net.ravendb.demo.presenters;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import net.ravendb.client.documents.operations.attachments.AttachmentName;
import net.ravendb.client.documents.operations.attachments.CloseableAttachmentResult;
import net.ravendb.client.documents.session.IDocumentQuery;
import net.ravendb.client.documents.session.IDocumentSession;
import net.ravendb.client.documents.session.QueryStatistics;
import net.ravendb.client.exceptions.ConcurrencyException;
import net.ravendb.client.primitives.Reference;
import net.ravendb.demo.command.Attachment;
import net.ravendb.demo.db.RavenDBDocumentStore;
import net.ravendb.demo.model.Address;
import net.ravendb.demo.model.Patient;
import net.ravendb.demo.presenters.PatientViewable.PatientViewListener;

public class PatientPresenter implements PatientViewListener {
	
	private IDocumentSession session;

	public PatientPresenter() {

	}

	@Override
	public Pair<Collection<Patient>,Integer> getPatientsList(int offset, int limit, boolean order) {
		Collection<Patient> list = null;
		
		Reference<QueryStatistics> statsRef = new Reference<>();
		
		if (order) {
			IDocumentQuery<Patient> query = session.query(Patient.class);
			list = query.orderBy("birthDate").skip(offset).take(limit).statistics(statsRef).toList();
		} else {
			IDocumentQuery<Patient> query = session.query(Patient.class);
			list = query.skip(offset).take(limit).statistics(statsRef).toList();
		}
		
		int totalResults = statsRef.value.getTotalResults();

		for (Patient patient : list) {
			AttachmentName[] names = session.advanced().attachments().getNames(patient);
			if (names.length > 0) {
				try (CloseableAttachmentResult result = session.advanced().attachments().get(patient,
						names[0].getName())) {					
					Attachment attachment = new Attachment();
					attachment.setName(names[0].getName());
					attachment.setMimeType(names[0].getContentType());
					byte[] bytes = IOUtils.toByteArray(result.getData());
					attachment.setBytes(bytes);
					patient.setAttachment(attachment);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
		return new ImmutablePair<Collection<Patient>, Integer>(list, totalResults);
	}

	@Override
	public Pair<Collection<Patient>,Integer>  searchPatientsList(int offset, int limit, String term, boolean order) {
		IDocumentQuery<Patient> query = session.query(Patient.class).whereStartsWith("firstName", term);

		Collection<Patient> list;
		Reference<QueryStatistics> statsRef = new Reference<>();
		if (order) {
			list = query.skip(offset).take(limit).orderBy("birthDate").statistics(statsRef).toList();
		} else {
			list = query.skip(offset).take(limit).statistics(statsRef).toList();
		}
		
		int totalResults = statsRef.value.getTotalResults();
		
		for (Patient patient : list) {
			AttachmentName[] names = session.advanced().attachments().getNames(patient);
			if (names.length > 0) {
				try (CloseableAttachmentResult result = session.advanced().attachments().get(patient,
						names[0].getName())) {
					Attachment attachment = new Attachment();
					attachment.setName(names[0].getName());
					attachment.setMimeType(names[0].getContentType());
					byte[] bytes = IOUtils.toByteArray(result.getData());
					attachment.setBytes(bytes);
					patient.setAttachment(attachment);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
		return new ImmutablePair<Collection<Patient>, Integer>(list, totalResults);

	}

	@Override
	public Collection<String> getRegionsList() {

		return Arrays.asList("Lovech", "Sofia olast", "Sofia", "Plovdiv", "Varna", "Burgas", "Kustendil",
				"Veliko Tarnovo");
	}

	@Override
	public void create(Patient patient) {
		session.store(patient);

		if (patient.getAttachment() != null) {
			session.advanced().attachments().store(patient, patient.getAttachment().getName(),
					patient.getAttachment().getInputStream(), patient.getAttachment().getMimeType());
		}
		
		session.saveChanges();
		session.advanced().clear();
	}

	@Override
	public void update(Patient patient) throws ConcurrencyException {
		// enable oca
		// session.advanced().setUseOptimisticConcurrency(true);
		session.store(patient);

		// delete previous attachments
		AttachmentName[] names = session.advanced().attachments().getNames(patient);
		if (names.length > 0) {
			session.advanced().attachments().delete(patient, names[0].getName());
		}
		session.saveChanges();

		if (patient.getAttachment() != null) {
			session.advanced().attachments().store(patient, patient.getAttachment().getName(),
					patient.getAttachment().getInputStream(), patient.getAttachment().getMimeType());
		}

		session.saveChanges();
		session.advanced().clear();
	}

	@Override
	public void save(String patientId, Address address) {
		Patient patient = session.load(Patient.class, patientId);
		patient.setAddress(address);
		session.store(patient);
		session.saveChanges();

	}

	@Override
	public void delete(Patient patient) {
		session.delete(patient.getId());
		session.saveChanges();
	}

	@Override
	public void openSession() {
		session = RavenDBDocumentStore.INSTANCE.getStore().openSession();
	}

	@Override
	public void releaseSession() {
		session.close();
	}

}
