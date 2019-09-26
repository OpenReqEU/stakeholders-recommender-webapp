package com.upc.gessi.spring.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Person implements Serializable {

    private String username;
    private String name;

    public Person() {
    }

    public Person(String username) {
        this.username = username;
    }

    public Person(String username, String name) {
        this.username = username;
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
      return username;
    };

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Person)) {
            return false;
        }

        Person user = (Person) o;

        return
                //user.name.equals(name) &&
                user.username.equals(username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

}
