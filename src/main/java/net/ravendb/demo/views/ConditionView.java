package net.ravendb.demo.views;

import java.util.Collection;

import org.apache.commons.lang3.tuple.Pair;
import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.KeyDownEvent;
import com.vaadin.flow.component.button.Button;
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
import net.ravendb.demo.command.PatientVisit;
import net.ravendb.demo.components.editor.ConditionEditorDialog;
import net.ravendb.demo.components.grid.PageableGrid;
import net.ravendb.demo.model.Condition;
import net.ravendb.demo.presenters.ConditionPresenter;
import net.ravendb.demo.presenters.ConditionViewable;

@Route(value="condition",layout=RavenDBApp.class)
@PageTitle(value = "Hospital Management")
public class ConditionView extends VerticalLayout implements ConditionViewable{
	private final ConditionViewListener presenter;
	private PageableGrid<Condition> grid;
	TextField search;
	Button edit,delete;
	
	public ConditionView() {
	   presenter=new ConditionPresenter();
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
			ConditionEditorDialog d = new ConditionEditorDialog("Edit", this.grid.getGrid().asSingleSelect().getValue(),
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
		   grid=new PageableGrid<>(this::loadPage);
		   grid.getGrid().setSelectionMode(SelectionMode.SINGLE);
		   grid.setWidth("50%");


		   grid.getGrid().addColumn(Condition::getName).setHeader("Name");
		   grid.getGrid().addColumn(Condition::getSymptoms).setHeader("Symptoms");
		   grid.getGrid().addColumn(Condition::getRecommendedTreatment).setHeader("Recommended Treatment");
		   grid.getGrid().addSelectionListener(e -> {
				if (grid.getGrid().getSelectedItems().size() > 0) {
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
	   grid.loadFirstPage();	
	}
	private Pair<Collection<Condition>, Integer> loadPage(int page, int pageSize) {
			return presenter.getConditionsList(page * pageSize, pageSize, search.getValue());
	}	
}
