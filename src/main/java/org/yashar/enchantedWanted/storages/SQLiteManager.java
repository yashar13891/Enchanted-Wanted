package org.yashar.enchantedWanted.storages;

import java.sql.*;
import java.util.logging.Logger;

import org.yashar.enchantedWanted.EnchantedWanted;

public class SQLiteManager implements DatabaseManager {

    private Connection connection;
    private final Logger logger = EnchantedWanted.getPluginLogger();

    @Override
    public void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/EnchantedWanted/database.db");
            logger.info("[DataBase] SQLite Connected!");
        } catch (SQLException e) {
            logger.severe("[DataBase] SQLite Connection Failed: " + e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        if (connection == null) return;
        try {
            connection.close();
            logger.info("[DataBase] SQLite Disconnected!");
        } catch (SQLException e) {
            logger.warning("[DataBase] Error closing SQLite connection: " + e.getMessage());
        } finally {
            connection = null;
        }
    }

    @Override
    public void createTable() {
        if (isConnected()) {
            logger.severe("[DataBase] Connection is null, cannot update wanted level.");
            return;
        }
        String sql = "CREATE TABLE IF NOT EXISTS players (uuid TEXT PRIMARY KEY, name TEXT, wanted INTEGER DEFAULT 0);";
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
        String sql = "UPDATE players SET wanted = wanted + 1 WHERE uuid = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                logger.warning("[DataBase] No player found with UUID: " + uuid);
            }
        } catch (SQLException e) {
            logger.severe("[DataBase] Error updating wanted level: " + e.getMessage());
        }
    }

    @Override
    public int getWanted(String uuid) {
        String sql = "SELECT wanted FROM players WHERE uuid = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("wanted");
            }
        } catch (SQLException e) {
            logger.severe("[DataBase] Error retrieving wanted level: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
