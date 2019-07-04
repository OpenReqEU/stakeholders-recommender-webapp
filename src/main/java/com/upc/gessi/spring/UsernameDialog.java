package com.upc.gessi.spring;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class UsernameDialog extends Dialog {

    private UsernameForm usernameForm;

    private VerticalLayout verticalLayout ;
    private TextField textField;
    private Button closeButton;
    private Button setUsername;

    public UsernameDialog(UsernameForm usernameForm, String username) {

        this.usernameForm = usernameForm;

        Label label = new Label("Set user");
        label.setClassName("subtitle");

        Label sublabel = new Label("Please introduce your e-mail");
        sublabel.setClassName("subsubtitle-no-set");

        textField = new TextField();
        textField.setLabel("Username (email)");
        textField.setPlaceholder("placeholder.mail@domain.com");
        textField.setMinWidth("18em");

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

        if (username != null)
            textField.setValue(username);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.add(setUsername, closeButton);

        verticalLayout = new VerticalLayout();
        verticalLayout.add(label, sublabel, textField, buttons);

        add(verticalLayout);

    }

    private void closeDialog() {
        close();
    }

    private void setUsername() {
        if (!textField.getValue().isEmpty()) {
            textField.setInvalid(false);
            usernameForm.setUsername(textField.getValue().trim());
            close();
        } else  {
            textField.setInvalid(true);
        }
    }

}
