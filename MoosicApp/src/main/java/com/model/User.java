package com.model;

public class User {
    private int id;
    private String fullName;
    private String address;
    private String username;
    private String password; 
    private String email;

    public User(int id, String fullName, String address, String username, String password, String email) {
        this.id = id;
        this.fullName = fullName;
        this.address = address;
        this.username = username;
        this.password = password;
        this.email = email;
    }
    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getAddress() { return address; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
}
