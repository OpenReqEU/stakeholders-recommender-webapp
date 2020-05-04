package com.upc.gessi.spring.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upc.gessi.spring.PropertiesLoader;
import com.upc.gessi.spring.entity.*;
import com.upc.gessi.spring.entity.persistence.*;
import com.upc.gessi.spring.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Imported class from @acasas
 */
@Service
public class BugzillaService {

    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private ResponsibleRepository responsibleRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private RequirementRepository requirementRepository;

    private final String bugzillaUrl = PropertiesLoader.getProperty("bugzillaUrl");
    private String gerritUrl = PropertiesLoader.getProperty("gerritUrl"); // /changes /accounts

    private RestTemplate restTemplate = new RestTemplate();
    private List<Responsible> responsibles;
    private List<Requirement> requirements;
    private List<Person> persons;
    private List<Participant> participants;
    private List<Project> project;

    private String token;

    private Map<Person,List<BugzillaBug>> bugs=new HashMap<>();

    private static BugzillaService instance;

    public static BugzillaService getInstance() {
        if (instance == null) {
            instance = new BugzillaService();
        }
        return instance;
    }

    public String getBugzillaUrl() {
        return bugzillaUrl;
    }

    /**
     * Get bugzilla info
     * @param components
     * @param products
     * @param date
     */
    public void extractInfo(String[] components, String[] products, String[] statuses, String date) {
        setBugs(components, products, statuses, date);
        extractPersons();
        extractResponsibles();
        extractParticipants();
        extractProject();
        persistData();
        System.out.println("Data stored");
    }

    private void persistData() {
        requirementRepository.saveAll(requirements);
        personRepository.saveAll(persons);
        responsibleRepository.saveAll(responsibles);
        participantRepository.saveAll(participants);
        projectRepository.saveAll(project);
    }

    private void extractProject() {
        Project p=new Project();
        p.setId("1");
        List<String> specifiedRequirements=new ArrayList<String>();

        /*for (Requirement r : requirements) {
            specifiedRequirements.add(r.getId());
        }*/
        p.setSpecifiedRequirements(requirements);

        List<Project> projList=new ArrayList<>();
        projList.add(p);
        project=projList;
    }

    private void extractParticipants() {
        List<Participant> part=new ArrayList<Participant>();
        for (Person p:persons) {
            Participant participant = new Participant();
            participant.setPerson(p.getUsername());
            participant.setProject("1");
            part.add(participant);
        }
        participants=part;
    }

    private void setBugs(String[] components, String[] products, String[] statuses, String date) {
        requirements = new ArrayList<>();
        for (String component : components) {
            for (String product : products) {
                for (String status : statuses) {
                    List<Requirement> reqs = getRequirements(date, product, status, component);
                    for (Requirement r : reqs)
                        if (!requirements.contains(r)) requirements.add(r);
                }
            }
        }
    }

    private List<Requirement> getRequirements(String date, String product, String status, String component) {
        BugzillaBugsSchema response=calltoServiceBugs("?include_fields=id,status,see_also,cc,assigned_to,summary,last_change_time,component,whiteboard" +
                "&status=" + status.toUpperCase() +
                "&product=" + product +
                "&component=" + component +
                "&creation_time=" + date +
                "&limit=100000" +
                "&offset=0");
        List<Requirement> reqs= new ArrayList<Requirement>();

        for (BugzillaBug bu:response.getBugs()) {
            if (bu.getAssigned_to() != "nobody@mozilla.org") {
                String[] test = bu.getAssigned_to().split("@");
                if (!test[1].equals("bugzilla.bugs")) {
                    List<BugzillaBug> list = new ArrayList<>();
                    Person assign;
                    assign = new Person(bu.getAssigned_to(),bu.getAssigned_to_detail().getReal_name());
                    if (bugs.containsKey(assign)) {
                        List<BugzillaBug> aux = bugs.get(assign);
                        aux.add(bu);
                        bugs.put(assign, aux);
                    } else {
                        list.add(bu);
                        bugs.put(assign, list);
                    }
                    Requirement requirement = new Requirement();
                    requirement.setId(bu.getId());
                    requirement.setDescription(bu.getSummary());
                    requirement.setModified_at(bu.getLast_change_time());
                    requirement.setCc(bu.getCc().size());
                    requirement.setEffort(1);
                    requirement.setAssigned(!assign.getUsername().toLowerCase().contains("inbox")
                            && !assign.getUsername().toLowerCase().contains("triage") ? assign.getName() : null);
                    requirement.setRequirementParts(Collections.singletonList(new RequirementPart(bu.getComponent(), bu.getComponent())));
                    requirement.setStalebug(bu.getWhiteboard() != null && bu.getWhiteboard().equals("stalebug"));
                    if (bu.getSee_also() != null ) {
                        requirement.setGerrit(bu.getSee_also().stream().filter(s -> s.contains("https://git.eclipse.org/r/")).findFirst().orElse(null));
                    }
                    requirement.setStatus(bu.getStatus());

                    reqs.add(requirement);
                }
            }
        }
        System.out.println(reqs.size());
        return reqs;
    }


    private void extractPersons() {
        persons = new ArrayList<>(bugs.keySet());
        List<Person> delete = new ArrayList<>();
        for (Person p : persons) {
            if (p.getUsername().toLowerCase().contains("inbox") ||
                    p.getUsername().toLowerCase().contains("triaged")) {
                delete.add(p);
            }
        }
        persons.removeAll(delete);
    }

    private void extractResponsibles() {
        List<Responsible> resp = new ArrayList<>();
        int i =0;
        for (Person person : persons) {
            if (bugs.containsKey(person)) {
                for (BugzillaBug b : bugs.get(person)) {
                    if (requirements.contains(new Requirement(b.getId()))) {
                        Responsible re = new Responsible();
                        re.setPerson(person.getUsername());
                        re.setRequirement(b.getId());
                        resp.add(re);
                    }
                }
            }
        }
        responsibles = resp;
    }

    public List<Responsible> getResponsibles() {
        return responsibles;
    }

    public void setResponsibles(List<Responsible> responsibles) {
        this.responsibles = responsibles;
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<Requirement> requirements) {
        this.requirements = requirements;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }


    private BugzillaBugsSchema calltoServiceBugs(String param) {
        String callUrl = bugzillaUrl + "/rest/bug" + param;
        System.out.println(callUrl);
        ResponseEntity<BugzillaBugsSchema> response = restTemplate.exchange(
                callUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<BugzillaBugsSchema>() {
                });
        return response.getBody();
    }
    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public List<Project> getProject() {
        return project;
    }

    public void setProject(List<Project> project) {
        this.project = project;
    }

    public Boolean login(String user, String password) {
        try {
            String callUrl = bugzillaUrl + "/rest/login?login=" + user + "&password=" + password;
            ResponseEntity<BugzillaToken> response = restTemplate.exchange(
                    callUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<BugzillaToken>() {
                    });
            this.token = response.getBody().getToken();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean assignUser(Recommendation selectedRecommendation) {
        try {
            String callUrl = bugzillaUrl + "rest/bug/" + selectedRecommendation.getRequirement().getId() + "?token=" + token;
            UpdateBug updateBug = new UpdateBug();
            updateBug.setAssigned_to(selectedRecommendation.getPerson().getUsername());
            ResponseEntity response = restTemplate.exchange(
                    callUrl,
                    HttpMethod.PUT,
                    new HttpEntity<>(updateBug),
                    new ParameterizedTypeReference<BugzillaToken>() {
                    });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean ccUser(Recommendation selectedRecommendation) {
        try {
            String callUrl = bugzillaUrl + "rest/bug/" + selectedRecommendation.getRequirement().getId() + "?token=" + token;
            UpdateBug updateBug = new UpdateBug();
            updateBug.setCc(new CcList(Collections.singletonList(selectedRecommendation.getPerson().getUsername())));
            ResponseEntity response = restTemplate.exchange(
                    callUrl,
                    HttpMethod.PUT,
                    new HttpEntity<>(updateBug),
                    new ParameterizedTypeReference<BugzillaToken>() {
                    });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addGerritUserToList(Requirement selectedRequirement, List<Recommendation> recommendations, int k) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            String callUrl = gerritUrl + "changes/" + selectedRequirement.getGerrit().split(gerritUrl)[1];
            ResponseEntity<String> response = restTemplate.exchange(
                    callUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<String>() {
                    });
            String body = response.getBody().replace(")]}'", "");
            GerritChange gerritChange = objectMapper.readValue(body, GerritChange.class);

            callUrl = gerritUrl + "accounts/" + gerritChange.getOwner().get_account_id();
            response = restTemplate.exchange(
                    callUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<String>() {
                    });

            body = response.getBody().replace(")]}'", "");
            Account account = objectMapper.readValue(body, Account.class);
            Person person = persons.stream().filter(p -> p.getUsername().equals(account.getEmail())).findFirst().orElse(null);

            if (person == null) throw new Exception("Person not found");
            else if (!recommendations.stream().anyMatch(r -> r.getPerson().getUsername().equals(person.getUsername()))) {
                Recommendation recommendation = new Recommendation(person, selectedRequirement, 1.0);
                recommendations.remove(k-1);
                recommendations.add(0, recommendation);
            } else {
                recommendations.remove(recommendations.stream().filter(r -> r.getPerson().getUsername().equals(person.getUsername())).findFirst().orElse(null));
                recommendations.add(0, new Recommendation(person, selectedRequirement, 1.0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

