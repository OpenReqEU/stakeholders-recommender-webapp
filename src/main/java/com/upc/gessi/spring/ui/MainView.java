package com.upc.gessi.spring.ui;

import com.upc.gessi.spring.entity.Recommendation;
import com.upc.gessi.spring.entity.persistence.Requirement;
import com.upc.gessi.spring.exception.NotificationException;
import com.upc.gessi.spring.service.BugzillaService;
import com.upc.gessi.spring.service.RequirementsService;
import com.upc.gessi.spring.service.StakeholdersRecommenderService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
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
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Route
@HtmlImport("frontend://styles/shared-styles.html")
@PWA(name = "Stakeholders Recommender service web Application", shortName = "ORSR - Webapp")
@StyleSheet("frontend://styles/styles.css") // Relative to Servlet URL
@SpringComponent
@UIScope
public class MainView extends VerticalLayout {

    private StakeholdersRecommenderService stakeholdersRecommenderService;
    private BugzillaService bugzillaService;
    private RequirementsService requirementsService;

    private Grid<Requirement> requirementsGrid = new Grid<>(Requirement.class);
    private Grid<Recommendation> recommendationGrid = new Grid<>(Recommendation.class);
    private BugzillaForm bugzillaForm;
    private UsernameLayout usernameLayout;

    private TextField filterText = new TextField();

    private NumberField stepperField;

    private Requirement selectedRequirement;
    private Recommendation selectedRecommendation;

    private Recommendation lastRejection;

    public MainView(@Autowired BugzillaService bugzillaService,
                    @Autowired StakeholdersRecommenderService stakeholdersRecommenderService,
                    @Autowired RequirementsService requirementsService) {

        this.stakeholdersRecommenderService = stakeholdersRecommenderService;
        this.bugzillaService = bugzillaService;
        this.requirementsService = requirementsService;

        bugzillaForm = new BugzillaForm();
        bugzillaForm.setClassName("bugzilla-form");
        usernameLayout = new UsernameLayout(bugzillaService);

        selectedRequirement = null;

        populateRequirementsTable();
        populateRecommendationsTable();

        HorizontalLayout header = generateHeaderPanel();
        Button recommend = configureRecommendButton();
        Button showRequirementDetails = configureRequirementDetailsButton();

        configureStepper();

        //Build requirements heading bar
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.add(filterText, showRequirementDetails,
                recommend,
                stepperField);

        //Build leftPanel
        VerticalLayout leftPanel = new VerticalLayout();
        leftPanel.add(buttons, requirementsGrid);

        Button rejectRecommendation = configureRejectRecommendation();
        Button acceptRecommendation = configureAcceptRecommendation();
        Button ccUser = configureCcUser();
        Button undoRejection = configureUndoRejection();
        Button loadData = configureLoadData();
        Button filterData = configureFilterData();
        Button dropDatabase = configureDropDatabase();

        //BUILD LAYOUT
        buildLayout(header, leftPanel, rejectRecommendation, acceptRecommendation, ccUser, undoRejection, loadData, dropDatabase, filterData);

    }

    private void buildLayout(HorizontalLayout header, VerticalLayout leftPanel, Button rejectRecommendation, Button acceptRecommendation,
                             Button ccUser, Button undoRejection, Button loadData, Button dropDatabase, Button filterData) {
        HorizontalLayout recommendationButtons = new HorizontalLayout();
        recommendationButtons.add(ccUser, acceptRecommendation, rejectRecommendation, undoRejection);
        recommendationButtons.getClassNames().add("bugzilla-form");

        VerticalLayout rightPanel = new VerticalLayout();
        rightPanel.add(recommendationButtons);
        rightPanel.add(recommendationGrid);

        HorizontalLayout mainPanel = new HorizontalLayout();
        mainPanel.add(leftPanel, rightPanel);
        mainPanel.setSizeFull();

        HorizontalLayout subheader = new HorizontalLayout();

        VerticalLayout toolbar = new VerticalLayout();


        subheader.add(bugzillaForm, loadData, filterData, dropDatabase);
        subheader.setClassName("subheader");

        toolbar.add(subheader);
        subheader.setWidthFull();
        toolbar.setWidthFull();

        add(header, toolbar, new Hr(), mainPanel);

        setSizeFull();
        if (usernameLayout.getUsername() == null) {
            usernameLayout.openUsernameDialog();
        }
        else {
            usernameLayout.setUsername(usernameLayout.getUsername());
        }
        updateList(false);


    }

    private Button configureDropDatabase() {
        Button dropDatabase = new Button("Drop database");
        dropDatabase.getElement().setProperty("title", "Deletes requirements from local database");
        dropDatabase.getClassNames().add("custom-button");
        dropDatabase.getClassNames().add("header-button");
        dropDatabase.getClassNames().add("delete-button");

        //Dialog
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        Button confirmButton = new Button("Confirm", event -> {
            dropDatabase();
            dialog.close();
        });
        Button cancelButton = new Button("Cancel", event -> {
            dialog.close();
        });
        VerticalLayout dialogLayout = new VerticalLayout();
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        Label alert = new Label("All requirements will be deleted. Do you wish to continue?");

        buttonsLayout.add(confirmButton, cancelButton);
        dialogLayout.add(alert, buttonsLayout);

        dialog.add(dialogLayout);

        dropDatabase.addClickListener(event -> {
            dialog.open();
        });

        return dropDatabase;
    }

    private void dropDatabase() {
        requirementsService.deleteRequirements();
        updateList(false);
        emptyRecommendationList();
    }

    private Button configureFilterData() {
        Button filterData = new Button("Filter");
        filterData.getElement().setProperty("title", "Filters imported requirements data");
        filterData.getClassNames().add("custom-button");
        filterData.getClassNames().add("header-button");

        filterData.addClickListener(buttonClickEvent -> {
            updateList(true);
        });

        return filterData;
    }

    private Button configureLoadData() {
        Button loadData = new Button("Import");
        loadData.getElement().setProperty("title", "Import requirements from Eclipse Bugzilla repository");
        loadData.getClassNames().add("custom-button");
        loadData.getClassNames().add("header-button");

        //Dialog
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        Button confirmButton = new Button("Confirm", event -> {
            dialog.close();
            try {
                importRequirements();
            } catch (IOException | NotificationException e) {
                e.printStackTrace();
            }
        });
        Button cancelButton = new Button("Cancel", event -> {
            dialog.close();
        });
        VerticalLayout dialogLayout = new VerticalLayout();
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        Label alert = new Label("Existing requirements will be updated. Do you wish to continue?");

        buttonsLayout.add(confirmButton, cancelButton);
        dialogLayout.add(alert, buttonsLayout);

        dialog.add(dialogLayout);

        loadData.addClickListener(event -> {
                try {
                    if (!bugzillaForm.isFieldEmpty()) {
                        if (requirementsService.findRequirements("").size() > 0)
                            dialog.open();
                        else
                            importRequirements();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    sendNotification("There was a problem reaching the Eclipse Bugzilla service. Please contact an administrator");
                }
        });
        return loadData;
    }

    private void importRequirements() throws IOException, NotificationException {
        bugzillaService.extractInfo(bugzillaForm.getComponents(), bugzillaForm.getProducts(), bugzillaForm.getStatuses(),
                bugzillaForm.getDate());

        stakeholdersRecommenderService.setBatchProcess(usernameLayout.getUsername(), bugzillaService.getParticipants(),
                bugzillaService.getPersons(), bugzillaService.getProject(),
                bugzillaService.getRequirements(), bugzillaService.getResponsibles(), bugzillaForm.getKeywords(),
                bugzillaForm.getKeywordTool());

        updateList(true);
        emptyRecommendationList();
    }

    private Button configureUndoRejection() {
        Button undoRejection = new Button();
        undoRejection.getClassNames().add("custom-button");
        undoRejection.setText("Undo last rejection");
        undoRejection.addClickListener(event -> {
            if (lastRejection != null) {
                try {
                    this.stakeholdersRecommenderService.undoRejection(usernameLayout.getUsername(), lastRejection);
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
        return undoRejection;
    }

    private Button configureCcUser() {
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
        return ccUser;
    }

    private Button configureAcceptRecommendation() {
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
        return acceptRecommendation;
    }

    private Button configureRejectRecommendation() {
        Button rejectRecommendation = new Button();
        rejectRecommendation.getClassNames().add("custom-button");
        rejectRecommendation.setText("Reject recommendation");
        rejectRecommendation.addClickListener(event -> {
            try {
                if (selectedRecommendation != null) {
                    lastRejection = selectedRecommendation;

                    this.stakeholdersRecommenderService.rejectRecommendation(selectedRecommendation, usernameLayout.getUsername());

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
        return rejectRecommendation;
    }

    private void configureStepper() {
        stepperField = new NumberField("Nº recommendations");
        stepperField.setValue(5d);
        stepperField.setStep(1d);
        stepperField.setMin(1);
        stepperField.setHasControls(true);
        stepperField.setMinWidth("9em");
        stepperField.setClassName("down");
    }

    private Button configureRequirementDetailsButton() {
        Button showRequirementDetails = new Button();
        showRequirementDetails.setText("Show requirement details");
        showRequirementDetails.addClickListener(event -> {
            if (selectedRequirement == null) {
               sendNotification("No requirement is selected");
            } else {
                this.stakeholdersRecommenderService.getRequirementKeywords(selectedRequirement.getId());
                RequirementDetailsView requirementDetailsView = new RequirementDetailsView(selectedRequirement,
                        this.stakeholdersRecommenderService.getRequirementKeywords(selectedRequirement.getId()));
                requirementDetailsView.open();
            }
        });

        showRequirementDetails.getClassNames().add("custom-button");
        return showRequirementDetails;
    }

    private Button configureRecommendButton() {
        Button recommend = new Button();
        recommend.setText("Recommend stakeholders");
        recommend.addClickListener(event -> {
            if (selectedRequirement != null && usernameLayout.getUsername() != null) {
                recommend(stepperField.getValue().intValue());
            }
            else if (selectedRequirement == null){
                sendNotification("Please select a requirement");
            } else if (usernameLayout.getUsername() == null) {
                sendNotification("Please set a user");
            }
        });
        recommend.getClassNames().add("custom-button");
        return recommend;
    }

    private void populateRecommendationsTable() {
        recommendationGrid.addComponentColumn(item -> createPersonButton(recommendationGrid, item))
                .setHeader("Person");
        recommendationGrid.removeColumnByKey("person");
        recommendationGrid.removeColumnByKey("requirement");
        recommendationGrid.removeColumnByKey("appropiatenessScore");
        recommendationGrid.removeColumnByKey("availabilityScore");
        recommendationGrid.removeColumnByKey("appropiatenessScoreDouble");

        recommendationGrid.addColumns("appropiatenessScore");
        recommendationGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        recommendationGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        recommendationGrid.addSelectionListener(event -> {
            selectedRecommendation = event.getFirstSelectedItem().orElse(null);
        });
    }

    private void populateRequirementsTable() {
        filterText.setPlaceholder("Search...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.EAGER);
        filterText.addValueChangeListener(e -> updateList(false));
        filterText.setClassName("filter");

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
        requirementsGrid.removeColumnByKey("product");
        requirementsGrid.removeColumnByKey("component");

        requirementsGrid.addColumns("description", "cc", "modified_at", "assigned");
        requirementsGrid.getColumnByKey("description").setFlexGrow(10).setResizable(true);
        requirementsGrid.getColumnByKey("cc").setFlexGrow(1).setResizable(true);
        requirementsGrid.getColumnByKey("modified_at").setFlexGrow(3).setResizable(true);
        requirementsGrid.getColumnByKey("assigned").setFlexGrow(4).setResizable(true);

        requirementsGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        requirementsGrid.addSelectionListener(event -> {
            selectedRequirement = event.getFirstSelectedItem().orElse(null);
            if (selectedRequirement != null) recommend(stepperField.getValue().intValue());
        });
    }

    private HorizontalLayout generateHeaderPanel() {
        Image logo = new Image("images/logo.png", "OpenReq");
        logo.setWidth("350px");
        logo.setHeight("100px");

        HorizontalLayout header = new HorizontalLayout();
        Label title = new Label("Stakeholders Recommender");
        title.setClassName("title");
        usernameLayout.setClassName("login-box");
        header.add(logo, title, usernameLayout);
        header.setClassName("header");
        return header;
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
                        stakeholdersRecommenderService.getPersonSkills(item.getPerson().getUsername()));
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

    private void updateList(Boolean alert) {
        //List<Requirement> reqs = stakeholdersRecommenderService.getRequirements(filterText.getValue(), Arrays.asList(bugzillaForm.getStatuses()));
        List<Requirement> reqs = requirementsService.findRequirements(filterText.getValue(), bugzillaForm.getProducts(),
                bugzillaForm.getComponents(), bugzillaForm.getStatuses(), bugzillaForm.getDate());
        requirementsGrid.setItems(reqs);
        System.out.println("Nº reqs: " + reqs.size());
        if (alert) {
            if (reqs.size() == 0) {
                sendNotification("No requirements found");
            } else {
                sendNotification(reqs.size() + " requirements found");
            }
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
            recommendations = stakeholdersRecommenderService.recommend(selectedRequirement,
                    usernameLayout.getUsername(), k);
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
