package net.ravendb.demo.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class MainHeader extends Composite<HorizontalLayout> {

	private HorizontalLayout contentLayout = new HorizontalLayout();

    public MainHeader() {
        Image logo = new Image("/frontend/images/ravendb-logo.jpg", "App logo");
        logo.addClassName("header-logo");
       
        H2 title = new H2("Hospital management");

        contentLayout.setPadding(true);
        contentLayout.setJustifyContentMode(JustifyContentMode.END);
        contentLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        getContent().add(logo, title, contentLayout);
        getContent().setFlexGrow(1, contentLayout);
        getContent().addClassName("header");
        getContent().setWidth("100%");
        getContent().setAlignSelf(Alignment.CENTER, title);
    }

    public void add(Component component) {
        contentLayout.add(component);
    }
    
}
