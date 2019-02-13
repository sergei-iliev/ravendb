package net.ravendb.test;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import net.ravendb.client.documents.session.IDocumentSession;
import net.ravendb.demo.command.DoctorVisit;
import net.ravendb.demo.command.PatientVisit;
import net.ravendb.demo.db.RavenDBDocumentStore;
import net.ravendb.demo.model.Doctor;
import net.ravendb.demo.model.Patient;
import net.ravendb.demo.model.Visit;
import net.ravendb.demo.model.Visit.Type;
import net.ravendb.demo.presenters.PatientPresenter;
import net.ravendb.demo.presenters.PatientVisitPresenter;
import net.ravendb.demo.presenters.VisitsPresenter;

public class VisitsTest {

	@Test
	public void testVisitCreate() {
	   PatientVisitPresenter presenter=new PatientVisitPresenter(null);
	   Patient patient=presenter.getPatientById("patients/3500-A");
	   
	   
		
	  
           
		   
		   Visit v=new Visit();
	       v.setDate(new Date());
	       v.setType(Type.EMERGENCYROOM);
	       v.setConditionSummery("minor headache");
		   presenter.save(patient.getId(),v);
		   
       
	}

	@Test
	public void testGetDoctorsListByVisistsCount() {
		   try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {
			   List<DoctorVisit> results =session.advanced().documentQuery(Patient.class)
					   .groupBy("visits[].doctorId")
					   .selectKey("visits[].doctorId", "doctorId")							   
                       .selectCount()
                       .whereNotEquals("doctorId",null)
                       .orderByDescending("count")
                       .ofType(DoctorVisit.class)
                       .toList();
			 Set<String> doctorIds=results.stream().map(p->p.getDoctorId()).collect(Collectors.toSet());
			 Map<String,Doctor> map= session.load(Doctor.class,doctorIds);
			 //fetch doctors  
			 results.forEach(v->{ 
			  v.setDoctorName(map.get(v.getDoctorId()).getName());	 
	           System.out.println(v.getCount()+"::"+v.getDoctorName());;
			 });
		   }
		   
	}
	
	@Test
	public void testGetPatientsVisitList() {
		   VisitsPresenter presenter=new VisitsPresenter(null);
		   presenter.getVisistsList(0,1000,true);
		   
	}
	
	@Test
	public void testDoctorCreate() {
	   Doctor d=new Doctor();
	   d.setAge(34);
	   d.setDepartment("RD");
	   d.setName("Sergei Molotov");
       
	   try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {
           session.store(d);
           session.saveChanges();
       }
	}
	
	@Test
	public void testDoctorsList() {
       
         PatientVisitPresenter p=new PatientVisitPresenter(null);
         p.getDoctorsList();
	}
	
	@Test
	public void testVisistsList() {
       
         PatientVisitPresenter p=new PatientVisitPresenter(null);
         Collection<PatientVisit> visits=p.getVisistsList("patients/321-A","s",false);
         visits.forEach(v->{
        	 System.out.println(v.getFirstName()+"::"+v.getDoctorName()+"::"+ v.getType());;
         });
         
	}
	
}
