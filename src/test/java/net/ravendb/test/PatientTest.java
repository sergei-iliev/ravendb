package net.ravendb.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;

import org.junit.Test;

import net.ravendb.client.documents.operations.attachments.AttachmentName;
import net.ravendb.client.documents.operations.attachments.CloseableAttachmentResult;
import net.ravendb.client.documents.session.IDocumentSession;
import net.ravendb.demo.assets.Gender;
import net.ravendb.demo.db.RavenDBDocumentStore;
import net.ravendb.demo.model.Address;
import net.ravendb.demo.model.Patient;
import net.ravendb.demo.presenters.PatientPresenter;

public class PatientTest {
  
	@Test
	public void testPatientCreate() {
	   Patient patient=new Patient();
	   patient.setEmail("ser@ili.com");
	   patient.setFirstName("Serge");
	   patient.setLastName("Geriho");
	   patient.setGender(Gender.MALE);

	   try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {
           session.store(patient);
           session.saveChanges();
       }
	}
	@Test
	public void testPatientAttachmentCreate() throws FileNotFoundException{
		   Patient patient=new Patient();
		   patient.setEmail("ser@ili.com");
		   patient.setFirstName("Veronika Attachment");
		   patient.setLastName("Geriho");
		   patient.setGender(Gender.MALE);
		   patient.setBirthDate(new Date());
		   
		try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {			
			 session.store(patient);
	
			 File initialFile = new File("D:\\photo.jpg");
			 InputStream targetStream = new FileInputStream(initialFile);
			 session.advanced().attachments().store(patient, "photo.jpg",targetStream, "image/jpg");
			 
             session.saveChanges();
		 }
	}
	@Test
	public void testPatientAttachmentRead() throws IOException{

		try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {			
			Patient patient = session.load(Patient.class,"patients/193-A");
	
			 
			 
			 AttachmentName[] names=session.advanced().attachments().getNames(patient);
			 if(names.length>0){
				try(CloseableAttachmentResult result= session.advanced().attachments().get(patient,names[0].getName())){
					InputStream in=result.getData();
				    
				}
				 
			 }
          
		 }
	}
	
	@Test
	public void testAddPatientAddress() {
		Address address=new Address();
		address.setCity("Sofia");
		address.setLine("Baba Tonka 7");
		address.setRegion("Sofia");
		
		   try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {
			   Patient patient = session.load(Patient.class,"patients/97-A");
			   patient.setAddress(address);
			   session.store(patient);
			   session.saveChanges();			   
	       }
	}
//	@Test
//	public void testPatientList() {
//       PatientPresenter presenter=new PatientPresenter();
//       
//       Collection<Patient> p= presenter.getPatientsList(0,5,false);
//       p.forEach(pp->{
//    	   System.out.println(pp.getBirthDate());;
//       });
//	}	
//	@Test
//	public void testPatientSearch() {
//       PatientPresenter presenter=new PatientPresenter();
//       Collection<Patient> p= presenter.searchPatientsList(0,10,"ser",true);
//       p.forEach(pp->{
//    	   System.out.println(pp.getBirthDate());;
//       });
//	}	
	
	@Test
	public void testGeneratePatients() {
		 try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {   
			 for(int i=30;i<1000;i++){
				 Patient p=new Patient();
				 p.setFirstName("SELENIUM "+String.valueOf(i));
				 p.setLastName("L"+String.valueOf(i));
				 p.setGender(Gender.MALE);        	 
				 p.setEmail("selenium@gmail.com");
        	 
        	     session.store(p);
          }
			 session.saveChanges();
		 }
	}	
	
}
