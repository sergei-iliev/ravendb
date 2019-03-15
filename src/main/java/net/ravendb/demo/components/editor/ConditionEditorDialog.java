package net.ravendb.demo.components.editor;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;

import net.ravendb.demo.model.Condition;
import net.ravendb.demo.presenters.ConditionViewable.ConditionViewListener;

public class ConditionEditorDialog extends AbstractEditorDialog<Condition>{

	private ConditionViewListener presenter;
	private Runnable run;
	
	public ConditionEditorDialog(String title,Condition condition,ConditionViewListener presenter,Runnable run) {
		super(title,condition);
		this.run=run;
		this.presenter=presenter;

	}
	protected void fetch(){
		load();
	    super.fetch();
	}
	@Override
	protected Component buildFormContent() {
		FormLayout layout=new FormLayout();		
      
        TextField description=new TextField();
        binder.forField(description).bind(Condition::getName,Condition::setName);
        layout.addFormItem(description, "Name");
        
        TextField prescription=new TextField();
        binder.forField(prescription).bind(Condition::getSymptoms,Condition::setSymptoms);
        layout.addFormItem(prescription, "Symptoms");
        


        TextField type=new TextField();        
        binder.forField(type).bind(Condition::getRecommendedTreatment,Condition::setRecommendedTreatment);
        layout.addFormItem(type, "Recommended Treatment");
        
        
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1)
              );
		return layout;
	}
	private void load(){

	}

	@Override
	protected void save(ClickEvent<Button> e) {		   
		presenter.save(binder.getBean());	   
		run.run();
		this.close();
	}

}
