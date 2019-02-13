package net.ravendb.demo.components.editor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.io.IOUtils;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.server.StreamResource;

import net.ravendb.demo.command.Attachment;
import net.ravendb.demo.model.Doctor;
import net.ravendb.demo.model.Patient;
import net.ravendb.demo.model.Patient.Gender;
import net.ravendb.demo.model.Visit;
import net.ravendb.demo.presenters.DoctorViewable.DoctorViewListener;
import net.ravendb.demo.presenters.PatientViewable.PatientViewListener;
import net.ravendb.demo.presenters.PatientVisitViewable.PatientVisitViewListener;

public class DoctorEditorDialog extends AbstractEditorDialog<Doctor>{

	private DoctorViewListener presenter;
	private Runnable run;
	ComboBox<String> department;
	
	public DoctorEditorDialog(String title,Doctor bean,DoctorViewListener presenter,Runnable run) {
		super(title,bean);
		this.run=run;
		this.presenter=presenter;
		
	}
	protected void fetch(){
      department.setItems(presenter.getDepartments());
      super.fetch();
	}

	@Override
	protected Component buildFormContent() {
		FormLayout layout=new FormLayout();		
		
		
        TextField name =
                new TextField();
        name.setRequiredIndicatorVisible(true);
        binder.forField(name)
                .asRequired()
                .bind(Doctor::getName, Doctor::setName);
        layout.addFormItem(name, "Name");
        
        TextField age =
                new TextField();        
        binder.forField(age)
        		.withConverter(new StringToIntegerConverter(""))
                .asRequired()
                .bind(Doctor::getAge, Doctor::setAge);
        layout.addFormItem(age, "Age");

        
        department=new ComboBox<>(); 
  
        binder.forField(department).bind(Doctor::getDepartment,Doctor::setDepartment);
        layout.addFormItem(department, "Department");

        
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1)
              );
		return layout;
	}

	@Override
	protected void save(ClickEvent<Button> e) {		   
		presenter.save(binder.getBean());	   
		run.run();
		this.close();
	}

}
