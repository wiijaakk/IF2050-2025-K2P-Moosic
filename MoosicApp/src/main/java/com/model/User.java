package com.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int id_user;
    private String role;
    private String username;
    private String full_name;
    private String email;
    private String password;
    private String address;

    public User(int id_user, String role, String username, String full_name, String email, String password, String address) {
        this.id_user = id_user;
        this.role = role;
        this.username = username;
        this.full_name = full_name;
        this.email = email;
        this.password = password;
        this.address = address;
    }

    public int getId_user() {
        return id_user;
    }

    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getAddress() {
        return address;
    }

    public int getId() { 
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
