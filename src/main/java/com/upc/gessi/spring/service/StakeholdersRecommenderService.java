package com.upc.gessi.spring.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upc.gessi.spring.entity.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.util.ArrayList;
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

    public void setBatchProcess(List<Participant> participants, List<Person> persons, List<Project> project,
                                List<Requirement> requirements, List<Responsible> responsibles) throws IOException {
        batchProcess = new BatchProcess();
        batchProcess.setParticipants(participants);
        batchProcess.setPersons(persons);
        batchProcess.setProjects(project);
        batchProcess.setRequirements(requirements);
        batchProcess.setResponsibles(responsibles);
        batch_process();
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

    public void loadRequirements(InputStream inputStream) throws IOException {
        batchProcess = mapper.readValue(inputStream, BatchProcess.class);
        batch_process();
    }



    private void batch_process() throws IOException {
        String response = sendPostHttpRequest(stakeholdersRecommenderServiceUrl + "/batch_process?" +
                        "withAvailability=false" +
                        "&withComponent=true" +
                        "&autoMapping=false" +
                        "&keywords=true" +
                        "&organization=" + company,
                new StringEntity(mapper.writeValueAsString(batchProcess), "UTF-8"));
        KeywordsBatch keywordsBatch = mapper.readValue(response, KeywordsBatch.class);
    }

    public List<Recommendation> recommend(Requirement first) throws IOException{

        //FIXME
        Recommend recommend = new Recommend();
        recommend.setProject(batchProcess.getProjects().get(0));
        recommend.setRequirement(first);
        recommend.setUser(batchProcess.getPersons().get(0));

        String response = sendPostHttpRequest(stakeholdersRecommenderServiceUrl + "/recommend?" +
                        "k=10" +
                        "&projectSpecific=false" +
                        "&organization=" + company,
                new StringEntity(mapper.writeValueAsString(recommend), "UTF-8"));

        return mapper.readValue(response, new TypeReference<List<Recommendation>>(){});

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

    public void rejectRecommendation(Recommendation selectedRecommendation) throws IOException {
        sendPostHttpRequest(stakeholdersRecommenderServiceUrl +
                "/reject_recommendation?" +
                "organization=" + company +
                "&rejected=" + selectedRecommendation.getPerson().getUsername() +
                "&requirement=" + selectedRecommendation.getRequirement().getId() +
                "&user=" + metauser, null);
    }

    public void acceptRecommendation(Recommendation selectedRecommendation) {
        //TODO
    }

}
