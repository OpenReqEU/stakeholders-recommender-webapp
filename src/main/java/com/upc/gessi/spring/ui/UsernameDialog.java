package com.upc.gessi.spring.ui;

import com.upc.gessi.spring.exception.NotificationException;
import com.upc.gessi.spring.service.BugzillaService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

public class UsernameDialog extends Dialog {

    private UsernameForm usernameForm;

    private VerticalLayout verticalLayout ;
    private TextField textField;
    private PasswordField passwordField;
    private TextField apiKey;
    private Button setUsername;

    public UsernameDialog(UsernameForm usernameForm, String username) {

        setCloseOnOutsideClick(false);
        setCloseOnEsc(false);

        this.usernameForm = usernameForm;

        Label label = new Label("Log in form");
        label.setClassName("subtitle");

        textField = new TextField();
        textField.setLabel("Username (email)");
        textField.setMinWidth("18em");
        textField.setRequired(true);

        passwordField = new PasswordField();
        passwordField.setLabel("Password");
        passwordField.setMinWidth("18em");
        passwordField.setRequired(true);

        apiKey = new TextField();
        apiKey.setLabel("API key (only if required)");
        apiKey.setMinWidth("18em");

        setUsername = new Button("Log in");
        setUsername.setClassName("custom-button");
        setUsername.addClickListener(event -> {
            login();
        });



        if (username != null)
            textField.setValue(username);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.add(setUsername);

        verticalLayout = new VerticalLayout();
        verticalLayout.add(label, textField, passwordField, apiKey, buttons);

        add(verticalLayout);

    }

    private void closeDialog() {
        close();
    }

    private void login() {
        Boolean b = BugzillaService.getInstance().login(textField.getValue(), passwordField.getValue(), apiKey.getValue());
        if (b) {
            usernameForm.setUsername(textField.getValue().trim());
            close();
        } else {
            Notification notification = new Notification(
                    "Either your credentials are incorrect, or you need to use a valid API key to log in. Please contact " +
                            "an administrator", 5000,
                    Notification.Position.TOP_CENTER);
            notification.open();
        }
    }

}
