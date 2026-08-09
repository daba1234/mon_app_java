package com.microgest.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {

    private static final String URL = System.getProperty("microgest.db.url", "jdbc:postgresql://localhost:5432/microGest_db");
    private static final String USER = System.getProperty("microgest.db.user", "postgres");
    private static final String PASSWORD = System.getProperty("microgest.db.password", "P@sser123");

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}