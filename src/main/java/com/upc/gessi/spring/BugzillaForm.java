package com.upc.gessi.spring;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;

import java.time.LocalDate;

public class BugzillaForm extends FormLayout {

    private TextField productField;
    private TextField componentField;
    private Select statusField;
    private DatePicker datePicker;
    private Checkbox checkbox;

    public BugzillaForm() {

        productField = new TextField();
        componentField = new TextField();
        statusField = new Select<>("new", "resolved", "verified", "closed");
        datePicker = new DatePicker();
        checkbox = new Checkbox();

        productField.setLabel("Product");
        productField.setPlaceholder("Platform");

        componentField.setLabel("Component");
        componentField.setPlaceholder("UI");

        statusField.setPlaceholder("status");
        statusField.setLabel("Status");
        statusField.setValue("new");

        datePicker.setLabel("Creation date");
        datePicker.setValue(LocalDate.of(2015, 1, 1));

        checkbox.setLabel("Extract keywords");
        checkbox.setValue(false);

        add(productField, componentField, statusField, datePicker, checkbox);

        setResponsiveSteps(
                new ResponsiveStep("0", 1),
                new ResponsiveStep("21em", 2),
                new ResponsiveStep("22em", 3),
                new ResponsiveStep("15em", 4),
                new ResponsiveStep("15em", 5));
    }

    public String getProduct() {
        return productField.getValue();
    }

    public String getComponent() {
        return componentField.getValue();
    }

    public String getStatus() {
        return statusField.getValue().toString();
    }

    public Boolean getKeywords() {
        return checkbox.getValue();
    }

    public String getDate() {
        return datePicker.getValue().getYear() + "-" + datePicker.getValue().getMonthValue() + "-" + datePicker.getValue().getDayOfMonth();
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
