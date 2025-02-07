package org.yashar.enchantedWanted.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yashar.enchantedWanted.EnchantedWanted;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class ConfigManager {
    private static ConfigManager instance;
    private static EnchantedWanted plugin = null;
    private static FileConfiguration config;
    private static File configFile;

    private ConfigManager(EnchantedWanted plugin) {
        ConfigManager.plugin = plugin;
        loadConfig();
    }

    public static void init(EnchantedWanted plugin) {
        if (instance == null) {
            instance = new ConfigManager(plugin);
        }
    }

    public static ConfigManager getInstance() {
        return instance;
    }

    public static void loadConfig() {
        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            plugin.getLogger().warning("Could not create plugin data folder!");
        }

        configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
            plugin.getLogger().info("Default config file created!");
        }

        config = YamlConfiguration.loadConfiguration(configFile);
        plugin.getLogger().info("Config file loaded successfully!");
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public void saveConfig() {
        if (config == null || configFile == null) {
            plugin.getLogger().severe("Config file is not initialized!");
            return;
        }

        try {
            config.save(configFile);
            plugin.getLogger().info("Config file saved successfully!");
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config file!", e);
        }
    }

    public void reloadConfig() {
        if (configFile == null) {
            plugin.getLogger().severe("Config file is not initialized!");
            return;
        }

        config = YamlConfiguration.loadConfiguration(configFile);
        plugin.getLogger().info("Config file reloaded successfully!");
    }
}
