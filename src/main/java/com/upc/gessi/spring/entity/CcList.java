package com.upc.gessi.spring.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CcList implements Serializable {

    private List<String> add;

    public CcList(List<String> singletonList) {
        this.add = singletonList;
    }

    public List<String> getAdd() {
        return add;
    }

    public void setAdd(List<String> add) {
        this.add = add;
    }
}
