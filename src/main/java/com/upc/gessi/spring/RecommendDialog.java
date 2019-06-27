package com.upc.gessi.spring;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;

public class RecommendDialog extends Dialog {

    private MainView mainView;

    private VerticalLayout verticalLayout ;
    private FormLayout formLayout;
    private Button close;
    private Button sendRecommend;
    private NumberField stepperField;

    public RecommendDialog(MainView mainView) {
        this.mainView = mainView;

        formLayout = new FormLayout();
        verticalLayout = new VerticalLayout();

        Label label = new Label("Recommend stakeholders");
        label.setClassName("subtitle");

        stepperField = new NumberField("Nº of recommendations");
        stepperField.setValue(1d);
        stepperField.setMin(1);
        stepperField.setHasControls(true);

        formLayout.add(stepperField);

        close = new Button("Close");
        close.setClassName("custom-button");
        close.addClickListener(event -> {
            closeDialog();
        });

        sendRecommend = new Button("Recommend");
        sendRecommend.setClassName("custom-button");
        sendRecommend.addClickListener(event -> {
            recommend();
        });

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.add(sendRecommend, close);

        verticalLayout.add(label, formLayout, buttons);

        add(verticalLayout);


    }

    private void recommend() {
        this.mainView.recommend(stepperField.getValue().intValue());
        close();
    }

    private void closeDialog() {
        close();
    }

}
