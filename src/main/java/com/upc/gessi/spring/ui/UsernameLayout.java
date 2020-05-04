package com.upc.gessi.spring.ui;

import com.upc.gessi.spring.service.BugzillaService;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class UsernameLayout extends HorizontalLayout {

    private BugzillaService bugzillaService;
    private String username;
    private Label label;
    private Label mail;
    private Button editUser;

    public UsernameLayout(BugzillaService bugzillaService) {

        this.bugzillaService = bugzillaService;

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

    LogInLayout logInLayout;

    public void openUsernameDialog() {
        logInLayout = new LogInLayout(this, username, bugzillaService);
        logInLayout.open();
    }

    public void setOnCloseListener(ComponentEventListener e) {
        logInLayout.addDialogCloseActionListener(e);
    }
}
