package com.example.librabry_management;

import java.util.ArrayList;
import java.util.List;
import com.example.Controller.*;


public class User extends Account {

    public User(String name, String birthdate, String phone_number, String email, String location, String password) {
        super(name, birthdate, phone_number, email, location, password);
    }
}
