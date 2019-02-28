package net.ravendb.demo.views;

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
import net.ravendb.demo.model.Doctor;
import net.ravendb.demo.presenters.DoctorPresenter;
import net.ravendb.demo.presenters.DoctorViewable;
import net.ravendb.demo.presenters.SettingsPresenter;
import net.ravendb.demo.presenters.SettingsViewable;
import net.ravendb.demo.presenters.SettingsViewable.SettingsViewListener;

@Route(value="settings",layout=RavenDBApp.class)
@PageTitle(value = "Hospital Management")
public class SettingsView extends VerticalLayout implements SettingsViewable{

	private final SettingsViewListener presenter;
    private Grid<Doctor> grid;
    private Button edit;
    
	public SettingsView() {
		   presenter=new SettingsPresenter();  
		   init();	
	}
	
	private void init(){
		this.setWidth("100%");
		H4 title = new H4("Settings");	
		add(title);
		
		add(createHeader());
		
		
	}
	private Component createHeader(){
		 HorizontalLayout header=new HorizontalLayout();
		 
		 Button add=new Button("Add",e->{
			 //PatientEditorDialog d=new PatientEditorDialog("Add",new Patient(),this.presenter);
			 //d.open();			 
		 });		 
		 header.add(add);
		 
		  edit=new Button("Edit",e->{			 
			 //PatientEditorDialog d=new PatientEditorDialog("Edit",this.grid.asSingleSelect().getValue(),this.presenter);
			 //d.open();
		 });
		 edit.setEnabled(false);
		 header.add(edit);
		 
//		  delete=new Button("Delete",e->{
//				 ConfirmDialog
//				    .createQuestion()			    
//				    .withCaption("System alert")
//				    .withMessage("Do you want to continue?")
//				    .withOkButton(() -> {
//				        
//				    }, ButtonOption.focus(), ButtonOption.caption("YES"))
//				    .withCancelButton(ButtonOption.caption("NO"))			    
//				    .open();			 
//		 });		 		 
//		 delete.setEnabled(false);
//		 header.add(delete);		
		 
		 
		 return header;
				     	
	}


}
