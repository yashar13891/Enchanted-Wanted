package org.yashar.enchantedWanted.storage;

import java.sql.*;
import java.util.logging.Logger;
import org.yashar.enchantedWanted.EnchantedWanted;
public class MySQLStorage {
    private static Connection connection;
    private static final Logger logger = EnchantedWanted.getPluginLogger();

    public static void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String host = "localhost";
            int port = 3306;
            String database = "enchanted_wanted";
            String url = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&autoReconnect=true", host, port, database);
            String username = "root";
            String password = "";
            connection = DriverManager.getConnection(url, username, password);
            logger.info("[DataBase] MySQL Connected!");
        } catch (ClassNotFoundException e) {
            logger.severe("[DataBase] MySQL Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            logger.severe("[DataBase] MySQL Connection Failed: " + e.getMessage());
        }
    }

    public static void disconnect() {
        if (connection == null) return;

        try {
            connection.close();
            logger.info("[DataBase] MySQL Disconnected!");
        } catch (SQLException e) {
            logger.warning("[DataBase] Error closing MySQL connection: " + e.getMessage());
        } finally {
            connection = null;
        }
    }

    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS players ("
                + "uuid VARCHAR(36) PRIMARY KEY, "
                + "name VARCHAR(16) NOT NULL, "
                + "wanted INT DEFAULT 0"
                + ");";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.execute();
            logger.info("[DataBase] Table 'players' checked/created successfully.");
        } catch (SQLException e) {
            logger.severe("[DataBase] Error creating table: " + e.getMessage());
        }
    }

    public static void addWanted(String uuid, String name) {
        String sql = "INSERT INTO players (uuid, name, wanted) VALUES (?, ?, 1) "
                + "ON DUPLICATE KEY UPDATE wanted = wanted + 1, name = VALUES(name);";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid);
            stmt.setString(2, name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("[DataBase] Error updating wanted level: " + e.getMessage());
        }
    }

    public static int getWanted(String uuid) {
        String sql = "SELECT wanted FROM players WHERE uuid = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("wanted");
                }
            }
        } catch (SQLException e) {
            logger.severe("[DataBase] Error retrieving wanted level: " + e.getMessage());
        }
        return 0;
    }
}
