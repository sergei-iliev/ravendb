package net.ravendb.demo.views;

import java.net.URLEncoder;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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
import com.vaadin.flow.component.html.H4;
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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import net.ravendb.demo.RavenDBApp;
import net.ravendb.demo.components.editor.AddressEditorDialog;
import net.ravendb.demo.components.editor.PatientEditorDialog;
import net.ravendb.demo.model.Patient;
import net.ravendb.demo.model.Patient.Gender;
import net.ravendb.demo.presenters.PatientPresenter;
import net.ravendb.demo.presenters.PatientViewable;

@Route(value = "patient", layout = RavenDBApp.class)
@PageTitle(value = "Hospital Management")
public class PatientView extends VerticalLayout implements PatientViewable {

	private final PatientViewListener presenter;
	private Grid<Patient> grid;
	private Button edit, delete, visits;
	private Checkbox order;

	public PatientView() {
		presenter = new PatientPresenter(this);
		init();
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		load();
	}

	private void init() {
		this.setWidth("100%");
		H4 title = new H4("Patients");
		add(title);

		add(createHeader());
		add(createSearchBox());
		add(createGrid());

	}

	private Component createHeader() {
		HorizontalLayout header = new HorizontalLayout();

		Button add = new Button("Add", e -> {
			PatientEditorDialog d = new PatientEditorDialog("Add", new Patient(), this.presenter, () -> {
				load();
			});
			d.open();
		});
		header.add(add);

		edit = new Button("Edit", e -> {
			PatientEditorDialog d = new PatientEditorDialog("Edit", this.grid.asSingleSelect().getValue(),
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

		visits = new Button("Manage Visits", e -> {
			Map<String, String> map = new HashMap<>();
			map.put("patientId", grid.asSingleSelect().getValue().getId());
			try {
				UI.getCurrent().navigate(
						"patientvisit/" + URLEncoder.encode(grid.asSingleSelect().getValue().getId(), "UTF-8"));
			} catch (Exception e1) {

				e1.printStackTrace();
			}
		});
		visits.setEnabled(false);
		header.add(visits);

		return header;

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

		order = new Checkbox("Order by birth date");
		order.addValueChangeListener(e -> {
			if (search.getValue() == null) {
				load();
			} else {
				search(search.getValue());
			}
		});

		span.add(new Icon(VaadinIcon.SEARCH), search, order);

		layout.add(span);
		return layout;
	}

	private Component createGrid() {
		grid = new Grid<>();
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setWidth("100%");
		
		
		grid.addComponentColumn(p -> {
			if (p.getAttachment() == null) {
				Image image = new Image("/frontend/images/avatar.jpeg", "");
				image.setWidth("60px");
				image.setHeight("60px");
				image.getStyle().set("borderRadius", "50%");
				return image;
			} else {
				Image image = new Image(p.getAttachment().getStreamResource(), "");
				image.setWidth("60px");
				image.setHeight("60px");
				image.getStyle().set("borderRadius", "50%");
				return image;
			}
		});
		grid.addColumn(Patient::getFirstName).setHeader("First Name");
		grid.addColumn(Patient::getLastName).setHeader("Last Name");
		grid.addColumn(Patient::getEmail).setHeader("Email");
		grid.addColumn(new ComponentRenderer<>(person -> {
			if (person.getGender() == Gender.MALE) {
				return new Icon(VaadinIcon.MALE);
			} else {
				return new Icon(VaadinIcon.FEMALE);
			}
		})).setHeader("Gender");
		grid.addColumn(new LocalDateRenderer<>(Patient::getBirthLocalDate,
				DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))).setHeader("Birth Date");

		grid.addComponentColumn(p -> {
			Button address = new Button();
			address.setIcon(new Icon(VaadinIcon.HOME));
			address.addClickListener(e -> {
				AddressEditorDialog d = new AddressEditorDialog("Address", p, this.presenter);
				d.open();
			});
			return address;
		}).setHeader("Address");

		grid.addSelectionListener(e -> {
			if (grid.getSelectedItems().size() > 0) {
				edit.setEnabled(true);
				delete.setEnabled(true);
				visits.setEnabled(true);
			} else {
				edit.setEnabled(false);
				delete.setEnabled(false);
				visits.setEnabled(false);
			}
		});
		return grid;
	}

	private void search(String term) {
		grid.setDataProvider(searchDataProvider(term, order.getValue()));
	}

	private void load() {
		grid.setDataProvider(listDataProvider(order.getValue()));
	}

	private DataProvider<Patient, Void> listDataProvider(boolean sort) {
		DataProvider<Patient, Void> dataProvider = DataProvider.fromCallbacks(
				// First callback fetches items based on a query
				query -> {
					// The index of the first item to load
					int offset = query.getOffset();
					// The number of items to load
					int limit = query.getLimit();

					return presenter.getPatientsList(offset, limit, sort).stream();
				},
				// Second callback fetches the number of items for a query
				query -> presenter.getPatientsCount());

		return dataProvider;
	}

	private DataProvider<Patient, Void> searchDataProvider(String term, boolean sort) {
		DataProvider<Patient, Void> dataProvider = DataProvider.fromCallbacks(
				// First callback fetches items based on a query
				query -> {
					// The index of the first item to load
					int offset = query.getOffset();
					// The number of items to load
					int limit = query.getLimit();

					return presenter.searchPatientsList(offset, limit, term, sort).stream();
				},
				// Second callback fetches the number of items for a query
				query -> presenter.searchPatientsCount(term));

		return dataProvider;
	}

}
