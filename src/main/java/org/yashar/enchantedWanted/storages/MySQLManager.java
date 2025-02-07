package org.yashar.enchantedWanted.storages;

import java.sql.*;
import java.util.logging.Logger;
import org.yashar.enchantedWanted.EnchantedWanted;

public class MySQLManager implements DatabaseManager {
    private Connection connection;
    private final Logger logger = EnchantedWanted.getPluginLogger();

    @Override
    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String host = EnchantedWanted.getInstance().getConfig().getString("database.host", "localhost");
            int port = EnchantedWanted.getInstance().getConfig().getInt("database.port", 3306);
            String database = EnchantedWanted.getInstance().getConfig().getString("database.name", "enchanted_wanted");
            String username = EnchantedWanted.getInstance().getConfig().getString("database.username", "root");
            String password = EnchantedWanted.getInstance().getConfig().getString("database.password", "");

            String url = String.format("jdbc:mysql://%s:%d/%s?useSSL=true&requireSSL=false&autoReconnect=true", host, port, database);

            connection = DriverManager.getConnection(url, username, password);
            logger.info("[DataBase] MySQL Connected!");
        } catch (ClassNotFoundException e) {
            logger.severe("[DataBase] MySQL Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            logger.severe("[DataBase] MySQL Connection Failed: " + e.getMessage());
        }
    }

    @Override
    public void disconnect() {
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

    @Override
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public void createTable() {
        if (!isConnected()) {
            logger.severe("[DataBase] Connection is null, cannot create table.");
            return;
    }
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

    @Override
    public void addWanted(String uuid) {
        if (isConnected()) {
            logger.severe("[DataBase] Connection is null, cannot update wanted level.");
            return;
        }

        String sql = "INSERT INTO players (uuid, name, wanted) VALUES (?, ?, 1) "
                + "ON DUPLICATE KEY UPDATE wanted = wanted + 1, name = VALUES(name);";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("[DataBase] Error updating wanted level for UUID " + uuid + ": " + e.getMessage());
        }
    }

    @Override
    public int getWanted(String uuid) {
        if (!isConnected()) {
            logger.severe("[DataBase] Connection is null, cannot create table.");
            return 0;
        }
        String sql = "SELECT wanted FROM players WHERE uuid = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("wanted");
                }
            }
        } catch (SQLException e) {
            logger.severe("[DataBase] Error retrieving wanted level for UUID " + uuid + ": " + e.getMessage());
        }
        return 0;
    }
}
