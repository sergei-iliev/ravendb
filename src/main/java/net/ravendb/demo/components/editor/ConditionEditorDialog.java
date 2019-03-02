package net.ravendb.demo.components.editor;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;

import net.ravendb.demo.assets.Type;
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
        binder.forField(description).bind(Condition::getDescription,Condition::setDescription);
        layout.addFormItem(description, "Name");
        
        TextField prescription=new TextField();
        binder.forField(prescription).bind(Condition::getPrescription,Condition::setPrescription);
        layout.addFormItem(prescription, "Prescription");
        


        ComboBox<Type> type=new ComboBox<>();
        type.setItems(Type.values());
        binder.forField(type).bind(Condition::getSeverity,Condition::setSeverity);
        layout.addFormItem(type, "Severity");
        
        
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
