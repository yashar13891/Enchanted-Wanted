package org.yashar.enchantedWanted.storages;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.yashar.enchantedWanted.EnchantedWanted;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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
        int maxWanted = EnchantedWanted.getInstance().getConfig().getInt("wanted.max", 6);
        setWanted(uuid, Math.min(maxWanted, getWanted(uuid) + amount));
        setWanted(uuid, getWanted(uuid) + amount);
    }


    @Override
    public void removeWanted(UUID uuid, int amount) {
        if (amount < 1) return;
        setWanted(uuid, Math.max(0, getWanted(uuid) - amount));
    }

    @Override
    public void setWanted(UUID uuid, int level) {
        int maxWanted = EnchantedWanted.getInstance().getConfig().getInt("wanted.max", 6);
        if (level < 0) level = 0;
        if (level > maxWanted) level = maxWanted;
        wantedCache.put(uuid, level);
        int finalLevel = level;
        CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO players (uuid, name, wanted) VALUES (?, ?, ?) ON CONFLICT(uuid) DO UPDATE SET wanted = ?;";
            String name = Bukkit.getOfflinePlayer(uuid).getName();
            if (name == null) name = "Unknown";

            try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
                stmt.setString(1, uuid.toString());
                stmt.setString(2, name);
                stmt.setInt(3, finalLevel);
                stmt.setInt(4, finalLevel);
                stmt.executeUpdate();
            } catch (SQLException e) {
                logger.severe("[Database] Error setting wanted level: " + e.getMessage());
            }
        });
    }
    public void reconnectDatabase() {
        disconnect();
        connect();
        logger.info("[Database] Reconnected successfully!");
    }

    public void saveCacheToDatabase() {
        logger.info("[Database] Saving cached wanted levels to database ...");

        if (!isConnected()) {
            logger.severe("[Database] Connection lost! Trying to reconnect...");
            reconnectDatabase();
            if (!isConnected()) {
                logger.severe("[Database] Reconnection failed! Cache not saved.");
                return;
            }
        }

        String sql = "INSERT INTO players (uuid, name, wanted) VALUES (?, ?, ?) ON CONFLICT(uuid) DO UPDATE SET wanted = ?;";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            for (UUID uuid : wantedCache.asMap().keySet()) {
                Integer wantedLevel = wantedCache.getIfPresent(uuid);
                if (wantedLevel == null) wantedLevel = 0;
                String name = Bukkit.getOfflinePlayer(uuid).getName();
                if (name == null) name = "Unknown";

                stmt.setString(1, uuid.toString());
                stmt.setString(2, name);
                stmt.setInt(3, wantedLevel);
                stmt.setInt(4, wantedLevel);
                stmt.addBatch();
            }
            stmt.executeBatch();
            logger.info("[Database] Cache successfully saved to database.");
        } catch (SQLException e) {
            logger.severe("[Database] Error in bulk update: " + e.getMessage());
        }

        wantedCache.invalidateAll();
    }



}
