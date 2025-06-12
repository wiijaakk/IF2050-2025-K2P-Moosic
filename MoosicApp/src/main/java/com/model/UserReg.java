package com.model;

public class UserReg {
    private int id;
    private String fullName;
    private String address;
    private String username;
    private String password;
    private String email;

    public UserReg(int id, String fullName, String address, String username, String password, String email) {
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

    public void setId(int id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setAddress(String address) { this.address = address; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
}
