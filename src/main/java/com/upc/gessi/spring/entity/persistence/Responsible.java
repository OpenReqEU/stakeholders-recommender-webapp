package com.upc.gessi.spring.entity.persistence;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.upc.gessi.spring.entity.persistence.id.ResponsibleId;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@IdClass(ResponsibleId.class)
public class Responsible implements Serializable {

    @Id
    private String person;
    @Id
    private String requirement;

    public Responsible() {
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }
}
