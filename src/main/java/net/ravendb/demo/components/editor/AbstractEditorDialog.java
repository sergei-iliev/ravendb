package net.ravendb.demo.components.editor;

import java.io.Serializable;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;

public abstract class  AbstractEditorDialog<T> extends Dialog{

	protected final Binder<T> binder;
	protected final T bean;
    
	public AbstractEditorDialog(String title,T bean) {
		setCloseOnEsc(true);
		setCloseOnOutsideClick(true);
		setWidth("600px");
		this.bean=bean;
	    this.binder=new Binder<>();	    
	    this.init(title);
	}
	@Override
	protected void onAttach(AttachEvent attachEvent) {				
	    this.fetch();		
	}
	protected void init(String title){
		 initTitle(title);
	     initFormLayout();
	     initFooter();
	}
	
	private void  initTitle(String title){
		H3 head=new H3();
		head.add(new Icon(VaadinIcon.COG));
		head.add(new Label("  "+title));
		
		add(head);
	}
	
	private void initFormLayout(){
		
	    add(buildFormContent());
	   
	}
	private void initFooter() {
		  HorizontalLayout l=new HorizontalLayout();
		  l.setSizeFull();
		  l.setJustifyContentMode(JustifyContentMode.CENTER);
		  
		  Button saveButton = new Button("Save");
	      saveButton.addClickListener(this::save);
	      l.add(saveButton);
	      
	      Button cancelButton = new Button("Cancel");
	      cancelButton.addClickListener(this::cancel);
          l.add(cancelButton);
          
          add(l);
	}
    protected void fetch(){
    	this.binder.setBean(bean);
    };
	
	protected abstract Component buildFormContent();

    protected abstract void save(ClickEvent<Button> e);

    protected void cancel(ClickEvent<Button> e){
        this.close();
    }	
}
