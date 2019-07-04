package com.upc.gessi.spring.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upc.gessi.spring.entity.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Imported class from @acasas
 */
public class BugzillaService {

    private static final String bugzillaUrl = "https://bugs.eclipse.org/bugs";

    private RestTemplate restTemplate = new RestTemplate();
    private List<Responsible> responsibles;
    private List<Requirement> requirements;
    private List<Person> persons;
    private List<Participant> participants;
    private List<Project> project;

    private Map<String,List<BugzillaBug>> bugs=new HashMap<String,List<BugzillaBug>>();
    private Map<String,Integer> emailToNumber;

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
     * @param component
     * @param status
     * @param product
     * @param date
     */
    public void extractInfo(String component, String status, String product, String date) {
        setBugs(component, status, product, date);
        extractPersons();
        extractResponsibles();
        extractParticipants();
        extractProject();
    }

    private void extractProject() {
        Project p=new Project();
        p.setId("1");
        List<String> specifiedRequirements=new ArrayList<String>();

        for (Requirement r : requirements) {
            specifiedRequirements.add(r.getId());
        }
        p.setSpecifiedRequirements(specifiedRequirements);

        List<Project> projList=new ArrayList<Project>();
        projList.add(p);
        project=projList;
    }

    private void extractParticipants() {
        List<Participant> part=new ArrayList<Participant>();
        for (Person p:persons) {
            Participant participant=new Participant();
            participant.setPerson(p.getUsername());
            //participant.setAvailability(100);
            participant.setProject("1");
            part.add(participant);
        }
        participants=part;
    }

    private void setBugs(String component, String status, String product, String date) {
        Integer offset=0;
        BugzillaBugsSchema response=calltoServiceBugs("?include_fields=id,assigned_to,summary,last_change_time,component" +
                "&status=" + status.toUpperCase() +
                "&product=" + product +
                "&component=" + component +
                "&creation_time=" + date +
                "&limit=10000" +
                "&offset="+offset);
        List<Requirement> reqs= new ArrayList<Requirement>();
        emailToNumber=new HashMap<String,Integer>();
        Integer counter=0;
        // while (response.getBugs()!=null && response.getBugs().size()>0) {
        for (BugzillaBug bu:response.getBugs()) {
            if (bu.getAssigned_to() != "nobody@mozilla.org") {
                String[] test = bu.getAssigned_to().split("@");
                if (!test[1].equals("bugzilla.bugs")) {
                    List<BugzillaBug> list = new ArrayList<BugzillaBug>();
                    String assign;
                    /*if (emailToNumber.containsKey(bu.getAssigned_to())) assign=emailToNumber.get(bu.getAssigned_to()).toString();
                    else {
                        emailToNumber.put(bu.getAssigned_to(),counter);
                        assign=counter.toString();
                        counter++;
                    }*/
                    assign = bu.getAssigned_to();
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
                    requirement.setEffort(1);
                    requirement.setRequirementParts(Arrays.asList(new RequirementPart("1", bu.getComponent())));

                    reqs.add(requirement);
                }
            }
        }


        /*response=calltoServiceBugs("?include_fields=id,assigned_to,summary,last_change_time" +
                "&status=closed" +
                "&product=Platform" +
                "&component=SWT" +
                "&creation_time=2013-01-01" +
                "&limit=10000" +
                "&offset="+offset);
        for (BugzillaBug bu:response.getBugs()) {
            if (bu.getAssigned_to() != "nobody@mozilla.org") {
                String[] test = bu.getAssigned_to().split("@");
                if (!test[1].equals("bugzilla.bugs")) {
                    List<BugzillaBug> list = new ArrayList<BugzillaBug>();
                    String assign;
                    if (emailToNumber.containsKey(bu.getAssigned_to())) assign=emailToNumber.get(bu.getAssigned_to()).toString();
                    else {
                        emailToNumber.put(bu.getAssigned_to(),counter);
                        assign=counter.toString();
                        counter++;
                    }
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
                    requirement.setEffort(1);
                    requirement.setModified_at(bu.getLast_change_time());
                    requirement.setRequirementParts(Arrays.asList(new RequirementPart("1", bu.getComponent())));

                    reqs.add(requirement);
                }
            }
        }
        //}*/
        System.out.println(reqs.size());
        requirements=reqs;

    }

    public void extractPersons() {
        Set<String> stakeholders= new HashSet<String>();
        stakeholders.addAll(bugs.keySet());
        List<Person> pers = new ArrayList<Person>();
        for (String s : stakeholders) {
            pers.add(new Person(s));
        }
        persons = pers;
    }

    public void extractResponsibles() {
        List<Responsible> resp = new ArrayList<Responsible>();
        int i =0;
        for (Person person : persons) {
            for (BugzillaBug b: bugs.get(person.getUsername())) {
                if (requirements.contains(new Requirement(b.getId()))) {
                    Responsible re = new Responsible();
                    re.setPerson(person.getUsername());
                    re.setRequirement(b.getId());
                    resp.add(re);
                }
            }
        }
        responsibles = resp;
    }
    /*
        public void extractRequirements() {
            List<Requirement> req = new ArrayList<Requirement>();
            Set<String> reqId = new HashSet<String>();
            int i =0;
            for (Responsible resp : responsibles) {
                reqId.add(resp.getRequirement());
            }
            for (String s : reqId) {
                List<BugzillaBug> bug = bugs.getBugs();
                Requirement requirement = new Requirement();
                requirement.setId(s);
                requirement.setDescription(bug.getSummary());
                req.add(requirement);
            }
            requirements = req;
        }
    */
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

    public Integer getNumber(String email) {
        return emailToNumber.get(email);
    }

}

