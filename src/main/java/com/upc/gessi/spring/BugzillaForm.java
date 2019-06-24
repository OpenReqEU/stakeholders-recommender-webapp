package com.upc.gessi.spring;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;

public class BugzillaForm extends FormLayout {

    private TextField productField;
    private TextField componentField;
    private TextField statusField;

    public BugzillaForm() {

        productField = new TextField();
        componentField = new TextField();
        statusField = new TextField();

        productField.setLabel("Product");
        productField.setPlaceholder("Platform");

        componentField.setLabel("Component");
        componentField.setPlaceholder("UI");

        statusField.setLabel("Status");
        statusField.setPlaceholder("closed");

        add(productField, componentField, statusField);

        setResponsiveSteps(
                new ResponsiveStep("0", 1),
                new ResponsiveStep("21em", 2),
                new ResponsiveStep("22em", 3));
    }

    public String getProduct() {
        return productField.getValue();
    }

    public String getComponent() {
        return componentField.getValue();
    }

    public String getStatus() {
        return statusField.getValue();
    }

    public boolean isFieldEmpty() {
        boolean isEmpty = false;
        if (productField.isEmpty()) {
            isEmpty = true;
            productField.setInvalid(true);
        } else productField.setInvalid(false);
        if (componentField.isEmpty()) {
            isEmpty = true;
            componentField.setInvalid(true);
        } else componentField.setInvalid(false);
        if (statusField.isEmpty()) {
            isEmpty = true;
            statusField.setInvalid(true);
        } else statusField.setInvalid(false);
        return isEmpty;
    }
}
