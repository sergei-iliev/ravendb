package net.ravendb.demo.components.editor;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;

import net.ravendb.demo.command.ComboValue;
import net.ravendb.demo.model.Visit;
import net.ravendb.demo.presenters.PatientVisitViewable.PatientVisitViewListener;

public class PatientVisitEditorDialog extends AbstractEditorDialog<Visit>{

	private PatientVisitViewListener presenter;
	private Runnable run;
	private ComboBox<ComboValue> doctor;
	private String patientId;
	ComboBox<ComboValue> condition;
	ComboBox<String> location;
	
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
	    
		list= presenter.getConditionsList().stream().map(d->new ComboValue(d.getId(), d.getName())).collect(Collectors.toList());
		list.add(ComboValue.NULL);
		condition.setItems(list);
		
        location.setItems(presenter.getLocationsList());
        
	    super.fetch();
	}
	@Override
	protected Component buildFormContent() {
		FormLayout layout=new FormLayout();		
      

        
        DatePicker date=new DatePicker();
        date.setWeekNumbersVisible(false);        
        binder.forField(date).bind(Visit::getLocalDate, Visit::setLocalDate);
        layout.addFormItem(date,"Date");

        location=new ComboBox<>();
        binder.forField(location).bind(Visit::getType,Visit::setType);
        layout.addFormItem(location, "Type");
        
        doctor=new ComboBox<>(); 
        doctor.setItemLabelGenerator(ComboValue::getName);
        doctor.setAllowCustomValue(true);
        binder.forField(doctor).bind(Visit::getDoctorValue,Visit::setDoctorValue);
        layout.addFormItem(doctor, "Doctor");
        
        condition=new ComboBox<>();
        condition.setItemLabelGenerator(ComboValue::getName);
        binder.forField(condition).bind(Visit::getConditionValue,Visit::setConditionValue);
        layout.addFormItem(condition, "Condition");
        
        TextArea summery=new TextArea();
        
        binder.forField(summery).bind(Visit::getVisitSummery,Visit::setVisitSummery);
        layout.addFormItem(summery, "Visit Summery");
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
