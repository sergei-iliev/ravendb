package net.ravendb.demo.components.editor;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;

import net.ravendb.demo.command.ComboValue;
import net.ravendb.demo.model.Visit;
import net.ravendb.demo.presenters.PatientVisitViewable.PatientVisitViewListener;

public class PatientVisitEditorDialog extends AbstractEditorDialog<Visit>{

	private PatientVisitViewListener presenter;
	private Runnable run;
	private ComboBox<ComboValue> doctor;
	private String patientId;
	
	public PatientVisitEditorDialog(String title,String patientId,Visit bean,PatientVisitViewListener presenter,Runnable run) {
		super(title,bean);
		this.run=run;
		this.patientId=patientId;
		this.presenter=presenter;
	}
	protected void fetch(){
		List<ComboValue> list= presenter.getDoctorsList().stream().map(d->new ComboValue(d.getId(), d.getName())).collect(Collectors.toList());
		list.add(ComboValue.NULL);
	    doctor.setItems(list);
	    super.fetch();
	}
	@Override
	protected Component buildFormContent() {
		FormLayout layout=new FormLayout();		
      

        
        DatePicker date=new DatePicker();
        date.setWeekNumbersVisible(false);        
        binder.forField(date).bind(Visit::getLocalDate, Visit::setLocalDate);
        layout.addFormItem(date,"Date");

        ComboBox<Visit.Type> type=new ComboBox<>();
        type.setItems(Visit.Type.values());
        binder.forField(type).bind(Visit::getType,Visit::setType);
        layout.addFormItem(type, "Type");
        
        doctor=new ComboBox<>(); 
        doctor.setItemLabelGenerator(ComboValue::getName);
        doctor.setAllowCustomValue(true);
        binder.forField(doctor).bind(Visit::getDoctorValue,Visit::setDoctorValue);
        layout.addFormItem(doctor, "Doctor");
        
        TextField summery=new TextField();
        binder.forField(summery).bind(Visit::getConditionSummery,Visit::setConditionSummery);
        layout.addFormItem(summery, "Summery");
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1)
              );
		return layout;
	}

	@Override
	protected void save(ClickEvent<Button> e) {		   
		presenter.save(patientId,binder.getBean());	   
		run.run();
		this.close();
	}

}
