package com.example.librabry_management;

import java.util.ArrayList;
import java.util.Calendar;

public class Account {
    protected String id;
    protected String name;
    protected String birthdate;
    protected String phone_number;
    protected String email;
    protected String location;
    protected String password;
    protected static boolean isLoggedIn;
    protected static ArrayList<Account> accountList = new ArrayList<>();

    public Account() {}

    public Account(String name, String birthdate, String phone_number, String email, String location, String password) {
        this.id = CreateId.CreateUserId();
        this.name = name;
        if (isValidBirthdate(birthdate)) {
            this.birthdate = birthdate;
        } else {
            throw new IllegalArgumentException("Invalid birthdate format: dd/mm/yyyy.");
        }
        this.phone_number = phone_number;
        this.email = email;
        this.location = location;
        this.password = password;
        this.isLoggedIn = false;
    }

    private boolean isValidBirthdate(String birthdate) {
        if (!birthdate.matches("\\d{2}/\\d{2}/\\d{4}")) {
            return false;
        }

        String[] parts = birthdate.split("/");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        return isValidDate(day, month, year);
    }

    private boolean isValidDate(int day, int month, int year) {
        if (month < 1 || month > 12) {
            return false;
        }

        if (day < 1 || day > daysInMonth(month, year)) {
            return false;
        }

        if (year < 0 || year > Calendar.getInstance().get(Calendar.YEAR)) {
            return false;
        }

        return true;
    }

    private int daysInMonth(int month, int year) {
        switch (month) {
            case 4: case 6: case 9: case 11:
                return 30;
            case 2:
                return (isLeapYear(year)) ? 29 : 28;
            default:
                return 31;
        }
    }

    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
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