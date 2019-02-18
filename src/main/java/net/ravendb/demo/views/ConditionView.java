package net.ravendb.demo.views;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.KeyDownEvent;
import com.vaadin.flow.component.UI;
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
import net.ravendb.demo.components.editor.PatientEditorDialog;
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
	Button edit,delete;
	
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
		add(createHeader());
		add(createSearchBox());
		add(createGrid());
	}
	private Component createHeader() {
		HorizontalLayout header = new HorizontalLayout();

		Button add = new Button("Add", e -> {
			ConditionEditorDialog d = new ConditionEditorDialog("Add", new Condition(), this.presenter, () -> {
				load();
			});
			d.open();
		});
		header.add(add);

		edit = new Button("Edit", e -> {
			ConditionEditorDialog d = new ConditionEditorDialog("Edit", this.grid.asSingleSelect().getValue(),
					this.presenter, () -> {
						load();
					});
			d.open();
		});
		edit.setEnabled(false);
		header.add(edit);

		delete = new Button("Delete", e -> {
			ConfirmDialog.createQuestion().withCaption("System alert").withMessage("Do you want to delete?")
					.withOkButton(() -> {
						presenter.delete(grid.asSingleSelect().getValue());
						load();
					}, ButtonOption.focus(), ButtonOption.caption("YES")).withCancelButton(ButtonOption.caption("NO"))
					.open();
		});
		delete.setEnabled(false);
		header.add(delete);


		return header;

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
		   grid.addColumn(Condition::getPrescription).setHeader("Prescription");
		   grid.addColumn(Condition::getSeverity).setHeader("Severity");
		   grid.addSelectionListener(e -> {
				if (grid.getSelectedItems().size() > 0) {
					edit.setEnabled(true);
					delete.setEnabled(true);

				} else {
					edit.setEnabled(false);
					delete.setEnabled(false);
				}
			});
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
