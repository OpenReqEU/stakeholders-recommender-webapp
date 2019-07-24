package com.upc.gessi.spring;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BugzillaForm extends FormLayout {

    private TextField productField;
    private TextField componentField;
    private MultiselectComboBox<String> statusField;
    private DatePicker datePicker;
    private Checkbox checkbox;

    public BugzillaForm() {

        productField = new TextField();
        componentField = new TextField();
        statusField = new MultiselectComboBox();
        datePicker = new DatePicker();
        checkbox = new Checkbox();

        productField.setLabel("Product");
        productField.setPlaceholder("ex.: Platform,PDE");

        componentField.setLabel("Component");
        componentField.setPlaceholder("ex.: UI,SWT");

        statusField.setPlaceholder("status");
        statusField.setLabel("Status");
        //open: new, unconfirmed, assigned, reopened
        //close: resolved, verified, closed
        statusField.setItems("open","closed");

        datePicker.setLabel("Creation date");
        datePicker.setValue(LocalDate.of(2015, 1, 1));

        checkbox.setLabel("Extract keywords");
        checkbox.setValue(false);

        add(productField, componentField, statusField, datePicker, checkbox);

        setResponsiveSteps(
                new ResponsiveStep("0", 1),
                new ResponsiveStep("21em", 2),
                new ResponsiveStep("21em", 3),
                new ResponsiveStep("15em", 4),
                new ResponsiveStep("15em", 5));
    }

    public String[] getProducts() {
        return productField.getValue().split(",");
    }

    public String[] getComponents() {
        return componentField.getValue().split(",");
    }

    public String[] getStatuses() {
        List<String> statuses = new ArrayList<>();
        if (statusField.getValue().contains("open"))
            statuses.addAll(Arrays.asList("new", "unconfirmed", "assigned", "reopened"));
        if (statusField.getValue().contains("closed"))
            statuses.addAll(Arrays.asList("resolved", "verified", "closed"));
        return statuses.toArray(new String[statuses.size()]);
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
