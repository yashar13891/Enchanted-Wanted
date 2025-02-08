package org.yashar.enchantedWanted.storages;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.yashar.enchantedWanted.EnchantedWanted;
import java.sql.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class MySQLManager implements DatabaseManager {
    private Connection connection;
    private final Logger logger = EnchantedWanted.getPluginLogger();

    private final Cache<UUID, Integer> wantedCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    private Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String host = EnchantedWanted.getInstance().getConfig().getString("database.host", "localhost");
            int port = EnchantedWanted.getInstance().getConfig().getInt("database.port", 3306);
            String database = EnchantedWanted.getInstance().getConfig().getString("database.name", "enchanted_wanted");
            String username = EnchantedWanted.getInstance().getConfig().getString("database.username", "root");
            String password = EnchantedWanted.getInstance().getConfig().getString("database.password", "");

            String url = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&autoReconnect=true", host, port, database);
            connection = DriverManager.getConnection(url, username, password);
        }
        return connection;
    }

    @Override
    public void connect() {
        if (isConnected()) return;
        try {
            getConnection();
            logger.info("[Database] MySQL Connected!");
        } catch (SQLException e) {
            logger.severe("[Database] MySQL Connection Failed: " + e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        if (connection == null) return;
        try {
            connection.close();
            connection = null;
            wantedCache.invalidateAll();
            logger.info("[Database] MySQL Disconnected!");
        } catch (SQLException e) {
            logger.warning("[Database] Error closing MySQL connection: " + e.getMessage());
        }
    }

    @Override
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS players (uuid VARCHAR(36) PRIMARY KEY, name VARCHAR(16), wanted INT DEFAULT 0);";
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

        String sql = "INSERT INTO players (uuid, name, wanted) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE wanted = VALUES(wanted);";
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