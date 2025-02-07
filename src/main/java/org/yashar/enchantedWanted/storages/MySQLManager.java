package org.yashar.enchantedWanted.storages;

import java.sql.*;
import java.util.UUID;
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

    public void addWanted(UUID uuid, int amount) {
        if (isConnected()) {
            logger.severe("[DataBase] Connection is null, cannot update wanted level.");
            return;
        }
        if (amount < 1) return;

        int currentWanted = getWanted(uuid);
        int newWanted = Math.max(currentWanted - amount, 5);
        String sql = "UPDATE players SET wanted = wanted + ? WHERE uuid = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newWanted);
            stmt.setString(2, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("[DataBase] Error increasing wanted level: " + e.getMessage());
        }
    }

    public void removeWanted(UUID uuid, int amount) {
        if (isConnected()) {
            logger.severe("[DataBase] Connection is null, cannot update wanted level.");
            return;
        }
        if (amount < 1) return;

        int currentWanted = getWanted(uuid);
        int newWanted = Math.max(currentWanted - amount, 0);

        String sql = "UPDATE players SET wanted = ? WHERE uuid = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newWanted);
            stmt.setString(2, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("[DataBase] Error decreasing wanted level: " + e.getMessage());
        }
    }

    public void setWanted(UUID uuid, int level) {
        if (isConnected()) {
            logger.severe("[DataBase] Connection is null, cannot update wanted level.");
            return;
        }
        if (level < 0) level = 0;

        String sql = "UPDATE players SET wanted = ? WHERE uuid = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, level);
            stmt.setString(2, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("[DataBase] Error setting wanted level: " + e.getMessage());
        }
    }

    @Override
    public int getWanted(UUID uuid) {
        if (!isConnected()) {
            logger.severe("[DataBase] Connection is null, cannot retrieve wanted level.");
            return 0;
        }

        String sql = "SELECT wanted FROM players WHERE uuid = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, String.valueOf(uuid));
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

