package org.yashar.enchantedWanted.storages;

import java.sql.*;
import java.util.UUID;
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
        String sql = "CREATE TABLE IF NOT EXISTS players (uuid TEXT PRIMARY KEY, name TEXT, wanted INTEGER DEFAULT 0);";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.execute();
            logger.info("[DataBase] Table 'players' checked/created successfully.");
        } catch (SQLException e) {
            logger.severe("[DataBase] Error creating table: " + e.getMessage());
        }
    }

    @Override
    public int getWanted(UUID uuid) {
        if (playerExists(uuid)) {
            insertPlayer(uuid, 0);
        }

        String sql = "SELECT wanted FROM players WHERE uuid = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("wanted");
            }
        } catch (SQLException e) {
            logger.severe("[DataBase] Error retrieving wanted level for UUID " + uuid + ": " + e.getMessage());
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

    @Override
    public void addWanted(UUID uuid, int amount) {
        if (amount < 1) return;

        if (playerExists(uuid)) {
            insertPlayer(uuid, amount);
        } else {
            String sql = "UPDATE players SET wanted = wanted + ? WHERE uuid = ?;";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, amount);
                stmt.setString(2, uuid.toString());
                stmt.executeUpdate();
            } catch (SQLException e) {
                logger.severe("[DataBase] Error increasing wanted level: " + e.getMessage());
            }
        }
    }

    @Override
    public void removeWanted(UUID uuid, int amount) {
        if (amount < 1) return;

        if (playerExists(uuid)) {
            insertPlayer(uuid, 0);
        } else {
            int currentWanted = getWanted(uuid);
            int newWanted = Math.max(0, currentWanted - amount);

            String sql = "UPDATE players SET wanted = ? WHERE uuid = ?;";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, newWanted);
                stmt.setString(2, uuid.toString());
                stmt.executeUpdate();
            } catch (SQLException e) {
                logger.severe("[DataBase] Error decreasing wanted level: " + e.getMessage());
            }
        }
    }

    @Override
    public void setWanted(UUID uuid, int level) {
        if (level < 0) level = 0;

        if (playerExists(uuid)) {
            insertPlayer(uuid, level);
        } else {
            String sql = "UPDATE players SET wanted = ? WHERE uuid = ?;";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, level);
                stmt.setString(2, uuid.toString());
                stmt.executeUpdate();
            } catch (SQLException e) {
                logger.severe("[DataBase] Error setting wanted level: " + e.getMessage());
            }
        }
    }

    private boolean playerExists(UUID uuid) {
        String sql = "SELECT COUNT(*) FROM players WHERE uuid = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) <= 0;
            }
        } catch (SQLException e) {
            logger.severe("[DataBase] Error checking player existence: " + e.getMessage());
        }
        return true;
    }

    private void insertPlayer(UUID uuid, int wanted) {
        String sql = "INSERT INTO players (uuid, name, wanted) VALUES (?, ?, ?);";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.setInt(3, wanted);
            stmt.executeUpdate();
            logger.info("[DataBase] New player added: " + uuid);
        } catch (SQLException e) {
            logger.severe("[DataBase] Error inserting new player: " + e.getMessage());
        }
    }
}
