package com.upc.gessi.spring;

import com.upc.gessi.spring.entity.Recommend;
import com.upc.gessi.spring.entity.Recommendation;
import com.upc.gessi.spring.entity.Requirement;
import com.upc.gessi.spring.exception.NotificationException;
import com.upc.gessi.spring.service.BugzillaService;
import com.upc.gessi.spring.service.StakeholdersRecommenderService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

import java.io.IOException;
import java.util.List;

@Route
@PWA(name = "Stakeholders Recommender service web Application", shortName = "ORSR - Webapp")
@StyleSheet("frontend://styles/styles.css") // Relative to Servlet URL
public class MainView extends VerticalLayout {

    private StakeholdersRecommenderService service = StakeholdersRecommenderService.getInstance();
    private BugzillaService bugzillaService = BugzillaService.getInstance();

    private Grid<Requirement> grid = new Grid<>(Requirement.class);
    private Grid<Recommendation> recommendationGrid = new Grid<>(Recommendation.class);
    private BugzillaForm bugzillaForm = new BugzillaForm();
    private TextField filterText = new TextField();
    private UsernameForm usernameForm = new UsernameForm();

    private Button showRequirementDetails = new Button();
    private Button recommend = new Button();
    private Button rejectRecommendation = new Button();
    private Button acceptRecommendation = new Button();

    private Requirement selectedRequirement;
    private Recommendation selectedRecommendation;

    public MainView(@Autowired MessageBean bean) {

        selectedRequirement = null;

        Image logo = new Image("images/logo.png", "OpenReq");
        logo.setWidth("350px");
        logo.setHeight("100px");

        HorizontalLayout header = new HorizontalLayout();
        Label title = new Label("Stakeholders Recommender");
        title.setClassName("title");
        header.add(logo, title);
        header.setClassName("header");

        filterText.setPlaceholder("Search...");
        filterText.setClearButtonVisible(true);

        grid.setColumns("id", "description", "modified_at");
        grid.getColumnByKey("id").setFlexGrow(1).setResizable(true);
        grid.getColumnByKey("description").setFlexGrow(10).setResizable(true);
        grid.getColumnByKey("modified_at").setFlexGrow(3).setResizable(true);
        recommendationGrid.setColumns("person", "appropiatenessScore", "availabilityScore");

        filterText.setValueChangeMode(ValueChangeMode.EAGER);
        filterText.addValueChangeListener(e -> updateList());

        recommend.setText("Recommend stakeholders");
        recommend.addClickListener(event -> {
            if (selectedRequirement != null && usernameForm.getUsername() != null) {
                RecommendDialog recommendDialog = new RecommendDialog(this);
                recommendDialog.open();
            }
            else if (selectedRequirement == null){
                sendNotification("Please select a requirement");
            } else if (usernameForm.getUsername() == null) {
                sendNotification("Please set a user");
            }
        });

        showRequirementDetails.setText("Show requirement keywords");
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

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addSelectionListener(event -> {
            selectedRequirement = event.getFirstSelectedItem().orElse(null);
        });

        recommendationGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        recommendationGrid.addSelectionListener(event -> {
            selectedRecommendation = event.getFirstSelectedItem().orElse(null);
        });

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.add(filterText, showRequirementDetails, recommend);
        filterText.setClassName("filter");

        VerticalLayout leftPanel = new VerticalLayout();
        leftPanel.add(buttons);
        leftPanel.add(grid);

        rejectRecommendation.getClassNames().add("custom-button");
        rejectRecommendation.setText("Reject recommendation");
        rejectRecommendation.addClickListener(event -> {
            try {
                if (selectedRecommendation != null) {
                    service.rejectRecommendation(selectedRecommendation, bugzillaService.getNumber(usernameForm.getUsername()));
                    this.recommendations.remove(selectedRecommendation);
                    recommendationGrid.setItems(this.recommendations);
                    sendNotification("Recommendation rejected");
                    selectedRecommendation = null;
                } else {
                    sendNotification("No recommendation selected");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        acceptRecommendation.getClassNames().add("custom-button");
        acceptRecommendation.setText("Accept recommendation");
        acceptRecommendation.addClickListener(event -> {
            if (selectedRecommendation != null) {
                service.acceptRecommendation(selectedRecommendation);
                sendNotification("Recommendation accepted");
            } else {
                sendNotification("No recommendation selected");
            }
        });

        //BUILD LAYOUT
        HorizontalLayout recommendationButtons = new HorizontalLayout();
        recommendationButtons.add(acceptRecommendation, rejectRecommendation);

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
                    //sendNotification("Loading data and sending batch process. This may take a while...");
                    bugzillaService.extractInfo(bugzillaForm.getComponent(), bugzillaForm.getStatus(), bugzillaForm.getProduct(),
                            bugzillaForm.getDate());
                    service.setBatchProcess(bugzillaService.getParticipants(), bugzillaService.getPersons(), bugzillaService.getProject(),
                            bugzillaService.getRequirements(), bugzillaService.getResponsibles(), bugzillaForm.getKeywords());
                    updateList();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        subheader.add(bugzillaForm, loadData);
        subheader.setClassName("subheader");

        toolbar.add(usernameForm, subheader);
        subheader.setWidthFull();
        toolbar.setWidthFull();

        add(header, toolbar, mainPanel);

        setSizeFull();
        if (usernameForm.getUsername() == null)
            usernameForm.openUsernameDialog();
        else
            usernameForm.setUsername(usernameForm.getUsername());

    }

    private void sendNotification(String s) {
        Notification notification = new Notification(
                s, 5000,
                Notification.Position.TOP_CENTER);
        notification.open();
    }

    List<Recommendation> recommendations;

    private void updateList() {
        grid.setItems(service.getRequirements(filterText.getValue()));
    }

    private void setRecommendation(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
        recommendationGrid.setItems(recommendations);
    }

    public void recommend(Integer k) {
        List<Recommendation> recommendations = null;
        try {
            recommendations = service.recommend(selectedRequirement,
                    usernameForm.getUsername(),
                    bugzillaService.getNumber(usernameForm.getUsername()), k);
            setRecommendation(recommendations);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NotificationException e) {
            Notification notification = new Notification(e.getMessage(), 5000, Notification.Position.TOP_CENTER);
            notification.open();
        }
    }

}
