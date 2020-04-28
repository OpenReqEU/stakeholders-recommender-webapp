package com.upc.gessi.spring.entity.persistence;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.upc.gessi.spring.entity.persistence.id.ParticipantId;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@IdClass(ParticipantId.class)
public class Participant implements Serializable {

    private Integer availability;
    @Id
    private String person;
    @Id
    private String project;

    public Participant() {

    }

    public Participant(String person, String project) {
        this.person = person;
        this.project = project;
    }

    public Integer getAvailability() {
        return availability;
    }

    public void setAvailability(Integer availability) {
        this.availability = availability;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }
}
