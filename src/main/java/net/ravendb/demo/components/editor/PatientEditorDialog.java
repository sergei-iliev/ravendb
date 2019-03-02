package net.ravendb.demo.components.editor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.crypto.CipherInputStream;
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
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.server.StreamResource;

import net.ravendb.client.exceptions.ConcurrencyException;
import net.ravendb.demo.assets.Gender;
import net.ravendb.demo.command.Attachment;
import net.ravendb.demo.model.Patient;
import net.ravendb.demo.presenters.PatientViewable.PatientViewListener;

public class PatientEditorDialog extends AbstractEditorDialog<Patient>{

	private PatientViewListener presenter;
	private Image image;
	private Runnable run;
	
	public PatientEditorDialog(String title,Patient bean,PatientViewListener presenter,Runnable run) {
		super(title,bean);
		this.run=run;
		this.presenter=presenter;
	}

	@Override
	protected Component buildFormContent() {
		FormLayout layout=new FormLayout();		
		
		HorizontalLayout photoLayout=new HorizontalLayout();
		Attachment attachment= bean.getAttachment();
		if(attachment==null){
			image = new Image("/frontend/images/avatar.jpeg", "");
			image.setWidth("60px");
			image.setHeight("60px");
			image.getStyle().set("borderRadius", "50%");
		}else{
			image = new Image(attachment.getStreamResource(),"");
			image.setWidth("60px");
			image.setHeight("60px");
			image.getStyle().set("borderRadius", "50%");			
		}
		
		photoLayout.add(image);
		
		MemoryBuffer fileBuffer = new MemoryBuffer();
		Upload upload = new Upload(fileBuffer);
		
		upload.addSucceededListener(e->{
			  this.processUpload(e,fileBuffer);
              //presenter.onComponentEvent(e);
		});
		upload.setDropAllowed(false);
		
		
		photoLayout.add(upload);
		
		layout.add(photoLayout);
		
        TextField firstname =
                new TextField();
        firstname.setRequiredIndicatorVisible(true);
        binder.forField(firstname)
                .asRequired()
                .bind(Patient::getFirstName, Patient::setFirstName);
        layout.addFormItem(firstname, "First Name");
        
        TextField lastname =
                new TextField();
        lastname.setRequiredIndicatorVisible(true);
        binder.forField(lastname)
                .asRequired()
                .bind(Patient::getLastName, Patient::setLastName);
        layout.addFormItem(lastname, "Last Name");
        
        TextField email =
                new TextField();
        lastname.setRequiredIndicatorVisible(true);
        binder.forField(email)
                .asRequired()
                .bind(Patient::getEmail, Patient::setEmail);
        layout.addFormItem(email, "Email");
        
        ComboBox<Gender> gender=new ComboBox<>();
        gender.setItems(Gender.values());
        binder.forField(gender).bind(Patient::getGender,Patient::setGender);
        layout.addFormItem(gender, "Gender");
        
        DatePicker birth=new DatePicker();
        birth.setWeekNumbersVisible(false);        
        binder.forField(birth).bind(Patient::getBirthLocalDate, Patient::setBirthLocalDate);
        layout.addFormItem(birth,"Date of birth");
        
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1)
              );
		return layout;
	}

	private void processUpload(SucceededEvent event,MemoryBuffer fileBuffer){
		InputStream is=fileBuffer.getInputStream();
		try{
            byte[] bytes = IOUtils.toByteArray(is);
            image.getElement().setAttribute("src", new StreamResource(
                    event.getFileName(), () -> new ByteArrayInputStream(bytes)));
            //create attachment
            Attachment attachment=new Attachment();
            attachment.setBytes(bytes);
            attachment.setName(event.getFileName());
            attachment.setMimeType(event.getMIMEType());
            binder.getBean().setAttachment(attachment);
            
            try (ImageInputStream in = ImageIO.createImageInputStream(
                    new ByteArrayInputStream(bytes))) {
                final Iterator<ImageReader> readers = ImageIO
                        .getImageReaders(in);
                if (readers.hasNext()) {
                    ImageReader reader = readers.next();
                    try {
                        reader.setInput(in);
                        image.setWidth("60px");
                        image.setHeight("60px");
                    } finally {
                        reader.dispose();
                    }
                }
            }		  
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	@Override
	protected void save(ClickEvent<Button> e) {		   
		try{
		  if(binder.getBean().getId()!=null)	
		    presenter.update(binder.getBean());
		  else
			presenter.create(binder.getBean());  
		}catch(ConcurrencyException ce){
			Notification.show("Document was updated by another user",5000, Notification.Position.TOP_CENTER);
		}
		 run.run();
		 this.close();

	}

}
