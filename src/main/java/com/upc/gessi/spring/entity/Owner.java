package com.upc.gessi.spring.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Owner implements Serializable {

    private String _account_id;

    public String get_account_id() {
        return _account_id;
    }

    public void set_account_id(String _account_id) {
        this._account_id = _account_id;
    }
}
