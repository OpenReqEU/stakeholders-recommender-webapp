package com.upc.gessi.spring.entity.persistence;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.upc.gessi.spring.entity.persistence.id.RequirementPartId;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class RequirementPart implements Serializable {

    @Id
    @GeneratedValue
    private Long generatedId;

    private String id;
    private String name;

    public RequirementPart() {

    }

    public RequirementPart(String s, String component) {
        this.id = s;
        this.name = component;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
