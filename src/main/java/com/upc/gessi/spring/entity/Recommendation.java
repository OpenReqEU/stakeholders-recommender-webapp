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

    public Recommendation(Person person, Requirement requirement, Double appropiatenessScore) {
        this.person = person;
        this.requirement = requirement;
        this.appropiatenessScore = appropiatenessScore;
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

    public String getAppropiatenessScore() {
        return String.format("%.2f", appropiatenessScore * 100) + " %";
    }

    public Double getAppropiatenessScoreDouble() {
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
