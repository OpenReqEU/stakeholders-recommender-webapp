package com.upc.gessi.spring.service;

import com.upc.gessi.spring.entity.persistence.Person;
import com.upc.gessi.spring.entity.persistence.Project;
import com.upc.gessi.spring.entity.persistence.Requirement;
import com.upc.gessi.spring.repository.PersonRepository;
import com.upc.gessi.spring.repository.ProjectRepository;
import com.upc.gessi.spring.repository.RequirementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<Requirement> findRequirements(String text) {
        return requirementRepository.findByDescription(text.toLowerCase());
    }

    public List<Person> getPersons() {
        return StreamSupport.stream(personRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    public List<Project> getProjects() {
        return StreamSupport.stream(projectRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }
}
