package org.yashar.enchantedWanted.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yashar.enchantedWanted.EnchantedWanted;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class ConfigManager {
    private final EnchantedWanted plugin;
    private FileConfiguration config;
    private final File configFile;

    public ConfigManager(EnchantedWanted plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        load();
    }

    public void load() {
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
            plugin.getLogger().info("config.yml created & loaded.");
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(configFile);
        plugin.getLogger().info("config.yml reloaded.");
    }

    public void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Error saving config.yml", e);
        }
    }

    public String getString(String path, String def) {
        return config.getString(path, def);
    }

    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }

    public boolean getBoolean(String path, boolean def) {
        return config.getBoolean(path, def);
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
