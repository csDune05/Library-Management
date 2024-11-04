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
    protected boolean isLoggedIn;

    protected static ArrayList<User> userList = new ArrayList<>();
    protected static ArrayList<Admin> adminList = new ArrayList<>();

    public Account() {
        this.isLoggedIn = false;
    }

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

    public void register(Account account) {
        if (!isLoggedIn) {
            if (account instanceof Admin) {
                for (Admin admin : adminList) {
                    if (admin.getEmail().equals(account.email)) {
                        System.out.println("Admin with this email already exists.");
                        return;
                    }
                }
                adminList.add((Admin) account);
                System.out.println("Admin registered successfully.");
            } else if (account instanceof User) {
                for (User user : userList) {
                    if (user.getEmail().equals(account.email)) {
                        System.out.println("User with this email already exists.");
                        return;
                    }
                }
                userList.add((User) account);
                System.out.println("User registered successfully.");
            }
        }
    }

    public void login(Account account) {
        if (!isLoggedIn) {
            if (account instanceof Admin) {
                for (Admin admin : adminList) {
                    if (admin.getEmail().equals(email) && admin.getPassword().equals(password)) {
                        this.isLoggedIn = true;
                        System.out.println("Admin login successful.");
                        return;
                    }
                }
            }

            if (account instanceof User) {
                for (User user : userList) {
                    if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                        this.isLoggedIn = true;
                        System.out.println("User login successful.");
                        return;
                    }
                }
            }
            System.out.println("Login failed: Incorrect email or password.");
        } else {
            System.out.println("Already logged in.");
        }
    }


    public void logout() {
        if (isLoggedIn) {
            this.isLoggedIn = false;
            System.out.println("Logout successful.");
        } else {
            System.out.println("You are not logged in.");
        }
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