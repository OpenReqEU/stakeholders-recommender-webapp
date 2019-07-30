package com.upc.gessi.spring.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upc.gessi.spring.entity.*;
import com.upc.gessi.spring.exception.NotificationException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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

    private static final String stakeholdersRecommenderServiceUrl = "http://localhost:9410/upc/stakeholders-recommender";
    //private static final String stakeholdersRecommenderServiceUrl = "http://217.172.12.199:9410/upc/stakeholders-recommender";
    private static final String company = "Vogella";

    private static StakeholdersRecommenderService instance;

    private ObjectMapper mapper = new ObjectMapper();
    private BatchProcess batchProcess;
    private List<Recommendation> recommendations;
    private List<KeywordsBatch> keywordsBatch;

    public void setBatchProcess(List<Participant> participants, List<Person> persons, List<Project> project,
                                List<Requirement> requirements, List<Responsible> responsibles, Boolean keywords) throws IOException,
            NotificationException {
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

    public List<Requirement> getRequirements(String value) {
        List<Requirement> reqs = new ArrayList<>();
        for (Requirement req : batchProcess.getRequirements()) {
            if (req.getDescription().contains(value) || req.getId().contains(value)) reqs.add(req);
        }
        return reqs;
    }

    private void batch_process(Boolean keywords) throws IOException, NotificationException {
        Long start = Calendar.getInstance().getTimeInMillis();
        String response = sendPostHttpRequest(stakeholdersRecommenderServiceUrl + "/batch_process?" +
                        "withAvailability=true" +
                        "&withComponent=true" +
                        "&autoMapping=true" +
                        "&keywords=" + keywords +
                        "&organization=" + company,
                new StringEntity(mapper.writeValueAsString(batchProcess), "UTF-8"));
        Long end = Calendar.getInstance().getTimeInMillis();
        System.out.println("Batch process took: " + (end - start) / 1000 + " seconds");
        if (keywords)
            keywordsBatch = mapper.readValue(response, new TypeReference<List<KeywordsBatch>>(){});
        System.out.println("Batch process completed");
    }

    public List<Recommendation> recommend(Requirement first, String username, Integer k) throws IOException, NotificationException {

        Recommend recommend = new Recommend();
        recommend.setProject(findProject(first));
        recommend.setRequirement(first);
        recommend.setUser(findPerson(username));

        String response = sendPostHttpRequest(stakeholdersRecommenderServiceUrl + "/recommend?" +
                        "k=" + k +
                        "&projectSpecific=true" +
                        "&organization=" + company,
                new StringEntity(mapper.writeValueAsString(recommend), "UTF-8"));

        return mapper.readValue(response, new TypeReference<List<Recommendation>>(){});

    }

    private Person findPerson(String username) throws NotificationException {
        for (Person p : batchProcess.getPersons()) {
            if (p.getUsername().equals(username)) return p;
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

    private String sendPostHttpRequest(String url, StringEntity body) throws IOException, NotificationException {
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("content-type", "application/json");
            httpPost.setEntity(body);

            StringBuilder content = new StringBuilder();
            if (body != null) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(body.getContent()))) {
                    content.append(br.lines().collect(Collectors.joining(System.lineSeparator())));
                }
            }
            System.out.println(url);
            System.out.println(content.toString());

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
            e.printStackTrace();
            throw new NotificationException("There was a problem reaching the SR service. Please contact an administrator");
        } finally {
            client.close();
        }
    }

    public void rejectRecommendation(Recommendation selectedRecommendation, String username) throws IOException, NotificationException {
        sendPostHttpRequest(stakeholdersRecommenderServiceUrl +
                "/reject_recommendation?" +
                "organization=" + company +
                "&rejected=" + selectedRecommendation.getPerson().getUsername() +
                "&requirement=" + selectedRecommendation.getRequirement().getId() +
                "&user=" + username, null);
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

    public List<Skill> getPersonSkills(String username) throws NotificationException, IOException {
        String response = sendGetHttpRequest(stakeholdersRecommenderServiceUrl +
                "/getPersonSkills?" +
                "organization=" + company +
                "&person=" + username);
        return mapper.readValue(response, new TypeReference<List<Skill>>(){});
    }

    private String sendGetHttpRequest(String url) throws NotificationException, IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("content-type", "application/json");

            System.out.println(url);

            CloseableHttpResponse response = client.execute(httpGet);
            System.out.println("HTTP Request " + httpGet.getMethod());
            System.out.println("Response code: " + response.getStatusLine());
            //TODO throw exception if 400
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
                sb.append(br.lines().collect(Collectors.joining(System.lineSeparator())));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotificationException("There was a problem reaching the SR service. Please contact an administrator");
        } finally {
            client.close();
        }
    }

    public void undoRejection(String username, Recommendation lastRejection) throws IOException, NotificationException {
        sendPostHttpRequest(stakeholdersRecommenderServiceUrl +
                "/reject_recommendation?" +
                "organization=" + company +
                "&rejected=" + lastRejection.getPerson().getUsername() +
                "&requirement=" + lastRejection.getRequirement().getId() +
                "&user=" + username, null);
    }

}
