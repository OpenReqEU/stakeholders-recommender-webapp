package com.upc.gessi.spring.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BugzillaBug implements Serializable {

    String assigned_to;
    String id;
    String status;
    String summary;
    String last_change_time;
    String component;
    List<String> cc;
    List<String> see_also;
    AssignedDetail assigned_to_detail;

    public String getAssigned_to() {
        return assigned_to;
    }

    public void setAssigned_to(String assigned_to) {
        this.assigned_to = assigned_to;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLast_change_time() {
        return last_change_time;
    }

    public void setLast_change_time(String last_change_time) {
        this.last_change_time = last_change_time;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public List<String> getCc() {
        return cc;
    }

    public void setCc(List<String> cc) {
        this.cc = cc;
    }

    public AssignedDetail getAssigned_to_detail() {
        return assigned_to_detail;
    }

    public void setAssigned_to_detail(AssignedDetail assigned_to_detail) {
        this.assigned_to_detail = assigned_to_detail;
    }

    public List<String> getSee_also() {
        return see_also;
    }

    public void setSee_also(List<String> see_also) {
        this.see_also = see_also;
    }
}
