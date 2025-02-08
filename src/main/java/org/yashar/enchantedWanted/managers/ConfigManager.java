package org.yashar.enchantedWanted.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yashar.enchantedWanted.EnchantedWanted;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class ConfigManager {
    private static ConfigManager instance;
    private final EnchantedWanted plugin;
    private FileConfiguration config;
    private final File configFile;
    public ConfigManager(EnchantedWanted plugin) {
        this.plugin = plugin;

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        loadConfigFile();
    }

    public static ConfigManager getInstance(EnchantedWanted plugin) {
        if (instance == null) {
            instance = new ConfigManager(plugin);
        }
        return instance;
    }

    public void loadConfigFile() {
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
            plugin.getLogger().info("config.yml has been loaded from resources.");
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public String getMessage(String key, String defaultMessage) {
        if (config.contains(key)) {
            return config.getString(key, defaultMessage);
        } else {
            return "Message not found: " + key;
        }
    }

    public void reloadConfig() {
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
            plugin.getLogger().info("config.yml was not found, loaded from resources.");
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        plugin.getLogger().info("config.yml reloaded successfully.");
    }

    public void saveConfig() {
        try {
            config.save(configFile);
            plugin.getLogger().info("config.yml saved successfully.");
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save config.yml", e);
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
