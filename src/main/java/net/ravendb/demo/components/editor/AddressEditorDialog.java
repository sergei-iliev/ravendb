package net.ravendb.demo.components.editor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.io.IOUtils;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.server.StreamResource;

import net.ravendb.demo.model.Address;
import net.ravendb.demo.model.Patient;
import net.ravendb.demo.model.Patient.Gender;
import net.ravendb.demo.presenters.PatientViewable.PatientViewListener;

public class AddressEditorDialog extends AbstractEditorDialog<Patient>{

	private PatientViewListener presenter;
    private Binder<Address> addbinder;
    private Patient bean;
    private ComboBox<String> region;
    
	public AddressEditorDialog(String title,Patient bean,PatientViewListener presenter) {
		super(title,bean);
		this.presenter=presenter;
        this.bean=bean;
		if(bean.getAddress()==null){
			bean.setAddress(new Address());
		}		
	    region.setItems(presenter.getRegionsList());
		this.addbinder.setBean(bean.getAddress());
	}

//	protected void init(String title){
//		
//	     super.init(title);
//	}
	@Override
	protected Component buildFormContent() {

		this.addbinder=new Binder<>(); 
		FormLayout layout=new FormLayout();		
		
        TextField city =
                new TextField();

        addbinder.forField(city)
                .asRequired()
                .bind(Address::getCity, Address::setCity);
        layout.addFormItem(city, "City");
        
        TextField line =
                new TextField();
        line.setRequiredIndicatorVisible(true);
        addbinder.forField(line)
                .asRequired()
                .bind(Address::getLine, Address::setLine);
        layout.addFormItem(line, "Address");
        
        region =
                new ComboBox<>();
    
        addbinder.forField(region)
                .asRequired()
                .bind(Address::getRegion, Address::setRegion);
        layout.addFormItem(region, "Region");
        
        TextField code =
                new TextField();
        addbinder.forField(code).bind(Address::getCode, Address::setCode);
        layout.addFormItem(code, "Code");
        
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1)
              );
	   	    
        return layout;
	}


	@Override
	protected void save(ClickEvent<Button> e) {
		presenter.save(bean.getId(),addbinder.getBean());
		this.close();
	}

}
