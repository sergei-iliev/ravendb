package net.ravendb.demo.views;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import net.ravendb.demo.RavenDBApp;
import net.ravendb.demo.command.DoctorVisit;
import net.ravendb.demo.components.editor.DoctorEditorDialog;
import net.ravendb.demo.model.Doctor;
import net.ravendb.demo.presenters.DoctorPresenter;
import net.ravendb.demo.presenters.DoctorViewable;

@Route(value="doctor",layout=RavenDBApp.class)
@PageTitle(value = "Hospital Management")
public class DoctorView extends VerticalLayout implements DoctorViewable{

	private final DoctorViewListener presenter;
    private Grid<Doctor> grid;
    private Grid<DoctorVisit> doctorVisitGrid;
    private Button edit,delete;
    
	public DoctorView() {
		   presenter=new DoctorPresenter();  
		   init();	
	}
	
	@Override
	protected void onAttach(AttachEvent attachEvent) {	
		load();
	}
	private void init(){
		this.setWidth("100%");
		H4 title = new H4("Doctors");	
		add(title);
		
		add(createHeader());
		add(createDoctorGrid());
		
		title = new H4("Most visited doctors");	
		add(title);
		
		add(createDoctorVisitsGrid());
	}
	private Component createHeader(){
		 HorizontalLayout header=new HorizontalLayout();
		 
		 Button add=new Button("Add",e->{
			 DoctorEditorDialog d=new DoctorEditorDialog("Add",new Doctor(),this.presenter,() -> {load();});
			 d.open();			 
		 });		 
		 header.add(add);
		 
		  edit=new Button("Edit",e->{
				 DoctorEditorDialog d=new DoctorEditorDialog("Add",this.grid.asSingleSelect().getValue(),this.presenter,() -> {load();});
				 d.open();				 
		 });
		 edit.setEnabled(false);
		 header.add(edit);
		 
		  delete=new Button("Delete",e->{
				 ConfirmDialog
				    .createQuestion()			    
				    .withCaption("System alert")
				    .withMessage("Do you want to continue?")
				    .withOkButton(() -> {
				    	presenter.delete(grid.asSingleSelect().getValue());
				    	load();
				    }, ButtonOption.focus(), ButtonOption.caption("YES"))
				    .withCancelButton(ButtonOption.caption("NO"))			    
				    .open();			 
		 });		 		 
		 delete.setEnabled(false);
		 header.add(delete);		
		 
		 
		 return header;
				     	
	}

	private Component createDoctorGrid(){
	   grid=new Grid<>();
	   grid.setSelectionMode(SelectionMode.SINGLE);
	   grid.setWidth("50%");


	   grid.addColumn(Doctor::getName).setHeader("Name");
	   grid.addColumn(Doctor::getDepartment).setHeader("Department");
	   grid.addColumn(Doctor::getAge).setHeader("Age");
	   
	   
	   grid.addSelectionListener(e->{
		   if(grid.getSelectedItems().size()>0){
			   edit.setEnabled(true);
			   delete.setEnabled(true);
		   }else{
			   delete.setEnabled(false);
			   edit.setEnabled(false);			   
		   }
	   });
	   return grid;
	}
	
	private Component createDoctorVisitsGrid(){
		doctorVisitGrid=new Grid<>();
		doctorVisitGrid.setSelectionMode(SelectionMode.NONE);
		doctorVisitGrid.setWidth("50%");


		doctorVisitGrid.addColumn(DoctorVisit::getDoctorName).setHeader("Name");
		doctorVisitGrid.addColumn(DoctorVisit::getCount).setHeader("Number of Visits");

		   		   
		return doctorVisitGrid;
	}
	
	private void load(){
	  grid.setItems(presenter.getDoctorsList());	
	  doctorVisitGrid.setItems(presenter.getDoctorVisitsList());
	}
}
