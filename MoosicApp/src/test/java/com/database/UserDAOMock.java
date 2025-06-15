package com.database;

import com.model.User;


public class UserDAOMock extends UserDAO {

    @Override
    public String login(String usernameOrEmail, String password) {
        if (("admin".equals(usernameOrEmail) || "admin@example.com".equals(usernameOrEmail)) && "admin123".equals(password)) {
            return "Login Berhasil sebagai admin!";
        } else if (("user".equals(usernameOrEmail) || "user@example.com".equals(usernameOrEmail)) && "user123".equals(password)) {
            return "Login Berhasil sebagai customer!";
        } else {
            return "Login Gagal: Username atau password salah.";
        }
    }


    @Override
    public boolean register(String username, String password, String email, String fullName, String address) {
        if ("existing_user".equals(username) || "existing@example.com".equals(email)) {
            System.out.println("UserDAOMock: Registration failed. Username or email already exists in mock DB.");
            return false; 
        } else if ("error_case".equals(username)) {
            System.err.println("UserDAOMock: Simulating a registration error.");
            return false; 
        } else {
            System.out.println("UserDAOMock: New user registered successfully in mock DB: " + username);
            return true; 
        }
    }

   
    @Override
    public void logout() {
        System.out.println("UserDAOMock: User logged out (mocked).");
    }
}