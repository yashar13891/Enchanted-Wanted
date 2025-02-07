package org.yashar.enchantedWanted.managers;

import org.yashar.enchantedWanted.EnchantedWanted;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class ConfigManager {
    public static EnchantedWanted plugin;
    private static FileConfiguration config;
    private static File configFile;


    public static void setupConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
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

    public static void saveConfig() {
        try {
            config.save(configFile);
            plugin.getLogger().info("Config file saved successfully!");
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config file!", e);
        }
    }

    public static void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        plugin.getLogger().info("Config file reloaded successfully!");
    }
}