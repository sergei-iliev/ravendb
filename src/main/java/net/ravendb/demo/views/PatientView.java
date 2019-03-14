package net.ravendb.demo.views;

import java.net.URLEncoder;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.KeyDownEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import net.ravendb.demo.RavenDBApp;
import net.ravendb.demo.assets.Gender;
import net.ravendb.demo.components.editor.AddressEditorDialog;
import net.ravendb.demo.components.editor.PatientEditorDialog;
import net.ravendb.demo.components.grid.PageableGrid;
import net.ravendb.demo.model.Patient;
import net.ravendb.demo.presenters.PatientPresenter;
import net.ravendb.demo.presenters.PatientViewable;

@Route(value = "patient", layout = RavenDBApp.class)
@PageTitle(value = "Hospital Management")
public class PatientView extends VerticalLayout implements PatientViewable {

	private final PatientViewListener presenter;
	private PageableGrid<Patient> grid;
	private Button edit, delete, visits;
	private Checkbox order;
	private TextField search;

	public PatientView() {
		presenter = new PatientPresenter();
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
			PatientEditorDialog d = new PatientEditorDialog("Edit", this.grid.getGrid().asSingleSelect().getValue(),
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
						presenter.delete(grid.getGrid().asSingleSelect().getValue());
						load();
					}, ButtonOption.focus(), ButtonOption.caption("YES")).withCancelButton(ButtonOption.caption("NO"))
					.open();
		});
		delete.setEnabled(false);
		header.add(delete);

		visits = new Button("Manage Visits", e -> {
			Map<String, String> map = new HashMap<>();
			map.put("patientId", grid.getGrid().asSingleSelect().getValue().getId());
			try {
				UI.getCurrent().navigate("patientvisit/"
						+ URLEncoder.encode(grid.getGrid().asSingleSelect().getValue().getId(), "UTF-8"));
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

		search = new TextField();
		search.setPlaceholder("Search");
		search.addKeyDownListener(com.vaadin.flow.component.Key.ENTER,
				(ComponentEventListener<KeyDownEvent>) keyDownEvent -> {
					load();
				});

		order = new Checkbox("Order by birth date");
		order.addValueChangeListener(e -> {
			load();
		});

		span.add(new Icon(VaadinIcon.SEARCH), search, order);

		layout.add(span);
		return layout;
	}

	private Component createGrid() {
		grid = new PageableGrid<>(this::loadPage);
		grid.getGrid().setSelectionMode(SelectionMode.SINGLE);
		grid.setWidth("100%");

		grid.getGrid().addComponentColumn(p -> {
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
		grid.getGrid().addColumn(Patient::getFirstName).setHeader("First Name");
		grid.getGrid().addColumn(Patient::getLastName).setHeader("Last Name");
		grid.getGrid().addColumn(Patient::getEmail).setHeader("Email");
		grid.getGrid().addColumn(new ComponentRenderer<>(person -> {
			if (person.getGender() == Gender.MALE) {
				return new Icon(VaadinIcon.MALE);
			} else {
				return new Icon(VaadinIcon.FEMALE);
			}
		})).setHeader("Gender");
		grid.getGrid().addColumn(new LocalDateRenderer<>(Patient::getBirthLocalDate,
				DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))).setHeader("Birth Date");

		grid.getGrid().addComponentColumn(p -> {
			Button address = new Button();
			address.setIcon(new Icon(VaadinIcon.HOME));
			address.addClickListener(e -> {
				AddressEditorDialog d = new AddressEditorDialog("Address", p, this.presenter);
				d.open();
			});
			return address;
		}).setHeader("Address");

		grid.getGrid().addSelectionListener(e -> {
			if (grid.getGrid().getSelectedItems().size() > 0) {
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

	private void load() {
		grid.loadFirstPage();
	}

	private Pair<Collection<Patient>, Integer> loadPage(int page, int pageSize) {
		if (search.getValue().length() > 1) {
			return presenter.searchPatientsList(page * pageSize, pageSize, search.getValue(), order.getValue());
		} else {
			return presenter.getPatientsList(page * pageSize, pageSize, order.getValue());
		}
	}

}
