package com.upc.gessi.spring.entity.persistence;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Requirement implements Serializable {

    @Id
    private String id;
    private String description;
    private Integer effort;
    private String modified_at;
    private Integer cc;
    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private List<RequirementPart> requirementParts;
    private String assigned;
    private String gerrit;
    private Boolean stalebug;
    private String status;

    public Requirement() {

    }

    public Requirement(String id) {
        this.id = id;
    }

    public Requirement(String id, String description, Integer effort, String modified_at) {
        this.id = id;
        this.description = description;
        this.effort = effort;
        this.modified_at = modified_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getEffort() {
        return effort;
    }

    public void setEffort(Integer effort) {
        this.effort = effort;
    }

    public String getModified_at() {
        return modified_at;
    }

    public void setModified_at(String modified_at) {
        this.modified_at = modified_at;
    }

    public List<RequirementPart> getRequirementParts() {
        return requirementParts;
    }

    public void setRequirementParts(List<RequirementPart> requirementParts) {
        this.requirementParts = requirementParts;
    }

    public Integer getCc() {
        return cc;
    }

    public void setCc(Integer cc) {
        this.cc = cc;
    }

    public String getAssigned() {
        return assigned != null ? assigned : "✖" ;
    }

    public void setAssigned(String assigned) {
        this.assigned = assigned;
    }

    public String getGerrit() {
        return gerrit;
    }

    public void setGerrit(String gerrit) {
        this.gerrit = gerrit;
    }

    public Boolean getStalebug() {
        return stalebug;
    }

    public void setStalebug(Boolean stalebug) {
        this.stalebug = stalebug;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        Requirement r = (Requirement) o;
        if (r == null || r.getId() == null || this.getId() == null) return false;
        return r.getId().equals(this.getId());
    }

}
