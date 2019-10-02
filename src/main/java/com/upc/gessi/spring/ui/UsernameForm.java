package com.upc.gessi.spring.ui;

import com.upc.gessi.spring.ui.UsernameDialog;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class UsernameForm extends HorizontalLayout {

    private String username;
    private Label label;
    private Label mail;
    private Button editUser;

    public UsernameForm() {

        label = new Label("No user selected, please set username");
        label.setClassName("subsubtitle-no-set");

        editUser = new Button("Log in");
        editUser.setClassName("custom-button");
        editUser.addClickListener(event -> {
            openUsernameDialog();
        });

        mail = new Label();
        mail.setClassName("subsubtitle");

        add(label, mail, editUser);

        Object user = UI.getCurrent().getSession().getAttribute("username");
        username = user != null ? user.toString() : null;

    }

    public void setUsername(String username) {
        this.username = username;
        label.setText("Welcome");
        mail.setText(username);

        UI.getCurrent().getSession().setAttribute("username", username);
    }

    public String getUsername() {
        return username;
    }


    public void openUsernameDialog() {
        UsernameDialog usernameDialog = new UsernameDialog(this, username);
        usernameDialog.open();
    }

}
