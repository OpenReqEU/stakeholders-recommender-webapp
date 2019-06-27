package com.upc.gessi.spring.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upc.gessi.spring.entity.*;
import com.upc.gessi.spring.exception.NotificationException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class StakeholdersRecommenderService {

    private static final String stakeholdersRecommenderServiceUrl = "http://217.172.12.199:9410/upc/stakeholders-recommender";
    private static final String company = "Vogella";
    private static final String metauser = "web-client-SR-service";

    private static StakeholdersRecommenderService instance;

    private ObjectMapper mapper = new ObjectMapper();
    private BatchProcess batchProcess;
    private List<Recommendation> recommendations;
    private List<KeywordsBatch> keywordsBatch;

    public void setBatchProcess(List<Participant> participants, List<Person> persons, List<Project> project,
                                List<Requirement> requirements, List<Responsible> responsibles, Boolean keywords) throws IOException {
        batchProcess = new BatchProcess();
        batchProcess.setParticipants(participants);
        batchProcess.setPersons(persons);
        batchProcess.setProjects(project);
        batchProcess.setRequirements(requirements);
        batchProcess.setResponsibles(responsibles);
        batch_process(keywords);
    }

    public StakeholdersRecommenderService() {
        batchProcess = new BatchProcess();
    }

    public static StakeholdersRecommenderService getInstance() {
        if (instance == null) {
            instance = new StakeholdersRecommenderService();
        }
        return instance;
    }

    public List<Requirement> getRequirements() {
        return batchProcess.getRequirements();
    }

    public List<Requirement> getRequirements(String value) {
        List<Requirement> reqs = new ArrayList<>();
        for (Requirement req : batchProcess.getRequirements()) {
            if (req.getDescription().contains(value) || req.getId().contains(value)) reqs.add(req);
        }
        return reqs;
    }

    private void batch_process(Boolean keywords) throws IOException {
        Long start = Calendar.getInstance().getTimeInMillis();
        String response = sendPostHttpRequest(stakeholdersRecommenderServiceUrl + "/batch_process?" +
                        "withAvailability=false" +
                        "&withComponent=true" +
                        "&autoMapping=false" +
                        "&keywords=" + keywords +
                        "&organization=" + company,
                new StringEntity(mapper.writeValueAsString(batchProcess), "UTF-8"));
        Long end = Calendar.getInstance().getTimeInMillis();
        System.out.println("Batch process took: " + (end - start) / 1000 + " seconds");
        if (keywords)
            keywordsBatch = mapper.readValue(response, new TypeReference<List<KeywordsBatch>>(){});
        System.out.println("Batch process completed");
    }

    public List<Recommendation> recommend(Requirement first, String username, Integer intUsername, Integer k) throws IOException, NotificationException {

        Recommend recommend = new Recommend();
        recommend.setProject(findProject(first));
        recommend.setRequirement(first);
        recommend.setUser(findPerson(intUsername));

        String response = sendPostHttpRequest(stakeholdersRecommenderServiceUrl + "/recommend?" +
                        "k=" + k +
                        "&projectSpecific=false" +
                        "&organization=" + company,
                new StringEntity(mapper.writeValueAsString(recommend), "UTF-8"));

        return mapper.readValue(response, new TypeReference<List<Recommendation>>(){});

    }

    private Person findPerson(Integer intUsername) throws NotificationException {
        for (Person p : batchProcess.getPersons()) {
            if (p.getUsername().equals(String.valueOf(intUsername))) return p;
        }
        throw new NotificationException("Person not found");
    }

    private Project findProject(Requirement first) throws NotificationException {
        for (Project p : batchProcess.getProjects()) {
            if (p.getSpecifiedRequirements() != null
                    && p.getSpecifiedRequirements().contains(first.getId())) return p;
        }
        throw new NotificationException("Requirement not found in any project");
    }

    private String sendPostHttpRequest(String url, StringEntity body) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("content-type", "application/json");
            httpPost.setEntity(body);
            CloseableHttpResponse response = client.execute(httpPost);
            System.out.println("HTTP Request " + httpPost.getMethod());
            System.out.println("Response code: " + response.getStatusLine());
            //TODO throw exception if 400
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
                sb.append(br.lines().collect(Collectors.joining(System.lineSeparator())));
            }
            return sb.toString();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            client.close();
        }
        return null;
    }

    public void rejectRecommendation(Recommendation selectedRecommendation, Integer username) throws IOException {
        sendPostHttpRequest(stakeholdersRecommenderServiceUrl +
                "/reject_recommendation?" +
                "organization=" + company +
                "&rejected=" + selectedRecommendation.getPerson().getUsername() +
                "&requirement=" + selectedRecommendation.getRequirement().getId() +
                "&user=" + username, null);
    }

    public void acceptRecommendation(Recommendation selectedRecommendation) {

    }

    public List<String> getRequirementKeywords(String id) {
        if (keywordsBatch != null) {
            for (KeywordsBatch keywordsBatchItem : keywordsBatch) {
                for (RequirementsSkills rs : keywordsBatchItem.getRequirements()) {
                    if (rs.getRequirement().equals(id)) return rs.getSkills();
                }
            }
        }
        return null;
    }
}
