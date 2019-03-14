package net.ravendb.demo.views;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collection;

import org.apache.commons.lang3.tuple.Pair;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.KeyDownEvent;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import net.ravendb.demo.RavenDBApp;
import net.ravendb.demo.command.PatientVisit;
import net.ravendb.demo.components.grid.PageableGrid;
import net.ravendb.demo.model.Patient;
import net.ravendb.demo.presenters.VisitsPresenter;
import net.ravendb.demo.presenters.VisitsViewable;

@Route(value="visits",layout=RavenDBApp.class)
@PageTitle(value = "Hospital Management")
public class VisitsView extends VerticalLayout implements  VisitsViewable{
	

	private H5 name;
	private VisitsViewListener presenter;
	private PageableGrid<PatientVisit> grid;
	private Checkbox order;
	private TextField search;
	
	public VisitsView() {
	   presenter=new VisitsPresenter();  
	   init();	
	}
	@Override
	protected void onAttach(AttachEvent attachEvent) {
		presenter.openSession();
		load();
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		presenter.releaseSession();
		super.onDetach(detachEvent);
	}

	private void init(){
		this.setWidth("100%");
		H4 title = new H4("Visits");	
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

	    search = new TextField();
		search.setPlaceholder("Search");
		search.addKeyDownListener(com.vaadin.flow.component.Key.ENTER,
				(ComponentEventListener<KeyDownEvent>) keyDownEvent -> {					
						load();				
				});

		order = new Checkbox("Order by visit date");
		order.addValueChangeListener(e -> {
				load();
		});

		span.add(new Icon(VaadinIcon.SEARCH), search, order);

		layout.add(span);
		return layout;
	}	
	private Component createHeader(){
		 HorizontalLayout header=new HorizontalLayout();
		 

		 
		 return header;
				     	
	}
	private Component createGrid(){
		   grid=new PageableGrid<>(this::loadPage);
		   grid.getGrid().setSelectionMode(SelectionMode.SINGLE);
		   grid.setWidth("100%");

		   grid.getGrid().addColumn(v->v.getDoctorName()).setHeader("Doctor");
		   grid.getGrid().addColumn(new LocalDateRenderer<>(PatientVisit::getLocalDate,
			        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))).setHeader("Visit Date");
		   
		   grid.getGrid().addColumn(v->v.getFirstName()).setHeader("First Name");
		   grid.getGrid().addColumn(v->v.getLastName()).setHeader("Last Name");
		   grid.getGrid().addColumn(v->v.getVisitSummery()).setHeader("Visit Summery");	
		   return grid;
	}
	private void load() {
		grid.loadFirstPage();
	}
	
	private Pair<Collection<PatientVisit>, Integer> loadPage(int page, int pageSize) {
		if (search.getValue().length() > 1) {
			return presenter.searchVisitsList(page * pageSize, pageSize, search.getValue(), order.getValue());
		} else {
			return presenter.getVisistsList(page * pageSize, pageSize, order.getValue());
		}
	}


}