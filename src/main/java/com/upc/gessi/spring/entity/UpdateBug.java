package com.upc.gessi.spring.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateBug implements Serializable {

    private String assigned_to;
    private CcList cc;

    public String getAssigned_to() {
        return assigned_to;
    }

    public void setAssigned_to(String assigned_to) {
        this.assigned_to = assigned_to;
    }

    public CcList getCc() {
        return cc;
    }

    public void setCc(CcList cc) {
        this.cc = cc;
    }
}
