package com.upc.gessi.spring.entity.persistence;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.springframework.data.repository.cdi.Eager;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Project implements Serializable {

    @Id
    private String id;
    @OneToMany(fetch = FetchType.EAGER)
    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
    @JsonIdentityReference(alwaysAsId=true)
    private List<Requirement> specifiedRequirements;

    public Project() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Requirement> getSpecifiedRequirements() {
        return specifiedRequirements;
    }

    public void setSpecifiedRequirements(List<Requirement> specifiedRequirements) {
        this.specifiedRequirements = specifiedRequirements;
    }
}
