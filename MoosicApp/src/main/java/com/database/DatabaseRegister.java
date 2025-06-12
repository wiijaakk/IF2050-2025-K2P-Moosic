package com.database;

import java.sql.*;

public class DatabaseRegister {

    public static void init() {
        String sql = "CREATE TABLE IF NOT EXISTS users ("
                   + "id_user SERIAL PRIMARY KEY, "
                   + "full_name TEXT NOT NULL, "
                   + "address TEXT, "
                   + "username TEXT NOT NULL UNIQUE, "
                   + "password TEXT NOT NULL, "
                   + "email TEXT NOT NULL UNIQUE, "
                   + "role user_role NOT NULL DEFAULT 'user'"
                   + ");";

        try (Connection conn = DCRegister.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Users table checked/created.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    public static boolean isUsernameTaken(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = DCRegister.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // true if found

        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
            return false;
        }
    }

    public static boolean isEmailTaken(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = DCRegister.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.err.println("Error checking email: " + e.getMessage());
            return false;
        }
    }

    public static boolean insertUser(String fullName, String address, String username,
                                     String password, String email, String role) {
        String sql = "INSERT INTO users (full_name, address, username, password, email, role) "
                   + "VALUES (?, ?, ?, ?, ?, ?::user_role)";

        try (Connection conn = DCRegister.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fullName);
            ps.setString(2, address);
            ps.setString(3, username);
            ps.setString(4, password);
            ps.setString(5, email);
            ps.setString(6, role);

            int affected = ps.executeUpdate();
            return affected > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
            return false;
        }
    }
}
