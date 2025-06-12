package com.database;

import com.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public UserDAO() {
    }

    public String login(String usernameOrEmail, String password) {
        System.out.println("UserDAO: Attempting login for: " + usernameOrEmail);

        try (Connection conn = DatabaseConnectUser.getConnection()) { // Menggunakan DatabaseConnectUser
            if (conn == null) {
                return "Login Gagal: Koneksi database tidak tersedia.";
            }

            String sql = "SELECT * FROM users WHERE (username = ? OR email = ?) AND password = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, usernameOrEmail);
                pstmt.setString(2, usernameOrEmail);
                pstmt.setString(3, password);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    String role = rs.getString("role");
                    System.out.println("UserDAO: Login successful as " + role + ".");
                    return "Login Berhasil sebagai " + role + "!";
                }
            }

            System.out.println("UserDAO: Login failed for: " + usernameOrEmail);
            return "Login Gagal: Username atau password salah.";

        } catch (SQLException e) {
            System.err.println("Error during login process in UserDAO: " + e.getMessage());
            e.printStackTrace();
            return "Login Gagal: Terjadi kesalahan server.";
        }
    }
    
    public boolean register(String username, String password, String email, String fullName, String address) {
        try (Connection conn = DatabaseConnectUser.getConnection()) {
            if (conn == null) {
                System.err.println("Registrasi Gagal: Koneksi database tidak tersedia.");
                return false;
            }

            String checkSql = "SELECT COUNT(*) FROM users WHERE username = ? OR email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, email);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("UserDAO: Registration failed. Username or email already exists in DB.");
                    return false;
                }
            }

            String insertSql = "INSERT INTO users (role, username, full_name, email, password, address) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setString(1, "customer");
                pstmt.setString(2, username);
                pstmt.setString(3, fullName);
                pstmt.setString(4, email);
                pstmt.setString(5, password);
                pstmt.setString(6, address);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("UserDAO: New customer registered successfully in DB: " + username);
                    return true;
                } else {
                    System.err.println("UserDAO: Registration failed, no rows affected for " + username);
                    return false;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error during registration process in UserDAO: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void logout() {
        System.out.println("UserDAO: User logged out.");
    }
}