package com.upc.gessi.spring.entity.persistence.id;

import com.upc.gessi.spring.entity.persistence.Participant;

import javax.persistence.Id;
import java.io.Serializable;

public class ParticipantId implements Serializable {

    @Id
    private String person;
    @Id
    private String project;

    public ParticipantId() {

    }

    public ParticipantId(String person, String project) {
        this.person = person;
        this.project = project;
    }

}
