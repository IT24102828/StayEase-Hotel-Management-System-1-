package com.stayease.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database connection manager using standard JDBC.
 * Reads configuration from db.properties or system environment.
 */
public class DBConnection {

    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());
    private static final Properties PROPERTIES = new Properties();

    private static String dbDriver;
    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;

    static {
        loadConfiguration();
    }

    private static synchronized void loadConfiguration() {
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                PROPERTIES.load(input);
            } else {
                LOGGER.warning("db.properties not found on classpath, defaulting to local MySQL config");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load db.properties", e);
        }

        // Allow system properties or environment variables to override
        dbDriver = getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        dbUrl = getProperty("db.url", "jdbc:mysql://localhost:3306/stayease_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        dbUser = getProperty("db.user", "root");
        dbPassword = getProperty("db.password", "");

        try {
            Class.forName(dbDriver);
            LOGGER.info("JDBC Driver loaded successfully: " + dbDriver);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "JDBC Driver class not found: " + dbDriver, e);
        }
    }

    private static String getProperty(String key, String defaultValue) {
        // System property > Env var > properties file > default
        String envKey = key.replace('.', '_').toUpperCase();
        String sysVal = System.getProperty(key);
        if (sysVal != null && !sysVal.trim().isEmpty()) {
            return sysVal;
        }
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.trim().isEmpty()) {
            return envVal;
        }
        return PROPERTIES.getProperty(key, defaultValue);
    }

    /**
     * Obtains a new database Connection.
     *
     * @return Connection instance
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    /**
     * For testing/reconfiguration purposes.
     */
    public static synchronized void resetConfiguration() {
        loadConfiguration();
    }
}
