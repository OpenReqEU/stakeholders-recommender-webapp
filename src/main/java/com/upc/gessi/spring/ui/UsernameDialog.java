package com.upc.gessi.spring.ui;

import com.upc.gessi.spring.service.BugzillaService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

public class UsernameDialog extends Dialog {

    private UsernameForm usernameForm;

    private VerticalLayout verticalLayout ;
    private TextField textField;
    private PasswordField passwordField;
    private Button closeButton;
    private Button setUsername;

    public UsernameDialog(UsernameForm usernameForm, String username) {

        this.usernameForm = usernameForm;

        Label label = new Label("Log in form");
        label.setClassName("subtitle");

        textField = new TextField();
        textField.setLabel("Username (email)");
        textField.setPlaceholder("Platform-UI-Inbox@eclipse.org");
        textField.setMinWidth("18em");

        passwordField = new PasswordField();
        passwordField.setLabel("Password");
        passwordField.setMinWidth("18em");

        setUsername = new Button("Log in");
        setUsername.setClassName("custom-button");
        setUsername.addClickListener(event -> {
            login();
        });

        closeButton = new Button("Close");
        closeButton.setClassName("custom-button");
        closeButton.addClickListener(event -> {
            closeDialog();
        });

        if (username != null)
            textField.setValue(username);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.add(setUsername, closeButton);

        verticalLayout = new VerticalLayout();
        verticalLayout.add(label, textField, passwordField, buttons);

        add(verticalLayout);

    }

    private void closeDialog() {
        close();
    }

    private void login() {
        Boolean b = BugzillaService.getInstance().login(textField.getValue(), passwordField.getValue());
        if (b) {
            usernameForm.setUsername(textField.getValue().trim());
            close();
        } else {
            //TODO
            usernameForm.setUsername(textField.getValue().trim());
            close();
        }
    }

}
