package com.upc.gessi.spring.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Recommendation implements Serializable {

    private Person person;
    private Requirement requirement;
    private Double appropiatenessScore;
    private Double availabilityScore;

    public Recommendation() {

    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public void setRequirement(Requirement requirement) {
        this.requirement = requirement;
    }

    public Double getAppropiatenessScore() {
        return appropiatenessScore;
    }

    public void setAppropiatenessScore(Double apropiatenessScore) {
        this.appropiatenessScore = apropiatenessScore;
    }

    public Double getAvailabilityScore() {
        return availabilityScore;
    }

    public void setAvailabilityScore(Double availabilityScore) {
        this.availabilityScore = availabilityScore;
    }
}
