package com.upc.gessi.spring.ui;

import com.upc.gessi.spring.entity.Recommendation;
import com.upc.gessi.spring.entity.Requirement;
import com.upc.gessi.spring.exception.NotificationException;
import com.upc.gessi.spring.service.BugzillaService;
import com.upc.gessi.spring.service.StakeholdersRecommenderService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Route
@HtmlImport("frontend://styles/shared-styles.html")
@PWA(name = "Stakeholders Recommender service web Application", shortName = "ORSR - Webapp")
@StyleSheet("frontend://styles/styles.css") // Relative to Servlet URL
public class MainView extends VerticalLayout {

    private StakeholdersRecommenderService service = StakeholdersRecommenderService.getInstance();
    private BugzillaService bugzillaService = BugzillaService.getInstance();

    private Grid<Requirement> requirementsGrid = new Grid<>(Requirement.class);
    private Grid<Recommendation> recommendationGrid = new Grid<>(Recommendation.class);
    private BugzillaForm bugzillaForm = new BugzillaForm();
    private UsernameForm usernameForm = new UsernameForm();

    private TextField filterText = new TextField();

    private NumberField stepperField;

    private Requirement selectedRequirement;
    private Recommendation selectedRecommendation;

    private Recommendation lastRejection;

    public MainView(@Autowired MessageBean bean) {

        selectedRequirement = null;

        Image logo = new Image("images/logo.png", "OpenReq");
        logo.setWidth("350px");
        logo.setHeight("100px");

        HorizontalLayout header = new HorizontalLayout();
        Label title = new Label("Stakeholders Recommender");
        title.setClassName("title");
        usernameForm.setClassName("login-box");
        header.add(logo, title, usernameForm);
        header.setClassName("header");

        filterText.setPlaceholder("Search...");
        filterText.setClearButtonVisible(true);

        // Or you can use an ordinary function to setup the component
        requirementsGrid.addComponentColumn(item -> createRemoveButton(requirementsGrid, item))
                .setHeader("ID");
        requirementsGrid.removeColumnByKey("id");
        requirementsGrid.removeColumnByKey("description");
        requirementsGrid.removeColumnByKey("effort");
        requirementsGrid.removeColumnByKey("modified_at");
        requirementsGrid.removeColumnByKey("requirementParts");
        requirementsGrid.removeColumnByKey("cc");
        requirementsGrid.removeColumnByKey("assigned");
        requirementsGrid.removeColumnByKey("gerrit");
        requirementsGrid.removeColumnByKey("status");
        requirementsGrid.removeColumnByKey("stalebug");

        requirementsGrid.addColumns("description", "cc", "modified_at", "assigned");
        requirementsGrid.getColumnByKey("description").setFlexGrow(10).setResizable(true);
        requirementsGrid.getColumnByKey("cc").setFlexGrow(1).setResizable(true);
        requirementsGrid.getColumnByKey("modified_at").setFlexGrow(3).setResizable(true);
        requirementsGrid.getColumnByKey("assigned").setFlexGrow(4).setResizable(true);

        recommendationGrid.addComponentColumn(item -> createPersonButton(recommendationGrid, item))
                .setHeader("Person");
        recommendationGrid.removeColumnByKey("person");
        recommendationGrid.removeColumnByKey("requirement");
        recommendationGrid.removeColumnByKey("appropiatenessScore");
        recommendationGrid.removeColumnByKey("availabilityScore");
        recommendationGrid.removeColumnByKey("appropiatenessScoreDouble");

        recommendationGrid.addColumns("appropiatenessScore");
        recommendationGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        filterText.setValueChangeMode(ValueChangeMode.EAGER);
        filterText.addValueChangeListener(e -> updateList());

        Button recommend = new Button();
        recommend.setText("Recommend stakeholders");
        recommend.addClickListener(event -> {
            if (selectedRequirement != null && usernameForm.getUsername() != null) {
                recommend(stepperField.getValue().intValue());
            }
            else if (selectedRequirement == null){
                sendNotification("Please select a requirement");
            } else if (usernameForm.getUsername() == null) {
                sendNotification("Please set a user");
            }
        });

        Button showRequirementDetails = new Button();
        showRequirementDetails.setText("Show requirement details");
        showRequirementDetails.addClickListener(event -> {
            if (selectedRequirement == null) {
               sendNotification("No requirement is selected");
            } else {
                service.getRequirementKeywords(selectedRequirement.getId());
                RequirementDetailsView requirementDetailsView = new RequirementDetailsView(selectedRequirement,
                        service.getRequirementKeywords(selectedRequirement.getId()));
                requirementDetailsView.open();
            }
        });

        showRequirementDetails.getClassNames().add("custom-button");
        recommend.getClassNames().add("custom-button");

        requirementsGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        requirementsGrid.addSelectionListener(event -> {
            selectedRequirement = event.getFirstSelectedItem().orElse(null);
            if (selectedRequirement != null) recommend(stepperField.getValue().intValue());
        });

        recommendationGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        recommendationGrid.addSelectionListener(event -> {
            selectedRecommendation = event.getFirstSelectedItem().orElse(null);
        });


        stepperField = new NumberField("Nº recommendations");
        stepperField.setValue(5d);
        stepperField.setStep(1d);
        stepperField.setMin(1);
        stepperField.setHasControls(true);
        stepperField.setMinWidth("9em");
        stepperField.setClassName("down");

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.add(filterText, showRequirementDetails,
                recommend,
                stepperField);
        filterText.setClassName("filter");
        filterText.setClassName("filter");

        VerticalLayout leftPanel = new VerticalLayout();
        leftPanel.add(buttons, requirementsGrid);

        Button rejectRecommendation = new Button();
        rejectRecommendation.getClassNames().add("custom-button");
        rejectRecommendation.setText("Reject recommendation");
        rejectRecommendation.addClickListener(event -> {
            try {
                if (selectedRecommendation != null) {
                    lastRejection = selectedRecommendation;

                    service.rejectRecommendation(selectedRecommendation, usernameForm.getUsername());

                    this.recommendations.remove(selectedRecommendation);
                    recommendationGrid.setItems(this.recommendations);

                    sendNotification("Recommendation rejected");
                    selectedRecommendation = null;
                } else {
                    sendNotification("No recommendation selected");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NotificationException ne) {
                ne.printStackTrace();
                sendNotification(ne.getMessage());
            }
        });

        Button acceptRecommendation = new Button();
        acceptRecommendation.getClassNames().add("custom-button");
        acceptRecommendation.setText("Accept recommendation");
        acceptRecommendation.addClickListener(event -> {
            if (selectedRecommendation != null) {
                boolean result = bugzillaService.assignUser(selectedRecommendation);
                if (result) sendNotification("Recommendation accepted");
                else sendNotification("You don't have permission to perform this operation. Please contact an administrator.");
            } else {
                sendNotification("No recommendation selected");
            }
        });

        Button ccUser = new Button();
        ccUser.getClassNames().add("custom-button");
        ccUser.setText("Cc user");
        ccUser.addClickListener(event -> {
            if (selectedRecommendation != null) {
                boolean result = bugzillaService.ccUser(selectedRecommendation);
                if (result) sendNotification("User added to cc list");
                else sendNotification("You don't have permission to perform this operation. Please contact an administrator.");
            } else {
                sendNotification("No recommendation selected");
            }
        });

        Button undoRejection = new Button();
        undoRejection.getClassNames().add("custom-button");
        undoRejection.setText("Undo last rejection");
        undoRejection.addClickListener(event -> {
            if (lastRejection != null) {
                try {
                    service.undoRejection(usernameForm.getUsername(), lastRejection);
                    if (selectedRequirement.getId().equals(lastRejection.getRequirement().getId())) {
                        this.recommendations.add(lastRejection);
                        this.recommendations.sort(Comparator.comparingDouble(Recommendation::getAppropiatenessScoreDouble)
                                .reversed());
                        recommendationGrid.setItems(this.recommendations);
                    }
                    lastRejection = null;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NotificationException e) {
                    sendNotification(e.getMessage());
                    e.printStackTrace();
                }
            } else
                sendNotification("No recommendation has been rejected yet");
        });

        //BUILD LAYOUT
        HorizontalLayout recommendationButtons = new HorizontalLayout();
        recommendationButtons.add(ccUser, acceptRecommendation, rejectRecommendation, undoRejection);

        VerticalLayout rightPanel = new VerticalLayout();
        rightPanel.add(recommendationButtons);
        rightPanel.add(recommendationGrid);

        HorizontalLayout mainPanel = new HorizontalLayout();
        mainPanel.add(leftPanel, rightPanel);
        mainPanel.setSizeFull();

        HorizontalLayout subheader = new HorizontalLayout();

        VerticalLayout toolbar = new VerticalLayout();

        Button loadData = new Button("Load data");
        loadData.getClassNames().add("custom-button");
        loadData.getClassNames().add("header-button");
        loadData.addClickListener(event -> {
            try {
                if (!bugzillaForm.isFieldEmpty()) {

                    bugzillaService.extractInfo(bugzillaForm.getComponents(), bugzillaForm.getProducts(),
                            bugzillaForm.getDate());

                    service.setBatchProcess(usernameForm.getUsername(), bugzillaService.getParticipants(), bugzillaService.getPersons(), bugzillaService.getProject(),
                            bugzillaService.getRequirements(), bugzillaService.getResponsibles(), bugzillaForm.getKeywords(),
                            bugzillaForm.getKeywordTool());

                    updateList();
                    emptyRecommendationList();
                }
            } catch (IOException e) {
                e.printStackTrace();
                sendNotification("There was a problem reaching the SR service. Please contact an administrator");
            } catch (NotificationException ne) {
                ne.printStackTrace();
                sendNotification(ne.getMessage());
            }
        });

        subheader.add(bugzillaForm, loadData);
        subheader.setClassName("subheader");

        toolbar.add(subheader);
        subheader.setWidthFull();
        toolbar.setWidthFull();

        add(header, toolbar, new Hr(), mainPanel);

        setSizeFull();
        if (usernameForm.getUsername() == null)
            usernameForm.openUsernameDialog();
        else
            usernameForm.setUsername(usernameForm.getUsername());

    }

    private void emptyRecommendationList() {
        recommendationGrid.setItems(new ArrayList<>());
    }

    private Component createRemoveButton(Grid<Requirement> grid, Requirement item) {
        Label label = new Label();
        label.getElement().setProperty("innerHTML", "<a href=\""
                + bugzillaService.getBugzillaUrl() + "show_bug.cgi?id="
                + item.getId()
                + "\" target=\"_blank\" class=\"link\" style=\"target-new: tab ! important;\">"
                + item.getId() + "</a>");
        label.setClassName("link");
        return label;
    }

    private Component createPersonButton(Grid<Recommendation> grid, Recommendation item) {
        Button b = new Button(item.getPerson().getName());
        b.addClickListener(event -> {
            PersonDetailsView personDetailsView = null;
            try {
                personDetailsView = new PersonDetailsView(item.getPerson(),
                        service.getPersonSkills(item.getPerson().getUsername()));
                personDetailsView.open();
            } catch (NotificationException | IOException e) {
                e.printStackTrace();
            }
        });
        b.addClassName("link-person");
        return b;
    }

    private void sendNotification(String s) {
        Notification notification = new Notification(
                s, 5000,
                Notification.Position.TOP_CENTER);
        notification.open();
    }

    List<Recommendation> recommendations;

    private void updateList() {
        List<Requirement> reqs = service.getRequirements(filterText.getValue(), Arrays.asList(bugzillaForm.getStatuses()));
        requirementsGrid.setItems(reqs);
        System.out.println("Nº reqs: " + reqs.size());
        if (reqs.size() == 0) {
            sendNotification("No requirements found");
        }
    }

    private void setRecommendation(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
        recommendationGrid.setItems(recommendations);
        if (recommendations.size() == 0) {
            sendNotification("No recommendations have been given");
        }
    }

    private void recommend(Integer k) {
        List<Recommendation> recommendations = null;
        try {
            recommendations = service.recommend(selectedRequirement,
                    usernameForm.getUsername(), k);
            if (selectedRequirement.getGerrit() != null) {
                bugzillaService.addGerritUserToList(selectedRequirement, recommendations, k);
            }
            setRecommendation(recommendations);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NotificationException e) {
            Notification notification = new Notification(e.getMessage(), 5000, Notification.Position.TOP_CENTER);
            notification.open();
        }
    }

}
