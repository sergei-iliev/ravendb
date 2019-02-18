package net.ravendb.demo.views;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Map;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.KeyDownEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import net.ravendb.demo.RavenDBApp;
import net.ravendb.demo.command.PatientVisit;
import net.ravendb.demo.components.editor.AddressEditorDialog;
import net.ravendb.demo.components.editor.ConditionEditorDialog;
import net.ravendb.demo.components.editor.PatientEditorDialog;
import net.ravendb.demo.components.editor.PatientVisitEditorDialog;
import net.ravendb.demo.model.Condition;
import net.ravendb.demo.model.Patient;
import net.ravendb.demo.model.Patient.Gender;
import net.ravendb.demo.model.Visit;
import net.ravendb.demo.presenters.ConditionPresenter;
import net.ravendb.demo.presenters.PatientPresenter;
import net.ravendb.demo.presenters.PatientVisitPresenter;
import net.ravendb.demo.presenters.PatientVisitViewable;
import net.ravendb.demo.presenters.PatientVisitViewable.PatientVisitViewListener;

@Route(value="patientvisit",layout=RavenDBApp.class)
@PageTitle(value = "Hospital Management")
public class PatientVisitView extends VerticalLayout implements  PatientVisitViewable,HasUrlParameter<String>{
	

	private H5 name;
	private PatientVisitViewListener presenter;
	private Patient patient;
	private Grid<PatientVisit> grid;
	private String patientId;
	private Checkbox order;
	
	public PatientVisitView() {
	   presenter=new PatientVisitPresenter(this);  
	   init();	
	}
	@Override
	public void setParameter(BeforeEvent event, String id) {
		
		try {
			patientId=URLDecoder.decode(id,"UTF-8");
			load(patientId);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}
	
	private void init(){
		this.setWidth("100%");
		H4 title = new H4("Patient visit");	
		add(title);
	    
		name=new H5();
		name.setClassName("name");
		add(name);
		
		add(createHeader());
		add(createSearchBox());
		add(createGrid());
		
	}
	private Component createSearchBox() {
		HorizontalLayout layout = new HorizontalLayout();
		Span span = new Span();

		TextField search = new TextField();
		search.setPlaceholder("Search");
		search.addKeyDownListener(com.vaadin.flow.component.Key.ENTER,
				(ComponentEventListener<KeyDownEvent>) e -> {
					if(!e.isFromClient()){
						return; 
					}
					if (search.getValue().length() > 1) {
						load(search.getValue(),order.getValue());						
					} else {
						load(null,order.getValue());
					}
				});

		order = new Checkbox("Order by visit date");
		order.addValueChangeListener(e -> {
			if(!e.isFromClient()){
				return; 
			}
			if (search.getValue().length()>1) {
				load(search.getValue(),order.getValue());	
			} else {
				load(null,order.getValue());
			}
		});

		span.add(new Icon(VaadinIcon.SEARCH), search, order);

		layout.add(span);
		return layout;
	}	
	private Component createHeader(){
		 HorizontalLayout header=new HorizontalLayout();
		 
		 Button add=new Button("Add",e->{
			 
			 PatientVisitEditorDialog d=new PatientVisitEditorDialog("Add",patientId, new Visit(),this.presenter,()->{	
				 load(null,false);
			 });
			 d.open();			 
		 });		 
		 header.add(add);
		 
		 return header;
				     	
	}
	private Component createGrid(){
		   grid=new Grid<>();
		   grid.setSelectionMode(SelectionMode.SINGLE);
		   grid.setWidth("50%");

		   grid.addColumn(v->v.getDoctorName()).setHeader("Doctor");
		   grid.addColumn(new LocalDateRenderer<>(PatientVisit::getLocalDate,
			        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))).setHeader("Visit Date");
		   		   

		   grid.addColumn(v->v.getType()).setHeader("Type");
		   grid.addColumn(v->v.getVisitSummery()).setHeader("Visit Summery");
			
		   return grid;
		}	
	private void load(String patientId){
		patient=presenter.getPatientById(patientId);
		name.setText(patient.getFirstName()+" "+patient.getLastName());
		load(null,false);
	}

	private void load(String term,boolean order) {
		grid.setItems(presenter.getVisistsList(patientId,term,order));
	}


}