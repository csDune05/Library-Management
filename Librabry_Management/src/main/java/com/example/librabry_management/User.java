package com.example.librabry_management;

import com.example.Controller.*;
import com.example.Feature.*;

public class User {
    protected int id;
    protected String name;
    protected String birthdate;
    protected String phone_number;
    protected String email;
    protected String location;
    protected String password;

    /**
     * Constructor.
     */
    public User(String name, String birthdate, String phone_number, String email, String location, String password) {
        this.name = name;
        this.birthdate = birthdate;
        this.phone_number = phone_number;
        this.email = email;
        this.location = location;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getBirthDate() {
        return birthdate;
    }

    public void setBirthDate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}