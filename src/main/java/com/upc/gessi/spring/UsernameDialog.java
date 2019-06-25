package com.upc.gessi.spring;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class UsernameDialog extends Dialog {

    private UsernameForm usernameForm;

    private VerticalLayout verticalLayout ;
    private TextField textField;
    private Button closeButton;
    private Button setUsername;

    public UsernameDialog(UsernameForm usernameForm) {

        this.usernameForm = usernameForm;

        Label label = new Label("Edit username");
        label.setClassName("subtitle");

        textField = new TextField();
        textField.setLabel("Username (email)");
        textField.setPlaceholder("placeholder@mail.com");

        setUsername = new Button("Save");
        setUsername.setClassName("custom-button");
        setUsername.addClickListener(event -> {
            setUsername();
        });

        closeButton = new Button("Close");
        closeButton.setClassName("custom-button");
        closeButton.addClickListener(event -> {
            closeDialog();
        });

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.add(setUsername, closeButton);

        verticalLayout = new VerticalLayout();
        verticalLayout.add(label, textField, buttons);

        add(verticalLayout);

    }

    private void closeDialog() {
        close();
    }

    private void setUsername() {
        usernameForm.setUsername(textField.getValue());
        close();
    }

}
