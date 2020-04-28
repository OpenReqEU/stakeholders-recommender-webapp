package com.upc.gessi.spring.entity.persistence.id;

import com.upc.gessi.spring.entity.persistence.Responsible;

import javax.persistence.Id;
import java.io.Serializable;

public class ResponsibleId implements Serializable{

    @Id
    private String person;
    @Id
    private String requirement;

    public ResponsibleId() {

    }

    public ResponsibleId(String person, String requirement) {
        this.person = person;
        this.requirement = requirement;
    }

}
