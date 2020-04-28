package com.upc.gessi.spring.entity.persistence.id;

import com.upc.gessi.spring.entity.persistence.Requirement;

import javax.persistence.Id;
import java.io.Serializable;

public class RequirementPartId implements Serializable {

    @Id
    private String id;
    @Id
    private Requirement requirement;

    public RequirementPartId() {

    }

    public RequirementPartId(String id, Requirement requirement) {
        this.id = id;
        this.requirement = requirement;
    }

}
