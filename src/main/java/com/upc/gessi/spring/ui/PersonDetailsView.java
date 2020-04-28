package com.upc.gessi.spring.ui;

import com.upc.gessi.spring.entity.persistence.Person;
import com.upc.gessi.spring.entity.Skill;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import java.util.List;
import java.util.stream.Collectors;

@StyleSheet("frontend://styles/styles.css") // Relative to Servlet URL
public class PersonDetailsView extends Dialog {

    private VerticalLayout verticalLayout ;
    private FormLayout formLayout;
    private Button close;

    public PersonDetailsView(Person person, List<Skill> personSkills) {

        formLayout = new FormLayout();
        verticalLayout = new VerticalLayout();

        Label label = new Label("Person details");
        label.setClassName("subtitle");

        TextField id = new TextField("Username (e-mail)");
        id.setValue(person.getUsername());

        TextArea keywords = new TextArea("Skills");
        String keywordsString = personSkills == null ? "(no skills)" : String.join(", ", personSkills.stream()
                .map(Skill::getName)
                .collect(Collectors.toList()));
        keywords.setValue(keywordsString);
        if (personSkills == null) keywords.setEnabled(false);

        formLayout.add(id, keywords);

        close = new Button("Close");
        close.setClassName("custom-button");
        close.addClickListener(event -> {
            closeDialog();
        });

        verticalLayout.add(label, formLayout, close);

        add(verticalLayout);

    }

    private void closeDialog() {
        close();
    }

}
