package com.upc.gessi.spring.service;

import com.upc.gessi.spring.entity.persistence.Person;
import com.upc.gessi.spring.entity.persistence.Project;
import com.upc.gessi.spring.entity.persistence.Requirement;
import com.upc.gessi.spring.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class RequirementsService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private RequirementRepository requirementRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private ResponsibleRepository responsibleRepository;

    public List<Requirement> findRequirements(String text) {
        return requirementRepository.findByDescription(text.toLowerCase());
    }

    public List<Person> getPersons() {
        return StreamSupport.stream(personRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    public List<Project> getProjects() {
        return StreamSupport.stream(projectRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    public void deleteRequirements() {
        projectRepository.deleteAll();
        personRepository.deleteAll();
        participantRepository.deleteAll();
        responsibleRepository.deleteAll();
        requirementRepository.deleteAll();
    }

    public List<Requirement> findRequirements(String text, String[] products, String[] components, String[] statuses, String date) {
        String[] dateArray = date.split("-");

        return requirementRepository.filter(text,
                products == null || products.length == 0 || products.length == 1 && products[0].isEmpty() ? null : String.join(",", products),
                components == null || components.length == 0 || components.length == 1 && components[0].isEmpty() ? null : String.join(",", components),
                statuses == null || statuses.length == 0 || statuses.length == 1 && statuses[0].isEmpty() ? null : String.join(",", statuses),
                dateArray[0] + "-"
                        + (dateArray[1].length() == 1 ? '0' + dateArray[1] : dateArray[1]) + "-"
                        + (dateArray[2].length() == 1 ? '0' + dateArray[2] : dateArray[2]));
    }
}
