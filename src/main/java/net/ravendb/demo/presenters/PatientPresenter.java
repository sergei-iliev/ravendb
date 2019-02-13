package net.ravendb.demo.presenters;

import java.io.IOException;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.IOUtils;

import com.vaadin.flow.component.upload.SucceededEvent;

import net.ravendb.client.documents.operations.attachments.AttachmentName;
import net.ravendb.client.documents.operations.attachments.CloseableAttachmentResult;
import net.ravendb.client.documents.session.IDocumentQuery;
import net.ravendb.client.documents.session.IDocumentSession;
import net.ravendb.client.documents.session.QueryStatistics;
import net.ravendb.client.exceptions.ConcurrencyException;
import net.ravendb.client.primitives.CleanCloseable;
import net.ravendb.client.primitives.Reference;
import net.ravendb.demo.command.Attachment;
import net.ravendb.demo.db.RavenDBDocumentStore;
import net.ravendb.demo.model.Address;
import net.ravendb.demo.model.Patient;
import net.ravendb.demo.presenters.PatientViewable.PatientViewListener;

public class PatientPresenter implements PatientViewListener {
	private final PatientViewable view;
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");

	public PatientPresenter(PatientViewable view) {
		this.view = view;
	}
	@Override
	public int getPatientsCount() {
		 try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {			 
			 return session.query(Patient.class).count();			 			 			 
		 }
	}
	/*
	 * Use aggressive cache for fast scralling 
	 */
	@Override
	public Collection<Patient> getPatientsList(int offset,int limit,boolean order) {
		   try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {
			   try (CleanCloseable cacheScope = session.advanced().getDocumentStore().aggressivelyCacheFor(Duration.ofMinutes(5))) {
				   Collection<Patient> list=null;

				   if(order){
					   IDocumentQuery<Patient>  query = session.query(Patient.class);
					   list= query.orderBy("birthDate").skip(offset).take(limit).toList();
				     }else{
				       IDocumentQuery<Patient> query = session.query(Patient.class);
				       list= query.skip(offset).take(limit).toList();	 
				     }
				   
				   
				   for(Patient patient:list){
						 AttachmentName[] names=session.advanced().attachments().getNames(patient);					
						 if(names.length>0){
							try(CloseableAttachmentResult result= session.advanced().attachments().get(patient,names[0].getName())){
							  	Attachment attachment=new Attachment();
							  	attachment.setName(names[0].getName());
							  	attachment.setMimeType(names[0].getContentType());
							  	byte[] bytes = IOUtils.toByteArray(result.getData());
								attachment.setBytes(bytes);
							    patient.setAttachment(attachment);
							}catch(IOException e){
								e.printStackTrace();
							}
							 
						 }
				   }
				   return list;
				}
	       }		
	}
	
	@Override
	public int searchPatientsCount(String term) {
		 try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {			 
			 return session.query(Patient.class).whereStartsWith("firstName", term).count();			 			 			 
		 }
	}
	
	@Override
	public Collection<Patient> searchPatientsList(int offset,int limit,String term,boolean order) {
		 try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {
		        IDocumentQuery<Patient> query = session.query(Patient.class)		                
		                .whereStartsWith("firstName", term);
		           
		           Collection<Patient> list;   
		           if(order){
		        	  list= query.skip(offset).take(limit).orderBy("birthDate").toList();
		           }else{
		        	  list= query.skip(offset).take(limit).toList();   
		           }
				   
				   
				   for(Patient patient:list){
						 AttachmentName[] names=session.advanced().attachments().getNames(patient);					
						 if(names.length>0){
							try(CloseableAttachmentResult result= session.advanced().attachments().get(patient,names[0].getName())){
							  	Attachment attachment=new Attachment();
							  	attachment.setName(names[0].getName());
							  	attachment.setMimeType(names[0].getContentType());
							  	byte[] bytes = IOUtils.toByteArray(result.getData());
								attachment.setBytes(bytes);
							    patient.setAttachment(attachment);
							}catch(IOException e){
								e.printStackTrace();
							}
							 
						 }
				   }
				   return list;
		 }

		
	}

	@Override
	public Collection<String> getRegionsList() {

		return Arrays.asList("Lovech", "Sofia olast","Sofia","Plovdiv", "Varna", "Burgas", "Kustendil", "Veliko Tarnovo");
	}
  
	@Override
	public void create(Patient patient) {
		 try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {
			 
			   session.store(patient);
				 
	           if(patient.getAttachment()!=null){	        	 
			     session.advanced().attachments().store(patient,patient.getAttachment().getName(),patient.getAttachment().getInputStream(),patient.getAttachment().getMimeType());
	           }
	           session.saveChanges();
	           
	     }	  		
	}
	@Override
	public void update(Patient patient)throws ConcurrencyException{

		 try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {
			   
			   //enable oca			   
			   //session.advanced().setUseOptimisticConcurrency(true);			   			   
			   session.store(patient);
			   
	           //delete previous attachments	           
			   AttachmentName[] names=session.advanced().attachments().getNames(patient);
			   if(names.length>0){				
				  session.advanced().attachments().delete(patient,names[0].getName());				
			   }
				 
	           if(patient.getAttachment()!=null){	        	  
			     session.advanced().attachments().store(patient,patient.getAttachment().getName(),patient.getAttachment().getInputStream(),patient.getAttachment().getMimeType());			     
	           }
	           
	           session.saveChanges();
	           
	     }
	}

	@Override
	public void save(String patientId, Address address) {
		   try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {
			   Patient patient = session.load(Patient.class,patientId);
			   patient.setAddress(address);
			   session.store(patient);
			   session.saveChanges();			   
	       }
		
	}

	@Override
	public void delete(Patient patient) {
		 try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {
	           session.delete(patient.getId());
	           session.saveChanges();
	     }
		
	}



}
