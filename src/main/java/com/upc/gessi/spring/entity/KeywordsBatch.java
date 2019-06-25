package com.upc.gessi.spring.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KeywordsBatch implements Serializable {

    List<RequirementsSkills> requirements;

    public List<RequirementsSkills> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<RequirementsSkills> requirements) {
        this.requirements = requirements;
    }
}
