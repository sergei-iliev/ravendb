package net.ravendb.demo.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.KeyDownEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import net.ravendb.demo.RavenDBApp;
import net.ravendb.demo.components.editor.ConditionEditorDialog;
import net.ravendb.demo.components.editor.PatientVisitEditorDialog;
import net.ravendb.demo.model.Condition;
import net.ravendb.demo.model.Doctor;
import net.ravendb.demo.model.Patient;
import net.ravendb.demo.model.Visit;
import net.ravendb.demo.presenters.ConditionPresenter;
import net.ravendb.demo.presenters.ConditionViewable;
import net.ravendb.demo.presenters.DoctorViewable.DoctorViewListener;

@Route(value="condition",layout=RavenDBApp.class)
@PageTitle(value = "Hospital Management")
public class ConditionView extends VerticalLayout implements ConditionViewable{
	private final ConditionViewListener presenter;
	private Grid<Condition> grid;
	TextField search;
	
	public ConditionView() {
	   presenter=new ConditionPresenter(this);
	   init();	
	}
	
	@Override
	protected void onAttach(AttachEvent attachEvent) {	
		load();
	}
	
	private void init(){
		H2 title = new H2("Condition");	
		add(title);
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

		span.add(new Icon(VaadinIcon.SEARCH), search);

		layout.add(span);
		return layout;
	}	
	private Component createGrid() {
		   grid=new Grid<>();
		   grid.setSelectionMode(SelectionMode.SINGLE);
		   grid.setWidth("100%");


		   grid.addColumn(Condition::getDescription).setHeader("Description");
		   grid.addColumn(Condition::getDescription).setHeader("Prescription");
		   grid.addColumn(Condition::getSeverity).setHeader("Severity");
		   grid.addColumn(c->c.getPatient().getFirstName()).setHeader("First Name");
		   grid.addColumn(c->c.getPatient().getLastName()).setHeader("Last Name");
		   
		   return grid;

	}
	private void load() {
		
	   grid.setDataProvider(listDataProvider(search.getValue().length()==0?null:search.getValue()));

	}
	private DataProvider<Condition, Void> listDataProvider(String term) {
		DataProvider<Condition, Void> dataProvider = DataProvider.fromCallbacks(
				// First callback fetches items based on a query
				query -> {
					// The index of the first item to load
					int offset = query.getOffset();
					// The number of items to load
					int limit = query.getLimit();

					return presenter.getConditionsList(offset, limit,term).stream();
				},
				// Second callback fetches the number of items for a query
				query -> presenter.getConditionsCount(term));

		return dataProvider;
	}	
}
