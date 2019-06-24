package com.upc.gessi.spring;

import com.upc.gessi.spring.entity.Recommend;
import com.upc.gessi.spring.entity.Recommendation;
import com.upc.gessi.spring.entity.Requirement;
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
@PWA(name = "Project Base for Vaadin Flow with Spring", shortName = "Project Base")
@StyleSheet("frontend://styles/styles.css") // Relative to Servlet URL
public class MainView extends VerticalLayout {

    private StakeholdersRecommenderService service = StakeholdersRecommenderService.getInstance();
    private BugzillaService bugzillaService = BugzillaService.getInstance();

    private Grid<Requirement> grid = new Grid<>(Requirement.class);
    private Grid<Recommendation> recommendationGrid = new Grid<>(Recommendation.class);
    private BugzillaForm bugzillaForm = new BugzillaForm();
    private TextField filterText = new TextField();

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

        grid.setColumns("id", "description", "effort", "modified_at");
        grid.getColumnByKey("id").setFlexGrow(1).setResizable(true);
        grid.getColumnByKey("description").setFlexGrow(9).setResizable(true);
        grid.getColumnByKey("effort").setFlexGrow(1).setResizable(true);
        grid.getColumnByKey("modified_at").setFlexGrow(3).setResizable(true);
        recommendationGrid.setColumns("person", "apropiatenessScore", "availabilityScore");

        filterText.setValueChangeMode(ValueChangeMode.EAGER);
        filterText.addValueChangeListener(e -> updateList());

        recommend.setText("Recommend stakeholders");
        recommend.addClickListener(event -> {
            try {
                Notification notification;
                if (selectedRequirement != null) {
                    notification = new Notification(
                            "Recommend request sent to Stakeholders Recommender service", 5000,
                            Notification.Position.TOP_CENTER);
                    List<Recommendation> recommendations = service.recommend(selectedRequirement);
                    setRecommendation(recommendations);
                }
                else {
                    notification = new Notification(
                            "Please select a requirement", 5000,
                            Notification.Position.TOP_CENTER);
                }
                notification.open();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        showRequirementDetails.setText("Show requirement details");
        showRequirementDetails.addClickListener(event -> {
            if (selectedRequirement == null) {
                Notification notification = new Notification(
                        "No requirement is selected", 5000,
                        Notification.Position.TOP_CENTER);
                notification.open();
            } else {
                RequirementDetailsView requirementDetailsView = new RequirementDetailsView(selectedRequirement);
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
                service.rejectRecommendation(selectedRecommendation);
                this.recommendations.remove(selectedRecommendation);
                recommendationGrid.setItems(this.recommendations);
                Notification notification = new Notification(
                        "Recommendation rejected", 3000,
                        Notification.Position.TOP_CENTER);
                notification.open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        acceptRecommendation.getClassNames().add("custom-button");
        acceptRecommendation.setText("Accept recommendation");
        acceptRecommendation.addClickListener(event -> {
            service.acceptRecommendation(selectedRecommendation);
        });

        //BUILD LAYOUT
        HorizontalLayout recommendationButtons = new HorizontalLayout();
        recommendationButtons.add(rejectRecommendation);

        VerticalLayout rightPanel = new VerticalLayout();
        rightPanel.add(recommendationButtons);
        rightPanel.add(recommendationGrid);

        HorizontalLayout mainPanel = new HorizontalLayout();
        mainPanel.add(leftPanel, rightPanel);
        mainPanel.setSizeFull();

        HorizontalLayout subheader = new HorizontalLayout();

        Button loadData = new Button("Load data");
        loadData.setClassName("custom-button");
        loadData.addClickListener(event -> {
            try {
                if (!bugzillaForm.isFieldEmpty()) {
                    bugzillaService.extractInfo(bugzillaForm.getComponent(), bugzillaForm.getStatus(), bugzillaForm.getProduct());
                    service.setBatchProcess(bugzillaService.getParticipants(), bugzillaService.getPersons(), bugzillaService.getProject(),
                            bugzillaService.getRequirements(), bugzillaService.getResponsibles());
                    updateList();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        subheader.add(bugzillaForm, loadData);
        subheader.setClassName("subheader");
        add(header, subheader, mainPanel);

        setSizeFull();

    }

    List<Recommendation> recommendations;

    private void updateList() {
        grid.setItems(service.getRequirements(filterText.getValue()));
    }

    private void setRecommendation(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
        recommendationGrid.setItems(recommendations);
    }

}
