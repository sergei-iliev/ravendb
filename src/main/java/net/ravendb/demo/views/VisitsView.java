package net.ravendb.demo.views;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
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
import net.ravendb.demo.presenters.VisitsPresenter;
import net.ravendb.demo.presenters.VisitsViewable;

@Route(value="visits",layout=RavenDBApp.class)
@PageTitle(value = "Hospital Management")
public class VisitsView extends VerticalLayout implements  VisitsViewable{
	

	private H5 name;
	private VisitsViewListener presenter;
	private Grid<PatientVisit> grid;
	private Checkbox order;
	
	public VisitsView() {
	   presenter=new VisitsPresenter(this);  
	   init();	
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		load();
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

		TextField search = new TextField();
		search.setPlaceholder("Search");
		search.addKeyDownListener(com.vaadin.flow.component.Key.ENTER,
				(ComponentEventListener<KeyDownEvent>) keyDownEvent -> {
					if (search.getValue().length() > 1) {
						search(search.getValue());						
					} else {
						load();
					}
				});

		order = new Checkbox("Order by visit date");
		order.addValueChangeListener(e -> {
			
			if (search.getValue().length() == 0) {
				load();
			} else {
				search(search.getValue());
			}
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
		   grid=new Grid<>();
		   grid.setSelectionMode(SelectionMode.SINGLE);
		   grid.setWidth("100%");

		   grid.addColumn(v->v.getDoctorName()).setHeader("Doctor");
		   grid.addColumn(new LocalDateRenderer<>(PatientVisit::getLocalDate,
			        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))).setHeader("Visit Date");
		   
		   grid.addColumn(v->v.getFirstName()).setHeader("First Name");
		   grid.addColumn(v->v.getLastName()).setHeader("Last Name");
		   grid.addColumn(v->v.getVisitSummery()).setHeader("Visit Summery");	
		   return grid;
	}
	private void search(String term) {
		grid.setDataProvider(searchDataProvider(term, order.getValue()));
	}
	private void load() {
		grid.setDataProvider(listDataProvider(order.getValue()));
	}
	private DataProvider<PatientVisit, Void> listDataProvider(boolean sort) {
		DataProvider<PatientVisit, Void> dataProvider = DataProvider.fromCallbacks(
				// First callback fetches items based on a query
				query -> {
					// The index of the first item to load
					int offset = query.getOffset();
					// The number of items to load
					int limit = query.getLimit();

					return presenter.getVisistsList(offset, limit, sort).stream();
				},
				// Second callback fetches the number of items for a query
				query -> presenter.getVisistsCount());

		return dataProvider;
	}

	private DataProvider<PatientVisit, Void> searchDataProvider(String term, boolean sort) {
		DataProvider<PatientVisit, Void> dataProvider = DataProvider.fromCallbacks(
				// First callback fetches items based on a query
				query -> {
					// The index of the first item to load
					int offset = query.getOffset();
					// The number of items to load
					int limit = query.getLimit();

					return presenter.searchVisitsList(offset, limit, term, sort).stream();
				},
				// Second callback fetches the number of items for a query
				query -> presenter.searchVisitsCount(term));

		return dataProvider;
	}	


}