package com.example.librabry_management;

import java.util.ArrayList;
import java.util.Calendar;

public class Account {
    private String id;
    private String name;
    private String birthdate;
    private String phone_number;
    private String email;
    private String location;
    private String password;
    private static boolean isLoggedIn;

    private static ArrayList<Account> accountList = new ArrayList<>();

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

    public static void registerAccount(String name, String birthdate, String phone_number, String email, String location, String password) {
        if (!isLoggedIn) {
            if (isEmailRegistered(email)) {
                System.out.println("This email already has an account.");
            } else {
                Account newAccount = new Account(name, birthdate, phone_number, email, location, password);
                accountList.add(newAccount);
                System.out.println("Account registered successfully.");
            }
        }
    }

    private static boolean isEmailRegistered(String email) {
        if (accountList.contains(email)) {
            return true;
        }
        return false;
    }

    public static void login(String email, String password) {
        if (!isLoggedIn) {
            for (Account account : accountList) {
                if (account.getEmail().equals(email) && account.getPassword().equals(password)) {
                    System.out.println("Login successful.");
                    isLoggedIn = true;
                }
            }
            System.out.println("Login failed: Incorrect username or password.");
        }
    }

    public static void logout() {
        isLoggedIn = false;
        System.out.println("Logout successful.");
    }

    public void viewProfile() {
        if (isLoggedIn) {
            System.out.println("ID: " + this.id);
            System.out.println("Name: " + this.name);
            System.out.println("Birthdate: " + this.birthdate);
            System.out.println("Phone: " + this.phone_number);
            System.out.println("Email: " + this.email);
            System.out.println("Location: " + this.location);
            System.out.println("Password: " + this.password);
        } else {
            System.out.println("Not logged in.");
        }
    }

    public void editProfile(String newName, String newBirthdate, String newPhone_number, String newLocation, String newPassword) {
        if (isLoggedIn) {
            if (newName != null) {
                this.name = newName;
            }
            if (newBirthdate != null) {
                this.birthdate = newBirthdate;
            }
            if (newPhone_number != null) {
                this.phone_number = newPhone_number;
            }
            if (newLocation != null) {
                this.location = newLocation;
            }
            if (newPassword != null) {
                this.password = newPassword;
            }
            System.out.println("Edit profile successful.");
        } else {
            System.out.println("Not logged in.");
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