package org.yashar.enchantedWanted.storages;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.yashar.enchantedWanted.EnchantedWanted;
import java.sql.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class SQLiteManager implements DatabaseManager {
    private Connection connection;
    private final Logger logger = EnchantedWanted.getPluginLogger();

    private final Cache<UUID, Integer> wantedCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    private Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/EnchantedWanted/database.db");
        }
        return connection;
    }

    @Override
    public void connect() {
        if (isConnected()) return;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/EnchantedWanted/database.db");
            logger.info("[Database] SQLite Connected!");
        } catch (SQLException e) {
            logger.severe("[Database] SQLite Connection Failed: " + e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        if (connection == null) return;
        try {
            connection.close();
            connection = null;
            logger.info("[Database] SQLite Disconnected!");
        } catch (SQLException e) {
            logger.warning("[Database] Error closing SQLite connection: " + e.getMessage());
        }
    }

    @Override
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS players (uuid TEXT PRIMARY KEY, name TEXT, wanted INTEGER DEFAULT 0);";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.execute();
            logger.info("[Database] Table 'players' checked/created successfully.");
        } catch (SQLException e) {
            logger.severe("[Database] Error creating table: " + e.getMessage());
        }
    }

    @Override
    public int getWanted(UUID uuid) {
        Integer cachedValue = wantedCache.getIfPresent(uuid);
        if (cachedValue != null) return cachedValue;

        String sql = "SELECT wanted FROM players WHERE uuid = ?;";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int wanted = rs.getInt("wanted");
                wantedCache.put(uuid, wanted);
                return wanted;
            } else {
                setWanted(uuid, 0);
                return 0;
            }
        } catch (SQLException e) {
            logger.severe("[Database] Error retrieving wanted level for UUID " + uuid + ": " + e.getMessage());
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
        setWanted(uuid, getWanted(uuid) + amount);
    }

    @Override
    public void removeWanted(UUID uuid, int amount) {
        if (amount < 1) return;
        setWanted(uuid, Math.max(0, getWanted(uuid) - amount));
    }

    @Override
    public void setWanted(UUID uuid, int level) {
        if (level < 0) level = 0;
        wantedCache.put(uuid, level);

        String sql = "INSERT INTO players (uuid, name, wanted) VALUES (?, ?, ?) ON CONFLICT(uuid) DO UPDATE SET wanted = excluded.wanted;";
        String name = Bukkit.getOfflinePlayer(uuid).getName();
        if (name == null) name = "Unknown";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, name);
            stmt.setInt(3, level);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("[Database] Error setting wanted level: " + e.getMessage());
        }
    }
}
