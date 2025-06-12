package com.database;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHomepage {
    private static final Dotenv dotenv = Dotenv.load(); 
    private static final String DB_URL = dotenv.get("DB_URL");
    private static final String DB_USER = dotenv.get("DB_USER");
    private static final String DB_PASSWORD = dotenv.get("DB_PASSWORD");

    public static Connection getConnection() {
        try {
            if (DB_URL == null || DB_USER == null || DB_PASSWORD == null) {
                System.err.println("Error: Database credentials (DB_URL, DB_USER, DB_PASSWORD) not found in .env file.");
                System.err.println("Please create a .env file in your project root with these variables.");
                return null;
            }
            
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            e.printStackTrace();
            return null; 
        }
    }
}