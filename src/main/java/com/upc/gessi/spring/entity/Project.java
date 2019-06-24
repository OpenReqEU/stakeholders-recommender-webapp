package com.upc.gessi.spring.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Project implements Serializable {

    private String id;
    private List<String> specifiedRequirements;

    public Project() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getSpecifiedRequirements() {
        return specifiedRequirements;
    }

    public void setSpecifiedRequirements(List<String> specifiedRequirements) {
        this.specifiedRequirements = specifiedRequirements;
    }
}
