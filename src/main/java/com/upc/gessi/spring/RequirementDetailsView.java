package com.upc.gessi.spring;

import com.upc.gessi.spring.entity.Requirement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import java.util.List;

@StyleSheet("frontend://styles/styles.css") // Relative to Servlet URL
public class RequirementDetailsView extends Dialog {

    private VerticalLayout verticalLayout ;
    private FormLayout formLayout;
    private Button close;

    public RequirementDetailsView(Requirement requirement, List<String> requirementKeywords) {

        formLayout = new FormLayout();
        verticalLayout = new VerticalLayout();

        Label label = new Label("Requirement details");
        label.setClassName("subtitle");

        TextField id = new TextField("ID");
        id.setValue(requirement.getId());

        TextArea description = new TextArea("Description");
        description.setValue(requirement.getDescription());

        TextArea keywords = new TextArea("Keywords");
        String keywordsString = requirementKeywords == null ? "(batch process ran without keywords)" : String.join(", ", requirementKeywords);
        keywords.setValue(keywordsString);

        formLayout.add(id, description, keywords);

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
